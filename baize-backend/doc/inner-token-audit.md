# 内部令牌（baize.inner.token）启用风险审计

> 审计日期：2026-10-22；2026-10-28 更新：P1-E 落地后空令牌行为由「放行」改为「拒绝」（fail-closed），
> 令牌已在 Nacos `application-dev.yml` 正式启用（见第 1 节更新说明）。
> 结论先行：**可以安全启用**。全部 5 个 `@InnerAuth` 端点的调用方都是走 `baize-api-system`
> 的 Feign 客户端，且该模块已注册 `InnerAuthFeignConfig`，会自动附加令牌请求头；
> 全仓库不存在 `RestTemplate` / `WebClient`（grep 0 命中），因此没有"绕过 Feign 的内部调用"。
> 风险仅剩两处：**`baize-example` 模块**与**前端直连网关**，详见下文。

---

## 1. 现状（2026-10-28 更新：令牌已启用，fail-closed）

`baize.inner.token` 已在 Nacos `application-dev.yml` 配置为非空 UUID，
且 P1-E 已把 `InnerAuthInterceptor` 改为 **fail-closed**：
未配置令牌时，@InnerAuth 端点一律拒绝（抛 ForbiddenException / 403），
不再有"空令牌放行"的宽松模式。行为链：

| 组件 | 空令牌时的行为（现行为） |
|---|---|
| `InnerAuthInterceptor` | 打印 error 日志后**拒绝**（fail-closed） |
| `InnerAuthFeignConfig` | 不附加 `From-Inner` 请求头（error 日志） |
| `GatewayDenylist` | 与令牌无关，只按路径黑名单拦截 |

即：**@InnerAuth 现在具备真实鉴权能力；令牌未配置属于故障态而非降级态。**

---

## 2. `@InnerAuth` 端点全清单与调用方

| # | 端点 | 服务端位置 | 调用方 | 是否走 Feign |
|---|---|---|---|---|
| 1 | `GET /user/getByUsername` | `SysUserController:112` | `SysUserFeignClient.getByUsername` | ✅ |
| 2 | `GET /user/getByUsernameAndTenant` | `SysUserController:122` | `SysUserFeignClient.getByUsernameAndTenantId` | ✅ |
| 3 | `GET /user/getPasswordByUsername` | `SysUserController:137` | `SysUserFeignClient.getPasswordByUsername` | ✅ |
| 4 | `PUT /user/updateLastLoginTime` | `SysUserController:147` | `SysUserFeignClient.updateLastLoginTime` | ✅ |
| 5 | `GET /role/menu/user/{userId}/perms` | `SysRoleMenuController:39` | `SysMenuFeignClient.getPermCodesByUserId` | ✅ |

**关键事实**：以上 5 个客户端全部定义在 `baize-api-system` 模块，
该模块的 `META-INF/spring/...AutoConfiguration.imports` 已注册 `InnerAuthFeignConfig`，
因此只要服务启动时读到令牌，Feign 调用就会带上 `From-Inner` 请求头。两端对称，不会自锁。

---

## 3. 启用后的风险清单（按严重度）

| 级别 | 风险点 | 说明 | 处置建议 |
|---|---|---|---|
| 中 | **`baize-example` 模块** | 示例模块通过 Feign 调用 system 的 `DeptFeignController.page`。该接口**没有** `@InnerAuth`，不受影响；但若示例代码将来复用 `@InnerAuth` 端点，且示例模块未依赖 `baize-api-system`，就会 403 | 当前无影响；给示例模块加依赖时确认 Feign 客户端来自 `baize-api-system` 即可 |
| 中 | **前端直连 system 服务** | 若绕过网关直接访问 `http://system:9200/user/getPasswordByUsername`，无令牌会 403 —— 这恰是**期望行为** | 无需处置 |
| 低 | **`SysLoginLogController.record`** | `POST /loginlog/record` 原本就没有 `@InnerAuth`，不受令牌影响，但它同样是个"内部写入"端点，目前仅靠路径隐藏保护 | 建议后续补 `@InnerAuth`（本次未改，避免影响认证链路联调） |
| 低 | **令牌轮换的原子性** | 令牌在多服务间共享，轮换时若只改一个服务的配置，会出现"签发方带新头、服务端校验旧值"的 403 | 轮换时在 Nacos 一次性更新 `application-dev.yml`，让所有服务同时重启生效 |

**未发现**的风险：
- 不存在绕过 Feign 的内部 HTTP 调用（`RestTemplate`/`WebClient` grep 0 命中）；
- 不存在"服务端校验、客户端不带头"的错配（客户端与服务端读的是同一个 `baize.inner.token`）。

---

## 4. 建议的启用方式

按用户既定偏好（**Nacos 一律字面量、禁止 `${ENV}`**），建议在 Nacos
`application-dev.yml` 的 `baize:` 段下增加：

```yaml
baize:
  # 内部调用令牌：Feign 客户端自动附带 From-Inner 请求头，@InnerAuth 端点据此校验
  inner:
    token: baize-inner-a1f4c9d2-7e3b-4f18-9c05-8d6e2b7a41f3
```

需要同步改动的服务（令牌为共享密钥，**必须全部重启后才生效**）：

| 服务 | 原因 |
|---|---|
| baize-system | 服务端校验方（拦截器读令牌） |
| baize-auth | 客户端调用方（Feign 带令牌） |
| baize-gateway | 与令牌无关，但同批重启避免半生效状态 |

> 注：以上 token 值是本次审计生成的随机 UUID，仅作建议值；是否采用、以及是否真的启用，
> 需要用户确认。若暂不启用，当前"网关黑名单 + 空令牌放行"的组合仍可防外部访问，
> 只是缺少"服务间互调"这一层的纵深。

---

## 5. 代码现状评估（无需改动）

| 文件 | 评估 |
|---|---|
| `InnerAuth.java` | `TOKEN_PROPERTY = "baize.inner.token"`，常量定义清晰 |
| `InnerAuthInterceptor.java` | 空令牌 **fail-closed 拒绝**（P1-E，2026-10-28）；非空时 `equals` 精确比较；失败抛 `ForbiddenException`（走统一异常处理返回 403） |
| `WebMvcConfig.java` | `order(-9)`，在 `HeaderInterceptor` 之后注册；`@InnerAuth` 方法之外的路径直接 `return true`，无性能损耗 |
| `InnerAuthFeignConfig.java` | 空令牌 warn 且不带头；非空时对所有 Feign 请求统一附加，作用域正确 |

结论：~~代码实现本身没有缺陷，空令牌的行为是"显式声明的开发期宽松模式"，不是疏漏。~~
（2026-10-28 修订：宽松模式已被 P1-E 移除，空令牌现在是硬拒绝。）
