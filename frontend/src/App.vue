<template>
  <router-view></router-view>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

// 初始化用户信息
const initUserInfo = async () => {
  // 如果有token但没有用户信息，重新获取用户信息
  if (userStore.token && !userStore.userInfo?.id) {
    try {
      const success = await userStore.getCurrentUser()
      if (!success) {
        userStore.clearToken()
        userStore.clearUserInfo()
        // 如果当前不在登录页，则跳转到登录页
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }
      }
    } catch (error) {
      console.error('初始化用户信息失败:', error)
      userStore.clearToken()
      userStore.clearUserInfo()
      // 如果当前不在登录页，则跳转到登录页
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }
  }
}

onMounted(() => {
  initUserInfo()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  width: 100%;
}

#app {
  height: 100%;
  width: 100%;
}
</style>
