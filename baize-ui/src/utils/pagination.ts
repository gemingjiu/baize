/**
 * 分页契约工具
 *
 * 后端出参已从 MyBatis-Plus `Page` 收敛为项目自有 `PageResult`：
 *   { records, total, size, current, countId }
 * 其中 `pages`、`orders`、`optimizeCountSql`、`searchCount`、`maxLimit` 均已移除。
 * 因此前端：
 * - 总页数只能通过 Math.ceil(total / size) 计算（见 totalPages）；
 * - 需要「全量」数据时用 fetchAllPages 按页拉取，不要再写死 size: 1000；
 * - ProTable 的查询参数需要转成后端查询体时统一走 toPageQuery，避免散落的 any 断言。
 */

/** 表格默认分页大小 */
export const DEFAULT_PAGE_SIZE = 10;

/** 全量拉取时的单页大小（后端 PageResult.size 会回传真实值） */
export const FETCH_ALL_PAGE_SIZE = 200;

/** 全量拉取的硬保护，避免后端 total 异常时无限翻页 */
const MAX_FETCH_ALL_PAGES = 100;

/**
 * ProTable 自身消费、不属于业务查询条件的分页/交互参数。
 * 这些键不能透传到后端 DTO 请求体里。
 */
const TABLE_ONLY_KEYS = new Set([
  'current',
  'pageSize',
  'pageNum',
  'keyword',
  'sortField',
  'sortOrder',
  'filter',
  '_timestamp',
]);

/**
 * 由 total / size 计算总页数（后端不再返回 pages）。
 * total 或 size 非法时返回 0。
 */
export function totalPages(
  total?: number | null,
  size?: number | null,
): number {
  if (typeof total !== 'number' || !Number.isFinite(total) || total <= 0) {
    return 0;
  }
  if (typeof size !== 'number' || !Number.isFinite(size) || size <= 0) {
    return 0;
  }
  return Math.ceil(total / size);
}

/**
 * 把 ProTable 的 request 参数转成后端查询体。
 *
 * - 剔除 current/pageSize/keyword 等表格专用参数；
 * - 丢弃 undefined / null / 空字符串，避免覆盖后端默认查询条件；
 * - 唯一一次类型收敛集中在此处，页面里不再写 `rest as API.XxxDto`。
 */
export function toPageQuery<T extends object>(
  params?: Record<string, unknown> | null,
): Partial<T> {
  const query: Record<string, unknown> = {};
  if (!params) {
    return query as Partial<T>;
  }
  Object.entries(params).forEach(([key, value]) => {
    if (TABLE_ONLY_KEYS.has(key)) {
      return;
    }
    if (value === undefined || value === null || value === '') {
      return;
    }
    query[key] = value;
  });
  return query as Partial<T>;
}

/** 分页接口的通用签名（与 services/system/*.ts 中的 page 方法一致） */
export type PageFetcher<T> = (
  params: API.PageParams,
) => Promise<API.ApiResult<API.PageResult<T>>>;

/**
 * 按页拉取全量数据（用于树形下拉、全量角色/权限列表等场景）。
 *
 * 页码边界完全依赖 total / size：
 * 1. 先取第 1 页，拿到 size 与 total；
 * 2. 总页数 = Math.ceil(total / size)；
 * 3. 逐页累加，中途失败或某页返回空即停止。
 */
export async function fetchAllPages<T>(
  fetcher: PageFetcher<T>,
  pageSize: number = FETCH_ALL_PAGE_SIZE,
): Promise<T[]> {
  const size = Math.max(1, Math.floor(pageSize));
  const first = await fetcher({ current: 1, size });
  if (!first?.success || !first.data) {
    return [];
  }
  const records: T[] = Array.isArray(first.data.records)
    ? [...first.data.records]
    : [];
  const actualSize =
    typeof first.data.size === 'number' && first.data.size > 0
      ? first.data.size
      : size;
  const total =
    typeof first.data.total === 'number' ? first.data.total : records.length;
  const last = Math.min(totalPages(total, actualSize), MAX_FETCH_ALL_PAGES);

  for (let current = 2; current <= last; current += 1) {
    const res = await fetcher({ current, size });
    if (!res?.success || !res.data) {
      break;
    }
    const pageRecords = Array.isArray(res.data.records) ? res.data.records : [];
    if (pageRecords.length === 0) {
      break;
    }
    records.push(...pageRecords);
  }
  return records;
}
