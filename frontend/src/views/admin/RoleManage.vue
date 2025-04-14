<template>
  <div class="role-manage-container">
    <!-- 角色列表区域 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="role-card">
          <template #header>
            <div class="card-header">
              <span>角色列表</span>
              <el-button type="primary" @click="handleAddRole">
                <el-icon><Plus /></el-icon>新增角色
              </el-button>
            </div>
          </template>
          <el-table
            v-loading="loading"
            :data="roleList"
            style="width: 100%"
            highlight-current-row
            @current-change="handleRoleSelect"
          >
            <el-table-column prop="name" label="角色名称" />
            <el-table-column prop="code" label="角色标识" width="120" />
            <el-table-column label="操作" width="150" align="center">
              <template #default="scope">
                <el-button-group>
                  <el-button type="primary" link @click="handleEditRole(scope.row)">
                    编辑
                  </el-button>
                  <el-button
                    type="danger"
                    link
                    @click="handleDeleteRole(scope.row)"
                    :disabled="scope.row.isSystem"
                  >
                    删除
                  </el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="permission-card" v-loading="permissionLoading">
          <template #header>
            <div class="card-header">
              <span>权限配置 - {{ currentRole?.name || '请选择角色' }}</span>
              <div class="header-operations" v-if="currentRole">
                <el-button type="primary" @click="handleSavePermissions" :loading="saveLoading">
                  保存配置
                </el-button>
              </div>
            </div>
          </template>

          <!-- 权限树形控件 -->
          <div v-if="currentRole" class="permission-tree">
            <div class="tree-operations">
              <el-checkbox
                v-model="checkAll"
                :indeterminate="isIndeterminate"
                @change="handleCheckAllChange"
              >
                全选
              </el-checkbox>
              <div class="operation-buttons">
                <el-button type="primary" link @click="expandAll">展开全部</el-button>
                <el-button type="info" link @click="collapseAll">收起全部</el-button>
              </div>
            </div>
            <el-tree
              ref="permissionTree"
              :data="permissionList"
              :props="defaultProps"
              show-checkbox
              node-key="id"
              default-expand-all
              :default-checked-keys="checkedPermissions"
              @check="handlePermissionCheck"
            >
              <template #default="{ node, data }">
                <div class="custom-tree-node">
                  <span>
                    <el-icon v-if="data.icon"><component :is="data.icon" /></el-icon>
                    {{ node.label }}
                  </span>
                  <el-tag size="small" :type="getPermissionType(data.type)">
                    {{ getPermissionTypeText(data.type) }}
                  </el-tag>
                </div>
              </template>
            </el-tree>
          </div>
          <el-empty v-else description="请选择要配置的角色" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 角色表单对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
        class="role-form"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="code">
          <el-input
            v-model="roleForm.code"
            placeholder="请输入角色标识"
            :disabled="dialogType === 'edit' && roleForm.isSystem"
          />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="roleForm.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitRole" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 角色列表数据
const loading = ref(false)
const roleList = ref([])
const currentRole = ref(null)

// 权限树相关
const permissionLoading = ref(false)
const permissionList = ref([])
const checkedPermissions = ref([])
const permissionTree = ref(null)
const checkAll = ref(false)
const isIndeterminate = ref(false)

// 表单对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const submitting = ref(false)
const saveLoading = ref(false)
const roleFormRef = ref(null)
const roleForm = reactive({
  name: '',
  code: '',
  description: '',
  sort: 0,
  isSystem: false
})

// 表单验证规则
const roleRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色标识', trigger: 'blur' },
    { pattern: /^[a-z_]+$/, message: '只能包含小写字母和下划线', trigger: 'blur' }
  ]
}

// 树形控件配置
const defaultProps = {
  children: 'children',
  label: 'name'
}

// 获取角色列表
const getRoleList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/role/list')
    roleList.value = res.data
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 获取权限列表
const getPermissionList = async () => {
  permissionLoading.value = true
  try {
    const res = await request.get('/admin/permission/tree')
    permissionList.value = res.data
  } catch (error) {
    console.error('获取权限列表失败:', error)
    ElMessage.error('获取权限列表失败')
  } finally {
    permissionLoading.value = false
  }
}

