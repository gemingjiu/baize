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
| P1-D | ~~限流器按客户端 `X-Real-IP` 计数 + 内存态 + 默认关闭 → 可被绕过~~ ✅ 已撤销（用户决策 2026-10-28：不改造） | 安全面 | `RateLimiterFilter.java:70-82` 保持现状（默认 disabled + 单机内存计数）；留待真实限流需求出现时再评估 Redis 滑动窗口方案 |
| P1-E | ~~`@InnerAuth` 内网 token 为空时放行~~ ✅ 已修（fail-closed） | 安全面 | `InnerAuthInterceptor` 空 token 抛 `ForbiddenException`（五态验证） |
| P1-F | **转换器 `BeanUtils.copyProperties` 静默丢字段**（sort 缺失即典型）→ `编辑菜单缺少排序` 已踩 | 后端 | `SysMenuConvert.java:15-30` 双向拷贝；新增实体字段漏加 DTO 即丢 |
| P1-G | **任务调度缓存未失效** → UI 新增/修改/删除 job 后调度器不刷新 | 后端 | `SysJobServiceImpl` `addJob:101`/`updateJob:120`/`removeJob:140` 均无 `@CacheEvict`，`@Cacheable getJobList:38` 被 quartz 读取 |
| P1-H | 缺失 `.env.example`（README 与 compose 都引用）| 部署/文档 | README:27/117/164、`docker-compose.yml:22` `${POSTGRES_PASSWORD:?…}`；仓库无该文件 |
| P1-I | **Nacos 弱默认 token 明文** | 部署 | `deploy/k8s/03-nacos.yaml:12`、`helm/.../nacos-configmap.yaml` 用官方示例 64 位 key |
| P1-J | **k8s 明文数据库口令** | 部署 | `01-postgres.yaml:18` `POSTGRES_PASSWORD: "baize@2023"`；helm `values.yaml` 同值 |
| P1-K | ~~**frontend CI `continue-on-error:true`** 长期掩盖构建失败~~ | CI | `ci.yml:46` — ✅ 已改为 `continue-on-error: false`（2026-10-28，`npx max build` 11.8s + `tsc --noEmit` 本机验证通过；CI 用 `npm run build` 即 max CLI，注意 `npx umi build` 不带 max preset 会报 Invalid config keys） |
| P1-L | **init 与 migration 物理主键不一致（sys_menu id=4）** | DB | 已核实**不成立**：init（`baize-system-init.sql:59`）与 migration（`V2026_10_23__home_menu.sql:41`）都用固定 id=4；migration 自带 snowflake 行对齐逻辑（id≠4 且 id=4 空闲时 UPDATE 主键并同步 sys_role_menu）。live DB 已确认 /welcome = id 4。残余说明：两脚本路径本就互斥（新库走 init 不跑迁移，老库补跑迁移），现在殊途同归到 id=4 |
| P1-M | 权限码字面量分散多处 → 拼错即静默隐藏按钮或误放行 | 前端 | `config/index.tsx:125`、`role/index.tsx`、`loginlog/index.tsx`、`routePermission.ts:9-23` |

### 🟡 P2 — 次要（✅ 全部闭环，2026-10-28 批量收尾）

- ~~**P2-A**~~ init `sys_menu` 用 `ON CONFLICT(tenant_id,menu_name,parent_id)` 与 DDL 主键 `id` 不同键 → 删除重插被静默吞（`baize-system-init.sql:63`）— ✅ 已被 P0-E 种子表补主键连带解决（`ON CONFLICT (id)` 现有仲裁索引），见 :77 补主键块
- ~~**P2-B**~~ migration `WHERE NOT EXISTS` 并发双插（TOCTOU，固定 id 主键冲突）— ✅ README 已备案设计说明（部署窗口单实例执行，风险可接受）
- ~~**P2-C**~~ Nacos 版本错配（compose v2.2.3 / k8s v2.5.2 / helm v2.2.3）；k8s 用 derby 内嵌而 helm 用外置 pg — ✅ `03-nacos.yaml:61` 统一降级到 v2.2.3（对齐 spring-cloud-alibaba 2023.0.3.3 对应的 client 2.3.x 兼容线）
- ~~**P2-D**~~ k8s postgres/redis 缺探针 + 无 securityContext；nacos 有探针但无 securityContext — ✅ 三 yaml 补齐（pg_isready/redis-cli ping 探针 + 全部 securityContext 加固）
- ~~**P2-E**~~ migration glob 字母序脆弱（`V2026_10_22_02` vs `V2026_10_22__constraints_and_align` 依赖 `_`<`0`）— ✅ README 已备案命名规则（新增脚本须保证依赖顺序的字母序一致）
- ~~**P2-F**~~ DDL 类型不一致：`sys_user`/`sys_oper_log` 的 `version` 无 `DEFAULT 0`；DDL 单独建库 20/21 表无主键（仅 `sys_oper_log` 有）— ✅ DDL 13 处 `version`/`sort` 补 `DEFAULT 0`，与 migration S2 段语义对齐；主键问题已由 P0-E 连带解决
- ~~**P2-G**~~ 日志表（loginlog/operlog）`removeById` 仍是物理删除（与业务表逻辑删除不一致，需文档说明）— ✅ README 已备案（数据量大无需回收 id，刻意设计）
- ~~**P2-H**~~ 刷新 token 7 天、可重放、`logout` 当 `tenantId` 为 null 时 key 拼错未删除真 token（`AuthController.java:76`、`RedisTokenManager.java:51`）— ✅ 大幅缓解：logout 已从 Bearer 正确解析 tenantId，save/delete key 拼法一致，refreshToken 覆写同 key；残余仅 7 天 TTL 偏长 + 多端共享 key（可接受）
- ~~**P2-I**~~ denylist 匹配朴素（仅精确/`/**` 前缀），编码/大小写变体可绕过（`GatewayPathMatcher.java:25-44`）— ✅ README 已备案（白名单有 P0-A 签名校验兜底）
- ~~**P2-J**~~ `sort` 列 DDL `int4 NULL` 与 Java `BaseEntity.private int sort`（基本类型，MP fill 永不生效）冲突 → 全新库无 DEFAULT 行为分叉（`baize-system-ddl.sql:321-324`）— ✅ 同 P2-F，DDL 补 `DEFAULT 0`

