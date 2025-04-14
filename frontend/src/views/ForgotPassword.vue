<template>
  <div class="forgot-password-container">
    <div class="forgot-password-box">
      <h2>重置密码</h2>
      <el-form
        ref="forgotPasswordFormRef"
        :model="forgotPasswordForm"
        :rules="formRules"
        label-width="0"
        class="forgot-password-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="forgotPasswordForm.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="forgotPasswordForm.email"
            placeholder="注册邮箱"
            prefix-icon="Message"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="submit-btn" @click="handleSubmit">
            发送重置链接
          </el-button>
        </el-form-item>
      </el-form>
      <div class="form-links">
        <el-button link type="primary" @click="$router.push('/login')">返回登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)

// 表单数据
const forgotPasswordForm = reactive({
  username: '',
  email: ''
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 表单引用
const forgotPasswordFormRef = ref(null)

// 处理提交
const handleSubmit = async () => {
  if (!forgotPasswordFormRef.value) return
  
  try {
    const valid = await forgotPasswordFormRef.value.validate()
    if (valid) {
      loading.value = true
      const res = await request.post('/auth/forgot-password', forgotPasswordForm)
      
      if (res.code === 200) {
        ElMessage.success('重置链接已发送到您的邮箱，请查收')
        router.push('/login')
      } else {
        ElMessage.error(res.message || '发送失败')
      }
    }
  } catch (error) {
    console.error('发送失败:', error)
    ElMessage.error(error.response?.data?.message || '发送失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forgot-password-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

.forgot-password-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.forgot-password-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.forgot-password-form {
  margin-bottom: 20px;
}

.submit-btn {
  width: 100%;
}

.form-links {
  display: flex;
  justify-content: center;
  font-size: 14px;
}

.form-links a {
  color: #409EFF;
  text-decoration: none;
}

.form-links a:hover {
  color: #66b1ff;
}
</style> 