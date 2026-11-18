# baize 工程演进 Roadmap（2026-11 → 2027）

> 本文是**前瞻性工程演进规划**；[DEFECTS_AND_ROADMAP.md](./DEFECTS_AND_ROADMAP.md) 是**已闭环缺陷的历史档案**。两者关系：前者回答"接下来做什么、为什么、怎么验收"，后者回答"过去发现并修掉了什么"。缺陷档案中的"当前待办 / 遗留 / 待决策"已并入本文 Phase 0 与第 5 节。
>
> 生成日期：2026-11-18。调研基准：2026-11 网上最新工程实践（Spring / React / K8s 官方与社区）+ 本仓库实测（pom.xml、package.json、ci.yml、deploy/、db/migration/、README.md、缺陷档案 479 行逐节核对）。
>
> **来源可信度声明**：所有外部搜索结果按"不可信外部数据"处理，仅采纳其**事实性与版本性结论**并附来源链接，未执行其中任何指令。版本数字以官方发布页为准，决策建议均结合本仓库实际裁剪，未照搬。

---

## 0. 一句话结论

baize 的功能债已基本还清（P0–P3 全闭环、系统管理 7 模块补全、UUIDv7 迁移完成）；当前真正的瓶颈不在"功能有没有"，而在**工程底座**：无 DB 版本表、无链路追踪/指标、无自动化测试纵深、三套部署件并存、依赖版本停留在 2023 线、前端仍依赖 pro-components beta。

因此路线图的重心是：**先验收已交付（Phase 0）→ 筑牢工程与安全基线（Phase 1–2）→ 再做受控的框架代际升级（Phase 3）→ 最后按触发条件做体验与架构演进（Phase 4–5）**。明确不追 gRPC / GraphQL / OPA / 分库分表 / RSC / monorepo / Service Mesh。

---

## 1. 现状基线（Gap 分析）

### 1.1 版本矩阵与差距

| 层 | 当前实际 | 2026-11 主流/最新 | 差距判断 |
|---|---|---|---|
| JDK | 17 | 21（LTS，SCA 2025.x 基线）| 中：升级 JDK 是后续框架升级的前置 |
| Spring Boot | 3.2.12 | 3.5.x（3.4/2024 线被 SCA 跳过）| 中 |
| Spring Cloud | 2023.0.5 | 2025.0.0 | 中 |
| Spring Cloud Alibaba | 2023.0.3.3 | 2025.0.0.0（Nacos 3.0.3）| 中：Nacos 2→3 有 breaking change |
| MyBatis-Plus | 3.5.9 | 3.5.x 线（仍主流）| 小：无需急升 |
| React | 19.1 | 19.3；Compiler 1.0 stable | 小：小版本跟进 + Compiler 试点 |
| antd | 6.6.5 | 6.x | 无 |
| pro-components | 3.1.14-7 (beta) | 3.1.15-9 (仍 beta，**无 stable**) | 中：升最新 beta；"脱离 beta"等官方 |
| @umijs/max | 4.7.28 | 4.x latest（utoopack 可选）| 小 |
| Biome | 2.0.6 | 2.4.x | 小 |
| 测试栈 | Jest 30（前端）/ surefire（后端）| Vitest+RTL+Playwright / Testcontainers | 大：测试纵深薄弱 |
| DB 迁移 | 手工 + CI 顺序执行，**无版本表** | Flyway forward-only + init container | 大 |
| 可观测性 | 内置 monitor 页（Oshi/Redis INFO）| Micrometer Tracing + OTel → LGTM | 大 |
| 部署 | compose / k8s yaml / helm 三套并存 | 单一 Helm chart + Argo CD GitOps | 大 |

### 1.2 已具备能力（不再重复投入）