### 🟢 P3 — 收尾 / 杂项（✅ 全部闭环，2026-10-28 批量收尾）

- ~~`getDeptList` 把 DTO 包成 `{dto:{...}}` 且当前无调用方（死代码陷阱，`dept.ts:25`）~~ — ✅ 已删除（grep 全库无调用方，tsc 通过）
- ~~`App.useApp()` 在可能脱离 `<App>` 祖先处调用（`TenantSwitcher.tsx:45`、`login/index.tsx:176`）~~ — ✅ 核查不成立：`config.ts` `antd.appConfig` 已启用，umi 外层包 `<App>`，两处调用点祖先链完整；`global.tsx` 用静态 message/notification 属模块顶层无法用 hooks，合理
- ~~`.dockerignore` 漏 `**/node_modules`/`*.log`；`.gitignore:24` `.debug/` 只匹配根~~ — ✅ `baize-ui/.dockerignore` 补 `**/node_modules` 与 `*.md`；根 `.gitignore` 补 `**/.debug/` 与 Umi 临时产物（`.umi*`）
- ~~helm chart 无 `helm lint`；CI frontend `continue-on-error` 掩盖~~ — ✅ CI 新增 helm job（`helm lint` + dummy 密码冒烟渲染）；`continue-on-error` 已在 P1-K 改为 false
- ~~migration 注释"68 条"计数失实（`menu_buttons_incremental.sql:8-9`）~~ — ✅ 头注释删失实计数；尾注「预期 85」→「预期：既有授权数 + 17」
- ~~`SnowflakeGenerator.generate()` 返回 `""` 死代码（`baize-common-core/.../strategy/SnowflakeGenerator.java:13`）~~ — ✅ 真实现（1+41+10+12，MAC 哈希 worker-id + SecureRandom 兜底，AtomicLong 同毫秒自旋；javadoc 注明未接分布式 worker-id 协调）
- ~~CI 缺 `-Dfile.encoding=UTF-8`（构建不加载配置故当前良性，但文档强调的项未落到 CI）~~ — ✅ backend job 两条 mvn 命令已加（带注释）
- JWT secret 处理**良好**（无默认、<32 字节启动失败、`verifyWith` 防 none）— 无需处理

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
3. ~~P1-H（.env.example）~~、~~P1-I/J（凭据改 Secret）~~、~~P1-K（CI 阻塞）~~、~~P1-L（核实不成立）~~、~~P1-F（MapStruct 替代 BeanUtils，12 Convert + 注入 INSTANCE 化，commit 2283642）~~、~~P1-M（权限码常量化 PermCodes，115 处替换，commit 2283642）~~ ✅；剩余 P1-G（job 缓存失效，经核查不成立——调度器 60s 轮询 DB 无缓存）
4. ~~**P1 安全加固**~~：~~P1-A（ThreadLocal 清理）~~、~~P1-C（CORS 白名单）~~、~~P1-E（强制 inner token）~~ ✅ 已修并闭环验证；~~P1-D（限流改造）~~ ✅ 已撤销（用户决策，保持现状）
5. ~~**P2/P3** 批量处理（P1-D 已撤销，P1 线全部闭环）~~ ✅ 2026-10-28 批量收尾完成：P2 全闭环（A 被连带解决 / B/E/G/I 备案 / C/D/F/J 实改 / H 大幅缓解）、P3 全闭环（含 SnowflakeGenerator 真实现、CI helm lint job）

---

## 2026-10-29 增量评审：数据模型 / 角色模型

### ✅ 已完成：角色表 role_type / is_builtin（commit 7d24747）

- **迁移** `db/migration/V2026_10_29__role_type_builtin.sql`：`sys_role` 加 `role_type varchar(1) DEFAULT '1'`（0 平台角色 / 1 租户角色）、`is_builtin varchar(1) DEFAULT '0'`（1 内置不可删）；id=1 admin 标记 `role_type='0', is_builtin='1'`（幂等守卫）；`data_scope` 注释补「5 仅本人」修正文档漂移
- **DDL/init 同步**：`baize-system-ddl.sql` 列定义 + COMMENT；`baize-system-init.sql:199-201` admin 种子补两列
- **后端双守卫** `SysRoleServiceImpl.removeById`：内置角色拒绝删除（`ErrorCode.BUSINESS_CONFLICT`）+ 被用户占用拒绝删除（`getUserIdsByRoleId` 非空即拒，提示解绑数量）
- **前端** `role/index.tsx`：新增「角色类型」列（平台 geekblue / 租户 cyan Tag + 内置金色 Tag，支持筛选）；内置角色删除按钮置灰并提示
- **不改**：tenant_id NOT NULL+0（租户过滤由 CustomTenantHandler 无条件下推）、data_scope 值域 1-5 是超集、status varchar(1) 全库约定、超管白名单判定不动

### ✅ 已完成：多角色聚合签发（commit e5f673c）

- **问题**：`SysRoleServiceImpl.getRoleByUserId` 只取第一个角色，导致三处缺陷——①超管判定只看第一个角色码（绑定超管角色但排第一的不是超管时权限被截断）；②`dataScope` 只签第一个角色的；③权限查询失败回退 role claim 时只写第一个角色码
- **system 侧**：新增 `GET /system/role/user/{userId}/list` 返回用户全部角色（排除已逻辑删除），`SysRoleFeignClient` 同步新增 `getRolesByUserId`
- **auth 签发**（login/refreshToken 共用）：任一角色命中 `super-admin-role-codes` 即签 `*:*:*`；回退 role claim 写全部角色码去重逗号串；`dataScope` 聚合取最大可见范围——任一角色 `1 全部` → `1`；否则含部门类 `2/3/4` → `2`（自定义档）；否则 `5 仅本人`
- **数据权限对齐**：`SysDataScopeDeptProvider` 自定义档按全部角色**逐角色解析部门并集**（各角色按自身档位：2 取 sys_role_dept 配置、3 取本部门、4 取子树），修复聚合档下"本部门/子树"角色被当作"未配置自定义部门"丢弃的问题
- **兼容**：role claim 格式不变（逗号分隔权限码，PermissionAspect 无需改动）；旧端点 `getRoleByUserId` 保留（前端未消费）

