<template>
  <div class="client-layout">
    <!-- 头部导航 -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">房屋租赁平台</router-link>
        </div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/house-list">房源列表</router-link>
          <router-link to="/house/map">地图找房</router-link>
          <template v-if="!userStore.token">
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </template>
          <template v-else>
            <router-link to="/message">消息中心</router-link>
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                {{ userStore.userInfo.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="contracts">我的租约</el-dropdown-item>
                  <el-dropdown-item command="landlord" v-if="userStore.userInfo.role === 'landlord' || userStore.userInfo.role === 'admin'">
                    房东中心
                  </el-dropdown-item>
                  <el-dropdown-item command="admin" v-if="userStore.userInfo.role === 'admin'">
                    后台管理
                  </el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </nav>
      </div>
    </header>

    <!-- 主要内容区 -->
    <main class="main">
      <router-view></router-view>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <p>© 2024 房屋租赁平台 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user')
      break
    case 'contracts':
      router.push('/contracts')
      break
    case 'landlord':
      router.push('/landlord/contracts')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.clearToken()
      userStore.clearUserInfo()
      ElMessage.success('退出登录成功')
      router.push('/')
      break
  }
}
</script>

<style scoped>
.client-layout {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo a {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  text-decoration: none;
}

.nav {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav a {
  color: #333;
  text-decoration: none;
  font-size: 14px;
}

.nav a:hover {
  color: #409EFF;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}

.main {
  flex: 1;
  margin-top: 60px;
  padding: 20px;
  max-width: 1200px;
  width: 100%;
  margin-left: auto;
  margin-right: auto;
}

.footer {
  background-color: #f5f5f5;
  padding: 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  color: #666;
}
</style> 