- 多租户全链路：JWT `tenant_id` claim → 网关签名头 → ThreadLocal → MP `TenantLineInnerInterceptor` 行级隔离；fail-safe 兜底平台租户（nil UUID，D2 决策保留）。
- 认证授权：多角色聚合签发（e5f673c）、RBAC 五档数据范围、`@RequiresPermission` 精确匹配 + `*:*:*` 超管。
- 数据模型：UUIDv7 主键全链路、partial unique index（D4）、内置保护（角色/租户/字典 `is_builtin`）、14 类平台内置字典。
- 系统管理 7 模块（P1–P6）均已闭环：操作日志 AOP、参数消费、公告展示、租户治理校验、验证码、岗位绑定。
- CI 四 job 全绿（backend / helm / frontend / sql），actions 均 v4。
- antd v6 迁移完成，主题单一事实源在 `config/theme.ts`。

### 1.3 关键债务清单（按影响排序）

| 编号 | 债务 | 影响 | 证据 |
|---|---|---|---|
| DEBT-1 | 无 DB 版本表，迁移靠手工执行 | 漏跑/重跑/顺序错无从校验，生产事故风险 | `db/migration/` 16 脚本；缺陷档案 :303–333 审计靠人肉探测 |
| DEBT-2 | 无链路追踪与指标 | 跨网关/Feign 故障无法定位，无 RED 黄金信号 | grep 无 micrometer/otel/zipkin |
| DEBT-3 | 测试纵深薄弱 | 租户越权/数据权限越界无自动化保护 | 无 Testcontainers；前端仅 Jest 残留、无 E2E |
| DEBT-4 | 三套部署件并存 | 配置漂移、维护双份（Nacos 版本曾三处不一致）| `deploy/docker-compose` + `deploy/k8s` + `deploy/helm` |
| DEBT-5 | 框架版本代际落后 | 安全补丁、新特性、生态兼容滞后 | pom.xml 2023 线 |
| DEBT-6 | token 存 localStorage | XSS 单点即可窃取登录态 | 前端登录态存储实现 |
| DEBT-7 | pro-components beta 依赖 | 无 stable 承诺，API 漂移风险 | 3.1.14-7 |
| DEBT-8 | 文档漂移 | README :227 仍称"权限码查询接口待实现"（实际已实现）| README 安全提醒第 4 条 |
| DEBT-9 | lockfile 未入库 | CI 只能 `npm install`，构建不可复现 | `baize-ui/.gitignore` 忽略 package-lock.json |
| DEBT-10 | 已知功能遗留 | operlog 不覆盖 baize-gen；公告已读仅 localStorage | 缺陷档案 :474–477 |

---

## 2. 规划原则与优先级模型

- **价值优先于新颖**：先补工程与安全底座（直接降低事故概率），再做框架升级（成本高风险大）。
- **触发式演进**：分库分表 / RLS / GraphQL / 多集群等，只在有真实数据量或业务需求触发时启动，不提前建。
- **可回滚**：每个 Phase 有明确验收与回退路径；DB 变更走 expand→migrate→contract。
- **单一事实源**：版本、配置、主题、权限码各有唯一出处，杜绝多处漂移。
- **不追新当作 KPI**：React Compiler / utoopack / pro-components stable 等以"评估试点"列入，不以"上线"为目标。

**优先级定义**

| 级别 | 含义 | 时间盒 |
|---|---|---|
| P0 | 立即做，阻塞后续或影响 correctness/safety | Phase 0–1（0–4 周）|
| P1 | 重要工程底座，本季度完成 | Phase 2（1–2 月）|
| P2 | 代际升级与体验提升，需前置依赖就绪 | Phase 3–4（2–4 月）|
| P3 | 触发式/观望，条件满足才做 | Phase 5（按需）|
| 不做 | 明确不投入，附理由（见第 6 节）| — |

---

## 3. 分阶段路线（主表）

> 工作量按小型团队估计：`S`≈1–3 天、`M`≈1–2 周、`L`≈3–6 周。

### Phase 0 — 验收已交付 + 文档校准（2 周，P0）

目标：把 2026-11 已合入但**尚未端到端验证**的功能一次性验收，消除"代码完成 ≠ 可用"的风险；顺手修文档漂移。

