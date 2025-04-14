<template>
  <div class="user-manage-container">
    <!-- 搜索筛选区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" ref="searchFormRef">
        <el-form-item label="关键字" prop="keyword">
          <el-input v-model="searchForm.keyword" placeholder="请输入用户名/手机号/邮箱" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="searchForm.role" placeholder="选择角色" clearable style="width: 120px">
            <el-option label="管理员" value="admin" />
            <el-option label="房东" value="landlord" />
            <el-option label="租客" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <div class="operation-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增用户
      </el-button>
      <el-button type="danger" :disabled="!selectedUsers.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>批量删除
      </el-button>
      <el-button type="warning" :disabled="!selectedUsers.length" @click="handleBatchDisable">
        <el-icon><Lock /></el-icon>批量禁用
      </el-button>
      <el-button type="success" :disabled="!selectedUsers.length" @click="handleBatchEnable">
        <el-icon><Unlock /></el-icon>批量启用
      </el-button>
    </div>

    <!-- 用户列表 -->
    <el-card class="list-card">
      <el-table
        v-loading="loading"
        :data="userList"
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.created_time) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button-group>
              <el-button type="primary" link @click="handleEdit(scope.row)">
                编辑
              </el-button>
              <el-button type="success" link @click="handleAssignRole(scope.row)">
                分配角色
              </el-button>
              <el-button
                :type="scope.row.status === 1 ? 'warning' : 'success'"
                link
                @click="handleToggleStatus(scope.row)"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="danger" link @click="handleDelete(scope.row)">
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      v-model="dialogVisible"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
        class="user-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="房东" value="landlord" />
            <el-option label="租客" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/upload/image"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :on-error="handleAvatarError"
            :before-upload="beforeAvatarUpload"
            name="file"
          >
            <template v-if="userForm.avatar">
              <img :src="userForm.avatar" class="avatar" />
            </template>
            <template v-else>
              <el-avatar :size="100">
                <el-icon><User /></el-icon>
              </el-avatar>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog title="分配角色" v-model="roleDialogVisible" width="400px">
      <el-form :model="roleForm" label-width="100px">
        <el-form-item label="当前用户">
          <span>{{ currentUser?.username }}</span>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="roleForm.role" placeholder="选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="房东" value="landlord" />
            <el-option label="租客" value="user" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRoleSubmit" :loading="roleSubmitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { Plus, Delete, Lock, Unlock, User } from '@element-plus/icons-vue'
import { getImageUrl, removeUploadsPrefix } from '@/utils/image'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  role: '',
  status: ''
})

// 列表数据
const loading = ref(false)
const userList = ref([])
const selectedUsers = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 用户表单对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const submitting = ref(false)
const userFormRef = ref(null)
const userForm = reactive({
  username: '',
  phone: '',
  email: '',
  password: '',
  role: '',
  avatar: ''
})

// 角色对话框
const roleDialogVisible = ref(false)
const roleSubmitting = ref(false)
const currentUser = ref(null)
const roleForm = reactive({
  role: ''
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.keyword || undefined,
      role: searchForm.role || undefined,
      status: searchForm.status
    }

    // 移除值为undefined的参数
    Object.keys(params).forEach(key => {
      if (params[key] === undefined || params[key] === '') {
        delete params[key]
      }
    })

    const res = await request.get('/admin/user/list', { params })
    console.log('用户列表数据:', res.data.list)
    userList.value = res.data.list.map(user => ({
      ...user,
      avatar: user.avatar ? getImageUrl(user.avatar) : ''
    }))
    total.value = res.data.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索和重置
const handleSearch = () => {
  currentPage.value = 1
  getUserList()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 分页操作
const handleSizeChange = (val) => {
  pageSize.value = val
  getUserList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getUserList()
}

// 角色和状态处理
const getRoleType = (role) => {
  const map = {
    admin: 'danger',
    landlord: 'warning',
    user: 'info',
    tenant: 'info'
  }
  return map[role] || 'info'
}

const getRoleText = (role) => {
  const map = {
    admin: '管理员',
    landlord: '房东',
    user: '租客',
    tenant: '租客'
  }
  return map[role] || '未知'
}

// 新增用户
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  resetForm()
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  // 填充表单数据
  Object.assign(userForm, {
    id: row.id,  // 确保设置用户ID
    username: row.username,
    phone: row.phone,
    email: row.email,
    role: row.role,
    avatar: row.avatar
  })
}

// 删除用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/admin/user/${row.id}`)
    ElMessage.success('删除成功')
    getUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (!selectedUsers.value.length) return
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      type: 'warning'
    })
    const ids = selectedUsers.value.map(item => item.id)
    await request.post('/admin/user/batch-delete', ids)
    ElMessage.success('批量删除成功')
    getUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 处理启用/禁用
