# baize — 系统缺陷评审合并报告

> 基线：`git diff 437a699..a9e80c6`（约 419 文件 / 3.4 万行）
> 评审方式：6 个只读子智能体（前端 / 后端 system+common / 安全面 / DB·CI·部署·文档）
> 生成日期：2026-10-26

---

## 总览（去重后，按严重度）

### 🔴 P0 — 可利用漏洞 / 数据损坏 / 跑不起来

| # | 缺陷 | 范围 | 关键证据 |
|---|------|------|----------|
| P0-A | **身份头在直连后端时完全可信**（X-Tenant-Id / X-Role / X-User-Id 可被伪造）→ 越权/跨租户 | 安全面 | `HeaderInterceptor.java:16-49` 直接读客户端头写入 `RequestContextHolder`；网关只覆盖不剥离；后端无"是否来自网关"校验 |
| P0-B | **`/auth/switchTenant` 不查黑名单** → 登出后可凭旧 token 换发新 token，登出失效 | 安全面 | `AuthController.java:111-125` 仅 `parsePayload`（验签+过期），`AuthServiceImpl.switchTenant:140-205` 未查 `TokenBlacklistService` |
| P0-C | **密码以 URL query 参数发送** → 网关/代理 access log 泄露 | 前端→后端 | `user.ts:78 updatePassword`、`user.ts:87 resetPassword` 用 `params:{userId,oldPassword,newPassword}` |
| P0-D | **docker-compose 挂载不存在的 redis.conf** → redis 容器起不来 | 部署 | `docker-compose.yml:61` `./redis/conf/redis.conf` 源路径不存在（仅目录被 Docker 当空目录创建）|
| P0-E | **DDL 不幂等，与 README 声明相悖** → 重跑建表即 `already exists` | DB/文档 | `baize-system-ddl.sql:7` 裸 `CREATE TABLE`，21 张表均无 `IF NOT EXISTS`；README §四 表(101 行)却标"是" |

### 🟠 P1 — 安全弱点 / 质量 / 可维护

| # | 缺陷 | 范围 | 关键证据 |
|---|------|------|----------|
| P1-A | ~~`RequestContextHolder` ThreadLocal 同步请求不清理~~ ✅ 已修 | 安全面 | `HeaderInterceptor` 已补 `afterCompletion` → `RequestContextHolder.remove()`（闭环验证） |
| P1-B | 租户缺失兜底到平台租户 "0"（fail-safe 静默放行而非拒绝）| 安全面 | `CustomTenantHandler.java:31-39`（待用户决策：平台态语义 m03289 已确认保留兜底）|
| P1-C | ~~放行 CORS 反射 Origin 且 `Allow-Credentials:true`~~ ✅ 已修 | 安全面 | 单一事实源 `globalcors.cors-configurations` 白名单；`CorsFilter` 仅留 OPTIONS 兜底（三态验证） |
| P1-D | 限流器按客户端 `X-Real-IP` 计数 + 内存态 + 默认关闭 → 可被绕过 | 安全面 | `RateLimiterFilter.java:70-82`（默认 disabled）（待评估：是否经 LB/NAT 拓扑）|
| P1-E | ~~`@InnerAuth` 内网 token 为空时放行~~ ✅ 已修（fail-closed） | 安全面 | `InnerAuthInterceptor` 空 token 抛 `ForbiddenException`（五态验证） |
| P1-F | **转换器 `BeanUtils.copyProperties` 静默丢字段**（sort 缺失即典型）→ `编辑菜单缺少排序` 已踩 | 后端 | `SysMenuConvert.java:15-30` 双向拷贝；新增实体字段漏加 DTO 即丢 |
| P1-G | **任务调度缓存未失效** → UI 新增/修改/删除 job 后调度器不刷新 | 后端 | `SysJobServiceImpl` `addJob:101`/`updateJob:120`/`removeJob:140` 均无 `@CacheEvict`，`@Cacheable getJobList:38` 被 quartz 读取 |
| P1-H | 缺失 `.env.example`（README 与 compose 都引用）| 部署/文档 | README:27/117/164、`docker-compose.yml:22` `${POSTGRES_PASSWORD:?…}`；仓库无该文件 |
| P1-I | **Nacos 弱默认 token 明文** | 部署 | `deploy/k8s/03-nacos.yaml:12`、`helm/.../nacos-configmap.yaml` 用官方示例 64 位 key |
| P1-J | **k8s 明文数据库口令** | 部署 | `01-postgres.yaml:18` `POSTGRES_PASSWORD: "baize@2023"`；helm `values.yaml` 同值 |
| P1-K | ~~**frontend CI `continue-on-error:true`** 长期掩盖构建失败~~ | CI | `ci.yml:46` — ✅ 已改为 `continue-on-error: false`（2026-10-28，`npx max build` 11.8s + `tsc --noEmit` 本机验证通过；CI 用 `npm run build` 即 max CLI，注意 `npx umi build` 不带 max preset 会报 Invalid config keys） |
| P1-L | **init 与 migration 物理主键不一致（sys_menu id=4）** | DB | 已核实**不成立**：init（`baize-system-init.sql:59`）与 migration（`V2026_10_23__home_menu.sql:41`）都用固定 id=4；migration 自带 snowflake 行对齐逻辑（id≠4 且 id=4 空闲时 UPDATE 主键并同步 sys_role_menu）。live DB 已确认 /welcome = id 4。残余说明：两脚本路径本就互斥（新库走 init 不跑迁移，老库补跑迁移），现在殊途同归到 id=4 |
| P1-M | 权限码字面量分散多处 → 拼错即静默隐藏按钮或误放行 | 前端 | `config/index.tsx:125`、`role/index.tsx`、`loginlog/index.tsx`、`routePermission.ts:9-23` |

