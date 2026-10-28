# 白泽（baize）

白泽是一个基于 **Spring Cloud Alibaba** 的微服务后台管理系统，提供用户、角色、菜单、权限、部门、岗位、字典、参数、通知、租户等系统管理能力，支持多租户 RBAC。

- 后端：Spring Boot 3.2.12 / Spring Cloud 2023.0.5 / Spring Cloud Alibaba 2023.0.3.3（**JDK 17**）
- 前端：Ant Design Pro（Umi Max 4 + React 19 + TypeScript）
- 中间件：Nacos 2.2.3（注册中心 + 配置中心）、PostgreSQL（docker compose 提供 16，实测 18.6 亦兼容）、Redis 7
- 详细设计：[baize-backend/doc/详细设计方案.md](baize-backend/doc/详细设计方案.md)

## 一、目录结构

```text
baize/
├── baize-parent/                # Maven 父工程（BOM：依赖版本、插件版本、JDK 17 编译配置）
├── baize-backend/               # 后端多模块工程（聚合 POM）
│   ├── baize-common/            # 公共模块：core / webmvc / security / database / cache / excel
│   ├── baize-api/               # Feign 客户端与 DTO
│   ├── baize-auth/              # 认证服务（8010）
│   ├── baize-gateway/           # API 网关（8000）
│   ├── baize-modules/
│   │   ├── baize-system/        # 系统管理（8020）
│   │   ├── baize-gen/           # 代码生成（9300）
│   │   └── baize-example/       # 示例模块（8030，可选）
│   ├── deploy/                  # docker-compose / k8s / helm
│   └── doc/                     # 设计文档、部署标准、Nacos 配置（DEFAULT_GROUP）
├── baize-ui/                    # Ant Design Pro 前端
├── .env.example                 # 环境变量样例（JWT 密钥、数据库口令等，禁止提交真实值）
├── .github/workflows/ci.yml     # CI：后端构建、前端构建、SQL 语法校验
└── README.md
```

## 二、环境要求

| 组件 | 版本要求 | 本机示例 |
| --- | --- | --- |
| JDK | **17（必须）**，Spring Boot 3.x 不支持 JDK 8 | `D:\tool\jdk\jdk-17` |
| Maven | 3.8+ | `D:\tool\apache-maven-3.8.6` |
| Node.js | >= 20（见 `baize-ui/package.json` 的 engines） | 建议 Node 20 LTS |
| PostgreSQL | 16（compose）／18.6（实测） | docker compose 提供 |
| Redis | 7 | docker compose 提供 |
| Nacos | 2.2.3 | docker compose 提供 |
| Sentinel | **未接入**（网关限流为自研 `RateLimiterFilter`，内存计数） | docker compose 仍带 dashboard 镜像，仅供观察 |

> ⚠️ 本机 `JAVA_HOME` 可能仍指向 JDK 8，构建/启动前必须显式切到 JDK 17，否则 Spring Boot 3 的编译和启动都会失败。
> ⚠️ JDK 17 在 zh-CN Windows 上默认字符集为 GBK，手工 `java -jar` 启动时若 Nacos 配置里含中文注释，
>   Nacos 客户端会把中文 YAML 按 GBK 编码导致 `MalformedInputException`。请加
>   `-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8`（`start-backend.ps1` 已内置）。

## 三、构建后端

PowerShell（每个新开的终端都要先执行前两行）：

```powershell
$env:JAVA_HOME = 'D:\tool\jdk\jdk-17'
$env:Path = "D:\tool\apache-maven-3.8.6\bin;$env:JAVA_HOME\bin;$env:Path"
java -version   # 必须输出 17.x
mvn -v          # 必须输出 Java version: 17.x

# 1) 先安装父工程到本地仓库
#    baize-backend 的 parent 是 com.gem:baize-parent:0.0.1-SNAPSHOT，虽然 pom.xml 里用
#    <relativePath>../baize-parent/pom.xml</relativePath> 指向了同级目录，但先 install 父 POM
#    才能保证本次构建、后续单模块构建、IDE 导入和 CI 都能从本地仓库解析到它，最稳妥。
mvn -f baize-parent/pom.xml install

# 2) 再构建后端全部模块（clean install 会产出 Spring Boot 可执行 jar）
mvn -f baize-backend/pom.xml clean install -DskipTests
```

说明：

