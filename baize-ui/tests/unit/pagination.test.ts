import { describe, expect, it, vi } from 'vitest';
import {
  FETCH_ALL_PAGE_SIZE,
  fetchAllPages,
  toPageQuery,
  totalPages,
} from '@/utils/pagination';

describe('totalPages', () => {
  it('按 total/size 向上取整', () => {
    expect(totalPages(101, 10)).toBe(11);
    expect(totalPages(100, 10)).toBe(10);
    expect(totalPages(0, 10)).toBe(0);
  });

  it('非法入参返回 0', () => {
    expect(totalPages(null, 10)).toBe(0);
    expect(totalPages(100, 0)).toBe(0);
    expect(totalPages(Number.NaN, 10)).toBe(0);
  });
});

describe('toPageQuery', () => {
  it('剔除表格专用参数与空值', () => {
    expect(
      toPageQuery({
        current: 1,
        pageSize: 10,
        keyword: 'abc',
        username: 'admin',
        status: undefined,
        deptId: null,
        remark: '',
        age: 0,
      }),
    ).toEqual({ username: 'admin', age: 0 });
  });

  it('空入参返回空对象', () => {
    expect(toPageQuery()).toEqual({});
    expect(toPageQuery(null)).toEqual({});
  });
});

describe('fetchAllPages', () => {
  it('按页拉取并汇总，遇到失败页即停止', async () => {
    const fetcher = vi
      .fn()
      .mockResolvedValueOnce({
        success: true,
        data: { records: [{ id: 1 }, { id: 2 }], total: 4, size: 2 },
      })
      .mockResolvedValueOnce({
        success: true,
        data: { records: [{ id: 3 }, { id: 4 }], total: 4, size: 2 },
      });

    const rows = await fetchAllPages(fetcher as never, 2);
    expect(rows).toEqual([{ id: 1 }, { id: 2 }, { id: 3 }, { id: 4 }]);
    expect(fetcher).toHaveBeenCalledTimes(2);
    expect(fetcher).toHaveBeenNthCalledWith(1, { current: 1, size: 2 });
    expect(fetcher).toHaveBeenNthCalledWith(2, { current: 2, size: 2 });
  });

  it('首页失败返回空数组，不继续翻页', async () => {
    const fetcher = vi.fn().mockResolvedValueOnce({ success: false });
    expect(await fetchAllPages(fetcher as never)).toEqual([]);
    expect(fetcher).toHaveBeenCalledTimes(1);
  });

  it('默认单页大小来自常量', async () => {
    const fetcher = vi.fn().mockResolvedValueOnce({
      success: true,
      data: { records: [], total: 0, size: FETCH_ALL_PAGE_SIZE },
    });
    await fetchAllPages(fetcher as never);
    expect(fetcher).toHaveBeenCalledWith({
      current: 1,
      size: FETCH_ALL_PAGE_SIZE,
    });
  });
});
