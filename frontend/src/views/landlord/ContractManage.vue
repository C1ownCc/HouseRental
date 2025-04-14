<template>
  <div class="contract-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>租约管理</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部租约" name="all">
          <el-empty v-if="contractList.length === 0" description="暂无租约数据"></el-empty>
          <el-table v-else :data="contractList" style="width: 100%" v-loading="loading">
            <el-table-column prop="contract.contractNumber" label="合同编号" width="180" />
            <el-table-column label="房源信息" min-width="200">
              <template #default="scope">
                <div>{{ scope.row.house.title }}</div>
                <div class="text-secondary">{{ scope.row.house.area }}㎡ | {{ scope.row.house.roomCount }}室{{ scope.row.house.hallCount }}厅</div>
                <div class="text-address">{{ scope.row.house.address }}</div>
              </template>
            </el-table-column>
            <el-table-column label="租期" width="200">
              <template #default="scope">
                {{ formatDate(scope.row.contract.startDate) }} 至 {{ formatDate(scope.row.contract.endDate) }}
              </template>
            </el-table-column>
            <el-table-column label="月租金" width="100">
              <template #default="scope">
                ¥{{ scope.row.contract.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column label="租户" width="120">
              <template #default="scope">
                {{ scope.row.tenant.nickname || scope.row.tenant.username }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.contract.status)">
                  {{ getStatusText(scope.row.contract.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="240" fixed="right">
              <template #default="scope">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="viewDetail(scope.row.contract.id)"
                  :disabled="loading"
                >
                  详情
                </el-button>
                <el-button 
                  v-if="scope.row.contract.status === 2" 
                  type="success" 
                  size="small" 
                  @click="signContract(scope.row.contract.id)"
                  :disabled="loading"
                >
                  签署
                </el-button>
                <el-button 
                  v-if="scope.row.contract.status === 3" 
                  type="warning" 
                  size="small" 
                  @click="generateBill(scope.row.contract.id)"
                  :disabled="loading"
                >
                  生成账单
                </el-button>
                <el-button 
                  v-if="scope.row.contract.status === 3" 
                  type="danger" 
                  size="small" 
                  @click="terminateContract(scope.row.contract.id)"
                  :disabled="loading"
                >
                  终止合同
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="待签署" name="pending">
          <el-empty v-if="pendingList.length === 0" description="暂无待签署的合同"></el-empty>
          <el-table v-else :data="pendingList" style="width: 100%" v-loading="loading">
            <el-table-column prop="contract.contractNumber" label="合同编号" width="180" />
            <el-table-column label="房源信息" min-width="200">
              <template #default="scope">
                <div>{{ scope.row.house.title }}</div>
                <div class="text-secondary">{{ scope.row.house.area }}㎡ | {{ scope.row.house.roomCount }}室{{ scope.row.house.hallCount }}厅</div>
                <div class="text-address">{{ scope.row.house.address }}</div>
              </template>
            </el-table-column>
            <el-table-column label="租期" width="200">
              <template #default="scope">
                {{ formatDate(scope.row.contract.startDate) }} 至 {{ formatDate(scope.row.contract.endDate) }}
              </template>
            </el-table-column>
            <el-table-column label="月租金" width="100">
              <template #default="scope">
                ¥{{ scope.row.contract.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column label="租户" width="120">
              <template #default="scope">
                {{ scope.row.tenant.nickname || scope.row.tenant.username }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.contract.status)">
                  {{ getStatusText(scope.row.contract.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="viewDetail(scope.row.contract.id)"
                  :disabled="loading"
                >
                  详情
                </el-button>
                <el-button 
                  v-if="scope.row.contract.status === 2" 
                  type="success" 
                  size="small" 
                  @click="signContract(scope.row.contract.id)"
                  :disabled="loading"
                >
                  签署
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="已生效" name="active">
          <el-empty v-if="activeList.length === 0" description="暂无已生效的合同"></el-empty>
          <el-table v-else :data="activeList" style="width: 100%" v-loading="loading">
            <el-table-column prop="contract.contractNumber" label="合同编号" width="180" />
            <el-table-column label="房源信息" min-width="200">
              <template #default="scope">
                <div>{{ scope.row.house.title }}</div>
                <div class="text-secondary">{{ scope.row.house.area }}㎡ | {{ scope.row.house.roomCount }}室{{ scope.row.house.hallCount }}厅</div>
                <div class="text-address">{{ scope.row.house.address }}</div>
              </template>
            </el-table-column>
            <el-table-column label="租期" width="200">
              <template #default="scope">
                {{ formatDate(scope.row.contract.startDate) }} 至 {{ formatDate(scope.row.contract.endDate) }}
              </template>
            </el-table-column>
            <el-table-column label="月租金" width="100">
              <template #default="scope">
                ¥{{ scope.row.contract.monthlyRent }}
              </template>
            </el-table-column>
            <el-table-column label="租户" width="120">
              <template #default="scope">
                {{ scope.row.tenant.nickname || scope.row.tenant.username }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.contract.status)">
                  {{ getStatusText(scope.row.contract.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="240" fixed="right">
              <template #default="scope">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="viewDetail(scope.row.contract.id)"
                  :disabled="loading"
                >
                  详情
                </el-button>
                <el-button 
                  type="warning" 
                  size="small" 
                  @click="generateBill(scope.row.contract.id)"
                  :disabled="loading"
                >
                  生成账单
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="terminateContract(scope.row.contract.id)"
                  :disabled="loading"
                >
                  终止合同
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 终止合同对话框 -->
    <el-dialog v-model="terminateDialogVisible" title="终止合同" width="30%">
      <el-form :model="terminateForm" :rules="terminateRules" ref="terminateFormRef" label-width="100px">
        <el-form-item label="终止原因" prop="remark">
          <el-input v-model="terminateForm.remark" type="textarea" rows="4" placeholder="请输入终止合同的原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="terminateDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmTerminate">确认终止</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const activeTab = ref('all')
const contractList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentContractId = ref(null)

// 终止合同相关
const terminateDialogVisible = ref(false)
const terminateFormRef = ref(null)
const terminateForm = ref({
  remark: ''
})

const terminateRules = {
  remark: [
    { required: true, message: '请输入终止合同的原因', trigger: 'blur' },
    { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
  ]
}

// 过滤不同状态的合同
const pendingList = computed(() => {
  return contractList.value.filter(item => item.contract.status === 2)
})

const activeList = computed(() => {
  return contractList.value.filter(item => item.contract.status === 3)
})

// 获取合同列表
const getContractList = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  loading.value = true
  try {
    console.log('当前房东ID:', userStore.userInfo.id)
    const res = await request.get(`/lease-contract/owner/${userStore.userInfo.id}`, {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })
    
    if (res.code === 200) {
      console.log('获取到的合同列表:', res.data)
      contractList.value = res.data.records || []
      total.value = res.data.total || 0
      
      // 显示调试信息
      if (contractList.value.length === 0) {
        ElMessage.info('暂无合同数据')
      } else {
        ElMessage.success(`成功获取${contractList.value.length}条合同数据`)
      }
    } else {
      ElMessage.error(res.message || '获取合同列表失败')
    }
  } catch (error) {
    console.error('获取合同列表失败:', error)
    ElMessage.error('获取合同列表失败')
  } finally {
    loading.value = false
  }
}

// 查看合同详情
const viewDetail = (contractId) => {
  // 跳转到详情页，实际项目中可能是单独的详情页或模态框
  router.push(`/contract/${contractId}`)
}

// 签署合同
const signContract = async (contractId) => {
  ElMessageBox.confirm('确认签署此租赁合同？签署后将具有法律效力。', '确认签署', {
    confirmButtonText: '确认签署',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      const res = await request.put(`/lease-contract/owner-sign/${contractId}`)
      if (res.code === 200) {
        ElMessage.success('合同签署成功')
        getContractList() // 刷新列表
      } else {
        ElMessage.error(res.message || '合同签署失败')
      }
    } catch (error) {
      console.error('合同签署失败:', error)
      ElMessage.error('合同签署失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 取消签署
  })
}

// 生成租金账单
const generateBill = async (contractId) => {
  ElMessageBox.confirm('确认为该合同生成租金账单？', '确认', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      const res = await request.post(`/rent-payment/generate-bill/${contractId}`)
      if (res.code === 200) {
        ElMessage.success('租金账单生成成功')
      } else {
        ElMessage.error(res.message || '租金账单生成失败')
      }
    } catch (error) {
      console.error('租金账单生成失败:', error)
      ElMessage.error('租金账单生成失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 取消生成
  })
}

// 打开终止合同对话框
const terminateContract = (contractId) => {
  currentContractId.value = contractId
  terminateForm.value.remark = ''
  terminateDialogVisible.value = true
}

// 确认终止合同
const confirmTerminate = async () => {
  if (!terminateFormRef.value) return
  
  await terminateFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }

    loading.value = true
    try {
      const res = await request.put(`/lease-contract/terminate/${currentContractId.value}`, {}, {
        params: {
          remark: terminateForm.value.remark
        }
      })
      
      if (res.code === 200) {
        ElMessage.success('合同已终止')
        terminateDialogVisible.value = false
        getContractList() // 刷新列表
      } else {
        ElMessage.error(res.message || '终止合同失败')
      }
    } catch (error) {
      console.error('终止合同失败:', error)
      ElMessage.error('终止合同失败')
    } finally {
      loading.value = false
    }
  })
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '草稿',
    1: '待租户签署',
    2: '待房东签署',
    3: '已生效',
    4: '已终止',
    5: '已到期'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'info',
    1: 'warning',
    2: 'warning',
    3: 'success',
    4: 'danger',
    5: 'info'
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 分页
const handleSizeChange = (size) => {
  pageSize.value = size
  getContractList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  getContractList()
}

onMounted(() => {
  getContractList()
})
</script>

<style scoped>
.contract-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.text-secondary {
  font-size: 13px;
  color: #909399;
  margin-top: 5px;
}

.text-address {
  font-size: 13px;
  color: #909399;
  margin-top: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 350px;
}
</style> 