### ✅ 已完成：预置角色种子（V2026_10_30__seed_roles.sql）

- **6 个预置角色**（id 2~7，tenant_id=0，幂等 ON CONFLICT DO NOTHING；租户侧角色为模板种子，其他租户由租户管理员自建）：
  - `ops` 运维管理员（平台，data_scope=1）：监控全目录 + 定时任务，无业务写权限
  - `auditor` 审计员（平台，data_scope=1）：日志查询/导出只读，不给删除/强退
  - `tenant_admin` 租户管理员（租户，**is_builtin=1 内置不可删**，data_scope=1）：租户内系统管理闭环（用户/角色/菜单/部门/字典/岗位/参数/公告+操作日志自审计），刻意排除租户管理/权限管理/监控/工具
  - `hr` 人事专员（租户，data_scope=1）：用户全套 + 部门查询 + 岗位全套 + 公告查询
  - `dept_manager` 部门主管（租户，**data_scope=4 本部门及以下**）：用户查询 + 公告查询
  - `employee` 普通员工（租户，**data_scope=5 仅本人**）：公告查询 + 修改密码（个人中心改密可用）
- **init 同步**：`baize-system-init.sql` 角色种子（admin 之后）+ 角色-菜单绑定（admin 绑定块之后）；DDL `sys_role` 表注释列明预置角色
- **联动**：`dept_manager` + `employee` 是多角色聚合签发（e5f673c）的天然验证组合——同人绑定两角色验证 dataScope 取最大可见范围；全部新角色走正常聚合签发，不触碰超管白名单

### 🟡 表模型评审发现（全 14 表核对，低风险动作 + 待决策）

**低风险动作（✅ 索引部分已完成，V2026_10_31__index_optimize.sql）**：
- ~~索引缺口：`sys_user.dept_id` 无索引；`sys_user_role.role_id` 不被 uk(tenant,user,role) 覆盖~~ — ✅ 补 `idx_sys_user_dept_id` + `idx_user_role_role_id`
- ~~冗余索引：4 个关系表单列 tenant_id 索引与 uk 前缀重复~~ — ✅ 已删（PK 最左前缀即 tenant_id，保留纯浪费写放大）
- ~~`sys_tenant.domain` 声明唯一但无 uk（ddl:635 vs :682）~~ — ✅ 补唯一索引 `uk_sys_tenant_domain`（PG 多 NULL 语义与"域名可选"一致）
- 软删除×唯一键冲突：逻辑删后编码永久占用（如 `uk_sys_user_tenant_user_name` ddl:755）——PG partial unique index `WHERE deleted='0'` 可解，但 init 的 ON CONFLICT 仲裁依赖现有 uk，需联动评估

**僵尸表（需用户决策删/留）**：
- `sys_data_perm`（ddl:67-101）：无 Java 实体引用，仅 `DataScopeInnerInterceptor.java:56` 白名单出现
- `sys_user_post`（ddl:765-785）：无实体、无 Mapper、无调用
- `sys_perm` + `sys_role_perm`：实体/服务/控制器全有，但**登录签发不消费**（`AuthServiceImpl.java:78` 注释确认走菜单 perms）——前端权限 UI 双轨冗余

### 🟢 架构分析结论（备案，无需改动）

- **用户↔租户 = 1:N 账号制，关系自洽**：`sys_user.tenant_id NOT NULL` + uk(tenant,user_name)；同一人两租户 = 两行两个 userId 同名。无 sys_user_tenant 中间表；switchTenant 归属判定 = 目标租户存在同名账号（`AuthServiceImpl.java:170`），超管例外跨租户签发（:179-194）。**当前模型不算缺陷**；若将来要"邀请加入租户 / 一账号多租户"才需 M:N 改造（成员表 + switchTenant 改查 + 存量合并），属新功能需用户决策
- 角色 model：`sys_role` UNIQUE(tenant_id, role_code)；数据权限五档 1-5；`sys_role_dept` 供自定义档使用——结构健康

---

## 2026-10-29 增量：主键全链路迁移 UUIDv7（✅ 完成，commit 1c0257e）

- **需求定案**：主键由雪花 bigint 迁移到 **UUIDv7**（用户决策；种子重刷为 UUID、删死代码 id 包、列型 `varchar(36)` 三问已确认）；`parent_id` 树根哨兵 `'0'` 保留原语义（TreeUtils/SysRoleMenuServiceImpl 判断不动）
- **后端生成器**：新建 `baize-common-database/.../generator/Uuidv7IdentifierGenerator.java`（implements `IdentifierGenerator`：高 52bit 毫秒 + 低 12bit 序列 + 62bit 随机，CAS 自旋/回拨沿用），`MybatisPlusConfig` 注册 `@Bean` 覆盖 MP 默认 `IdWorker.get32UUID()`（无连字符）；`nextId()` 抛 `UnsupportedOperationException` 防 `ASSIGN_ID` 回退
- **注解切换**：`BaseEntity` / `SysLoginLog` / `SysOperLog` 的 `@TableId` `ASSIGN_ID → ASSIGN_UUID`（全库清零）；代码生成模板 `GenUtils` 同步；`sys_user.dept_id Long → String`（全库唯一 Long 实体字段，Convert 桥接/parseLong 删除，数据权限直取）
- **租户哨兵 '0' → nil UUID**：`CustomTenantHandler`（StringValue，删 isNumeric 兜底——UUID 含连字符会永远误判）/ `BaseEntityObjectHandler` / `TenantFilter` / `AuthServiceImpl` / `AuthController` + 前端 `PLATFORM_TENANT_ID`（`constants/tenant.ts`，`tenantId==='0'` 三处 0 命中）；`status/deleted` 的 `'0'` 状态标志不动；yml `id-type: assign_uuid`
- **SQL 层**：新迁移 `db/migration/V2026_10_21__uuid_v7_id.sql`（置首；存量库 `int8 → varchar(36)`，通用 hex 映射 `N → 00000000-0000-7000-<hex4>-<hex12>` 覆盖雪花大数，tenant 0→nil、parent_id 0→'0'，幂等 DO 块）；`ddl/init/constraints/7 个数据迁移` 全部数字 id 字面量改写为同一映射（旧迁移才能与新装库两头跑）
- **验证闭环**：`mvn -T 1C compile` + `tsc --noEmit` 双绿；四轮 SQL 验证 `VALIDATE_EXIT=0`（M1×11 迁移 → init 双跑 → M2×11 重跑 → remaining bigint id 列 = 0），数据断言 `role_menu=199 / digit=0 / u0role=0`（各角色 89/23/8/51/19/5/4 与种子逐一对账）
- **过程发现并修复 2 个一次性转换脚本 bug**：①`_uuid_rewrite.py` 三元组规则丢尾组 → `seed_roles` 六段 role_menu 错转成 `(nil, u0, role)` 落 6 坏行；②`_uuid_init.py` 元组循环仅跳空白不跳 `--` 注释 → 种子段 110 元组静默漏转成裸数字；均修复重跑，DB 清除 116 坏行后重验全绿；三个一次性脚本（两转换器 + 验证器）已删除


