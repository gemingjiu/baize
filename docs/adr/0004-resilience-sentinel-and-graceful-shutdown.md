# ADR-0004：韧性基础 —— Sentinel 规则外置、优雅停机与探针约定

- 状态：Accepted
- 日期：2026-11-19
- 关联任务：T2.4（韧性基础）、T2.2（指标与告警）

## 背景

T2.4 要求「单实例驱逐不中断；限流规则热生效；探针全绿」。实施时发现三处约束与陷阱：

1. **Sentinel 规则只存在于控制台**：项目早期在 dev 通过 `spring.cloud.sentinel.enabled: false` 直接关掉 Sentinel（注释理由是「开发集群未部署控制台，避免启动反复连接 localhost:8858」）。规则无处存放，也无法追溯。
2. **Sentinel 规则为空时没有任何报错**：即便加了 Nacos 数据源，若 Sentinel 自动配置未装配，规则恒为空、限流静默失效——这是最难排查的一类故障。
3. **Boot 3 默认 `server.shutdown=immediate`**：K8s 滚动更新/节点排水时，在途请求会被硬切断，与 `terminationGracePeriodSeconds` 的语义配合不上。

## 决策

### 决策 1：Sentinel 保持 `enabled: true`，控制台地址留空

`application-dev.yml` 中 **不得** 把 `spring.cloud.sentinel.enabled` 设为 `false`。

原因：该类上有 `@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled", matchIfMissing = true)`，置 `false` 会让 `SentinelAutoConfiguration` 整类不装配 → `SentinelDataSourceHandler` 不注册 → Nacos 数据源永远不加载 → 规则恒空且**无任何日志告警**。

关闭控制台连接改用 `spring.cloud.sentinel.transport.dashboard:`（留空）实现：SCA 的元数据明确「地址为空时不尝试连接 dashboard」，既避免反复重连不存在的 `localhost:8858`，又不影响 Nacos 数据源。

> 若未来需要可视化或动态改规则，把 `dashboard` 填成 `localhost:8858` 并启动控制台即可，其余不变。

### 决策 2：规则文件入库，Nacos 作为发布通道

限流规则以 JSON 文件形式提交到仓库 `baize-backend/doc/DEFAULT_GROUP/sentinel/`，再由 Nacos 下发到各服务：

| 文件 | rule-type | 内容 |
|---|---|---|
| `baize-gateway-gw-flow.json` | gw-flow | auth 100 QPS / system 300 / gen 50 |
| `baize-auth-flow.json` | flow | `/api/v1/auth/login` 20 / `/refresh` 60 |
| `baize-system-flow.json` | flow | `/system/user/getByUsername` 200 / `/system/dict/data/all` 100 |

理由：规则只存在于控制台时无法 code review、无法回滚、无法追溯「谁在什么时候调低了阈值」。入库后规则变更有 diff、可评审。

**依赖声明注意**：SCA 的 `spring-cloud-alibaba-sentinel-datasource` 把 `sentinel-datasource-nacos` 声明为 `optional=true`，不会传递引入，必须在使用方 POM 显式声明（版本由 SCA BOM 管理，当前 1.8.8）。

### 决策 3：优雅停机 30s + Feign 超时基线，不启用 retryer

- `spring.lifecycle.timeout-per-shutdown-phase: 30s` + `server.shutdown: graceful`：收到 SIGTERM 后先停止接收新请求，等待在途请求完成。
- 与 K8s 侧配合：`preStop: sleep 5` + `terminationGracePeriodSeconds: 45`。5s 覆盖 Service Endpoint 摘除的传播延迟，45s > 30s 保证优雅停机窗口不会被 kubelet 提前 SIGKILL。
- `feign.client.config.default`: connectTimeout 5000 / readTimeout 10000。项目无 RestTemplate/WebClient，Feign 是唯一出站通道，统一在此设基线。
- **故意不开启 retryer**：写操作重试会放大重复提交（当前无幂等键）。需要重试的读接口应在业务层显式实现。

### 决策 4：探针路径必须带 context-path 前缀

三个服务的 `server.servlet.context-path` 均为 `/api/v1`，因此探针与指标路径是：

- liveness → `/api/v1/actuator/health/liveness`
- readiness → `/api/v1/actuator/health/readiness`
- metrics → `/api/v1/actuator/prometheus`

写成 `/actuator/health` 会 404，且探针失败会导致 Pod 无限重启——排障时优先怀疑这个前缀。

### 决策 5：actuator 端点收敛，未映射路径返回 404

- `management.endpoints.web.exposure.include` 统一收敛为 `health,info,prometheus,metrics`。原先 auth 用 `'*'`，会把 `env`/`heapdump`/`threaddump` 暴露在网关可达路径下。
- `GlobalExceptionHandler` 增加 `handleNotFoundPath`，把 `NoResourceFoundException`/`NoHandlerFoundException` 映射为 **404 + errorCode 40004**。此前它们落进 `Exception` 兜底分支返回 **500**，会让客户端 404 污染 5xx 错误率指标并误触发 `BaizeHighErrorRate` 告警。

## 后果

**正面**

- 限流规则可评审、可回滚、可追溯；三服务规则集中管理。
- 滚动更新与节点排水不中断在途请求。
- 客户端错误与服务端故障在指标层正确区分，告警不被噪声淹没。

**负面 / 待办**

- Nacos 上的 `application-dev.yml` 与三个 `-dev.yml` 需**人工同步**本次改动（仓库模板已更新，Nacos 配置中心尚未同步）。同步前本机验证依赖 JVM `-D` 覆盖。
- 规则文件变更后需重新发布到 Nacos 才生效（当前无 CI 自动发布）。
- 本机无 Docker/helm/kubectl，部署件仅做 YAML 静态校验（`yaml.safe_load_all` 全部通过），未在真实集群 apply。

## 备选方案与否决理由

| 方案 | 否决理由 |
|---|---|
| 保持 `sentinel.enabled: false`，靠网关层限流 | 网关限流看不到后端真实负载；且项目已引入 Sentinel 依赖，关掉等于白付依赖成本 |
| 规则只放 Nacos 控制台 | 无法 code review / 回滚 / 追溯，违反「配置即代码」 |
| 用 `server.shutdown: immediate` + 更短 grace | 会硬切断在途请求，长事务（如导出）必失败 |
| 开启 Feign retryer 统一重试 | 写操作无幂等键，重试会放大重复提交 |
| 未映射路径继续返回 500 | 污染错误率指标，告警失效 |