### 🟡 P2 — 次要

- **P2-A** init `sys_menu` 用 `ON CONFLICT(tenant_id,menu_name,parent_id)` 与 DDL 主键 `id` 不同键 → 删除重插被静默吞（`baize-system-init.sql:63`）
- **P2-B** migration `WHERE NOT EXISTS` 并发双插（TOCTOU，固定 id 主键冲突）— `home_menu.sql:45-50`、`menu_buttons_incremental.sql`
- **P2-C** Nacos 版本错配（compose v2.2.3 / k8s v2.5.2 / helm v2.2.3）；k8s 用 derby 内嵌而 helm 用外置 pg
- **P2-D** k8s postgres/redis 缺探针 + 无 securityContext；nacos 有探针但无 securityContext
- **P2-E** migration glob 字母序脆弱（`V2026_10_22_02` vs `V2026_10_22__constraints_and_align` 依赖 `_`<`0`）
- **P2-F** DDL 类型不一致：`sys_user`/`sys_oper_log` 的 `version` 无 `DEFAULT 0`；DDL 单独建库 20/21 表无主键（仅 `sys_oper_log` 有）
- **P2-G** 日志表（loginlog/operlog）`removeById` 仍是物理删除（与业务表逻辑删除不一致，需文档说明）
- **P2-H** 刷新 token 7 天、可重放、`logout` 当 `tenantId` 为 null 时 key 拼错未删除真 token（`AuthController.java:76`、`RedisTokenManager.java:51`）
- **P2-I** denylist 匹配朴素（仅精确/`/**` 前缀），编码/大小写变体可绕过（`GatewayPathMatcher.java:25-44`）
- **P2-J** `sort` 列 DDL `int4 NULL` 与 Java `BaseEntity.private int sort`（基本类型，MP fill 永不生效）冲突 → 全新库无 DEFAULT 行为分叉（`baize-system-ddl.sql:321-324`）

### 🟢 P3 — 收尾 / 杂项

- `getDeptList` 把 DTO 包成 `{dto:{...}}` 且当前无调用方（死代码陷阱，`dept.ts:25`）
- `App.useApp()` 在可能脱离 `<App>` 祖先处调用（`TenantSwitcher.tsx:45`、`login/index.tsx:176`）
- `.dockerignore` 漏 `**/node_modules`/`*.log`；`.gitignore:24` `.debug/` 只匹配根
- helm chart 无 `helm lint`；CI frontend `continue-on-error` 掩盖
- migration 注释"68 条"计数失实（`menu_buttons_incremental.sql:8-9`）
- `SnowflakeGenerator.generate()` 返回 `""` 死代码（`baize-common-core/.../strategy/SnowflakeGenerator.java:13`）
- CI 缺 `-Dfile.encoding=UTF-8`（构建不加载配置故当前良性，但文档强调的项未落到 CI）
- JWT secret 处理**良好**（无默认、<32 字节启动失败、`verifyWith` 防 none）

---

## 已在本会话修复的项（对照）