const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await request.put(`/admin/user/${row.id}/status?status=${newStatus}`)
    ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
    getUserList()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
  }
}

// 批量启用
const handleBatchEnable = async () => {
  if (!selectedUsers.value.length) return
  
  try {
    await ElMessageBox.confirm(`确定要启用选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      type: 'warning'
    })
    const ids = selectedUsers.value.map(item => item.id)
    await request.put('/admin/user/batch-status', ids, {
      params: { status: 1 }
    })
    ElMessage.success('批量启用成功')
    getUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量启用失败:', error)
      ElMessage.error('批量启用失败')
    }
  }
}

// 批量禁用
const handleBatchDisable = async () => {
  if (!selectedUsers.value.length) return
  
  try {
    await ElMessageBox.confirm(`确定要禁用选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      type: 'warning'
    })
    const ids = selectedUsers.value.map(item => item.id)
    await request.put('/admin/user/batch-status', ids, {
      params: { status: 0 }
    })
    ElMessage.success('批量禁用成功')
    getUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量禁用失败:', error)
      ElMessage.error('批量禁用失败')
    }
  }
}

// 分配角色
const handleAssignRole = (row) => {
  currentUser.value = row
  roleForm.role = row.role
  roleDialogVisible.value = true
}

const handleRoleSubmit = async () => {
  if (!currentUser.value) return
  
  roleSubmitting.value = true
  try {
    await request.put(`/admin/user/${currentUser.value.id}/role`, {
      role: roleForm.role
    })
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    getUserList()
  } catch (error) {
    console.error('角色分配失败:', error)
    ElMessage.error('角色分配失败')
  } finally {
    roleSubmitting.value = false
  }
}

// 头像上传
const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    console.log('头像上传成功，路径:', response.data)
    userForm.avatar = getImageUrl(response.data)
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const handleAvatarError = (error) => {
  console.error('头像上传失败:', error)
  ElMessage.error('头像上传失败，请重试')
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const data = { ...userForm }
        // 处理头像路径，移除/uploads前缀
        if (data.avatar) {
          data.avatar = removeUploadsPrefix(data.avatar)
        }
        
        if (dialogType.value === 'add') {
          await request.post('/admin/user/add', data)
          ElMessage.success('添加成功')
        } else if (data.id) {  // 确保有用户ID才进行编辑
          await request.put(`/admin/user/${data.id}`, data)
          ElMessage.success('更新成功')
        } else {
          throw new Error('用户ID不存在')
        }
        dialogVisible.value = false
        getUserList()
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error(error.message || '提交失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  userForm.id = undefined
  userForm.username = ''
  userForm.phone = ''
  userForm.email = ''
  userForm.password = ''
  userForm.role = ''
  userForm.avatar = ''
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 对话框关闭时重置表单
const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  getUserList()
})
</script>

<style scoped lang="scss">
.user-manage-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .operation-bar {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);

    &:hover {
      border-color: var(--el-color-primary);
    }
  }

  .avatar {
    width: 100px;
    height: 100px;
    display: block;
    object-fit: cover;
  }
}
</style> 