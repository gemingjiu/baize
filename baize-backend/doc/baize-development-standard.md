# Baize 开发规范

## 1. 项目结构规范

### 1.1 顶级模块

```
baize/
├── baize-api/              # API 接口定义（Feign Client）
├── baize-auth/             # 认证服务
├── baize-gateway/          # 网关服务
├── baize-modules/          # 业务模块
│   └── baize-system/       # 系统管理模块
│   └── baize-example/      # 示例模块
├── baize-common/           # 公共模块
│   ├── baize-common-core       # 核心工具
│   ├── baize-common-database   # 数据库
│   ├── baize-common-security   # 安全
│   ├── baize-common-webmvc     # Web MVC
│   └── baize-common-cache     # 缓存
├── db/                     # 数据库脚本
│   ├── ddl/               # 表结构
│   ├── init/              # 初始化数据
│   └── pdm/               # 数据模型
├── deploy/                 # 部署配置
│   ├── docker-compose/    # Docker Compose
│   └── helm/              # Helm Chart
└── doc/                    # 文档
```

### 1.2 业务模块结构

```
baize-modules/<module-name>/
├── pom.xml
└── src/
    └── main/
        ├── java/com/gem/baize/<module-name>/
        │   ├── <module>/
        │   │   ├── controller/     # 控制器
        │   │   ├── service/        # 服务接口
        │   │   │   └── impl/       # 服务实现
        │   │   ├── mapper/         # 数据访问
        │   │   ├── entity/         # 实体类
        │   │   └── config/         # 配置类
        │   └── <ModuleName>Application.java
        └── resources/
            ├── application.yml
            └── mapper/             # MyBatis XML
```

---

## 2. 命名规范

### 2.1 包命名

- **格式**: `com.gem.baize.<模块名>.<子模块>.<功能域>`
- **示例**: `com.gem.baize.system.user`, `com.gem.baize.system.role`

### 2.2 类命名

| 类型 | 命名规则 | 示例 |
|------|---------|------|
| 实体类 | `<Domain>` | `SysUser`, `SysRole` |
| DTO | `<Domain>Dto` | `SysUserDto`, `SysRoleDto` |
| VO | `<Domain>VO` | `LoginVO`, `UserVO` |
| Controller | `<Domain>Controller` | `SysUserController` |
| Service 接口 | `<Domain>Service` | `SysUserService` |
| Service 实现 | `<Domain>ServiceImpl` | `SysUserServiceImpl` |
| Mapper 接口 | `<Domain>Mapper` | `SysUserMapper` |
| Convert 转换器 | `<Domain>Convert` | `SysUserConvert` |

### 2.3 方法命名

| 场景 | 命名规则 | 示例 |
|------|---------|------|
| 根据ID查询 | `getById` | `getById(String id)` |
| 根据条件查询 | `getByXxx` | `getByUserName` |
| 新增 | `create` | `create(SysUserDto dto)` |
| 更新 | `updateById` | `updateById(SysUserDto dto)` |
| 删除 | `removeById` | `removeById(String id)` |
| 分页查询 | `page` | `page(Page, SysUserDto dto)` |
| 列表查询 | `list` | `list(SysUserDto dto)` |
| 统计 | `count` | `count(SysUserDto dto)` |

### 2.4 变量命名

- **POJO 变量**: 使用实际类型命名，不使用泛型缩写
  ```java
  // 正确
  SysUser sysUser = new SysUser();
  List<SysUser> userList = new ArrayList<>();
  
  // 避免
  SysUser u = new SysUser();
  List list = new ArrayList();
  ```

---

## 3. 代码分层规范

### 3.1 分层结构

```
Controller (控制层)
    ↓ 调用
Service (服务层)
    ↓ 调用
Mapper (数据访问层)
    ↓ 操作
Database (数据库)
```

### 3.2 Controller 层

**职责**:
- 接收 HTTP 请求
- 参数校验
- 调用 Service 业务逻辑
- 返回响应结果

