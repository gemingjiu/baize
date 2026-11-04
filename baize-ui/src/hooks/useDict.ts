import { useCallback, useEffect, useState } from 'react';
import { getAllDictData } from '@/services/system/dict';

/** 字典数据：key = dict_type，value = 该类型合并解析后的字典项列表 */
export type DictMap = Record<string, API.SysDictDataDto[]>;

/**
 * 模块级全局缓存：同一次会话内所有组件共享一份字典数据，只请求一次
 * （后端 /system/dict/data/all 一次返回全部；登录即可见，无需字典查询权限）。
 */
let dictCache: DictMap | null = null;
let inflight: Promise<DictMap> | null = null;

function loadDicts(): Promise<DictMap> {
  if (dictCache) {
    return Promise.resolve(dictCache);
  }
  if (!inflight) {
    inflight = getAllDictData()
      .then((res) => {
        if (res.success && res.data) {
          dictCache = res.data;
          return dictCache;
        }
        throw new Error('字典数据加载失败');
      })
      .finally(() => {
        inflight = null;
      });
  }
  return inflight;
}

/**
 * 强制刷新全局字典缓存（字典管理页增删改后调用）。
 * 返回最新字典数据，调用方可用于同步本地 state。
 */
export async function refreshDicts(): Promise<DictMap> {
  dictCache = null;
  return loadDicts();
}

/**
 * 全局字典 Hook：登录后任意页面首次使用时自动拉取一次并全局缓存，
 * 字典到达后触发一次重渲染，列/表单枚举自动补齐（首帧为空枚举，可接受）。
 *
 * 合并解析语义由后端保证：租户自定义项 → 平台内置项兜底。
 *
 * 用法：
 *   const { toValueEnum, getDictOptions, dictLabel } = useDict();
 *   // 表格列：valueEnum: toValueEnum('sys_normal_disable', { '0': 'Success', '1': 'Error' })
 *   // 表单：  options: getDictOptions('sys_user_sex')
 *   // 回显：  dictLabel('sys_oper_type', record.businessType)
 */
export function useDict() {
  const [dicts, setDicts] = useState<DictMap>(dictCache ?? {});

  useEffect(() => {
    let mounted = true;
    if (!dictCache) {
      loadDicts()
        .then((data) => {
          if (mounted) {
            setDicts(data);
          }
        })
        .catch(() => {
          // 拉取失败降级为空字典：页面仍可打开，枚举为空不阻塞业务
        });
    }
    return () => {
      mounted = false;
    };
  }, []);

  /**
   * 生成 ProTable valueEnum：{ [dictValue]: { text: dictLabel, status? } }。
   * statusMap 可选，用于给个别值补充徽标颜色（如 { '0': 'Success', '1': 'Error' }），
   * 只影响展示，不影响数据来源。
   */
  const toValueEnum = useCallback(
    (dictType: string, statusMap?: Record<string, string>) => {
      const result: Record<string, { text: string; status?: string }> = {};
      (dicts[dictType] ?? []).forEach((item) => {
        const value = item.dictValue ?? '';
        const status = statusMap?.[value];
        result[value] = status
          ? { text: item.dictLabel ?? '', status }
          : { text: item.dictLabel ?? '' };
      });
      return result;
    },
    [dicts],
  );

  /**
   * 生成表单 options：{ label, value }[]（ProFormSelect / Select 等）。
   */
  const getDictOptions = useCallback(
    (dictType: string) =>
      (dicts[dictType] ?? []).map((item) => ({
        label: item.dictLabel ?? '',
        value: item.dictValue ?? '',
      })),
    [dicts],
  );

  /**
   * 按 value 查字典标签（用于自定义 render 的文本回显），查不到回退原值。
   */
  const dictLabel = useCallback(
    (dictType: string, value?: string) =>
      (dicts[dictType] ?? []).find((item) => item.dictValue === value)?.dictLabel ?? value ?? '',
    [dicts],
  );

  /**
   * 强制刷新并同步本组件状态（字典管理页增删改后调用，
   * 让全局缓存立即生效，其他页面下次挂载即取到新数据）。
   */
  const reload = useCallback(async () => {
    const fresh = await refreshDicts();
    setDicts(fresh);
    return fresh;
  }, []);

  return { dicts, toValueEnum, getDictOptions, dictLabel, reload };
}

export default useDict;