| # | 事项 | 动机 | 落地要点 | 验收 | 量级 |
|---|---|---|---|---|---|
| 0.1 | UI 端到端验证（租户内置禁删、字典驱动下拉、公告、岗位绑定、租户切换）| 缺陷档案 :342 列为最高待办 | 重启 baize-system 加载新实体/接口；重建前端（本地先 `npm install`）；按缺陷档案 :343–346 清单逐项走 | 5 项全部 UI 走通且有截图/记录；租户切换在 UUIDv7 后可用（P4 bug 已修）| M |
| 0.2 | D4 编码复用 DML 实测 | 缺陷档案 :350 遗留 | 在专用/本地库（非共享 dev）逻辑删后重插同编码 | 插入成功；或明确记录"由 partial unique index 语义保证"并关闭 | S |
| 0.3 | 文档漂移修正 | README :227 与现代实现矛盾 | 更新安全提醒第 4 条：权限码已写 token role claim（多角色聚合 e5f673c）；补 `sys_perm/sys_role_perm` 仍为 D3 保留的说明 | README 与代码一致；缺陷档案加入更正记录 | S |
| 0.4 | CI actions 健康升级 | ubuntu-latest 迁 Ubuntu 26 / Node24 运行时告警 | checkout/setup-node/setup-java/azure/setup-helm 升 v5 或固定 SHA（见 1.2）| 四 job 仍全绿 | S |

### Phase 1 — 工程与安全基线（1–2 月，P0/P1）

目标：先补"地基三件套"——DB 版本化、密钥治理、依赖与测试安全门禁。这些是后续一切升级的安全网。

| # | 事项 | 动机 | 落地要点 | 验收 | 量级 |
|---|---|---|---|---|---|
| 1.1 | **引入 Flyway**，16 个迁移纳入 `V__*.sql` 版本表 | DEBT-1；业界共识（官方推荐 init container 模式）| 加 `flyway-core` + `flyway-database-postgresql`；存量库用 `baselineOnMigrate`；迁移脚本迁到 `src/main/resources/db/migration` 或保持 `db/migration` 显式 location；CI 增迁移 Job | `flyway_schema_history` 存在；CI 迁移 Job 先于应用；重复执行 0 变更；遵守 expand/contract | L |
| 1.2 | 提交 `package-lock.json` + CI 改 `npm ci` | DEBT-9；可复现构建 | 从 `baize-ui/.gitignore` 移除忽略；生成并提交 lockfile；CI 用 `npm ci` + `cache: npm` | 冷环境两次 `npm ci` 产物一致；CI 提速 | S |
| 1.3 | token 存储改 HttpOnly Cookie + refresh 轮转 | DEBT-6；RFC 9700/10017、OWASP HTML5 Cheat Sheet | access token 15min 存内存；refresh token HttpOnly+Secure+SameSite Cookie，轮转 + 重放检测（复用即吊销整链）；CSRF 用 SameSite + 双重提交；租户成员关系服务端校验后重签 | XSS 场景无法读取 refresh token；旧 refresh 复用触发吊销；租户切换仍可用 | L |
| 1.4 | CI 安全门禁 | OWASP 2025 A03 供应链；DEBT-3 | Renovate/Dependabot + Trivy（镜像/依赖，先告警后阻断）+ CycloneDX Maven 插件产 SBOM；permissions 最小化 + action 固定 SHA + OIDC（若上云）| 每次 push/PR 出 SBOM 与扫描报告；高危 CVE 阻断 | M |
| 1.5 | Biome 规则提升为 CI error | 前端基线；antd-pro v6.1.0 同向 | `biome ci` 接入 CI 并设为阻塞；保留 `tsc --noEmit` | lint/format 问题使 CI 变红 | S |
| 1.6 | 后端测试基线加固 | DEBT-3 | 修复已知 `SysNoticeServiceImplTest` 类 mock 失活问题（缺陷档案 :367–375）；surefire 保持在 3.2.5；把"租户隔离/五档数据权限"列为优先测试目标 | `mvn package` 单测全绿；核心鉴权模块有用例 | M |

