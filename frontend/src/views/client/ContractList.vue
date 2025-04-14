<template>
  <div class="contract-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的租约</span>
        </div>
      </template>
      
      <el-table :data="contractList" style="width: 100%" v-loading="loading">
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
        <el-table-column label="房东" width="120">
          <template #default="scope">
            {{ scope.row.owner.nickname || scope.row.owner.username }}
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
              v-if="scope.row.contract.status === 1" 
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
              @click="viewPayments(scope.row.contract.id)"
              :disabled="loading"
            >
              账单
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const contractList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取租约列表
const getContractList = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  loading.value = true
  try {
    const res = await request.get(`/lease-contract/tenant/${userStore.userInfo.id}`, {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })
    
    if (res.code === 200) {
      contractList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取租约列表失败')
    }
  } catch (error) {
    console.error('获取租约列表失败:', error)
    ElMessage.error('获取租约列表失败')
  } finally {
    loading.value = false
  }
}

// 查看合同详情
const viewDetail = (contractId) => {
  router.push(`/contract/${contractId}`)
}

// 查看租金账单
const viewPayments = (contractId) => {
  router.push(`/contract/${contractId}/payments`)
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
      const res = await request.put(`/lease-contract/tenant-sign/${contractId}`)
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
.contract-list {
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