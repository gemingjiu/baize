import { describe, expect, it } from 'vitest';
import {
  transformDeptTree,
  transformMenuTree,
  transformTree,
} from '@/utils/tree';

describe('transformTree', () => {
  const source = [
    {
      id: '1',
      name: '根',
      children: [{ id: '2', name: '子', children: [] }],
    },
  ];

  it('映射 label/value 并递归子节点', () => {
    expect(transformTree(source, { labelKey: 'name', valueKey: 'id' })).toEqual(
      [{ label: '根', value: '1', children: [{ label: '子', value: '2' }] }],
    );
  });

  it('filter 对每个层级生效，被过滤的子节点不进入 children', () => {
    // 根节点有 children 字段（原数组非空），过滤后子节点列表为空 → children: []
    expect(
      transformTree(source, {
        labelKey: 'name',
        valueKey: 'id',
        filter: (item) => item.id !== '2',
      }),
    ).toEqual([{ label: '根', value: '1', children: [] }]);
  });
});

describe('transformMenuTree / transformDeptTree', () => {
  it('菜单树过滤 menuType=2 的按钮项', () => {
    const menus = [
      { id: 'm1', menuName: '系统', menuType: '1' },
      { id: 'm2', menuName: '新增按钮', menuType: '2' },
    ] as API.SysMenuDto[];
    expect(transformMenuTree(menus)).toEqual([{ label: '系统', value: 'm1' }]);
  });

  it('部门树按 deptName/id 映射', () => {
    const depts = [
      { id: 'd1', deptName: '总部', children: [] },
    ] as API.SysDeptDto[];
    expect(transformDeptTree(depts)).toEqual([{ label: '总部', value: 'd1' }]);
  });
});