- **P0-A/P0-B 涉及"直连后端"威胁模型**：当前架构以网关为唯一可信边界。是否接受"网关是 sole ingress"的设计前提，还是要做后端侧的网关签名头校验？（P0-A 已做网关签名校验，此项指是否进一步收紧）
- **P1-B fail-safe 到 "0"**：是否改为"缺租户即拒绝（1=0）"？这会破坏平台态默认行为，需评估。
- ~~**⑫ 表单构建器 / Sentinel 取舍**~~ ✅ 已忽略（用户决策 2026-10-28：不做，roadmap 不新增功能线）
- **三组僵尸表删/留**（2026-10-29 评审新增）：`sys_data_perm` / `sys_user_post` / `sys_perm`+`sys_role_perm` 详见上方「表模型评审发现」——删除需迁移脚本 DROP + 清理实体/服务/控制器/白名单引用；保留建议在 DDL 注释标明用途
- **软删除×唯一键**（2026-10-29 评审新增）：是否引入 PG partial unique index（`WHERE deleted='0'`）解除逻辑删后编码永久占用——需联动 init ON CONFLICT 仲裁评估
- ~~**角色签发多角色聚合**~~ ✅ 已完成（2026-10-29，commit e5f673c，见上方「增量评审」章节）

---

## 2026-11-03 决策定案（评审遗留项，4 项全部确认）

| # | 决策项 | 结论 |
|---|--------|------|
| D1 | P0-A 威胁模型（网关是否 sole ingress）| ✅ 接受「网关唯一入口」前提，**不进一步收紧**（保持网关 SignatureFilter 签名校验兜底，部署层防火墙/隔离兜底直连威胁）|
| D2 | P1-B 租户缺失兜底 | ✅ **保留 fail-safe 兜底**到平台租户（nil UUID，原 `'0'`），不改 fail-closed |
| D3 | 三组僵尸表删/留 | ✅ **全部保留，仅注释**标明「暂未实现」（sys_data_perm / sys_user_post / sys_perm + sys_role_perm 已同步 DDL 注释）|
| D4 | 软删除 × 唯一键 | ✅ **引入 partial unique index**（`WHERE deleted='0'`）|

### D4 落地：V2026_11_03__soft_delete_unique_index.sql（✅ 完成）

- **11 张带 deleted 列的业务表**唯一键（约束/索引）统一转成部分唯一索引 `WHERE deleted='0'`：
  `sys_config` / `sys_dept` / `sys_dict_data` / `sys_dict_type` / `sys_menu` / `sys_perm` /
  `sys_post` / `sys_role` / `sys_tenant`（tenant_code + domain）/ `sys_user` / `sys_job`。
- **语义**：逻辑删除（deleted='1'）的记录不进入索引，编码可复用；只对活跃记录强制唯一。
- **联动评估（D4 决策的关键风险）**：init 对 sys_menu 的 `ON CONFLICT (tenant_id,menu_name,parent_id)`
  由该部分唯一索引继续支撑（PG 允许部分唯一索引作 ON CONFLICT 仲裁；插入行不满足 `deleted='0'`
  时不判冲突直接落库，正是复用语义）。其余 ON CONFLICT (id) 走主键、关系表 uk 无 deleted 列，
  均不受影响 → **init/migration 的 ON CONFLICT 无需改动**。
- **设计取舍**：**不改 DDL 的约束声明**——否则与存量迁移 `V2026_10_22__constraints_and_align`（S4 补
  sys_dept 唯一键约束）在全新库上「索引/约束同名冲突」；本脚本置于所有迁移之后执行，全新库
  （ddl→init→migration）与存量库殊途同归为部分唯一索引。索引沿用原 uk_ 名，引用方零改动。
- **幂等**：DO 块先判存在 DROP 约束/非部分索引、再判存在建部分索引；DROP/CREATE 前都统计
  「活跃重复组数」，有重复只 NOTICE 跳过不落 DDL。双跑（CI M1/M2）0 行变更。
- **⚠️ 手工重跑 caveat**：某库已跑过本脚本后，**不要**再手工重跑 `V2026_10_22__constraints_and_align.sql`
  的 S4（补 sys_dept 唯一键约束）——它只判 `pg_constraint` 里的约束、不判同名索引，会因
  `uk_sys_dept_tenant_parent_dept_name` 已是同名部分索引而报「already exists」。正常流程
  （CI 一次性跑迁移 / 新装库）不受影响；S4 仅应在执行本脚本之前的历史库上运行。
