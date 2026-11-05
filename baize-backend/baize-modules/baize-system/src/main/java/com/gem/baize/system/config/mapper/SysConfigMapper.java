package com.gem.baize.system.config.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gem.baize.system.config.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 参数配置 Mapper。
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 「当前租户 + 平台兜底」合并解析参数值（2026-11-05 P2：参数消费机制）。
     * <p>
     * 必须绕过租户行级拦截器（{@code @InterceptorIgnore(tenantLine="true")}）：
     * 平台内置参数挂在平台租户下，若不豁免，普通租户永远查不到兜底值。
     * 租户范围由本 SQL 手工限定（tenant_id IN (当前, 平台)），不会跨租户泄露；
     * 平台租户的行统一作为兜底域（参数表无 is_builtin 列，config_type='Y' 仅管理页展示用）。
     * 只返回启用（status='0'）且未删除（deleted='0'）的行，租户自定义优先于平台。
     *
     * @param tenantId         当前租户 ID
     * @param platformTenantId 平台租户 ID（nil UUID）
     * @param configKey        参数键名
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("""
            <script>
            SELECT config_value FROM "baize-system".sys_config
             WHERE config_key = #{configKey}
               AND deleted = '0' AND status = '0'
               AND tenant_id IN (#{tenantId}, #{platformTenantId})
             ORDER BY CASE WHEN tenant_id = #{tenantId} THEN 0 ELSE 1 END
             LIMIT 1
            </script>
            """)
    String selectVisibleValue(@Param("tenantId") String tenantId,
                              @Param("platformTenantId") String platformTenantId,
                              @Param("configKey") String configKey);
}