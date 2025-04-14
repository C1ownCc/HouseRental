<template>
  <div class="booking-manage-container">
    <!-- 搜索筛选区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" ref="searchFormRef">
        <el-form-item label="预约用户" prop="username">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="房源标题" prop="houseTitle">
          <el-input v-model="searchForm.houseTitle" placeholder="请输入房源标题" clearable />
        </el-form-item>
        <el-form-item label="预约状态" prop="status">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 160px">
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已取消" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已拒绝" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <div class="operation-bar">
      <el-button 
        type="success" 
        :disabled="!selectedBookings.length || !selectedBookings.every(item => item.status === 0)"
        @click="handleBatchConfirm"
      >
        <el-icon><Check /></el-icon>批量确认
      </el-button>
      <el-button 
        type="danger" 
        :disabled="!selectedBookings.length || !selectedBookings.some(item => [0, 1].includes(item.status))"
        @click="handleBatchCancel"
      >
        <el-icon><Close /></el-icon>批量取消
      </el-button>
      <el-button 
        type="warning" 
        :disabled="!selectedBookings.length || !selectedBookings.every(item => [2, 3, 4].includes(item.status))"
        @click="handleBatchDelete"
      >
        <el-icon><Delete /></el-icon>批量删除
      </el-button>
    </div>

    <!-- 预约列表 -->
    <el-card class="list-card">
      <el-table
        v-loading="loading"
        :data="bookingList"
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="预约编号" width="100" />
        <el-table-column label="预约用户" width="200">
          <template #default="scope">
            <div class="user-info">
              <span>{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="房源信息" min-width="300">
          <template #default="scope">
            <div class="house-info">
              <el-image
                v-if="scope.row.houseImages"
                :src="getFirstImage(scope.row.houseImages)"
                :preview-src-list="getImageList(scope.row.houseImages)"
                fit="cover"
                class="house-image"
              />
              <div class="house-detail">
                <div class="house-title">{{ scope.row.houseTitle }}</div>
                <div v-if="scope.row.housePrice" class="house-price">¥{{ scope.row.housePrice }}/月</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="180">
          <template #default="scope">
            <div class="booking-time">
              <div>{{ formatDate(scope.row.bookingTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" class="status-tag">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button-group>
              <el-button
                v-if="scope.row.status === 0"
                type="success"
                link
                @click="handleConfirm(scope.row)"
              >
                确认
              </el-button>
              <el-button
                v-if="scope.row.status === 1"
                type="primary"
                link
                @click="handleComplete(scope.row)"
              >
                完成
              </el-button>
              <el-button
                v-if="[0, 1].includes(scope.row.status)"
                type="warning"
                link
                @click="handleCancel(scope.row)"
              >
                取消
              </el-button>
              <el-button type="primary" link @click="handleView(scope.row)">
                查看
              </el-button>
              <el-button 
                v-if="[2, 3, 4].includes(scope.row.status)"
                type="danger" 
                link 
                @click="handleDelete(scope.row)"
              >
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

    <!-- 预约详情对话框 -->
    <el-dialog title="预约详情" v-model="detailDialogVisible" width="600px">
      <div class="booking-detail">
        <div class="detail-item">
          <span class="label">预约编号：</span>
          <span>{{ currentBooking?.id }}</span>
        </div>
        <div class="detail-item">
          <span class="label">预约用户：</span>
          <span>{{ currentBooking?.username }}</span>
        </div>
        <div class="detail-item">
          <span class="label">联系人：</span>
          <span>{{ currentBooking?.contactName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">联系电话：</span>
          <span>{{ currentBooking?.contactPhone }}</span>
        </div>
        <div class="detail-item">
          <span class="label">房源信息：</span>
          <div class="house-info">
            <el-image
              v-if="currentBooking?.houseImages"
              :src="getFirstImage(currentBooking.houseImages)"
              :preview-src-list="getImageList(currentBooking.houseImages)"
              fit="cover"
              class="house-image"
            />
            <div class="house-detail">
              <div class="house-title">{{ currentBooking?.houseTitle }}</div>
            </div>
          </div>
        </div>
        <div class="detail-item">
          <span class="label">房源价格：</span>
          <span v-if="currentBooking?.housePrice">¥{{ currentBooking.housePrice }}/月</span>
        </div>
        <div class="detail-item">
          <span class="label">预约时间：</span>
          <span>{{ formatDateTime(currentBooking?.bookingTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">当前状态：</span>
          <el-tag :type="getStatusType(currentBooking?.status)">
            {{ getStatusText(currentBooking?.status) }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="label">备注信息：</span>
          <span>{{ currentBooking?.remark || '无' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">创建时间：</span>
          <span>{{ formatDateTime(currentBooking?.createdTime) }}</span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <template v-if="currentBooking?.status === 0">
            <el-button type="success" @click="handleConfirm(currentBooking)">
              确认预约
            </el-button>
            <el-button type="warning" @click="handleCancel(currentBooking)">
              取消预约
            </el-button>
          </template>
          <el-button 
            v-if="currentBooking?.status === 1" 
            type="primary" 
            @click="handleComplete(currentBooking)"
          >
            完成预约
          </el-button>
          <el-button 
            v-if="[2, 3, 4].includes(currentBooking?.status)"
            type="danger" 
            @click="handleDelete(currentBooking)"
          >
            删除预约
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
import { getImageUrl, getImageUrls } from '@/utils/image'

// 搜索表单
const searchForm = reactive({
  username: '',
  houseTitle: '',
  status: '',
  dateRange: []
})

// 列表数据
const loading = ref(false)
const bookingList = ref([])
const selectedBookings = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 详情对话框
const detailDialogVisible = ref(false)
const currentBooking = ref(null)

// 获取预约列表
const getBookingList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    // 添加搜索条件
    if (searchForm.username) {
      params.username = searchForm.username
    }
    if (searchForm.houseTitle) {
      params.houseTitle = searchForm.houseTitle
    }
    if (searchForm.status !== '') {
      params.status = searchForm.status
    }
    if (searchForm.dateRange?.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }

    const res = await request.get('/booking/list', { params })
    if (res.code === 200) {
      bookingList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取预约列表失败:', error)
    ElMessage.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索和重置
const handleSearch = () => {
  currentPage.value = 1
  getBookingList()
}

const resetSearch = () => {
  searchForm.username = ''
  searchForm.houseTitle = ''
  searchForm.status = ''
  searchForm.dateRange = []
  currentPage.value = 1
  getBookingList()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectedBookings.value = selection
}

// 分页操作
const handleSizeChange = (val) => {
  pageSize.value = val
  getBookingList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getBookingList()
}

// 状态处理
const getStatusType = (status) => {
  const map = {
    0: 'warning',   // 待确认
    1: 'primary',   // 已确认
    2: 'info',      // 已取消
    3: 'success',   // 已完成
    4: 'danger'     // 已拒绝
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '待确认',
    1: '已确认',
    2: '已取消',
    3: '已完成',
    4: '已拒绝'
  }
  return map[status] || '未知'
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 格式化日期
const formatDate = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 查看详情
const handleView = (row) => {
  currentBooking.value = { ...row }
  detailDialogVisible.value = true
}

// 确认预约
const handleConfirm = async (row) => {
  try {
    await request.put(`/booking/${row.id}/confirm`)
    ElMessage.success('预约确认成功')
    getBookingList()
    if (detailDialogVisible.value) {
      detailDialogVisible.value = false
    }
  } catch (error) {
    console.error('确认预约失败:', error)
    ElMessage.error(error.response?.data?.message || '确认预约失败')
  }
}

// 完成预约
const handleComplete = async (row) => {
  try {
    await request.put(`/booking/${row.id}/complete`)
    ElMessage.success('预约已完成')
    getBookingList()
  } catch (error) {
    console.error('完成预约失败:', error)
    ElMessage.error(error.response?.data?.message || '完成预约失败')
  }
}

// 取消预约
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
      type: 'warning'
    })
    await request.put(`/booking/${row.id}/cancel`)
    ElMessage.success('预约已取消')
    getBookingList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约失败:', error)
      ElMessage.error(error.response?.data?.message || '取消预约失败')
    }
  }
}

// 删除预约
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该预约记录吗？此操作不可恢复！', '提示', {
      type: 'warning'
    })
    await request.delete(`/booking/${row.id}`)
    ElMessage.success('删除成功')
    getBookingList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除预约失败:', error)
      ElMessage.error(error.response?.data?.message || '删除预约失败')
    }
  }
}

// 批量操作
const handleBatchConfirm = async () => {
  if (!selectedBookings.value.length) return
  
  try {
    const ids = selectedBookings.value.map(item => item.id)
    await request.put('/booking/batch/confirm', { ids })
    ElMessage.success('批量确认成功')
    getBookingList()
  } catch (error) {
    console.error('批量确认失败:', error)
    ElMessage.error('批量确认失败')
  }
}

const handleBatchCancel = async () => {
  if (!selectedBookings.value.length) return
  
  try {
    await ElMessageBox.confirm(`确定要取消选中的 ${selectedBookings.value.length} 个预约吗？`, '提示', {
      type: 'warning'
    })
    const ids = selectedBookings.value.map(item => item.id)
    await request.put('/booking/batch/cancel', { ids })
    ElMessage.success('批量取消成功')
    getBookingList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量取消失败:', error)
      ElMessage.error('批量取消失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (!selectedBookings.value.length) return
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedBookings.value.length} 个预约记录吗？`, '提示', {
      type: 'warning'
    })
    const ids = selectedBookings.value.map(item => item.id)
    await request.delete('/booking/batch', { data: { ids } })
    ElMessage.success('批量删除成功')
    getBookingList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 图片处理
const getFirstImage = (images) => {
  if (!images) return ''
  const imageList = images.split(',').filter(img => img)
  return imageList[0] ? getImageUrl(imageList[0]) : ''
}

const getImageList = (images) => {
  if (!images) return []
  return images.split(',')
    .filter(img => img)
    .map(img => getImageUrl(img))
}

onMounted(() => {
  getBookingList()
})
</script>

<style scoped lang="scss">
.booking-manage-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .operation-bar {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
  }

  .list-card {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .house-info {
      display: flex;
      gap: 10px;

      .house-image {
        width: 80px;
        height: 60px;
        border-radius: 4px;
        object-fit: cover;
      }

      .house-detail {
        .house-title {
          font-weight: bold;
          margin-bottom: 4px;
        }

        .house-price {
          color: #f56c6c;
          font-size: 14px;
          font-weight: bold;
        }
      }
    }

    .booking-time {
      font-size: 14px;
    }

    .status-tag {
      min-width: 64px;
      text-align: center;
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.booking-detail {
  .detail-item {
    margin-bottom: 16px;
    display: flex;
    align-items: flex-start;

    .label {
      width: 100px;
      color: #909399;
      flex-shrink: 0;
    }

    .house-info {
      display: flex;
      gap: 10px;
      flex: 1;

      .house-image {
        width: 120px;
        height: 90px;
        border-radius: 4px;
        object-fit: cover;
      }

      .house-detail {
        flex: 1;
        
        .house-title {
          font-weight: bold;
          margin-bottom: 4px;
        }
      }
    }
  }
}
</style> 