- **执行后自检**：查询 12 个 uk 的 `pg_get_indexdef`，预期全部以 `WHERE deleted` 结尾。
- **验证状态（已活库实测，2026-11-03 补充）**：经本机 **dbx（MCP）** 连 dev-postgres（192.168.5.4:30432），
  对真实 `baize` 库执行本迁移：
  - ✅ 迁移前 12 个 uk 均为无 WHERE 的普通唯一索引；
  - ✅ 执行迁移后 12 个 uk 全部变为 **partial unique index**（`WHERE ((deleted)::text='0'::text)`，`indpred` 非空）；
  - ✅ **幂等**：二次执行 0 报错、0 变更（DO 分支识别已有 partial index 直接跳过）。
  - ⚠️ 功能复用（逻辑删后可插同编码）未做活库 DML 实测：dbx 事务仅支持原生本地 MySQL、PG 不支持回滚，
    为避免在共享 dev 库留下测试数据未跑；该语义由 PG partial unique index 本身保证（`deleted='1'` 行不入索引）。
  - 说明：本机 PG18 安装仍不完整（缺 `share/postgres.bki`），本地独立起库验证不可行；本验证借道 dbx 完成，
    CI 的 `sql` job（PG16 容器：ddl → operlog → init×2 → migration）仍作为新装库全流程的权威校验。

---

## 2026-11-04 需求落地：租户内置逻辑（V2026_11_04__tenant_builtin.sql，✅ 完成）

**需求**："租户管理需要增加内置逻辑，将保留字段换成内置类型，不让删除"。

**背景**：平台租户（nil UUID）此前靠 `tenant_name='平台租户(保留)'` 的"名称内土法标记"表达保留语义，
后端无法据此做删除守卫——任何有 `system:tenant:remove` 权限的人都能逻辑删除平台租户，
全平台登录与平台态数据随之悬空。本次对齐角色模块已验证的 `is_builtin` 模式
（V2026_10_29__role_type_builtin + SysRoleServiceImpl 双守卫）。

**变更清单**：
1. **DDL**：`sys_tenant` 新增 `is_builtin varchar(1) DEFAULT '0' NOT NULL`
   （`0:非内置, 1:内置（内置不可删除）`，与 `sys_role.is_builtin` 同构）。
2. **迁移** `V2026_11_04__tenant_builtin.sql`（幂等）：加列 + 平台租户标记 `is_builtin='1'` +
   名称 `'平台租户(保留)'`→`'平台租户'` + remark 措辞「保留租户」→「内置租户」。
3. **init 种子**：6 个租户 INSERT 列清单同步加 `is_builtin`（平台租户 `'1'`，其余 `'0'`），
   平台租户名称去"(保留)"，头注释同步。
4. **后端**：`SysTenant` 实体 / `SysTenantDto` 加 `isBuiltin`（MapStruct 同名自动映射）；
   `SysTenantServiceImpl.removeById` 加内置守卫（先查实体，内置→`BUSINESS_CONFLICT`"内置租户不允许删除"，
   与角色守卫同款写法），原有逻辑删除不变。
5. **前端**：`typings.d.ts` `SysTenantDto` 加 `isBuiltin`；租户列表"租户名称"列显示金色「内置」Tag；
   删除按钮对内置租户 `disabled` + title 提示（对齐角色页写法）。

**ON CONFLICT 仲裁联动**：init 对 `sys_tenant` 走 `ON CONFLICT (id)` 主键仲裁，新增列不影响；
`sys_tenant` 现有 uk（tenant_code/domain，V2026_11_03 已转 partial）不涉及本列。✓

**验证状态（2026-11-04，dbx 活库实测 dev-postgres → dev baize 库，用户确认直改）**：
- ✅ 后端编译 `mvn -pl baize-api-system,baize-system -am compile` BUILD SUCCESS，
  `SysTenantConvert` 无新增 MapStruct 告警（isBuiltin 实体↔DTO 自动映射）；
- ✅ 前端 `tsc --noEmit` 0 错误；
- ✅ 迁移已应用到 dev baize 库：6 条语句 0 报错，`is_builtin` 标记 1 行、改名 1 行、
  remark 更新按预期 0 行（dev 库该行 remark 本为空）；
- ✅ 复查：平台租户 `tenant_name='平台租户'`、`is_builtin='1'`、全库内置数=1；
- ✅ **幂等**：二跑 6 条语句全部 0 行、0 报错；
- ✅ **删除守卫功能实测已完成（用户确认，2026-11-04）**：内置租户删除被拒（前端按钮禁用 +
  后端 `BUSINESS_CONFLICT`"内置租户不允许删除"）。
- 说明：CI `sql` job 仍作为新装库全流程（含新 init 列清单）权威校验，push 后需确认全绿。

> AI生成

---

## 2026-11-05 需求落地：字典模块生效改造（V2026_11_05__dict_model_align.sql，✅ 完成）

**需求**："字典表未生效，表模型不一定合理"——诊断后按用户五项决策落地（1A/2删/3批量/4全量/5后续增）。

**根因诊断（未生效）**：init 零字典种子（表空）+ 前端零消费（28 处枚举全硬编码，
`getDictDataByType` 无人调用）+ 读接口 `DICT_QUERY` 权限门槛错位（普通用户表单页 403）+
`listByDictType` 缓存键 `type_+dictType` 无租户维度（多租户下跨租户缓存污染）+
类型删除无引用守卫。

**变更清单**：
1. **表模型（1A 混合模型）**：`sys_dict_type`/`sys_dict_data` 加 `is_builtin`
   （0:租户自定义,1:平台内置，内置不可删除）；读路径按「租户自定义 → 平台内置兜底」合并解析
   （`SysDictDataMapper.selectVisible` 用 `@InterceptorIgnore(tenantLine="true")` 绕过租户拦截器、
   手工限定两域范围）；补 `dict_type`/`dict_value` NOT NULL；**删 RuoYi 遗留 `css_class`/`list_class`**（2删）。
2. **种子**：14 类 40 项平台内置字典（sys_normal_disable/sys_yes_no/sys_user_sex/sys_notice_type/
   sys_oper_type/sys_oper_status/sys_job_status/sys_job_concurrent/sys_job_misfire/sys_config_type/
   sys_role_type/sys_data_scope/sys_menu_type/sys_defaulted），值与前端原硬编码一一对应；
   init 与迁移同步（ID 段 N=4001..4014 / 4101..4140）。
