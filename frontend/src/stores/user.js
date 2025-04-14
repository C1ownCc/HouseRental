import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  // 从 localStorage 初始化状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const isLoggedIn = ref(!!token.value)

  // 设置token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
    isLoggedIn.value = true
  }

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 清除token
  const clearToken = () => {
    token.value = ''
    localStorage.removeItem('token')
    isLoggedIn.value = false
  }

  // 清除用户信息
  const clearUserInfo = () => {
    userInfo.value = {}
    localStorage.removeItem('userInfo')
  }

  // 登录
  const login = async (loginForm) => {
    try {
      const res = await request.post('/auth/login', loginForm)
      if (res.code === 200) {
        setToken(res.data.token)
        setUserInfo(res.data.userInfo)
        ElMessage.success('登录成功')
        return true
      }
      return false
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  // 退出登录
  const logout = async () => {
    try {
      await request.post('/auth/logout')
      clearToken()
      clearUserInfo()
      ElMessage.success('退出登录成功')
    } catch (error) {
      console.error('退出登录失败:', error)
      // 即使请求失败，也清除本地状态
      clearToken()
      clearUserInfo()
    }
  }

  // 获取当前用户信息
  const getCurrentUser = async () => {
    try {
      const res = await request.get('/auth/current-user')
      if (res.code === 200) {
        setUserInfo(res.data)
        return true
      }
      return false
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return false
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    logout,
    setToken,
    clearToken,
    setUserInfo,
    clearUserInfo,
    getCurrentUser
  }
}) 