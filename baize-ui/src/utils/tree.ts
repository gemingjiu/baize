/**
 * 通用树形数据转换工具
 */

/**
 * 将后端返回的树形数据转换为前端组件需要的格式
 * @param list 后端数据列表
 * @param config 转换配置
 * @returns 转换后的树形数据
 */
export function transformTree<T extends { children?: T[] }>(
  list: T[],
  config: {
    /** label 字段映射 */
    labelKey: keyof T;
    /** value 字段映射 */
    valueKey: keyof T;
    /** 过滤条件 */
    filter?: (item: T) => boolean;
  },
): any[] {
  return list
    .filter((item) => (config.filter ? config.filter(item) : true))
    .map((item) => {
      const result: any = {
        label: item[config.labelKey],
        value: item[config.valueKey],
      };
      const children = item.children;
      if (children && children.length > 0) {
        result.children = transformTree(children, config);
      }
      return result;
    });
}

/**
 * 将后端部门树转换为 ProFormTreeSelect 需要的格式
 * @param depts 后端部门数据
 * @returns 转换后的树形数据
 */
export function transformDeptTree(depts: API.SysDeptDto[]): any[] {
  return transformTree(depts, {
    labelKey: 'deptName',
    valueKey: 'id',
  });
}

/**
 * 将后端菜单树转换为 ProFormTreeSelect 需要的格式
 * @param menus 后端菜单数据
 * @returns 转换后的树形数据
 */
export function transformMenuTree(menus: API.SysMenuDto[]): any[] {
  return transformTree(menus, {
    labelKey: 'menuName',
    valueKey: 'id',
    filter: (item) => item.menuType !== '2',
  });
}
