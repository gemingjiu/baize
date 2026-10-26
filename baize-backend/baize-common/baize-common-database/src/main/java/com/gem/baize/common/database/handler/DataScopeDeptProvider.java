package com.gem.baize.common.database.handler;

import java.util.List;

/**
 * 数据权限的"部门解析"SPI。
 * <p>
 * 拦截层（baize-common-database）无法直接查 sys_user / sys_role_dept / sys_dept，
 * 因此把"给定用户 + 数据范围 → 可见部门集合"的解析能力下放给业务模块（baize-system）实现，
 * 由 Spring 容器注入。
 * <p>
 * <b>未注册实现时的行为：</b>拦截器直接放弃本次过滤（当作"全部可见"），
 * 而不是退化成"什么都查不到"——因为比起静默地让业务查询全部返空，
 * 更合理的是让"没接入数据权限的服务"维持原有行为。
 * 接入方只需在 baize-system 提供一个本接口的 Bean 并把
 * {@code baize.data-scope.enabled} 置为 true 即可生效。
 */
public interface DataScopeDeptProvider {

    /**
     * 解析用户在给定数据范围下的部门ID集合。
     *
     * @param userId    当前用户ID
     * @param dataScope 数据范围取值，见 DataScopeConstant
     * @return 部门ID集合；无实现时返回空集合
     */
    List<String> resolveDeptIds(String userId, String dataScope);

    /**
     * 解析用户所属部门ID（用于"本部门数据权限"）。
     *
     * @param userId 当前用户ID
     * @return 部门ID，无法解析时返回 null
     */
    String resolveDeptId(String userId);
}