**规范**:
```java
@RestController
@RequestMapping("/<module>/<domain>")
public class <Domain>Controller {

    @Autowired
    private <Domain>Service <domain>Service;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取<domain>")
    public ApiResult<<Domain>Dto> getById(@PathVariable String id) {
        // 参数校验
        if (StringUtils.isBlank(id)) {
            throw new ParamException("请求参数id不能为空");
        }
        return ApiResult.ok(<domain>Service.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建<domain>")
    public ApiResult<Void> create(@Valid @RequestBody <Domain>Dto dto) {
        <domain>Service.create(dto);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新<domain>")
    public ApiResult<Void> update(@PathVariable String id, @Valid @RequestBody <Domain>Dto dto) {
        // 双重验证
        if (!id.equals(dto.getId())) {
            throw new ParamException("请求参数id不一致");
        }
        <domain>Service.updateById(dto);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除<domain>")
    public ApiResult<Void> delete(@PathVariable String id) {
        <domain>Service.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询<domain>")
    public ApiResult<PageResult<<Domain>Dto>> page(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @Valid @RequestBody <Domain>Dto dto) {
        Page<<Domain>> query = new Page<>(current, pageSize);
        Page<<Domain>Dto> pages = <domain>Service.page(query, dto);
        PageConvert<<Domain>Dto> pageConvert = new PageConvert<>();
        PageResult<<Domain>Dto> pageResult = pageConvert.toDto(pages);
        return ApiResult.ok(pageResult);
    }
}
```

**注意**:
- 使用 `@Valid` 进行参数校验
- 使用 `@Operation` 添加 Swagger 文档
- 返回 `ApiResult` 统一响应格式
- 路径参数使用 `@PathVariable`
- 请求体使用 `@RequestBody`

### 3.3 Service 层

**职责**:
- 业务逻辑处理
- 事务管理
- 缓存处理
- 调用 Mapper 层

**接口规范**:
```java
public interface <Domain>Service extends IService<<Domain>Entity> {
    <Domain>Dto getById(String id);

    void create(<Domain>Dto <domain>Dto);

    void updateById(<Domain>Dto <domain>Dto);

    void removeById(String id);

    Page<<Domain>Dto> page(Page<<Domain>Entity> page, <Domain>Dto <domain>Dto);
}
```

**实现规范**:
```java
@Service
public class <Domain>ServiceImpl extends ServiceImpl<<Domain>Mapper, <Domain>Entity> 
        implements <Domain>Service {

    @Autowired
    private <Domain>Convert <domain>Convert;

    @Override
    @Cacheable(cacheNames = "<domain>", key = "#id")
    public <Domain>Dto getById(String id) {
        <Domain>Entity entity = Optional.ofNullable(super.getById(id))
                .orElseThrow(() -> new NotFoundException("<domain>不存在"));
        return <domain>Convert.toDto(entity);
    }

    @Override
    public void create(<Domain>Dto <domain>Dto) {
        <Domain>Entity entity = <domain>Convert.toEntity(<domain>Dto);
        try {
            super.save(entity);
        } catch (DuplicateKeyException e) {
            throw new DuplicateException("<domain>已存在，请更换后重试");
        }
    }

    @Override
    @CachePut(cacheNames = "<domain>", key = "#<domain>Dto.id")
    public void updateById(<Domain>Dto <domain>Dto) {
        <Domain>Entity entity = <domain>Convert.toEntity(<domain>Dto);
        boolean success = super.updateById(entity);
        if (!success) {
            throw new NotFoundException("<domain>不存在或已删除");
        }
    }

    @Override
    @CacheEvict(cacheNames = "<domain>", key = "#id")
    public void removeById(String id) {
        boolean success = super.removeById(id);
        if (!success) {
            throw new NotFoundException("<domain>不存在或已删除");
        }
    }

    @Override
    public Page<<Domain>Dto> page(Page<<Domain>Entity> page, <Domain>Dto <domain>Dto) {
        LambdaQueryWrapper<<Domain>Entity> wrapper = new LambdaQueryWrapper<>();
        
        // 默认排序
        wrapper.orderByAsc(<Domain>Entity::getSort);
        
        // 动态条件查询
        if (StringUtils.isNotBlank(<domain>Dto.getXxx())) {
            wrapper.like(<Domain>Entity::getXxx, <domain>Dto.getXxx());
        }
        
        Page<<Domain>Entity> entityPage = super.page(page, wrapper);
        return <domain>Convert.toDtoPage(entityPage);
    }
}
```

**注意**:
- 继承 `ServiceImpl<Mapper, Entity>`
- 使用 `Optional` 进行空值处理
- 使用合适的异常类型
- 使用 `@Cacheable`、`@CachePut`、`@CacheEvict` 处理缓存

### 3.4 Mapper 层

**职责**:
- 数据库操作
- 使用 MyBatis Plus

**规范**:
```java
public interface <Domain>Mapper extends BaseMapper<<Domain>Entity> {
}
```

### 3.5 Entity 层

**职责**:
- 数据模型映射
- 数据库表映射

