# ADR-0002：登录态改 HttpOnly Cookie + refresh 轮转（DEC-2）

- 状态：Accepted（用户 2026-11-19 拍板，DEC-2 = 改造）
- 日期：2026-11-19
- 决策人：项目所有者
- 关联：ROADMAP_2026.md DEC-2 / TASK_PLAN_2026.md T1.3 / DEBT-6 / RFC 9700 / RFC 10017 / OWASP HTML5 Security Cheat Sheet

## 背景

当前 access/refresh token 存前端 `localStorage`，XSS 可单点窃取全部登录态。DEBT-6 要求消除该风险。项目后端已有 JWT 工具（`baize-common-security` 的 `JwtUtils`，jjwt 0.12.5），认证走 Spring Security + Redis。

## 决策

1. **access token 短时（建议 15min）存前端内存**，不落 localStorage/sessionStorage；页面刷新时用 refresh 静默恢复。
2. **refresh token 长时随机串，服务端只存哈希到 Redis**，带 family id 轮转链；`/auth/refresh` 每次轮转作废旧 token、签发新 token；检测到旧 token 复用即吊销整条链（RFC 9700/10017）。
3. **refresh 通过 `Set-Cookie` 下发**：`HttpOnly; Secure; SameSite=Lax; Path=/api/auth`；前端请求带 `credentials: 'include'`。CSRF 用 SameSite + 双重提交 Cookie 防护。
4. **租户切换**：服务端校验成员关系后再重签 access+refresh，前端清空内存 token，不信任前端传参。
5. **feature flag 可回退**：保留原 localStorage 方案开关，异常时一键切回。

## 备选方案

- **维持现状**：零改动但保留 XSS 单点风险，被否决。
- **BFF 代理 token**：安全边界更强，但引入额外服务与运维成本，超出当前范围；记录为未来演进方向，不在本次实施。

## 后果

- 正面：XSS 无法读取 refresh token；refresh 复用可检测吊销；符合 OWASP 与 RFC 最佳实践。
- 负面：前后端改动面大（认证服务、前端请求层、租户切换、401 重试逻辑）；需处理 CSRF 与 Cookie 跨域（SameSite=Lax + 同域部署前提）。
- 约束：前端 dev 代理需透传 Cookie；若 gateway/auth 跨子域部署需复核 SameSite/Domain 设置。