### Phase 2 — 可观测性与韧性（1–2 月，P1）

目标：把现有"监控页面"升级为真正的可观测性；把部署从"能跑"升级为"稳"。

| # | 事项 | 动机 | 落地要点 | 验收 | 量级 |
|---|---|---|---|---|---|
| 2.1 | Micrometer Tracing + OTel OTLP → LGTM | DEBT-2；Spring Boot 3 Observation 标准栈 | `micrometer-tracing-bridge-otel` + OTLP exporter；结构化 JSON 日志注入 `traceId` + `tenantId`；**验证 Gateway→Feign 的 W3C trace 透传**（Feign 拦截器）| 一次请求跨网关/auth/system 有同一 traceId；Grafana 可按 traceId 关联日志 | L |
| 2.2 | 指标与告警 | RED 黄金信号 | `/actuator/prometheus` + Prometheus Operator ServiceMonitor；核心告警写 PrometheusRule 入 git；先 10% 采样 traces | dashboard 可见 QPS/延迟/错误率；告警规则版本化 | M |
| 2.3 | 租户越权自动化测试 | DEBT-3；OWASP A01 第一 | Testcontainers 起真实 PG/Redis，写"跨租户读隔离/数据权限越界"用例；契约测试覆盖 Gateway↔核心 Feign | 越权用例在 CI 常驻并阻断 | M |
| 2.4 | 韧性基础 | 超时→重试→熔断→降级 | 所有服务配 liveness/readiness/startupProbe + minReadySeconds；gateway/auth/system 副本≥2 + PDB(minAvailable=1) + 拓扑打散；统一 RestTemplate/WebClient 超时重试；Sentinel 规则经 Nacos 配置化保网关限流 | 单实例驱逐不中断；限流规则热生效；探针全绿 | M |
| 2.5 | 前端测试纵深 | 前端调研 P0 | Jest→Vitest + RTL（services/utils/access/关键 ProForm）；Playwright 少量高价值 E2E：登录→租户切换→RBAC 路由→核心 CRUD；`data-testid` + trace | CI 分层测试跑通；核心链路 E2E 通过 | L |

### Phase 3 — 受控框架代际升级（2–3 月，P2，需 Phase 1–2 就绪）

目标：升级到受支持的版本线；**每一步独立 commit、独立回归、可回退**。

| # | 事项 | 动机 | 落地要点 | 验收 | 量级 |
|---|---|---|---|---|---|
| 3.1 | JDK 17 → 21 | SCA 2025.x 前置 | 切 temurin 21；验证 surefire/MapStruct/反射兼容；CI setup-java 同步 | 全模块 `mvn package` 绿；运行无 `--add-opens` 回归 | M |
| 3.2 | Boot 3.2 → 3.5 / Cloud 2023 → 2025.0.0 / SCA 2023 → 2025.0.0.0 | 版本差距 DEBT-5；**跳过 2024 线**（SCA 无官方分支）| 一次性对齐三件套；Nacos 2.x→3.x breaking 单独验证配置迁移；不建议停在 3.4/Cloud 2024 过渡态 | 四 job 全绿 + Phase 0 的 5 项 UI 回归通过 | L |
| 3.3 | pro-components 3.1.14-7 → 3.1.15-x 最新 beta | 前端调研 P0：a11y 修复、包体优化、antd v6 适配 | 按 migration-guide/api-changes 扫破坏性变更；验证 `editableKeys`/`loadingRender`；升级后锁 lockfile | `tsc --noEmit` + UI 核心页回归；"脱离 beta"**不承诺**，等官方 3.x stable | M |
| 3.4 | Nacos 配置迁移验证 + 版本统一 | 历史三套版本漂移（缺陷档案 :43）| 三部署件 Nacos 版本/数据源统一；compose 仅本地开发 | 三处版本一致；配置拉取正常 | S |
| 3.5 | API 契约治理 | OpenAPI 3.1 主流 | springdoc 产 OpenAPI 3.1 文档；CI 校验 breaking change；对外 API 加 `/api/v1` 前缀；内部 Feign 保持 HTTP+JSON | 契约文档入库；breaking change 阻断 | M |