- `-f` 用于指定 POM 文件路径，`mvn -f baize-backend/pom.xml ...` 等价于 `cd baize-backend && mvn ...`，但不改变当前目录，也不会影响其他模块的相对路径。
- 第 1 步只在 `baize-parent` 版本变化时需要重跑；日常开发只需第 2 步。
- 构建产物示例：`baize-backend/baize-auth/target/baize-auth-0.0.1-SNAPSHOT.jar`（其余服务同理）。
- 只构建单个服务：`mvn -f baize-backend/pom.xml -pl :baize-auth -am clean install -DskipTests`（`-am` 同时构建其依赖的 common/api 模块）。

## 四、数据库初始化

```powershell
# 0) 创建业务库与 schema（db/ddl 脚本只建表，不包含 CREATE DATABASE / CREATE SCHEMA）
psql -U postgres -h localhost -c "CREATE DATABASE baize;"
psql -U postgres -h localhost -d baize -c 'CREATE SCHEMA IF NOT EXISTS "baize-system";'

# 1) 建表（表名在脚本内已带 "baize-system" schema 前缀）
psql -U postgres -h localhost -d baize -v ON_ERROR_STOP=1 -f baize-backend/db/ddl/baize-system-ddl.sql

# 2) 初始化数据（初始化脚本使用未限定表名，必须先设置 search_path）
#    脚本幂等：INSERT 均带 ON CONFLICT DO NOTHING，可重复执行
psql -U postgres -h localhost -d baize -v ON_ERROR_STOP=1 `
  -c 'SET search_path TO "baize-system";' `
  -f baize-backend/db/init/baize-system-init.sql

# 3) 操作日志建表（可选，幂等；PostgreSQL 方言）
psql -U postgres -h localhost -d baize -v ON_ERROR_STOP=1 `
  -f baize-backend/db/init/baize-system-operlog.sql
