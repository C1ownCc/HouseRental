<template>
  <div class="login-container">
    <div class="login-box">
      <h2>欢迎登录</h2>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="0"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-links">
        <el-button link type="primary" @click="handleRegister">注册账号</el-button>
        <el-button link type="primary" @click="handleForgotPassword">忘记密码</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { validatePassword } from '@/utils/validate'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      const result = validatePassword(value)
      if (!result.valid) {
        callback(new Error(result.message))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

// 登录表单引用
const loginFormRef = ref(null)

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    const valid = await loginFormRef.value.validate()
    if (valid) {
      loading.value = true
      console.log('登录请求参数:', loginForm)
      const res = await request.post('/auth/login', {
        username: loginForm.username.trim(),
        password: loginForm.password.trim()
      })
      console.log('登录响应:', res)
      
      // 检查登录响应
      if (res.code === 200 && res.data) {
        // 存储用户信息
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userRole', res.data.userInfo.role)
        localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
        
        // 更新 store
        userStore.setToken(res.data.token)
        userStore.setUserInfo(res.data.userInfo)
        
        ElMessage.success('登录成功')
        
        // 如果有重定向地址，则跳转到重定向地址
        const redirect = route.query.redirect || '/'
        router.push(redirect)
      } else {
        // 显示后端返回的错误信息
        ElMessage.error(res.message || '登录失败')
      }
    }
  } catch (error) {
    console.error('登录失败:', error)
    console.error('错误详情:', {
      response: error.response?.data,
      status: error.response?.status,
      message: error.message
    })
    // 显示具体的错误信息
    const errorMsg = error.response?.data?.message || error.message || '登录失败，请检查用户名和密码'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

// 处理注册
const handleRegister = () => {
  router.push('/register')
}

// 处理忘记密码
const handleForgotPassword = () => {
  router.push('/forgot-password')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.login-form {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
}

.login-links {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.login-links a {
  color: #409EFF;
  text-decoration: none;
}

.login-links a:hover {
  color: #66b1ff;
}
</style> 