| 原始缺陷 | 修复 | 状态 |
|----------|------|------|
| 列表查询框不显示（wrapper 不渲染 children）| `wrappers/authGuard.tsx` `<Outlet/>` 兜底 | ✅ 验证 |
| 菜单中英文混排 | `utils/menu.tsx` 图标映射 + `app.tsx locale:false` | ✅ 验证 |
| 编辑菜单缺少排序（SysMenuDto 无 sort）| 加 `private int sort` + 前端补 id | ✅ 验证 |
| 编辑提交 id 不一致（body 缺 id）| 10 个 service `data:{...data,id}` | ✅ 验证 |
| 平台租户 0 + 平台态隔离 | live DB + init + migration 插入 id=0 | ✅ 验证 |
| 逻辑删除 status=null bug | 11 个 Service `LambdaUpdateWrapper` 逻辑删除 | ✅ 构建验证 |
| Excel 导出 / 服务监控 / Redis 监控 / 租户切换 / 数据权限 / 代码生成器 | 见路线图 | ✅ 完成 |
| **P0-E 修复（DDL 不幂等）** — 迁移脚本 schema 限定 + 固定 id + path 唯一索引 + ON CONFLICT | `V2026_10_23__home_menu.sql`、`V2026_10_23__platform_tenant_0.sql` | ✅ 已提交（a2fe6e3） |
| **种子表缺主键（review P0-1 / 原 P2-A 升级）** — DDL 仅 `sys_oper_log` 有主键，15 张种子表 `id int8 NOT NULL` 无 PK → 全新库按 README 走 `ddl→init`（不跑 migration）时 `init` 的 `ON CONFLICT (id)` 无仲裁索引首跑即失败 | `db/ddl/baize-system-ddl.sql` 末尾追加幂等 `DO $$` 补主键块（15 张种子表 `pk_<table>(id)`，逐表判重跳过）| ✅ 已提交（a2fe6e3） |
| **README 幂等声明纠正** — 原称 `db/ddl/` 幂等「是」实为裸 `CREATE TABLE` 非幂等 | `README.md` 表项改为「否（增量用 migration）」 | ✅ 已改 |
| **P0-C 密码以 URL query 发送** → access log 泄露 | 后端新建 `UpdatePasswordDto`/`ResetPasswordDto`（jakarta `@NotBlank`），`SysUserController` 两个端点改 `@Valid @RequestBody`；前端 `services/system/user.ts` `params:` → `data:` | ✅ 编译+tsc 验证 |
| **P0-D compose 挂载不存在的 redis.conf** | 新建 `deploy/docker-compose/redis/conf/redis.conf`；`baize-redis` 增 `command: ["redis-server","/redis/conf/redis.conf"]` | ✅ 已改 |
| **P1-H 缺 `.env.example`** | 新建根目录 `.env.example`（POSTGRES_PASSWORD、NACOS_*、BAIZE_JWT_SECRET、NACOS_AUTH_*） | ✅ 已改 |
| **P1-I/J k8s/helm 明文凭据** | `01-postgres.yaml` Secret 与 helm 3 个 `values.yaml` 改 `CHANGE_ME_*` 占位符；`03-nacos.yaml` 弱默认 token/identity 移入新 `Secret nacos-secret` 并经 `envFrom.secretRef` 注入；`deploy.sh` 加占位符预检（命中即拒绝部署）且不再回显口令 | ✅ 语法验证 |
| **P0-A 直连后端可伪造身份头** | 网关 `SignatureFilter`（order -750）HMAC-SHA256 签名身份头；后端 `GatewaySignatureInterceptor` fail-closed（有身份头+签名不合法→403）；`HeaderInterceptor.afterCompletion` 清理 ThreadLocal（P1-A 同批） | ✅ 三态闭环验证（伪造 403 / 经网关 200 / 无身份头 403） |
| **P1-E InnerAuth 空 token 放行** | `InnerAuthInterceptor` 空 innerToken 由放行改抛 `ForbiddenException`（fail-closed）；文档 `doc/inner-token-audit.md` 同步 | ✅ 五态闭环验证 |
| **P1-C CORS Origin 回显** | 删除 `CorsFilter` 的 Origin 回显（已证实预检根本到不了 GlobalFilter）；单一事实源 = `globalcors.cors-configurations` 白名单（localhost:3000/8000），filter 仅留 OPTIONS 兜底 | ✅ 三态验证（白名单 200 / evil.example.com 403 / 登录 200） |
| **P1-K frontend CI 不阻塞** | `ci.yml:46` `continue-on-error: true→false` | ✅ 本机构建验证（`npx max build` 11.8s） |

---

## 建议修复顺序

1. **提交已完成的 P0-E 迁移修复**（防丢失）
2. ~~P0-C（密码改 PUT body）~~、~~P0-D（补 redis.conf）~~、~~P0-E（DDL IF NOT EXISTS / schema 限定）~~ ✅ 已修；~~P0-A~~ ✅ 已修并闭环验证（网关签名头）；P0-B 经核查已正确实现（switchTenant 验签+查黑名单）；剩余无
3. ~~P1-H（.env.example）~~、~~P1-I/J（凭据改 Secret）~~、~~P1-K（CI 阻塞）~~、~~P1-L（核实不成立）~~ ✅；剩余 P1-F（MapStruct 替代 BeanUtils）、P1-G（job 缓存失效）、P1-M（权限码字面量）
4. ~~**P1 安全加固**~~：~~P1-A（ThreadLocal 清理）~~、~~P1-C（CORS 白名单）~~、~~P1-E（强制 inner token）~~ ✅ 已修并闭环验证；剩余 P1-D（限流改真实 IP，需评估部署拓扑是否经 LB）
5. **P2/P3** 批量处理

---

## 待决策（需用户确认）

- **P0-A/P0-B 涉及"直连后端"威胁模型**：当前架构以网关为唯一可信边界。是否接受"网关是 sole ingress"的设计前提，还是要做后端侧的网关签名头校验？
- **P1-B fail-safe 到 "0"**：是否改为"缺租户即拒绝（1=0）"？这会破坏平台态默认行为，需评估。
- **⑫ 表单构建器 / Sentinel 取舍** 仍未做（Open objective）。
