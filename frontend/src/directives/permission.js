import { useUserStore } from '@/stores'

function checkPermission(el, binding) {
  const { value } = binding
  const userStore = useUserStore()
  const { userInfo } = userStore

  if (value && value instanceof Array) {
    if (value.length > 0) {
      const permissions = userInfo.permissions || []
      const hasPermission = permissions.some(permission => value.includes(permission))
      
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  } else {
    throw new Error('需要指定权限值，例如 v-permission="[\'admin\']"')
  }
}

export default {
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  updated(el, binding) {
    checkPermission(el, binding)
  }
} 