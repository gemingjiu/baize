# 架构决策记录（ADR）

本目录记录 baize 项目中影响面较大、需要追溯"为什么这么定"的技术决策。每项决策一个文件，编号递增，状态分为 Proposed / Accepted / Superseded。

| 编号 | 决策 | 状态 | 关联任务 |
|---|---|---|---|
| [0001](./0001-database-migration-flyway.md) | 引入 Flyway 管理数据库迁移（DEC-3） | Accepted | T1.1 |
| [0002](./0002-token-httponly-cookie.md) | 登录态改 HttpOnly Cookie + refresh 轮转（DEC-2） | Accepted | T1.3 |
| [0003](./0003-observability-otel-lgtm.md) | 可观测性迁移到 OpenTelemetry + LGTM（取代 SkyWalking） | Accepted | T2.1 / T2.2 |

> 决策门编号（DEC-1~7）见 [ROADMAP_2026.md](../ROADMAP_2026.md)，任务拆解见 [TASK_PLAN_2026.md](../TASK_PLAN_2026.md)。
