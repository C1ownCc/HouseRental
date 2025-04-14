import { useUserStore } from '@/stores'

/**
 * 检查是否有权限
 * @param {string|string[]} permission 权限标识或权限标识数组
 * @returns {boolean} 是否有权限
 */
export function hasPermission(permission) {
  const userStore = useUserStore()
  const { userInfo } = userStore
  const permissions = userInfo.permissions || []

  if (typeof permission === 'string') {
    return permissions.includes(permission)
  }

  if (Array.isArray(permission)) {
    return permission.some(item => permissions.includes(item))
  }

  return false
}

/**
 * 检查是否是管理员
 * @returns {boolean} 是否是管理员
 */
export function isAdmin() {
  const userStore = useUserStore()
  const { userInfo } = userStore
  return userInfo.role === 'admin'
}

/**
 * 检查是否是房东
 * @returns {boolean} 是否是房东
 */
export function isLandlord() {
  const userStore = useUserStore()
  const { userInfo } = userStore
  return userInfo.role === 'landlord'
}

/**
 * 检查是否有访问页面的权限
 * @param {string[]} requiredPermissions 需要的权限
 * @returns {boolean} 是否有权限
 */
export function hasPagePermission(requiredPermissions) {
  if (!requiredPermissions || requiredPermissions.length === 0) {
    return true
  }

  if (isAdmin()) {
    return true
  }

  return hasPermission(requiredPermissions)
}

/**
 * 检查是否有执行操作的权限
 * @param {string[]} requiredPermissions 需要的权限
 * @returns {boolean} 是否有权限
 */
export function hasActionPermission(requiredPermissions) {
  if (!requiredPermissions || requiredPermissions.length === 0) {
    return true
  }

  return hasPermission(requiredPermissions)
}

/**
 * 过滤没有权限的路由
 * @param {Array} routes 路由配置
 * @returns {Array} 过滤后的路由
 */
export function filterRoutes(routes) {
  return routes.filter(route => {
    if (route.meta && route.meta.permissions) {
      return hasPagePermission(route.meta.permissions)
    }
    return true
  })
} 