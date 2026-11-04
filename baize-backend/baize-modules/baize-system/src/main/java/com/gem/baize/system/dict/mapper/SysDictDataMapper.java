package com.gem.baize.system.dict.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gem.baize.system.dict.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 查询「当前租户自有 + 平台内置」两域合并的可见字典数据（2026-11-05 字典模型）。
     * <p>
     * 必须绕过租户行级拦截器（{@code @InterceptorIgnore(tenantLine="true")}）：
     * 平台内置字典行挂在平台租户下，若不豁免，普通租户永远查不到兜底数据。
     * 租户范围由本 SQL 手工限定（tenant_id=#{tenantId}），不会造成跨租户泄露；
     * 平台兜底行额外要求 is_builtin='1'，平台租户自建的非内置行不外泄给其他租户。
     * 只返回启用（status='0'）且未删除（deleted='0'）的行。
     *
     * @param tenantId          当前租户 ID
     * @param platformTenantId  平台租户 ID（nil UUID）
     * @param dictType          字典类型；null 表示查全部类型（/all 批量接口用）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("""
            <script>
            SELECT * FROM "baize-system".sys_dict_data
             WHERE deleted = '0' AND status = '0'
               AND (tenant_id = #{tenantId}
                    OR (tenant_id = #{platformTenantId} AND is_builtin = '1'))
            <if test="dictType != null and dictType != ''">AND dict_type = #{dictType}</if>
             ORDER BY dict_type, sort
            </script>
            """)
    List<SysDictData> selectVisible(@Param("tenantId") String tenantId,
                                    @Param("platformTenantId") String platformTenantId,
                                    @Param("dictType") String dictType);
}