```

### 数据库脚本目录

| 目录 | 内容 | 幂等 |
| --- | --- | --- |
| `db/ddl/` | 建表 DDL（表名自带 `"baize-system"` schema 前缀；**仅首次建表用，非幂等**——重跑会报 `already exists`，增量变更请走 `db/migration`） | 否（增量用 migration） |
| `db/init/` | 种子数据（租户/部门/菜单/角色/用户/授权关系） | 是（`ON CONFLICT DO NOTHING`） |
| `db/migration/` | 增量迁移（约束对齐、关系表主键、首页菜单、平台租户 0 等），每个脚本可重复执行 | 是 |
> CI 会对 `db/init` 连跑两次验证幂等，并逐个执行 `db/migration/*.sql` 与 `baize-system-operlog.sql`。
>
> 已知数据缺口：`sys_config` / `sys_dict_type` / `sys_dict_data` / `sys_post` / `sys_notice` / `sys_job`
> 暂无种子数据（这些页面打开是空列表，属预期，非脚本错误）。

## 五、启动后端（本地）

启动顺序：**Nacos → PostgreSQL / Redis → baize-auth → baize-system → baize-gateway**（网关最后启动，避免注册中心里出现空实例）。

```powershell
$env:JAVA_HOME = 'D:\tool\jdk\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# 按仓库根目录 .env.example 设置本进程需要的环境变量（或在 IDEA Run Configuration 中配置）
$env:BAIZE_JWT_SECRET = '<至少 32 字节的随机字符串>'
$env:BAIZE_DB_PASSWORD = '<数据库口令>'
$env:NACOS_PASSWORD = '<Nacos 口令>'

java -jar baize-backend/baize-auth/target/baize-auth-0.0.1-SNAPSHOT.jar
java -jar baize-backend/baize-modules/baize-system/target/baize-system-0.0.1-SNAPSHOT.jar
java -jar baize-backend/baize-gateway/target/baize-gateway-0.0.1-SNAPSHOT.jar
```

| 服务 | 端口 | 说明 |
| --- | --- | --- |
| baize-gateway | 8000 | 统一入口，前端代理目标 |
| baize-auth | 8010 | 认证、令牌签发/登出 |
| baize-system | 8020 | 系统管理 |
| baize-gen | 9300 | 代码生成（读取 `information_schema` 逆向出 Entity/DTO/Convert/Mapper/Service/Controller + 前端 service/page，产出 zip 下载） |
| baize-example | 8030 | 示例模块（Feign 调用示例，可选启动） |

## 六、启动前端

```powershell
cd baize-ui
npm install
npm start
```

- 默认端口 3000（`baize-ui/.env.local` 的 `PORT=3000`），开发代理把 `/auth/**`、`/system/**` 转发到 `http://localhost:8000/api/v1`（见 `baize-ui/config/proxy.ts`）。
- Node 需要 >= 20。若 PowerShell 因执行策略禁止运行 `npm.ps1`，改用 `npm.cmd install` / `npm.cmd start`。

## 七、Nacos 配置

1. 在 Nacos 中创建命名空间（**namespace ID 需为 `baize`**，与各服务 `application.yml` 一致；本地 `.run/*.run.xml` 与 `start-backend.ps1` 均用该值），分组使用 `DEFAULT_GROUP`。
2. 导入 `baize-backend/doc/DEFAULT_GROUP/` 下的 5 个配置（Data ID 与文件名一致）：
   - `application-dev.yml`：所有服务共享（Redis、日志、安全通用配置）
   - `baize-auth-dev.yml` / `baize-gateway-dev.yml` / `baize-system-dev.yml` / `baize-gen-dev.yml`：各服务私有配置
3. 网关路由只维护在 `baize-gateway-dev.yml`（对外前缀 `/api/v1/**`，**不剥离前缀**，由各服务自己的 `server.servlet.context-path` 承载），**不要再写回 `application-dev.yml`**，否则会出现两份互相冲突的路由。
4. `jwt.secret`、数据库/Redis 口令一律使用环境变量占位符，禁止把真实值写进 Nacos 或仓库（变量清单见 `.env.example`）。Nacos 开启鉴权时还需提供 `NACOS_USERNAME` / `NACOS_PASSWORD`。
5. **Nacos 配置里不要写中文注释**：JDK 17 默认 GBK 会把中文 YAML 编码成非法 UTF-8 字节，客户端解析即抛
   `java.nio.charset.MalformedInputException: Input length = 1`（`spring-alibaba-nacos-config` 对 YAML 分支直接
   `content.getBytes()`，没有 `properties` 分支那样的 Unicode 转义兜底）。确需中文请以 `\uXXXX` 写，或给 JVM 加
   `-Dfile.encoding=UTF-8`。

## 八、部署

中间件（PostgreSQL / Nacos / Redis；Sentinel Dashboard 镜像仅作观察，项目未接入）：

```powershell
# 先在 baize-backend/deploy/docker-compose/ 下创建 .env（参考根目录 .env.example，至少提供 POSTGRES_PASSWORD）
docker compose -f baize-backend/deploy/docker-compose/docker-compose.yml up -d
```

应用镜像（Dockerfile 在各模块目录；**构建上下文必须是仓库根目录**，因为既要打包 `baize-parent` 也要打包 `baize-backend`）：

```powershell
docker build -f baize-backend/baize-auth/Dockerfile    -t baize-auth:0.0.1-SNAPSHOT .
docker build -f baize-backend/baize-gateway/Dockerfile -t baize-gateway:0.0.1-SNAPSHOT .
docker build -f baize-backend/baize-modules/baize-system/Dockerfile -t baize-system:0.0.1-SNAPSHOT .
```

前端镜像：

```powershell
docker build -t baize-ui:1.0.0 baize-ui
```

nginx 配置位于 `baize-ui/deploy/nginx.conf`：`/auth/`、`/system/` 反向代理到 `gateway:8000` 并补 `/api/v1` 前缀，`/api/` 直接透传；网关地址如需环境变量注入，可把配置改为 nginx 官方镜像的 `templates` envsubst 模板。
容器内以非 root 用户 `nginx` 运行，因此 nginx 监听 **8080**（非 root 无法绑定 1024 以下端口），对外映射时用 `-p 80:8080`。

Kubernetes / Helm 部署见 `baize-backend/deploy/k8s/`、`baize-backend/deploy/helm/` 与 `baize-backend/doc/baize-helm-deployment-standard.md`。

## 九、CI

`.github/workflows/ci.yml` 在 push / pull request 时执行：

- **后端**：JDK 17 + Maven 缓存 → 安装 `baize-parent` → `mvn -B -f baize-backend/pom.xml package`（含单元测试）
- **前端**：Node 20 → `npm install && npm run build`（仓库未跟踪 `package-lock.json`，故未用 `npm ci`；完整 umi build 尚未在本机验证，暂设 `continue-on-error`，不阻塞主流程）
- **SQL**：PostgreSQL 16 service 容器 → 依次执行 `db/ddl`、`db/init/baize-system-operlog.sql`、`db/init`（**连跑两次验证幂等**）、`db/migration/*.sql`

## 九.5、本地一键启动

仓库根目录提供 `start-backend.ps1`（Windows / PowerShell），按 **system(8020) → auth(8010) → gateway(8000)** 顺序启动，
已内置 `-Dfile.encoding=UTF-8`、Nacos 环境变量与 90 秒端口就绪等待；日志写入 `%TEMP%\baize-logs\*.log`。
端口已监听则跳过。停止：`Get-Process java | Stop-Process`。

## 九.6、功能清单（本轮新增）

| 功能 | 前端入口 | 后端落点 | 说明 |
| --- | --- | --- | --- |
| 数据权限 | 角色管理「数据范围」下拉（`data_scope` 1-5） | `baize-common-database` 的 `DataScopeInnerInterceptor` + `CustomDataScopeHandler` | SELECT 时按 `dept_id IN (...)` / `create_by = X` 追加条件；`@DataScope(skip = true)` 可豁免单条查询。**默认关闭**（`baize.data-scope.enabled`），开启后由 `SysDataScopeDeptProvider` 解析部门树 |
| Excel 导出 | 用户/角色/操作日志/登录日志列表页「导出」按钮 | `baize-common-excel` + 各 Controller 的 `/export` 端点 | EasyExcel 流式写出，前端用 `fetch` + `blob` 下载（导出是 POST 且要带查询条件，不能用 `<a href>`） |
| 代码生成 | 系统工具 → 代码生成 | `baize-gen`（9300） | 从 `information_schema.columns` 逆向，产出 9 个文件（Entity/DTO/Convert/Mapper/Service/ServiceImpl/Controller + 前端 service/page）打成 zip |
| 租户切换 | 顶栏租户下拉 + 登录页「租户」选择 | `GET /system/tenant/list`（免鉴权）+ `POST /auth/switchTenant` | 目标租户存在性 + 用户归属（超管 `*:*:*` 可跨租户）+ 旧令牌进黑名单；切换后刷新页面重建菜单/权限 |
| 服务监控 | 系统监控 → 服务监控 | `GET /system/monitor/server` | CPU / 内存 / JVM / 磁盘真实采集（Oshi） |
| 缓存监控 | 系统监控 → 缓存监控 | `GET /system/monitor/cache`、`/cache/keys` | Redis `INFO` 指标 + `SCAN` 键列表（类型/TTL），支持清空 |

## 十、安全提醒
1. `jwt.secret` 必须通过环境变量 `BAIZE_JWT_SECRET`（或 Nacos 加密配置）提供，为空时认证服务会启动失败，这是有意设计。
2. 数据库、Redis、Nacos、PostgreSQL 容器口令一律不得提交到仓库，使用 `.env`（已加入 `.gitignore`）或密钥管理系统。
3. `.env.example` 中变量名与配置文件中的占位符一一对应，新增占位符时请同步更新该文件。
4. `PermissionAspect` 已改为**权限码精确匹配**（`@RequiresPermission("system:user:add")` 必须与令牌里的权限码完全一致，仅 `*:*:*` 视为超管），不再使用角色名包含判断与前缀匹配。当前系统还没有「按用户查询权限码」的接口，`sys_perm` / `sys_role_perm` 也无初始化数据，因此登录时按 `baize.security.super-admin-role-codes`（默认 `admin`）把超管角色编码签发为 `*:*:*`，其他角色仍写角色编码。
   **待办**：实现权限码查询接口后，应把用户实际权限码集合（逗号分隔）写入令牌的 role claim；在那之前非超管角色会被精确匹配拒绝，效果与改造前一致，但只有补齐这一步才算真正落地接口级权限。
5. 内部端点（`@InnerAuth`，如 `/system/user/getByUsername`）通过请求头 `X-Inner-Token` 与配置 `baize.inner.token` 比对来隔离。**该配置项为空时不做校验（仅打印告警）**，生产环境必须配置并同步到所有服务；Feign 侧由 `baize-api-system` 的 `InnerAuthFeignConfig` 统一追加请求头。
6. 单元测试已可正常执行（父 POM 固定 `maven-surefire-plugin` 3.2.5；此前 Maven 默认的 2.12.4 只识别 JUnit4，JUnit5 测试被静默跳过，日志表现为 `Tests run: 0`）。
