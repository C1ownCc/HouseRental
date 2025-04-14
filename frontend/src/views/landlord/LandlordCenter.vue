<template>
  <div class="landlord-center">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="route.path"
          router
        >
          <el-menu-item index="/landlord/houses">
            <el-icon><House /></el-icon>
            <span>房源管理</span>
          </el-menu-item>
          <el-menu-item index="/landlord/bookings">
            <el-icon><Calendar /></el-icon>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/landlord/contracts">
            <el-icon><Document /></el-icon>
            <span>租约管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { House, Calendar, Document } from '@element-plus/icons-vue'

const route = useRoute()
</script>

<style scoped>
.landlord-center {
  height: calc(100vh - 60px);
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #fff;
  border-right: 1px solid #dcdfe6;
}

.el-menu {
  border-right: none;
}

.el-main {
  padding: 20px;
  background-color: #f5f7fa;
}
</style> 