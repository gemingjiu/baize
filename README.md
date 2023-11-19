# 白泽管理系统
## 服务列表
### 服务网关
```yaml
# Tomcat
server:
  port: 8080

# Spring
spring:
  application:
    # 应用名称
    name: baize-gateway
```
### 认证中心
```yaml
# Tomcat
server:
  port: 9100

# Spring
spring:
  application:
    # 应用名称
    name: baize-auth
```
### 系统服务
```yaml
# Tomcat
server:
  port: 9200

# Spring
spring:
  application:
    # 应用名称
    name: baize-system
```
### 定时服务
```yaml
# Tomcat
server:
  port: 9300

# Spring
spring:
  application:
    # 应用名称
    name: baize-job
```
### 代码生成
```yaml
# Tomcat
server:
  port: 9400

# Spring
spring:
  application:
    # 应用名称
    name: baize-gen
```
### 注册中心
```yaml
name: nacos
port: 8848
```
### 配置中心
```yaml
name: nacos
port: 8848
```
### 缓存服务
```yaml
name: redis
port: 6379
```
### 数据库
```yaml
name: postgres
port: 5432
```

## 组件功能
### 服务网关
* 路由
* 聚合服务
* 负载均衡
* 认证授权
* 过载保护
* 流量控制
* 熔断
* 服务升降级
* 缓存
* 服务重试
* 日志
  > 1. 登录日志
  > 2. 操作日志
### 认证中心

### 注册中心

### 配置中心

### 服务调用

### 服务监控

### 系统接口

### 链路追踪

### 熔断和降级

### 分布式组件

> * 分布式文件
> * 分布式事务
> * 分布式日志

### 部署方式

### 分库分表