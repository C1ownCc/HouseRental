import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores'

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 使用 vite 的代理配置
  timeout: 10000, // 请求超时时间
  withCredentials: true // 允许携带cookie
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    console.log('响应数据:', res)
    
    // 如果返回的状态码不是200，说明出错了
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401: Token过期或未登录
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.clearToken()
        userStore.clearUserInfo()
        
        // 只有在不是登录页面时才重定向
        if (router.currentRoute.value.path !== '/login') {
          router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
        }
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    console.error('响应错误:', error)
    const { status, data } = error.response || {}
    
    // 构建更详细的错误信息
    let errorMessage = '请求失败'
    
    if (error.message.includes('timeout')) {
      errorMessage = '请求超时，请检查网络连接'
    } else if (error.message.includes('Network Error')) {
      errorMessage = '网络连接错误，请检查网络状态'
    } else if (data && data.message) {
      errorMessage = data.message
    } else if (error.message) {
      errorMessage = error.message
    }
    
    switch (status) {
      case 401:
        const userStore = useUserStore()
        userStore.clearToken()
        userStore.clearUserInfo()
        
        // 只有在不是登录页面时才重定向
        if (router.currentRoute.value.path !== '/login') {
          router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
        }
        ElMessage.error(data?.message || '登录已过期，请重新登录')
        break
      case 403:
        ElMessage.error(data?.message || '没有权限访问该资源')
        break
      case 404:
        ElMessage.error(data?.message || '请求的资源不存在')
        break
      case 500:
        ElMessage.error(data?.message || '服务器内部错误')
        break
      default:
        ElMessage.error(errorMessage)
    }
    
    // 将构建的错误信息附加到错误对象上
    error.displayMessage = errorMessage
    
    return Promise.reject(error)
  }
)

export default request