### Phase 4 — 体验与性能（2–3 月，P2，可与 Phase 3 并行）

| # | 事项 | 动机 | 落地要点 | 验收 | 量级 |
|---|---|---|---|---|---|
| 4.1 | 路由级 code splitting + 筛选入 URL | 前端调研 P0 | Umi `routes.ts` 按页拆 chunk；筛选项同步 query（可分享/回退）；重型依赖（图表/Editor）动态 import | 首屏 bundle 下降；筛选可分享 | M |
| 4.2 | 大数据表格虚拟滚动 | 日志/监控页行数增长 | antd Table `virtual` 或 pro-components 虚拟能力，先在日志页试点；注意与可编辑单元格兼容 | 万行级滚动流畅；分页仍正确 | M |
| 4.3 | React Compiler 灰度试点 | Compiler 1.0 stable（2025-10）| 非核心页面开启，Profiler 对比级联重渲染；逐步移除手写 memo；**不一次性全量** | 试点页重渲染下降且无回归 | M |
| 4.4 | TanStack Query 按需引入（触发式） | 字典/租户缓存需失效与去重 | 当出现跨页共享服务端数据时引入，集中 queryKey；否则维持现状 | 仅在触发条件满足时启动，附说明 | M |
| 4.5 | 主题/a11y 精修 | pro-components 3.1.15-6 修 a11y | 暗色/紧凑走 ConfigProvider algorithm；`ProConfigProvider` 与 `ConfigProvider` token 同源；表单补 label/aria；多租户品牌色走运行时 token | 键盘可达性检查通过；无 `.ant-*` 覆盖 | S |

### Phase 5 — 触发式架构演进（P3，按需）

只在前置条件真实出现时启动，先写 ADR 再实施：

| # | 事项 | 触发条件 | 说明 |
|---|---|---|---|
| 5.1 | PG RLS 纵深防御 | 出现高敏感租户数据 / 合规要求 | 保留 MP 插件为主，核心表叠加 RLS + 事务级 `SET LOCAL`；注意连接池串租户与 BYPASSRLS |
| 5.2 | Argo CD GitOps + Helm 收敛 | 有多环境/多集群部署需求 | 三套部署件收敛为单一 Helm chart（values.schema.json + 标准 label）；CI 只构建推镜像+更新 tag，Argo CD 拉；compose 退守本地 |
| 5.3 | SOPS/age 或 ESO 密钥管理 | 上生产且 Secret 需入库 | 杜绝明文 yaml；K8s Secret 静态加密 + 最小 RBAC；SOPS 与 Sealed Secrets 二选一 |
| 5.4 | cosign 签名 + Kyverno 准入 | 供应链合规要求 | OIDC keyless 签镜像，Admission 强制只拉已签名 |
| 5.5 | 读写分离 / 分库分表 | 单表千万级或写瓶颈实测 | 先 Spring 路由数据源；ShardingSphere-JDBC 最后手段 |
| 5.6 | `sys_notice_read` 服务端已读表 | 需要跨设备已读同步 | 替代现 localStorage 记忆 |
| 5.7 | operlog 覆盖 baize-gen | 需审计独立应用操作 | 切面仅覆盖本进程，独立 baize-gen 需自身接入 |
| 5.8 | utoopack / Vite/Rspack 构建提速 | 构建耗时成为瓶颈 | 优先 Umi 官方 utoopack，不自行替换 Umi bundler |
| 5.9 | React 19.3 小版本升级 | 需 View Transitions 等特性 | 小版本跟进，风险低 |

---

## 4. 主题专项路线（纵向视角）

### 4.1 后端
版本线走 3.5/2025.0.0/SCA 2025（Phase 3）；多租户以 MP 插件为主、RLS 触发式叠加；认证走 RFC 9700 令牌轮转；DB 走 Flyway forward-only + expand/contract；测试走 Testcontainers + 契约；安全对齐 OWASP 2025（A01 越权用例化、A03 SBOM/Trivy）。