3. **后端读路径（3批量）**：新增 `GET /system/dict/data/all` 批量接口（登录即可见，无权限门槛，
   对齐租户 `/list` 先例）返回 `Map<dictType, 合并列表>`；`listByDictType` 同步合并语义 +
   缓存键带租户维度（SpEL Elvis 兜底平台租户，修复跨租户污染）+ 过滤 `status='0'`；
   写操作对 `sys_dict_data` 缓存 allEntries 清空。
4. **后端守卫**：类型删除双守卫（内置拒删 + 数据引用计数拒删，对齐角色模块）；数据删除内置守卫；
   create/update 防伪造：非平台租户 is_builtin 强制归 0 / 保持原值。
5. **前端消费层**：新增 `useDict()` hook（模块级全局缓存，登录后拉一次 `/all`）+ `getAllDictData` API；
   **全量替换 10 页 28+ 处硬编码枚举**（tenant/role/loginlog/operlog/user/notice/menu/job/config/dict），
   展示色保留在页面 render（配色不进 DB）；字典管理页：类型/数据内置金 Tag、内置禁删、
   删"回显样式"列与表单字段、增删改后 `reload()` 刷新全局缓存。

**ON CONFLICT 仲裁联动**：字典种子 init 走 `ON CONFLICT (id)` 主键仲裁不受影响；迁移种子用裸
`ON CONFLICT DO NOTHING`（容忍 id 与 uk 冲突双情形）；`uk_sys_dict_*` 两个 uk 已是 partial index
（V2026_11_03），新增列不触碰。

**验证状态（2026-11-05，dbx 活库实测 dev-postgres → dev baize 库，用户确认直改）**：
- ✅ 后端 `clean compile`（-pl baize-api-system,baize-system -am）BUILD SUCCESS，全量真实重编译；
- ✅ 前端 `tsc --noEmit` 0 错误；`listClass/cssClass/ROLE_TYPE_OPTIONS/DATA_SCOPE_OPTIONS` 零残留；
- ✅ 迁移已应用 dev baize 库：11 语句 0 报错（14 类型 + 40 数据种子落库）；
- ✅ 复查：builtin 14/40、`css_class/list_class` 0 列、`dict_type/dict_value` 全 NOT NULL、无残留行；
- ✅ **幂等**：二跑 11 语句全 0 行 0 报错；
- ✅ **合并解析**：以租户 XIAOMI 视角执行 selectVisible 同款 SQL，返回 14 类共 40 项平台内置兜底数据；
- ⚠️ 端到端（/all 接口 + 页面下拉）需重启后端生效后走 UI 验证；CI `sql` job 仍为新装库全流程权威校验。

**已知取舍**：
- 字典值展示色（如角色类型 geekblue/cyan）保留在页面本地色表，不进 DB（决策 2 的自然推论）；
- 模块级字典缓存在字典变更后需页面重新挂载才取新值（管理页自身即时 reload，其他已挂载页下次进入生效）；
- `sys_dict_type`/`sys_dict_data` 仍参与租户行级隔离（管理页只看本租户/平台行），合并语义只在读路径。

> AI生成

---

## 2026-11-04 迁移执行状态审计（14/14 全部已执行，✅ 完成）

**背景**：本项目未引入 Flyway/Liquibase 版本表，CI 对 `db/migration/*.sql` 按文件名顺序直接执行，
库侧无执行记录可查。因此用 dbx（只读查询）对每个迁移的「标志性产物」逐一探测核对。

**结论**：截至审计时点，`db/migration/` 下全部 14 个迁移在 dev `baize` 库中**均已执行、均已生效**：

| 迁移 | 探测标志物 | 结果 |
|---|---|---|
| V2026_10_21 uuid_v7_id | `sys_user.id` 为 varchar(36) | ✅ |
| V2026_10_22 constraints_and_align | `sys_role` 有主键 | ✅ |
| V2026_10_22_02 menu_buttons_incremental | job/post/config/notice 按钮菜单 5 标志行 | ✅ 5/5 |
| V2026_10_22_03 relation_table_pk | `sys_role_menu` 有主键 | ✅ |
| V2026_10_22_04 user_updatepwd_perm | `system:user:updatePwd` 权限行 | ✅ |
| V2026_10_23 home_menu | `uk_sys_menu_tenant_path` 索引 | ✅ |
| V2026_10_23 platform_tenant_0 | nil UUID 平台租户行 | ✅ |
| V2026_10_25_01 monitor_query_perms | monitor:server/cache:query 权限 | ✅ 2/2 |
| V2026_10_29 role_type_builtin | `role_type`+`is_builtin` 列 | ✅ 2/2 |
| V2026_10_30 seed_roles | 平台角色数 | ✅ 7（见澄清 1） |
| V2026_10_31 index_optimize | 补 `idx_sys_user_dept_id` ✅、4 冗余索引 0 残留 ✅ | ✅ |
| V2026_11_03 soft_delete_unique_index | partial unique uk | ✅ 13（见澄清 2） |
| V2026_11_04 tenant_builtin | 内置租户 = 1 | ✅ |
| V2026_11_05 dict_model_align | 内置字典类型 14 / 数据 40 | ✅ |

**两处数字澄清（非问题）**：
1. 平台角色 7 = `admin`（init 既有种子，2025-08 建）+ 6 个预置角色
   （ops/auditor/tenant_admin/hr/dept_manager/employee，V2026_10_30 新增）；
   首次探测的预期值漏算 admin，库状态本身正常。
2. partial unique uk 13 = 12 个（V2026_11_03 转换，`WHERE deleted='0'`）+ 1 个
   （`uk_sys_menu_tenant_path`：V2026_10_23 建立时即 partial——
   `WHERE path IS NOT NULL AND path <> ''`，空路径不参与唯一），正常并存，互不冲突。

---

## 当前待办（截至 2026-11-05，CI 全绿后，按优先级）

1. ~~**提交与推送（最高优先）**~~ ✅ 已完成（2026-11-05）：三任务分三个 commit
   （`bbf5a60` 软删除唯一键 / `a441577` 租户内置 / `c2b17b2` 字典改造），已推送 origin/dev。
   ~~CI 确认~~ ✅ 修复两个历史遗留失败后四 job 全绿（见下方 2026-11-05 CI 诊断章节）。
