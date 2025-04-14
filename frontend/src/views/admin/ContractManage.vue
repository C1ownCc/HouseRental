<template>
  <div class="contract-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>合同管理</span>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input 
          v-model="searchForm.keyword" 
          placeholder="搜索合同编号/房源标题/用户名" 
          style="width: 300px" 
          clearable
          @clear="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch"></el-button>
          </template>
        </el-input>
        
        <el-select v-model="searchForm.status" placeholder="合同状态" clearable @change="handleSearch">
          <el-option label="全部状态" value="" />
          <el-option label="草稿" :value="0" />
          <el-option label="待租户签署" :value="1" />
          <el-option label="待房东签署" :value="2" />
          <el-option label="已生效" :value="3" />
          <el-option label="已终止" :value="4" />
          <el-option label="已到期" :value="5" />
        </el-select>
        
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      
      <el-table :data="contractList" style="width: 100%" v-loading="loading">
        <el-table-column prop="contract.contractNumber" label="合同编号" width="160" />
        <el-table-column label="房源信息" min-width="180">
          <template #default="scope">
            <div>{{ scope.row.house.title }}</div>
            <div class="text-secondary">{{ scope.row.house.area }}㎡ | {{ scope.row.house.roomCount }}室{{ scope.row.house.hallCount }}厅</div>
            <div class="text-address">{{ scope.row.house.address }}</div>
          </template>
        </el-table-column>
        <el-table-column label="租期" width="180">
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
            {{ scope.row.owner?.nickname || scope.row.owner?.username || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="租户" width="120">
          <template #default="scope">
            {{ scope.row.tenant?.nickname || scope.row.tenant?.username || '未知' }}
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
              v-if="scope.row.contract.status === 3" 
              type="warning" 
              size="small" 
              @click="generateBill(scope.row.contract.id)"
              :disabled="loading"
            >
              生成账单
            </el-button>
            <el-button 
              v-if="scope.row.contract.status !== 4 && scope.row.contract.status !== 5" 
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const contractList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentContractId = ref(null)

// 搜索表单
const searchForm = ref({
  keyword: '',
  status: ''
})

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

// 获取合同列表
const getContractList = async () => {
  loading.value = true
  try {
    console.log('开始获取合同列表，参数：', {
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.value.keyword,
      status: searchForm.value.status
    });
    
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    
    // 添加搜索条件
    if (searchForm.value.keyword) {
      params.keyword = searchForm.value.keyword
    }
    
    if (searchForm.value.status !== '') {
      params.status = searchForm.value.status
    }
    
    const res = await request.get('/lease-contract/list', { params })
    console.log('合同列表响应:', res);
    
    if (res.code === 200) {
      contractList.value = res.data.records || []
      total.value = res.data.total || 0
      
      if (contractList.value.length === 0) {
        ElMessage.info('暂无合同数据')
      } else {
        ElMessage.success(`成功获取${contractList.value.length}条合同数据`)
      }
    } else {
      ElMessage.error(res.message || '获取合同列表失败')
    }
  } catch (error) {
    console.error('获取合同列表失败，详细错误:', error)
    
    // 提供更友好的错误信息
    let errorMsg = '获取合同列表失败'
    if (error.response) {
      errorMsg += ': ' + (error.response.data?.message || `状态码 ${error.response.status}`)
    } else if (error.message) {
      errorMsg += ': ' + error.message
    }
    
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getContractList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    keyword: '',
    status: ''
  }
  currentPage.value = 1
  getContractList()
}

// 查看合同详情
const viewDetail = (contractId) => {
  router.push(`/admin/contract/detail/${contractId}`)
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
        ElMessage.success('合同已成功终止')
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

// 分页
const handleSizeChange = (size) => {
  pageSize.value = size
  getContractList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  getContractList()
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

.text-secondary {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}

.text-address {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
}
</style> 