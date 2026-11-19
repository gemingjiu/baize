# ADR-0003: 可观测性迁移到 OpenTelemetry + LGTM

- 状态：Accepted
- 日期：2026-11-19
- 关联任务：T2.1（链路追踪）、T2.2（指标与告警）、T2.4（韧性基础）
- 决策门：承接 ROADMAP_2026.md 可观测性专项

## 上下文

roadmap T2.1/T2.2 要求链路追踪跨 gateway/auth/system 串联，并提供指标与告警。项目现状：

- 已有 SkyWalking 体系，但覆盖不完整：仅 `baize-gateway` 显式依赖 `apm-toolkit-trace 9.4.0`，`TraceFilter` 用 `TraceContext.traceId()` 取 traceId；4 个模块（gateway/auth/system/example）的 logback pattern 含 `%X{traceId},%X{spanId}`，但没有任何 SkyWalking Java Agent 部署证据。
- 实测缺陷：下游 `HeaderInterceptor` 只把 traceId 写入 `RequestContextHolder`（ThreadLocal），**未写入 logback MDC**，导致 system/auth 日志的 traceId 字段恒空（`[baize-system,host@ip,,]`），无法与网关日志按同一 traceId 串联。
- 依赖树里只有传递进来的 micrometer-core/observation 与 sentinel 的 prometheus simpleclient，没有显式 tracing/registry 依赖。

2026-11-19 用户拍板：完整迁移 OTel+LGTM（不保留 SkyWalking）；越权测试 T2.3 本轮跳过；部署件交付 YAML + 静态校验。

## 决策

1. **统一追踪后端为 OpenTelemetry**：通过 `io.micrometer:micrometer-tracing-bridge-otel` 让 Micrometer Observation 以 OTel 为后端，配合 `io.opentelemetry:opentelemetry-exporter-otlp` 将 span 经 OTLP 导出到 OTel Collector。版本由 Spring Boot 3.2.12 BOM 管理（micrometer-tracing 1.2.12 / opentelemetry 1.31.0）。覆盖 gateway/auth/system/gen/example 五个模块。
2. **移除 SkyWalking**：删除 `baize-parent/pom.xml` 的 `skywalking.version` 属性与 `apm-toolkit-trace` dependencyManagement，删除 `baize-gateway/pom.xml` 的 `apm-toolkit-trace` 依赖；`TraceFilter` 改用 OTel `Span.current()` 读取 traceId（无有效 span 时回退请求头/UUID），保留 `X-Trace-Id` 自定义头以维持日志口径兼容。
3. **修复 MDC 贯通**：下游 `HeaderInterceptor` 读 `X-Trace-Id`，仅在 MDC 尚无 traceId 时写入（兜底，不覆盖 OTel 自动写入的值）。OTel bridge 自身通过 W3C `traceparent` 自动跨服务传播并写 MDC，二者 traceId 同源。
4. **指标与导出**：新增 `micrometer-registry-prometheus`（Boot BOM 管理）；暴露 `health,info,prometheus,metrics` 端点；开启 HTTP server 请求耗时直方图 percentile。
5. **LGTM 栈部署件**：交付 OTel Collector + Loki + Tempo + Mimir + Grafana 的 docker-compose 与 Helm/K8s YAML（含 ServiceMonitor、PrometheusRule、探针、副本、PDB、拓扑打散）。本机无 Docker/K8s/Helm，仅做静态校验，明确标注需目标集群验证。
6. **配置同步待办**：应用实际读取 Nacos 配置，仓库内 `doc/DEFAULT_GROUP/*.yml` 仅作模板；模板改动需在部署时同步到 Nacos（T0.1 已暴露同类疏漏）。

## 后果

- 正面：traceId 在网关与下游日志中贯通且可本机实证；追踪数据可导出到统一后端；指标暴露标准化；去除不完整且无 Agent 支撑的 SkyWalking 链路。
- 代价：完整迁移改动面覆盖 5 个模块；本机无 Docker/K8s，OTLP 导出与 LGTM 栈只能静态交付，端到端验证需目标环境；Nacos 配置需人工同步。
- 回退：保留各模块原 logback pattern；若目标环境暂不具备 Collector，可先关闭 OTLP endpoint（sampling 保留），bridge 仍可本地生成 traceId 填充日志。

## 备选方案（否决）

- 保留并补齐 SkyWalking：用户明确要求完整迁移，否决。
- OTel 与 SkyWalking 并存：双栈维护与配置冲突成本高，否决。