2. **UI 端到端验证（现为最高优先）**：重启 baize-system（加载新实体/Mapper/接口）并重新构建前端后验证：
   - 租户删除按钮对平台租户禁用、后端守卫返回「内置租户不允许删除」；
   - 登录后 `/system/dict/data/all` 一次拉取、10 页下拉/回显由字典驱动；
   - 字典管理页内置 Tag、内置禁删、租户自定义类型可正常增删；
   - 角色管理下拉（角色类型/数据权限）改为字典数据源后展示正常。
   - 前端注意：本地 `node_modules` 需重新 `npm install`（package.json 已删两个 @antv 死依赖，
     且本地需同步删除 gitignore 遮蔽的 tool/build 残留页——本次已删，新克隆不涉及）；
     package-lock.json 未跟踪无需提交。
3. **（可选）D4 功能复用实测**：逻辑删除后将同编码重新插入可用性的 DML 实测仍未做
   （dbx 对远程 PG 无事务回滚能力，避免在共享 dev 库留测试数据）；语义由 partial unique index
   本身保证，等待 CI 或专用环境补测。
4. **（低优先）后续字典扩充（决策 5）**：业务专属字典类型按需后续增加；上线流程见
   `builtin-resource-guard` 技能沉淀的七步法，不再重复展开。
5. **（低优先）CI 运维提醒**（不阻塞；CI 全绿后 annotations 提示）：ubuntu-latest 将于
   2026-10-19 起迁移 Ubuntu 26；actions/checkout@v4、setup-node@v4、setup-java@v4、
   azure/setup-helm@v4 存在 deprecated 提示（setup-java 建议 v5）；GitHub runner 已
   强制 Node 20 action 运行于 Node 24。建议择期统一升级 workflow actions 版本。

---

## 2026-11-05 CI 失败诊断与修复（c2b17b2 推送后）

**背景**：`c2b17b2` CI 四 job：SQL ✅、Helm ✅、Backend ❌、Frontend ❌。对比历史 runs 定位：

**Backend（Build and test backend 失败）**
- 根因：`SysNoticeServiceImplTest.shouldReturnPagedNotices` 单测 NPE。该测试依赖
  P1-F 重构**前**的行为——当时 `SysNoticeConvert` 是 `@Autowired` 字段注入，`@MockBean`
  拦截了它、mock 吞掉 null；P1-F（2283642）改为静态 `INSTANCE` 后 mock 不再生效，
  `super.page`（MP `IService.page` 默认实现= `baseMapper.selectPage` 直返，mock 未 stub → null）
  把 null 传进真实 `INSTANCE.toDtoPage` → `page.getCurrent()` NPE。该失败自 UUIDv7 批
  （0a3c824）起持续存在，非本次三任务引入。
- 修复：测试改为 stub `sysNoticeMapper.selectPage(any(),any())` 原样回传分页对象，
  让真实 MapStruct convert 走通，并断言分页字段；删除失效的 mock convert stub。
- 验证：本地 `mvn -pl baize-modules/baize-system -am package`（含单测）BUILD SUCCESS。

**Frontend（Install dependencies 失败，基线起一直红）**
- 2026-11-05 复盘更正：最初判断「ERESOLVE（@antv/l7-react peer react ^16/^17 vs React 19）」
  **不成立**——`baize-ui/.npmrc`（已被 git 跟踪）内含 `legacy-peer-deps=true`，CI 全程生效，
  依赖解析并不冲突（此前的本地复现在临时目录执行、未带上 .npmrc，属污染结论）。
  删除 `@antv/l7`/`@antv/l7-react` 仍保留：二者在 src 零引用，属死依赖清理（安装体积↓），
  但它**不是** CI 失败的修复。
- 真正根因（f982b04 日志取证确认）：`baize-ui/.gitignore` 裸 `build` 规则把
  `src/pages/tool/build/index.tsx`（表单构建器模板残留页）遮蔽——该文件从未入库，
  `config/routes.ts` 却静态引用 `component: './tool/build'`，CI 冷环境 postinstall 的
  `max setup` 解析该组件路径即 MODULE_NOT_FOUND 崩溃；本地正常只因磁盘恰有此文件
  （这也解释了 Frontend job 从建立依赖步骤起从未绿过）。
- 修复（用户选 A，2026-11-05）：routes.ts 删 `/tool/build` 路由项、`/tool` redirect
   改 `/tool/gen`；routePermission.ts 删对应映射；本地残留页
   `src/pages/tool/build/` 整目录回收站删除（与 2026-10-28「表单构建器不做」决策对齐）。
   验证：全仓 `tool/build` 代码引用清零（仅保留删除说明注释）、`tsc --noEmit` 0 错误；
   **CI 实测 ✅**：随 `27c94ce` 推送，run `41961921954` 四 job 全绿
   （Helm 7s / Frontend 2m8s / Backend 42s / SQL 29s）。

**结论（✅ 闭环）**：Backend（f982b04）与 Frontend（27c94ce）两个历史遗留失败均已修复，
2026-11-05 起 dev 分支 CI 四 job 全绿——Frontend job 自仓库建 CI 以来首次通过。
gh 工具链亦就绪（keyring 凭据；agent 环境需先清除 XDG_CONFIG_HOME 指向 TeleAgent
目录的注入才能读到 keyring，本修正已沉淀为本次排查记录，后续 CI 排障可直接
`gh run list / gh run view --log` 取证）。根因误判（ERESOLVE 假阳性）已在上方公开更正。

---

## 2026-11-05 系统管理功能补全（P1-P6 全做，✅ 代码+活库完成）

**背景**：审计发现 7 个模块只有 CRUD 壳无真实闭环。按用户"全做"决策（1范围 P1-P6 全部 /
2 operlog 全局 AOP / 3 config 初始密码+验证码开关 / 4 notice 弹窗+公告栏 / 5 过期拒绝 /
6 user-post 启用 / 7 perm 维持保留 D3 不动）实施。

