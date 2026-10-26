import { useCallback, useState } from 'react';

/**
 * 列表页「导出 Excel」按钮的通用状态封装。
 *
 * 只做两件事，让 4 个列表页的导出行为完全一致：
 * 1. 提供 loading 状态并加锁，避免用户重复点击导致重复下载；
 * 2. 把导出函数的异常统一吞掉（提示已在 services 里弹出），
 *    保证 finally 一定能解锁，不会让按钮永久卡在 loading。
 *
 * @param exporter 对应页面的导出服务函数（如 exportUsers）
 * @returns [导出中标志, 触发导出的回调]
 *
 * @example
 * const [exporting, handleExport] = useExportXlsx(exportUsers);
 * <Button loading={exporting} onClick={() => handleExport(query)}>导出</Button>
 */
export function useExportXlsx<T>(
  exporter: (params: Partial<T>) => Promise<void>,
): [boolean, (params?: Partial<T>) => Promise<void>] {
  const [exporting, setExporting] = useState(false);

  const run = useCallback(
    async (params?: Partial<T>) => {
      // 正在导出时直接忽略后续点击，避免并发重复下载
      if (exporting) {
        return;
      }
      setExporting(true);
      try {
        await exporter(params ?? {});
      } catch {
        // 失败提示由 services 层的 message.error 统一负责，这里只保证状态复位
      } finally {
        setExporting(false);
      }
    },
    [exporter, exporting],
  );

  return [exporting, run];
}