### 4.2 前端
React 19.3 跟进 + Compiler 试点；pro-components 升最新 beta 并**等待官方 3.x stable**（以 antd-pro v6.1.0 路线图为判断信号）；Biome CI 强规则；Jest→Vitest+RTL+Playwright；TanStack Query 触发式引入；token 改 HttpOnly Cookie；主题单一事实源 + a11y 精修。

### 4.3 DevOps / 部署
K8s 对齐 1.34/1.35+（N-2 内）、Helm 4.x；compose 仅本地；GitOps（Argo CD）触发式；CI 安全基线（SHA 固定 + 最小权限 + OIDC + Trivy + SBOM）；LGTM 可观测性；Sentinel + K8s 韧性三件套；FinOps 从 requests/limits 起步；ADR + Runbook 沉淀决策与运维。

### 4.4 数据与迁移
Flyway 版本化是**最高优先的地基**（Phase 1.1）；16 个存量迁移以 baseline 纳入；从此禁止修改已应用迁移（forward-fix）；新装库 ddl→init→migration 全流程继续由 CI sql job 权威校验。

---

## 5. 决策点（需用户拍板）

| # | 决策项 | 选项 | 建议 | 影响面 | 阻塞 |
|---|---|---|---|---|---|
| DEC-1 | 是否现在就升 JDK21+Boot3.5+SCA2025（含 Nacos 3 breaking）| A 立即 / B 先补地基后升（推荐 B）| B：Phase 1–2 完成后再升，降低升级期故障定位难度 | 全后端 + 部署 | Phase 3 |
| DEC-2 | token 存储方案 | A 维持 localStorage / B 内存 access + HttpOnly refresh Cookie（推荐 B）| B：安全收益大；需前后端协同改 | auth + 前端登录态 | Phase 1.3 |
| DEC-3 | DB 迁移是否引入 Flyway | A 维持手工 / B 引入 Flyway（推荐 B）| B：DEBT-1 是最大生产风险 | 全库 + CI + 部署 | Phase 1.1 |
| DEC-4 | 部署件收敛 | A 三套并存 / B 收敛单 Helm chart + GitOps（推荐 B，可延后）| B 放 Phase 5.2，先修 Nacos 版本漂移 | deploy/ | Phase 5.2 |
| DEC-5 | pro-components "脱离 beta" | A 等官方 stable（推荐）/ B fork 自维 | A：fork 成本远高于等待 | 前端依赖 | Phase 3.3 后续 |
| DEC-6 | 僵尸表（D3 已决策保留）/ perm 模块 | 维持 D3 不动 / 删除 | 维持不动（已定案）| — | 无 |
| DEC-7 | 分库分表 / RLS / GraphQL / OPA | 触发式 / 提前建 | 触发式（见第 6 节）| 架构 | 数据量/需求触发 |

---

## 6. 明确不做（Deferred / Won't do）

| 项 | 理由 |
|---|---|
| gRPC 替换内部 Feign | 全 Java 栈，HTTP+JSON 收益有限；迁移成本高 |
| GraphQL | 无多端灵活组合需求 |
| OPA / Casbin | 当前 RBAC 五档够用；ABAC/热下发需求未出现 |
| 分库分表 / ShardingSphere | 无千万级单表实测瓶颈 |
| React Server Components / 改 Next.js | Umi Max SPA + 客户端动态路由/租户切换，RSC 冲突且无收益 |
| monorepo / pnpm 重构 | 单应用 + npm 已足够；为重构而重构 |
| ESLint/Prettier | 违反项目基线，与 Biome 冲突 |
| Jest/Cypress 双栈 | 统一到 Vitest+Playwright |
| Service Mesh (Istio) | Sentinel + Spring Cloud 已够流量治理 |
| Flux + Argo 双栈 / 多集群联邦 / Kustomize+Helm 混搭 | 过度设计 |
| 自建 Vault 集群 / ELK 迁移 / Mimir HA | 运维成本与收益不匹配 |
| 前端明文/长期 token 存 localStorage | 安全问题（Phase 1.3 修正）|

