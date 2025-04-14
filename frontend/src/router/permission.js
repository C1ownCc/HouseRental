import router from './index'
import { useUserStore } from '@/stores'
import { ElMessage } from 'element-plus'

// 白名单路由
const whiteList = ['/login', '/register', '/', '/house/list', '/house/detail']

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const { token, userInfo } = userStore

  // 有token
  if (token) {
    if (to.path === '/login') {
      next('/')
    } else {
      // 判断是否有用户信息
      if (Object.keys(userInfo).length === 0) {
        try {
          // 获取用户信息
          await userStore.getUserInfo()
          // 获取成功后放行
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，可能是token过期
          userStore.logout()
          ElMessage.error('登录已过期，请重新登录')
          next(`/login?redirect=${to.path}`)
        }
      } else {
        // 判断是否有权限访问该页面
        if (to.meta.permissions) {
          if (userInfo.role === 'admin' || userInfo.permissions?.some(permission => 
            to.meta.permissions.includes(permission)
          )) {
            next()
          } else {
            ElMessage.error('暂无权限访问该页面')
            next('/403')
          }
        } else {
          next()
        }
      }
    }
  } else {
    // 没有token
    if (whiteList.includes(to.path) || to.path.startsWith('/house/detail/')) {
      // 白名单路由放行
      next()
    } else {
      // 其他路由跳转到登录页
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach(() => {
  // 路由切换后的操作，如关闭loading等
}) 