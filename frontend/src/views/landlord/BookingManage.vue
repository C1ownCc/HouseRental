<template>
  <div class="booking-manage">
    <div class="header">
      <h2>预约管理</h2>
    </div>

    <el-table
      v-loading="loading"
      :data="bookingList"
      style="width: 100%"
    >
      <el-table-column label="房源信息" min-width="200">
        <template #default="{ row }">
          <div class="house-info">
            <el-image 
              :src="getFirstImage(row.houseImages)" 
              class="house-image" 
              fit="cover"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="house-detail">
              <h4>{{ row.houseTitle }}</h4>
              <p class="price">¥{{ row.housePrice }}/月</p>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="预约时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.bookingTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="username" label="预约人" width="120" />
      <el-table-column prop="contactName" label="联系人" width="120" />
      <el-table-column prop="contactPhone" label="联系电话" width="120" />
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button-group v-if="row.status === 0">
            <el-button type="success" link @click="handleConfirm(row)">确认</el-button>
            <el-button type="danger" link @click="handleReject(row)">拒绝</el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 处理预约对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%">
      <el-form :model="form" ref="formRef" label-width="80px">
        <el-form-item label="备注">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            rows="3"
            placeholder="请输入备注信息（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button :type="form.status === 1 ? 'success' : 'danger'" @click="handleSubmit">
            确定{{ form.status === 1 ? '确认' : '拒绝' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getImageUrl } from '@/utils/image'

const loading = ref(false)
const bookingList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)

// 表单数据
const form = ref({
  id: null,
  status: null,
  remark: ''
})

// 对话框标题
const dialogTitle = computed(() => {
  return form.value.status === 1 ? '确认预约' : '拒绝预约'
})

// 获取预约列表
const loadBookingList = async () => {
  loading.value = true
  try {
    const res = await request.get('/booking/landlord/list', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    if (res.code === 200) {
      bookingList.value = res.data.list
      total.value = res.data.total
      console.log('预约列表数据:', bookingList.value)
    }
  } catch (error) {
    console.error('获取预约列表失败:', error)
    ElMessage.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

// 获取第一张图片
const getFirstImage = (images) => {
  if (!images) return ''
  const imageList = images.split(',')
  return getImageUrl(imageList[0])
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    0: 'warning',   // 待确认
    1: 'success',   // 已确认
    2: 'info',      // 已取消
    3: 'primary',   // 已完成
    4: 'danger'     // 已拒绝
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '待确认',
    1: '已确认',
    2: '已取消',
    3: '已完成',
    4: '已拒绝'
  }
  return texts[status] || '未知'
}

// 确认预约
const handleConfirm = (row) => {
  form.value = {
    id: row.id,
    status: 1,
    remark: ''
  }
  dialogVisible.value = true
}

// 拒绝预约
const handleReject = (row) => {
  form.value = {
    id: row.id,
    status: 4,
    remark: ''
  }
  dialogVisible.value = true
}

// 提交处理结果
const handleSubmit = async () => {
  try {
    const res = await request.put(`/booking/${form.value.id}/${form.value.status === 1 ? 'confirm' : 'reject'}`)
    
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadBookingList()
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val
  loadBookingList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadBookingList()
}

onMounted(() => {
  loadBookingList()
})
</script>

<style scoped>
.booking-manage {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.house-info {
  display: flex;
  gap: 12px;
  align-items: center;
}

.house-image {
  width: 80px;
  height: 60px;
  border-radius: 4px;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
}

.house-detail {
  flex: 1;
  min-width: 0;
}

.house-detail h4 {
  margin: 0 0 4px;
  font-size: 14px;
  line-height: 1.4;
}

.house-detail .price {
  margin: 0;
  font-size: 14px;
  color: #f56c6c;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 