**规范**:
```java
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("<table_name>")
public class <Domain> extends BaseEntity {
    /**
     * 字段描述
     */
    @TableField(value = "<column_name>")
    private <Type> <fieldName>;
}
```

### 3.6 Convert 转换层

**职责**:
- Entity ↔ DTO ↔ VO 之间的转换

**规范**:
```java
@Component
public class <Domain>Convert extends BaseConvert<<Domain>Entity, <Domain>Dto> {

    @Override
    public <Domain>Dto toDto(<Domain>Entity entity) {
        if (entity == null) {
            return null;
        }
        <Domain>Dto dto = new <Domain>Dto();
        BeanUtil.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public <Domain>Entity toEntity(<Domain>Dto dto) {
        if (dto == null) {
            return null;
        }
        <Domain>Entity entity = new <Domain>Entity();
        BeanUtil.copyProperties(dto, entity);
        return entity;
    }

    public Page<<Domain>Dto> toDtoPage(Page<<Domain>Entity> page) {
        if (page == null) {
            return null;
        }
        return page.convert(this::toDto);
    }
}
```

---

## 4. 数据传输对象规范

### 4.1 DTO (Data Transfer Object)

**用途**: Controller 与 Service 之间数据传输

**命名**: `<Domain>Dto`

**规范**:
```java
@Data
public class <Domain>Dto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    
    @NotBlank(message = "名称不能为空")
    private String name;
    
    @Range(min = 0, max = 100, message = "排序值必须在0-100之间")
    private Integer sort;
    
    private String remark;
}
```

### 4.2 VO (Value Object)

**用途**: 响应给前端的数据

**命名**: `<Domain>VO`

**规范**:
```java
@Data
@Builder
public class <Domain>VO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Integer sort;
    private String createTime;
    private String createBy;
}
```

### 4.3 DTO vs VO 使用场景

| 场景 | 使用类型 |
|------|---------|
| POST/PUT 请求体 | DTO |
| GET 请求参数 | DTO |
| 响应返回 | VO |
| 分页响应 | PageResult<VO> |

---

## 5. 异常处理规范

### 5.1 异常类型

| 异常类 | 使用场景 | HTTP 状态码 |
|--------|---------|-------------|
| NotFoundException | 资源不存在 | 404 |
| DuplicateException | 数据重复 | 400 |
| ParamException | 参数错误 | 400 |
| AuthException | 认证/授权失败 | 401/403 |
| BusinessException | 业务异常 | 400 |

### 5.2 异常定义位置

```
baize-common/baize-common-core/src/main/java/com/gem/baize/common/core/exception/model/
├── BaseException.java
├── BusinessException.java
├── NotFoundException.java
├── DuplicateException.java
├── ParamException.java
└── AuthException.java
```

### 5.3 异常抛出规范

```java
// 资源不存在
Optional.ofNullable(entity)
        .orElseThrow(() -> new NotFoundException("用户不存在"));

// 数据重复
try {
    super.save(entity);
} catch (DuplicateKeyException e) {
    throw new DuplicateException("用户名称已存在，请更换后重试");
}

// 参数校验失败
if (StringUtils.isBlank(id)) {
    throw new ParamException("请求参数id不能为空");
}
```

### 5.4 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ApiResult<Void> handleBaseException(BaseException e) {
        return ApiResult.fail(e.getCode(), e.getMessage());
    }
}
```

---

## 6. 日志规范

### 6.1 日志级别

| 级别 | 使用场景 |
|------|---------|
| ERROR | 程序异常，需要关注 |
| WARN | 潜在问题，需要注意 |
| INFO | 重要业务节点 |
| DEBUG | 调试信息 |

### 6.2 日志格式

```java
// 正确格式
log.info("创建用户: userName={}, deptId={}", userName, deptId);
log.error("删除用户失败: id={}, error={}", id, e.getMessage(), e);