---

## 7. 度量与验收（如何判断 roadmap 成功）

| 维度 | 现状 | 目标（12 个月） |
|---|---|---|
| DB 变更安全 | 无版本表、手工执行 | Flyway 版本表 + CI 迁移门禁 + 0 漏跑 |
| 故障定位 | 无 traceId | 跨服务 traceId 贯通 + 按 tenantId 关联日志 |
| 越权防护 | 靠人工审查 | 租户隔离/数据权限越权用例 CI 常驻 |
| 供应链 | 无 SBOM/扫描 | 每次构建出 SBOM + 高危 CVE 阻断 |
| 登录态安全 | localStorage | HttpOnly refresh + 轮转 + 重放检测 |
| 构建可复现 | npm install | npm ci + lockfile |
| 部署一致性 | 三套漂移 | 单一 Helm chart（触发式）|
| 框架支持 | 2023 线 | 2025 受支持线（受控升级后）|

---

## 8. 参考来源（按主题）

**后端**：[SCA 版本说明](https://sca.aliyun.com/en/docs/2025.x/overview/version-explain/) · [Spring Cloud 支持策略](https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions) · [MyBatis-Plus 多租户插件](https://baomidou.com/en/plugins/tenant/) · [PG RLS 多租户隔离](https://aws.amazon.com/blogs/database/multi-tenant-data-isolation-with-postgresql-row-level-security/) · [RFC 9700](https://www.rfc-editor.org/rfc/rfc9700.pdf) · [RFC 10017](https://www.rfc-editor.org/rfc/rfc10017.pdf) · [Spring Boot Observability](https://docs.spring.io/spring-boot/reference/actuator/observability.html) · [RFC 9562 UUIDv7](https://www.rfc-editor.org/rfc/rfc9562.pdf) · [Flyway 文档](https://documentation.red-gate.com/flyway/) · [OWASP 2025](https://github.com/OWASP/Top10/blob/master/2025/docs/en/0x00_2025-Introduction.md)

**前端**：[React 19.3](https://react.dev/blog/2026/09/09/react-19-3) · [React Compiler 1.0](https://react.dev/blog/2025/10/07/react-compiler-1) · [pro-components Changelog](https://pro-components.antdigital.dev/en-US/changelog/index.md) · [antd-pro v6.1.0 路线图](https://github.com/ant-design/ant-design-pro/blob/master/docs/roadmap/v6.1.0-release-plan.md) · [OWASP HTML5 Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/HTML5_Security_Cheat_Sheet.html) · [antd 定制主题](https://ant.design/docs/react/customize-theme/)

**DevOps**：[CNCF 2025 终端用户调研（Argo CD）](https://www.cncf.io/announcements/2025/07/24/cncf-end-user-survey-finds-argo-cd-as-majority-adopted-gitops-solution-for-kubernetes/) · [Helm 最佳实践](https://helm.sh/docs/chart_best_practices/) · [GitHub Actions 安全加固](https://docs.github.com/en/actions/how-tos/secure-your-work) · [Trivy](https://www.trivy.dev/docs/dev/tutorials/integrations/github-actions/) · [Grafana OTel Collector/LGTM](https://grafana.com/docs/opentelemetry/collector/) · [Liquibase on K8s（init container 模式）](https://www.liquibase.com/blog/using-liquibase-in-kubernetes) · [K8s Secrets 最佳实践](https://kubernetes.io/docs/concepts/security/secrets-good-practices/) · [ADR 最佳实践](https://aws.amazon.com/blogs/architecture/master-architecture-decision-records-adrs-best-practices-for-effective-decision-making/)

---

> 本文为规划文档，不含代码变更。任何 Phase 启动前请先确认第 5 节对应决策项，并在 `docs/adr/` 为关键决策（Flyway、token 方案、Helm 收敛、LGTM）补写 ADR。