**审计关键发现（实施前）**：
- **真实实现**（非壳）：job（完整 Quartz：60s 差异同步+反射执行）、loginlog（auth 写入）、
  monitor（server/cache/online 真实）、gen（baize-gen 独立应用结构完整）
- **纯壳**：config（0 消费方）、notice（0 消费方）、operlog（LogAspect 存在但
  baize-system 缺 spring-boot-starter-aop 依赖——@Aspect 从未激活，dev 库表恒为空是证据）、
  tenant 的 expire_time/max_user（0 校验点）、user-post（D3 保留表）、
  perm（D3 保留）、captcha（LoginDTO 字段存在无实现）

**P1 operlog 激活与补全**：
- 补齐 `spring-boot-starter-aop` 依赖（激活既有 `@Log` 注解 + `LogAspect`，dev 库 0 行是切面从未生效的铁证）
- `LogAspect` 升级 `@Around` 环绕计时（原 `@AfterReturning` 版 costTime 恒 0）
- `Log.BusinessType` 枚举 SELECT→GRANT（ordinal=4 对齐前端字典 sys_oper_type"4=授权"）
- 19 个 Controller × 58 处写接口机械标注 @Log（Python 脚本 utf-8 批量插入 + 裸 PostMapping 补漏 + @InnerAuth 接口剔除）
- **全局拦截**（POST→INSERT/PUT→UPDATE/DELETE→DELETE + 路径规则 /export→EXPORT、
  /assign→GRANT、/kickout→FORCE、/clean→CLEAN）

**P2 config 参数消费机制**：
- 新增 `SysConfigMapper.selectVisibleValue`（@InterceptorIgnore 绕租户拦截器，当前租户→平台兜底合并解析）
- `getConfigValueByKey`（@Cacheable 租户维度键，修复既有 `getByConfigKey` 的跨租户缓存污染缺陷）
- 写操作改 `@CacheEvict(allEntries=true)` 全量清缓存
- `SysConfigController` /inner/value @InnerAuth + `SysConfigFeignClient`
- **消费方 1**：`SysUserServiceImpl.create` 密码留空→取 sys.user.initPassword（缺失拒绝创建防空密码账号）
- 种子 V2026_11_06__config_seed.sql（N=4201/4202）+ init 同步

**P3 notice 公告展示闭环**：
- `SysNoticeService.listLatest(limit)` + Controller `GET /system/notice/latest`（登录即可见）
- `getLatestNotices` API + Welcome.tsx 公告栏（List+Tag+Drawer 详情）
- 登录后弹窗（最新一条 Modal，"我已知晓"localStorage 记忆不再重复弹同一条）

**P4 tenant 治理校验（含 2 个存量 bug 修复）**：
- **bug 修复 1**：`SysTenantServiceImpl.existsById` 用 `\d+` 校验租户 ID——UUIDv7 后所有
  合法租户被误判不存在，切换租户从 V2026_10_21 起完全不可用
- **bug 修复 2**：`AuthServiceImpl.switchTenant` 用 `isNumeric` 校验目标租户 ID——同族问题
- `checkTenantNotExpired`：登录（user.getTenantId）+ 切换租户（targetTenant）双点校验
  `expire_time`，过期拒绝（inner 接口 /tenant/inner/get @InnerAuth + Feign）
- `checkUserQuota`：创建用户时校验当前租户 `max_user` 配额，超限拒绝
  （getByCurrentTenantId 新增；查询失败放行+告警，治理增强不作为硬边界）

**P5 captcha 登录验证码**：
- `CaptchaService` + `CaptchaServiceImpl`（零第三方依赖：java.awt 手绘 4 位字符+干扰线
  → Base64 PNG，Redis TTL 120s 一次性消费）
- `AuthController` /captcha + /captcha/enabled（网关白名单 /api/v1/auth/** 天然放行，无需 Nacos 改动）
- `AuthServiceImpl.login` 开头校验（isEnabled 时 validate captchaKey+captcha）
- LoginDTO 加 captchaKey 字段；登录页探测开关渲染验证码+图片点击刷新+失败自动刷新

**P6 user-post 岗位绑定**：
- `SysUserPost` 实体（BaseRelationEntity）+ Mapper + Service（assignPosts 全量替换语义）
- 迁移 V2026_11_07__user_post_pk.sql（三列无主键→补 uk+PK，对齐 V2026_10_22_03 关系表三步法）
- `SysUserDto.postIds` + create/update 全量替换绑定 + removeById 清理防孤儿
- `SysUserController` GET /{id}/posts（独立回显接口，不污染用户主缓存）
- `SysPostController` GET /list（登录即可见，用户表单多选数据源）+ listAvailable
- `SysPostServiceImpl.removeById` 联动 removeByPostId（岗位删除清绑定）
- 前端：用户表单岗位多选（getPostList 选项+getUserPosts 回显）、
  密码改选填（"留空则使用系统初始密码"）

**验证状态（2026-11-05，代码+活库完成）**：
- ✅ 后端 `-am clean compile` BUILD SUCCESS（含 auth+system 全链路，真错误修复过程：
  AuthServiceImpl 缺 SysTenantDto import、user create 残留重复定义、getById 意外丢失、
  @Around 缺 value 属性、post impl PowerShell regex 写入损坏改编辑工具修复——每处已逐一核清）
- ✅ 前端 `tsc --noEmit` 0 错误
- ✅ dbx 活库（dev baize，用户确认直改）：V2026_11_06 config 种子 2 行落库；
  V2026_11_07 user_post 补 pk_sys_user_post 成功；**幂等二跑 5 语句全 0 行 0 报错**
- ⚠️ 端到端（重启后端生效后验证：操作日志有数据、参数初始密码创建用户、公告展示、
  岗位绑定、切换租户可用）待 push 后 UI 验证；CI sql job 为新装库全流程权威校验

**遗留（未做）**：
- perm 模块按决策 7 维持 D3 保留，不动
- operlog 覆盖 baize-gen（独立部署应用，不在本应用进程内，无法被本切面拦截）
- login 弹窗的"已读"仅 localStorage 记忆，无服务端已读记录（sys_notice_read 表待未来需求）

> AI生成
