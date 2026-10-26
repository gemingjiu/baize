declare namespace API {
  type ApiResult<T = any> = {
    success: boolean;
    data: T;
    errorCode: number;
    errorMessage: string;
    showType: number;
    traceId: string;
    timeStamp: number;
    code?: number;
    message: string;
  };

  type LoginDTO = {
    tenant?: string;
    username: string;
    password: string;
    captcha?: string;
    deviceType?: string;
  };

  type LoginParams = LoginDTO;

  type TokenRefreshDTO = {
    refreshToken: string;
    userId: string;
    tenantId: string;
    username: string;
  };

  type LoginVO = {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    user: UserVO;
  };

  type UserVO = {
    id: string;
    username: string;
    nickname: string;
    email: string;
    phone: string;
    avatar: string;
    tenantId: string;
  };

  type CurrentUser = UserVO;

  type PageParams = {
    current?: number;
    size?: number;
  };

  /**
   * 后端分页出参（自研 PageResult）。
   *
   * 契约样例：{ records, total, size, current, countId }
   * 注意：MyBatis-Plus `Page` 的 pages / orders / optimizeCountSql / searchCount / maxLimit
   * 已随契约变更从出参中移除，前端不得再读取；需要总页数时用 Math.ceil(total / size)。
   */
  type PageResult<T> = {
    records: T[];
    total: number;
    size: number;
    current: number;
    /** 后端透传字段，前端不使用，仅保留类型对齐 */
    countId?: string | null;
  };

  type SysUserDto = {
    id?: string;
    tenantId?: string;
    deptId?: string;
    userName?: string;
    nickName?: string;
    userType?: string;
    email?: string;
    phone?: string;
    gender?: string;
    avatar?: string;
    password?: string;
    status?: string;
  };

  type SysRoleDto = {
    id?: string;
    tenantId?: string;
    roleName?: string;
    roleCode?: string;
    dataScope?: string;
    menuCheckStrictly?: string;
    deptCheckStrictly?: string;
    sort?: number;
    status?: string;
    remark?: string;
  };

  type SysMenuDto = {
    id?: string;
    tenantId?: string;
    parentId?: string;
    menuName?: string;
    path?: string;
    component?: string;
    parameters?: string;
    external?: string;
    cacheable?: string;
    menuType?: string;
    visible?: string;
    icon?: string;
    perms?: string;
    sort?: number;
    status?: string;
    remark?: string;
    children?: SysMenuDto[];
  };

  type SysDeptDto = {
    id?: string;
    tenantId?: string;
    parentId?: string;
    deptName?: string;
    leader?: string;
    phone?: string;
    email?: string;
    children?: SysDeptDto[];
  };

  type SysTenantDto = {
    id?: string;
    sort?: number;
    status?: string;
    remark?: string;
    tenantName?: string;
    tenantCode?: string;
    expireTime?: string;
    contactPerson?: string;
    contactPhone?: string;
    contactEmail?: string;
    domain?: string;
    maxUser?: number;
  };

  type SysPostDto = {
    id?: string;
    tenantId?: string;
    postCode?: string;
    postName?: string;
    sort?: number;
    status?: string;
    remark?: string;
  };

  type SysPermDto = {
    id?: string;
    tenantId?: string;
    parentId?: string;
    permCode?: string;
    permName?: string;
    sort?: number;
    status?: string;
    remark?: string;
    children?: SysPermDto[];
  };

  type SysOperLogDto = {
    id?: string;
    tenantId?: string;
    title?: string;
    businessType?: number;
    method?: string;
    requestMethod?: string;
    operatorType?: number;
    userName?: string;
    deptName?: string;
    operUrl?: string;
    operIp?: string;
    operLocation?: string;
    operParam?: string;
    jsonResult?: string;
    status?: number;
    errorMsg?: string;
    operTime?: string;
    costTime?: number;
  };

  type SysDictTypeDto = {
    id?: string;
    tenantId?: string;
    dictName?: string;
    dictType?: string;
    sort?: number;
    status?: string;
    remark?: string;
  };

  type SysDictDataDto = {
    id?: string;
    tenantId?: string;
    dictType?: string;
    dictLabel?: string;
    dictValue?: string;
    cssClass?: string;
    listClass?: string;
    defaulted?: string;
    sort?: number;
    status?: string;
    remark?: string;
  };

  type SysConfigDto = {
    id?: string;
    tenantId?: string;
    configName?: string;
    configKey?: string;
    configValue?: string;
    configType?: string;
    sort?: number;
    status?: string;
    remark?: string;
  };

  type SysNoticeDto = {
    id?: string;
    tenantId?: string;
    noticeTitle?: string;
    noticeContent?: string;
    noticeType?: string;
    status?: string;
    remark?: string;
    createdTime?: string;
  };

  type SysLoginLogDto = {
    id?: string;
    tenantId?: string;
    userId?: string;
    userName?: string;
    ipaddr?: string;
    loginLocation?: string;
    browser?: string;
    os?: string;
    status?: string;
    msg?: string;
    loginTime?: string;
  };

  type SysJobDto = {
    id?: string;
    tenantId?: string;
    jobName?: string;
    jobGroup?: string;
    invokeTarget?: string;
    cronExpression?: string;
    misfirePolicy?: string;
    concurrent?: string;
    status?: string;
    remark?: string;
    createdTime?: string;
  };

  type ServerInfoVO = {
    os?: {
      name?: string;
      arch?: string;
      version?: string;
    };
    cpu?: {
      name?: string;
      physicalCount?: number;
      logicalCount?: number;
      /** CPU 使用率（百分数）；-1 表示取不到真实值，页面应显示「未知」 */
      usage?: number;
      /** 系统负载（system load average）；-1 表示不支持 */
      loadAverage?: number;
    };
    mem?: {
      total?: number;
      used?: number;
      free?: number;
      usage?: number;
    };
    jvm?: {
      name?: string;
      version?: string;
      total?: number;
      used?: number;
      free?: number;
      max?: number;
      usage?: number;
      startTime?: string;
      uptime?: number;
    };
    disk?: {
      total?: number;
      used?: number;
      free?: number;
      usage?: number;
      /** 分区名，如 C:\ */
      name?: string;
      /** 分区绝对路径 */
      path?: string;
    };
    /** 所有磁盘分区信息（多分区）；disk 指向第一个分区，保留以兼容旧渲染 */
    disks?: {
      total?: number;
      used?: number;
      free?: number;
      usage?: number;
      /** 分区名，如 C:\ */
      name?: string;
      /** 分区绝对路径 */
      path?: string;
    }[];
  };

  /** 缓存监控：Redis 基础信息 */
  type CacheInfoVO = {
    /** Redis 版本，如 7.4.9 */
    version?: string;
    /** 运行模式，如 standalone */
    mode?: string;
    /** 已连接客户端数 */
    connectedClients?: number;
    /** 已用内存（人类可读），如 1.59M */
    usedMemoryHuman?: string;
    /** 内存峰值（人类可读） */
    usedMemoryPeakHuman?: string;
    /** 已用内存（字节） */
    usedMemory?: number;
    /** 配置的最大内存（字节），0 表示不限 */
    maxMemory?: number;
    /** 内存淘汰策略 */
    maxMemoryPolicy?: string;
    /** 当前库 key 总数 */
    dbSize?: number;
    /** 命中率（百分数，保留一位小数）；-1 表示未知（尚无访问） */
    hitRate?: number;
    keyspaceHits?: number;
    keyspaceMisses?: number;
    /** 每秒操作数 */
    instantaneousOpsPerSec?: number;
    /** 运行时长（秒） */
    uptimeInSeconds?: number;
    /** 运行时长（人类可读） */
    uptimeHuman?: string;
    /** Redis 不可用（连接失败或 INFO 为空）时为 true */
    unavailable?: boolean;
    /** unavailable 时的原因 */
    errorMessage?: string;
    /** 按业务前缀分组的 key 数量 */
    prefixStats?: {
      prefix?: string;
      count?: number;
    }[];
    /** 按 db 维度统计的 key 数量 */
    dbStats?: {
      db?: string;
      keys?: number;
      expires?: number;
    }[];
  };

  /** 缓存监控：key 列表查询结果 */
  type CacheKeyVO = {
    /** 实际生效的匹配表达式 */
    pattern?: string;
    /** 本次返回的 key 数量 */
    size?: number;
    /** 是否因达到 limit 而截断 */
    truncated?: boolean;
    /** 本次返回的条数上限 */
    limit?: number;
    keys?: {
      key?: string;
      /** Redis 类型：string / list / set / zset / hash 等 */
      type?: string;
      /** 剩余 TTL（秒）；-1 永不过期，-2 已过期或不存在 */
      ttl?: number;
      /** TTL 人类可读 */
      ttlHuman?: string;
    }[];
  };

  type ErrorResponse = {
    errorCode: string;
    errorMessage?: string;
    success?: boolean;
  };
}
