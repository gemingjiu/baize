import { useEffect, useState } from 'react';

/**
 * 权限Hook
 */
export function usePermission() {
  const [permissions, setPermissions] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 从localStorage获取权限列表
    const savedPermissions = localStorage.getItem('permissions');
    if (savedPermissions) {
      try {
        setPermissions(JSON.parse(savedPermissions));
      } catch (e) {
        console.error('解析权限列表失败', e);
      }
    }
    setLoading(false);
  }, []);

  /**
   * 检查是否有指定权限
   */
  const hasPermission = (permission: string | string[]) => {
    if (!permission) return true;
    if (Array.isArray(permission)) {
      return permission.some(p => permissions.includes(p));
    }
    return permissions.includes(permission);
  };

  /**
   * 检查是否有所有指定权限
   */
  const hasAllPermissions = (permissionList: string[]) => {
    if (!permissionList || permissionList.length === 0) return true;
    return permissionList.every(p => permissions.includes(p));
  };

  return {
    permissions,
    setPermissions,
    hasPermission,
    hasAllPermissions,
    loading,
  };
}

export default usePermission;