// 避免
log.info("创建用户");
log.error(e.getMessage());
```

---

## 7. 数据库设计规范

### 7.1 表命名

- **格式**: `<prefix>_<domain>`
- **示例**: `sys_user`, `sys_role`, `sys_menu`

### 7.2 字段命名

- **格式**: `<snake_case>`
- **示例**: `user_name`, `login_ip`, `create_time`

### 7.3 公共字段

所有表必须包含以下公共字段：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | varchar(64) | 主键 |
| tenant_id | varchar(64) | 租户ID |
| create_by | varchar(64) | 创建者 |
| create_time | datetime | 创建时间 |
| update_by | varchar(64) | 更新者 |
| update_time | datetime | 更新时间 |
| remark | varchar(500) | 备注 |
| del_flag | bit(1) | 删除标志 |

### 7.4 索引规范

- 主键索引: `PRIMARY KEY (id)`
- 业务索引: 根据查询条件创建
- 唯一索引: 用于唯一性约束

---

## 8. API 设计规范

### 8.1 RESTful 规范

| 方法 | URI | 说明 |
|------|-----|------|
| GET | `/<module>/<domain>` | 查询列表 |
| GET | `/<module>/<domain>/{id}` | 根据ID查询 |
| POST | `/<module>/<domain>` | 创建 |
| PUT | `/<module>/<domain>/{id}` | 更新 |
| DELETE | `/<module>/<domain>/{id}` | 删除 |
| POST | `/<module>/<domain>/page` | 分页查询 |

### 8.2 响应格式

```json
{
  "code": 200,
  "msg": "success",
  "data": {},
  "timestamp": 1700000000000
}
```

### 8.3 分页响应

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "timestamp": 1700000000000
}
```

---

## 9. 配置管理规范

### 9.1 配置文件优先级

1. 命令行参数 `--spring.profiles.active=prod`
2. 环境变量 `SPRING_PROFILES_ACTIVE=prod`
3. application-{profile}.yml
4. application.yml

### 9.2 Nacos 配置

```yaml
spring:
  application:
    name: baize-system
  profiles:
    active: dev
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER_ADDR:192.168.5.4:8848}
        namespace: ${NACOS_NAMESPACE:baize-dev}
        group: DEFAULT_GROUP
        username: ${NACOS_USERNAME:baize}
        password: ${NACOS_PASSWORD:baize@2023}
      config:
        server-addr: ${NACOS_SERVER_ADDR:192.168.5.4:8848}
        namespace: ${NACOS_NAMESPACE:baize-dev}
        group: DEFAULT_GROUP
        file-extension: yml
  config:
    import:
      - optional:nacos:application-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
      - optional:nacos:${spring.application.name}-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
```

### 9.3 敏感配置

敏感信息（如密码、密钥）通过以下方式管理：
- 环境变量
- Kubernetes Secret
- Nacos 加密配置

---

## 10. Git 提交规范

### 10.1 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### 10.2 Type 类型

| 类型 | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug 修复 |
| docs | 文档更新 |
| style | 代码格式 |
| refactor | 重构 |
| test | 测试 |
| chore | 构建/工具 |

### 10.3 示例

```
feat(user): 新增用户导出功能

- 支持导出 Excel 格式
- 支持自定义导出字段
- 添加导出进度显示

Closes #123
```

---

## 11. 代码审查规范

### 11.1 必须检查项

- [ ] 代码符合本规范
- [ ] 单元测试通过
- [ ] 无硬编码敏感信息
- [ ] 异常处理完善
- [ ] 日志记录完整
- [ ] 参数校验完整

### 11.2 审查重点

1. **安全性**: 敏感信息、SQL 注入、XSS
2. **性能**: N+1 查询、缓存使用
3. **可维护性**: 代码复杂度、重复代码
4. **可测试性**: 依赖注入、接口抽象

---

## 12. 最佳实践

### 12.1 依赖注入

```java
// 构造函数注入（推荐）
private final <Domain>Service <domain>Service;

public <Domain>Controller(<Domain>Service <domain>Service) {
    this.<domain>Service = <domain>Service;
}

// 字段注入（避免）
@Autowired
private <Domain>Service <domain>Service;
```

### 12.2 事务管理

```java
@Transactional(rollbackFor = Exception.class)
public void create(<Domain>Dto dto) {
    // 业务逻辑
}
```

### 12.3 缓存使用

```java
// 查询缓存
@Cacheable(cacheNames = "<domain>", key = "#id")
public <Domain>Dto getById(String id) {}

// 更新缓存
@CachePut(cacheNames = "<domain>", key = "#dto.id")
public void updateById(<Domain>Dto dto) {}

// 删除缓存
@CacheEvict(cacheNames = "<domain>", key = "#id")
public void removeById(String id) {}
```

### 12.4 空值处理

```java
// 使用 Optional
Optional.ofNullable(entity)
        .orElseThrow(() -> new NotFoundException("不存在"));

// 使用 MapUtils
if (MapUtils.isNotEmpty(map)) {}

// 使用 CollectionUtils
if (!CollectionUtils.isEmpty(list)) {}
```

---

**文档版本**: v1.0.0  
**最后更新**: 2026-03-24  
**维护者**: Baize Team