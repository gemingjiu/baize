package com.gem.baize.system.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gem.baize.api.system.tenant.domain.dto.SysTenantDto;
import com.gem.baize.system.tenant.entity.SysTenant;

import java.util.List;

/**
 * 租户服务类接口
 */
public interface SysTenantService extends IService<SysTenant> {

    SysTenantDto getById(String id);

    void create(SysTenantDto sysTenantDto);

    void updateById(SysTenantDto sysTenantDto);

    void removeById(String id);

    Page<SysTenantDto> page(Page<SysTenant> page, SysTenantDto sysTenantDto);

    /**
     * 根据租户编码或域名获取租户信息
     * @param tenant 租户编码或域名
     * @return 租户信息
     */
    SysTenantDto getByTenantCodeOrDomain(String tenant);

    /**
     * 查询可用租户列表（登录页租户下拉框数据源）。
     * <p>
     * 只返回未删除且状态正常的租户，按 sort、id 升序；不分页，租户量级很小。
     *
     * @return 可用租户列表
     */
    List<SysTenantDto> listAvailable();

    /**
     * 判断租户ID是否存在（未删除且状态正常）
     *
     * @param id 租户ID
     * @return 存在返回 true
     */
    boolean existsById(String id);

    /**
     * 按当前请求租户上下文查询租户行（2026-11-05 P4：用户配额等治理校验取数）。
     * <p>
     * 上下文缺失时兜底平台租户；查不到返回 null（调用方自行决定放行/拦截）。
     */
    SysTenantDto getByCurrentTenantId();
}