// 获取角色权限
const getRolePermissions = async (roleId) => {
  permissionLoading.value = true
  try {
    const res = await request.get(`/admin/role/${roleId}/permissions`)
    checkedPermissions.value = res.data
    updateCheckAllStatus()
  } catch (error) {
    console.error('获取角色权限失败:', error)
    ElMessage.error('获取角色权限失败')
  } finally {
    permissionLoading.value = false
  }
}

// 选择角色
const handleRoleSelect = (row) => {
  currentRole.value = row
  if (row) {
    getRolePermissions(row.id)
  } else {
    checkedPermissions.value = []
  }
}

// 新增角色
const handleAddRole = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  // 重置表单
  if (roleFormRef.value) {
    roleFormRef.value.resetFields()
  }
  Object.assign(roleForm, {
    name: '',
    code: '',
    description: '',
    sort: 0,
    isSystem: false
  })
}

// 编辑角色
const handleEditRole = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(roleForm, row)
}

// 删除角色
const handleDeleteRole = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/admin/role/${row.id}`)
    ElMessage.success('删除成功')
    if (currentRole.value?.id === row.id) {
      currentRole.value = null
    }
    getRoleList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
      ElMessage.error('删除角色失败')
    }
  }
}

// 提交角色表单
const handleSubmitRole = async () => {
  if (!roleFormRef.value) return

  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const data = { ...roleForm }
        if (dialogType.value === 'add') {
          await request.post('/admin/role', data)
          ElMessage.success('添加成功')
        } else {
          await request.put(`/admin/role/${data.id}`, data)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        getRoleList()
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 权限树操作
const handlePermissionCheck = () => {
  const checkedKeys = permissionTree.value.getCheckedKeys()
  const halfCheckedKeys = permissionTree.value.getHalfCheckedKeys()
  checkedPermissions.value = [...checkedKeys, ...halfCheckedKeys]
  updateCheckAllStatus()
}

const updateCheckAllStatus = () => {
  const allPermissions = getAllPermissionIds(permissionList.value)
  checkAll.value = checkedPermissions.value.length === allPermissions.length
  isIndeterminate.value = checkedPermissions.value.length > 0 && 
    checkedPermissions.value.length < allPermissions.length
}

const getAllPermissionIds = (permissions) => {
  const ids = []
  const traverse = (items) => {
    items.forEach(item => {
      ids.push(item.id)
      if (item.children) {
        traverse(item.children)
      }
    })
  }
  traverse(permissions)
  return ids
}

const handleCheckAllChange = (checked) => {
  const allPermissions = getAllPermissionIds(permissionList.value)
  checkedPermissions.value = checked ? allPermissions : []
  permissionTree.value.setCheckedKeys(checkedPermissions.value)
  isIndeterminate.value = false
}

// 展开/收起树
const expandAll = () => {
  const nodes = permissionTree.value.store._getAllNodes()
  nodes.forEach(node => {
    node.expand(true)
  })
}

const collapseAll = () => {
  const nodes = permissionTree.value.store._getAllNodes()
  nodes.forEach(node => {
    node.expand(false)
  })
}

// 保存权限配置
const handleSavePermissions = async () => {
  if (!currentRole.value) return

  saveLoading.value = true
  try {
    await request.put(`/admin/role/${currentRole.value.id}/permissions`, {
      permissions: checkedPermissions.value
    })
    ElMessage.success('权限配置保存成功')
  } catch (error) {
    console.error('保存权限配置失败:', error)
    ElMessage.error('保存权限配置失败')
  } finally {
    saveLoading.value = false
  }
}

// 权限类型处理
const getPermissionType = (type) => {
  const map = {
    menu: 'primary',
    button: 'success',
    api: 'warning'
  }
  return map[type] || 'info'
}

const getPermissionTypeText = (type) => {
  const map = {
    menu: '菜单',
    button: '按钮',
    api: '接口'
  }
  return map[type] || '未知'
}

onMounted(() => {
  getRoleList()
  getPermissionList()
})
</script>

<style scoped lang="scss">
.role-manage-container {
  padding: 20px;

  .role-card, .permission-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .permission-tree {
    .tree-operations {
      margin-bottom: 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .operation-buttons {
        display: flex;
        gap: 10px;
      }
    }

    .custom-tree-node {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 14px;
      padding-right: 8px;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}

:deep(.el-tree-node__content) {
  height: 32px;
}
</style> 