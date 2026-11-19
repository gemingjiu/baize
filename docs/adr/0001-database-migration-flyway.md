# ADR-0001：引入 Flyway 管理数据库迁移（DEC-3）

- 状态：Accepted（用户 2026-11-19 拍板，DEC-3 = 引入）
- 日期：2026-11-19
- 决策人：项目所有者
- 关联：ROADMAP_2026.md DEC-3 / TASK_PLAN_2026.md T1.1 / DEBT-1

## 背景

当前 16 个迁移脚本（`baize-backend/db/migration/V2026_10_21__* … V2026_11_07__*`）靠手工顺序执行，CI 的 sql job 用 `ddl → operlog → init×2 → migration/*.sql 逐个执行` 模拟。没有版本表，无法回答"这个库跑到哪个版本"，漏跑/重跑/顺序错只能靠人盯。这是关键路径起点：`DEC-3 → T1.1 Flyway → T2.1 Tracing → Wave 3 升级`。

## 决策

1. **引入 Flyway**，依赖用 Spring Boot 3.2.12 BOM 管理的 `flyway-core` 9.22.3（该版本 PG 驱动内置在 core，无需 `flyway-database-postgresql`，那是 10.x 才拆出的）。
2. **权威脚本仍在 `baize-backend/db/migration/`**，避免与 `src/main/resources/db/migration` 形成双份漂移。构建期由 `maven-resources-plugin` 把权威脚本单向复制到 `target/classes/db/migration`，Flyway 配置 `spring.flyway.locations=classpath:db/migration`：fat jar（K8s）自包含迁移脚本，路径稳定，不依赖宿主机挂载 db 目录，也不形成第二份维护点。（原方案 `filesystem:db/migration` 因容器内 jar 运行无稳定文件系统路径而改为 classpath 单源。）
3. **baseline 策略**：
   - 存量库：`baseline-on-migrate=true` + `baseline-version=2026_11_07`（当前最新脚本版本），把已手工跑过的 16 个脚本标记为已应用，避免重复执行。
   - 新装库：仍走 `ddl → init → migration` 流程；init 已含种子，同样 baseline 到 `2026_11_07`，未来新脚本 `V2026_11_08+` 才由 Flyway 应用。
4. **forward-only**：禁止修改已应用的迁移；修正只能新增脚本（expand→migrate→contract）。
5. **落地范围**：Flyway 只在一个"schema 管理"入口启用（默认 `baize-system`，其数据源指向 `baize` schema），网关/auth 不重复启用，避免多实例并发 baseline 竞争；若多实例部署需配 `spring.flyway.enabled` 仅迁移实例为 true。
6. **CI 断言**：sql job 迁移后断言 `flyway_schema_history` 存在且版本连续、重复执行 0 变更。

## 备选方案

- **Liquibase**：生态与 Spring Boot 集成也成熟，但团队无既有使用、YAML changelog 学习成本高，且现有脚本是原生 SQL，Flyway 改动最小。
- **维持手工**：零改动，但债务持续、关键阻塞 T2.1/Wave3；被否决。

## 后果

- 正面：DB 变更可审计、可重复、CI 可阻断，解锁关键路径。
- 负面：新增一个启动依赖；存量库 baseline 是一次性有风险操作，执行前必须 `pg_dump` 备份并保留手工回退路径。
- 注意：Flyway 9.x 的 baseline 不计入 `flyway_schema_history` 的 checksum 校验，需确保 16 个脚本内容此后不再变化（与 forward-only 约束一致）。
