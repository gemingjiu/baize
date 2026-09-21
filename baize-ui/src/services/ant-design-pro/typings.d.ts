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

  type MybatisPage<T> = {
    records: T[];
    total: number;
    size: number;
    current: number;
    pages: number;
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

  type ErrorResponse = {
    errorCode: string;
    errorMessage?: string;
    success?: boolean;
  };
}
