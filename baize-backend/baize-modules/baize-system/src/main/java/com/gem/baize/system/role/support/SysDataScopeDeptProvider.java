package com.gem.baize.system.role.support;

import com.gem.baize.common.core.constant.DataScopeConstant;
import com.gem.baize.common.database.handler.DataScopeDeptProvider;
import com.gem.baize.system.dept.entity.SysDept;
import com.gem.baize.system.dept.mapper.SysDeptMapper;
import com.gem.baize.system.role.entity.SysRole;
import com.gem.baize.system.role.entity.SysRoleDept;
import com.gem.baize.system.role.mapper.SysRoleDeptMapper;
import com.gem.baize.system.role.mapper.SysRoleMapper;
import com.gem.baize.system.user.entity.SysUser;
import com.gem.baize.system.user.mapper.SysUserMapper;
import com.gem.baize.system.user.service.SysUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据权限的"部门解析"实现：把 {@code (userId, dataScope)} 翻译成可见部门ID集合。
 * <p>
 * 放在 baize-system 是因为只有这里能查 sys_user / sys_role_dept / sys_dept；
 * common-database 通过 {@link DataScopeDeptProvider} 接口反向依赖本实现，
 * 由 {@code MybatisPlusConfig} 用 {@code ObjectProvider} 注入。
 *
 * <p><b>四种需要下推部门的场景：</b>
 * <ul>
 *   <li>{@code 2 自定义}：按用户全部角色逐角色解析（各角色按自身档位）后取并集。</li>
 *   <li>{@code 3 本部门}：取用户所属 dept_id。</li>
 *   <li>{@code 4 本部门及以下}：以用户 dept_id 为根，递归取整棵子树。</li>
 *   <li>{@code 1 全部 / 5 仅本人}：不需要部门集合，分别由拦截器放行 / 下推 create_by。</li>
 * </ul>
 */
@Slf4j
@Component
public class SysDataScopeDeptProvider implements DataScopeDeptProvider {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Override
    public List<String> resolveDeptIds(String userId, String dataScope) {
        if (userId == null || userId.isBlank()) {
            return Collections.emptyList();
        }

        if (DataScopeConstant.DATA_SCOPE_CUSTOM.equals(dataScope)) {
            return resolveCustomDeptIds(userId);
        }
        if (DataScopeConstant.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
            String deptId = resolveDeptId(userId);
            if (deptId == null) {
                return Collections.emptyList();
            }
            return resolveDeptTree(deptId);
        }
        if (DataScopeConstant.DATA_SCOPE_DEPT.equals(dataScope)) {
            String deptId = resolveDeptId(userId);
            return deptId == null ? Collections.emptyList() : List.of(deptId);
        }
        // 1 全部 / 5 仅本人：不需要部门集合
        return Collections.emptyList();
    }

    @Override
    public String resolveDeptId(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getDeptId() == null || user.getDeptId().isBlank()) {
            log.warn("用户[{}]不存在或未设置部门，无法解析本部门数据权限", userId);
            return null;
        }
        return user.getDeptId();
    }

    /**
     * 自定义数据权限：按用户全部角色逐角色解析部门并取并集。
     * <p>
     * 多角色时取并集——数据权限是"可见范围"，取交集会让"多角色"反而看得更少，与直觉相悖。
     * 各角色按自身 data_scope 解析：2 自定义取 sys_role_dept 配置；3 本部门取用户部门；
     * 4 本部门及以下取部门子树。聚合签发后 2/3/4 会被归一为 2 下发，这里按角色真实档位
     * 还原可见范围，避免"本部门"角色在聚合档下被错误当作"未配置自定义部门"丢弃。
     */
    private List<String> resolveCustomDeptIds(String userId) {
        List<String> roleIds = sysUserRoleService.getRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        Set<String> deptIds = new LinkedHashSet<>();
        for (String roleId : roleIds) {
            deptIds.addAll(resolveRoleDeptIds(userId, roleId));
        }
        return new ArrayList<>(deptIds);
    }

    /**
     * 解析单个角色在数据权限下的部门集合：
     * 自定义档取 sys_role_dept 配置，本部门/本部门及以下档取用户部门（及子树），
     * 全部/仅本人档对部门维度无贡献。
     */
    private List<String> resolveRoleDeptIds(String userId, String roleId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysRole> roleWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        roleWrapper.select(SysRole::getDataScope);
        roleWrapper.eq(SysRole::getId, roleId);
        SysRole role = sysRoleMapper.selectOne(roleWrapper);
        String scope = role == null ? null : role.getDataScope();
        if (DataScopeConstant.DATA_SCOPE_CUSTOM.equals(scope)) {
            return getDeptIdsByRoleId(roleId);
        }
        if (DataScopeConstant.DATA_SCOPE_DEPT.equals(scope)) {
            String deptId = resolveDeptId(userId);
            return deptId == null ? Collections.emptyList() : List.of(deptId);
        }
        if (DataScopeConstant.DATA_SCOPE_DEPT_AND_CHILD.equals(scope)) {
            String deptId = resolveDeptId(userId);
            if (deptId == null) {
                return Collections.emptyList();
            }
            return resolveDeptTree(deptId);
        }
        // 1 全部 / 5 仅本人 / 未知值：部门维度无贡献
        return Collections.emptyList();
    }

    private List<String> getDeptIdsByRoleId(String roleId) {
        if (roleId == null || roleId.isBlank()) {
            return Collections.emptyList();
        }
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysRoleDept> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(SysRoleDept::getRoleId, roleId);
        return sysRoleDeptMapper.selectList(wrapper).stream()
                .map(SysRoleDept::getDeptId)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .toList();
    }

    /**
     * 取以 rootDeptId 为根的整棵部门子树（含根自身）。
     * <p>
     * 一次性拉全表再在内存里建父子索引，而不是逐层递归查库：
     * 部门量级是几十到几百，一次查询 + 内存 DFS 比 N 次库查询快得多，也避免递归栈深。
     */
    private List<String> resolveDeptTree(String rootDeptId) {
        List<SysDept> all = sysDeptMapper.selectList(null);
        if (CollectionUtils.isEmpty(all)) {
            return List.of(rootDeptId);
        }

        Map<String, List<SysDept>> childrenByParent = new HashMap<>();
        for (SysDept dept : all) {
            String parentId = dept.getParentId() == null || dept.getParentId().isBlank() ? "0" : dept.getParentId();
            childrenByParent.computeIfAbsent(parentId, k -> new ArrayList<>()).add(dept);
        }

        Set<String> result = new LinkedHashSet<>();
        collectChildren(rootDeptId, childrenByParent, result);
        return new ArrayList<>(result);
    }

    /**
     * 深度优先收集子树节点ID。
     * <p>
     * 用 visited 兜底防御脏数据造成的父子环（A.parent=B 且 B.parent=A）：
     * 一旦成环，纯递归会栈溢出并拖垮整个查询。
     */
    private void collectChildren(String deptId, Map<String, List<SysDept>> childrenByParent, Set<String> visited) {
        if (deptId == null || !visited.add(deptId)) {
            return;
        }
        List<SysDept> children = childrenByParent.get(deptId);
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (SysDept child : children) {
            collectChildren(child.getId(), childrenByParent, visited);
        }
    }
}
