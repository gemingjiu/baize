/**
 * 导出文件（xlsx 二进制流）通用工具
 *
 * 背景：导出接口返回的是二进制流，不能走项目里统一的 request()
 * （responseInterceptors / errorThrower 会按 JSON 响应体解析并抛业务错误），
 * 因此这里用原生 fetch + Blob 单独处理：
 * 1. 带上与 requestInterceptors 一致的 Authorization 头（Bearer <accessToken>）；
 *    租户信息由网关依据 JWT 自行解析，前端不再额外拼 X-Tenant-Id；
 * 2. 从 Content-Disposition 解析文件名（后端 url 编码），解析失败时回退默认名；
 * 3. 用 createObjectURL + 临时 <a download> 触发浏览器下载，完成后释放 URL。
 *
 * 文件内容：POST 请求，请求体为查询 DTO 的 JSON 序列化。
 *
 * 本工具只负责「发请求 + 落盘下载」，不做任何 UI 提示：
 * 成功/失败以返回值体现，由页面统一弹 message，避免多处重复提示。
 */

import { getToken } from '@/utils/auth';

/**
 * 从 Content-Disposition 响应头解析文件名。
 *
 * 支持后端常用的两种写法：
 * - `attachment;filename*=utf-8''%E7%94%A8%E6%88%B7%E5%88%97%E8%A1%A8.xlsx`（url 编码）
 * - `attachment;filename="用户列表.xlsx"`
 *
 * 解析不出文件名时返回 undefined，由调用方使用默认文件名。
 */
function parseFilename(contentDisposition?: string | null): string | undefined {
  if (!contentDisposition) {
    return undefined;
  }
  try {
    // 优先取 filename*=utf-8''xxx 中的 url 编码部分
    const encoded = /filename\*\s*=\s*[^']*''([^;]+)/i.exec(contentDisposition);
    if (encoded?.[1]) {
      return decodeURIComponent(encoded[1].trim());
    }
    // 退化到 filename=xxx（可能带引号）
    const plain = /filename\s*=\s*"?([^";]+)"?/i.exec(contentDisposition);
    if (plain?.[1]) {
      return decodeURIComponent(plain[1].trim());
    }
  } catch {
    // 编码非法时按解析失败处理，交由调用方回退默认文件名
    return undefined;
  }
  return undefined;
}

/**
 * 触发浏览器下载 Blob。
 *
 * @param blob 服务端返回的二进制内容
 * @param filename 下载时的文件名
 */
function triggerDownload(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  // 释放 object URL，避免内存泄漏
  URL.revokeObjectURL(url);
}

/**
 * 导出文件（对接后端 xlsx 导出接口）。
 *
 * 只负责发请求与触发下载，不弹提示：调用方根据返回值决定提示文案，
 * 保证页面侧的 UX（loading / success / error）保持一致。
 *
 * @param url 完整接口地址（含 API_VERSION 前缀）
 * @param params 查询条件，作为 JSON 请求体传给后端；传 {} 表示全量导出
 * @param defaultFilename Content-Disposition 解析失败时使用的默认文件名（含 .xlsx）
 * @returns 导出成功返回 true，失败返回 false（网络异常 / 非 2xx 响应）
 */
export async function downloadExportFile<T extends object>(
  url: string,
  params: Partial<T>,
  defaultFilename: string,
): Promise<boolean> {
  try {
    const token = getToken();
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(url, {
      method: 'POST',
      headers,
      body: JSON.stringify(params ?? {}),
    });

    // HTTP 非 2xx：后端回的是 JSON 错误体，没有可下载的文件
    if (!response.ok) {
      return false;
    }

    const blob = await response.blob();
    const filename = parseFilename(response.headers.get('Content-Disposition'));
    triggerDownload(blob, filename || defaultFilename);
    return true;
  } catch {
    // 网络中断、跨域失败等
    return false;
  }
}
