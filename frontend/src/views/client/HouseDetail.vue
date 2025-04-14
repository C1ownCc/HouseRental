<template>
  <div class="house-detail">
    <el-card v-if="house">
      <!-- 房源标题和基本信息 -->
      <div class="header">
        <h1>{{ house.title }}</h1>
        <div class="price">¥{{ house.price }}/月</div>
      </div>

      <!-- 房源图片轮播 -->
      <div class="image-carousel">
        <el-carousel :interval="4000" type="card" height="400px">
          <el-carousel-item v-for="(image, index) in houseImages" :key="index">
            <img :src="image" class="carousel-image" />
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 房源基本信息 -->
      <div class="info-section">
        <h2>基本信息</h2>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="面积">{{ house.area }}㎡</el-descriptions-item>
          <el-descriptions-item label="户型">{{ house.roomCount }}室{{ house.hallCount }}厅</el-descriptions-item>
          <el-descriptions-item label="朝向">{{ house.orientation }}</el-descriptions-item>
          <el-descriptions-item label="楼层">{{ house.floor }}层</el-descriptions-item>
          <el-descriptions-item label="装修">{{ house.decoration }}</el-descriptions-item>
          <el-descriptions-item label="房屋类型">{{ house.houseType }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 房源位置 -->
      <div class="location-section">
        <h2>房源位置</h2>
        <p>{{ house.address }}</p>
      </div>

      <!-- 房源描述 -->
      <div class="description-section">
        <h2>房源描述</h2>
        <p>{{ house.description }}</p>
      </div>

      <!-- 联系房东 -->
      <div class="contact-section">
        <h2>联系房东</h2>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="联系人">{{ house.contactName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ house.contactPhone }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleBook">预约看房</el-button>
        <el-button type="success" @click="handleLease" v-if="house.status === 1">申请签约</el-button>
        <el-button :type="house.isFavorite ? 'danger' : 'default'" @click="handleFavorite">
          <el-icon><Star /></el-icon>
          {{ house.isFavorite ? '取消收藏' : '收藏' }}
        </el-button>
      </div>
    </el-card>

    <!-- 预约看房对话框 -->
    <el-dialog v-model="bookingDialogVisible" title="预约看房" width="30%">
      <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="100px">
        <el-form-item label="预约时间" prop="bookingTime">
          <el-date-picker
            v-model="bookingForm.bookingTime"
            type="datetime"
            placeholder="选择预约时间"
            :disabled-date="disabledDate"
            :disabled-hours="disabledHours"
          />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="bookingForm.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="bookingForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="bookingForm.remark" type="textarea" rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="bookingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBooking">确认预约</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 签约对话框 -->
    <el-dialog v-model="leaseDialogVisible" title="申请签约" width="40%">
      <el-form :model="leaseForm" :rules="leaseRules" ref="leaseFormRef" label-width="100px">
        <el-form-item label="租期开始" prop="startDate">
          <el-date-picker
            v-model="leaseForm.startDate"
            type="date"
            placeholder="选择租期开始日期"
            :disabled-date="disabledStartDate"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="租期结束" prop="endDate">
          <el-date-picker
            v-model="leaseForm.endDate"
            type="date"
            placeholder="选择租期结束日期"
            :disabled-date="disabledEndDate"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="月租金" prop="monthlyRent">
          <el-input-number v-model="leaseForm.monthlyRent" :min="1" :precision="2" :step="100" :disabled="true"/>
        </el-form-item>
        <el-form-item label="押金" prop="deposit">
          <el-input-number v-model="leaseForm.deposit" :min="0" :precision="2" :step="1000" />
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-select v-model="leaseForm.paymentMethod" placeholder="请选择支付方式">
            <el-option label="月付" value="月付" />
            <el-option label="季付" value="季付" />
            <el-option label="半年付" value="半年付" />
            <el-option label="年付" value="年付" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="leaseForm.remark" type="textarea" rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="leaseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitLease">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getImageUrls } from '@/utils/image'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const house = ref(null)
const bookingDialogVisible = ref(false)
const bookingFormRef = ref(null)
const bookingForm = ref({
  bookingTime: '',
  contactName: '',
  contactPhone: '',
  remark: ''
})
const userStore = useUserStore()

// 签约相关变量
const leaseDialogVisible = ref(false)
const leaseFormRef = ref(null)
const leaseForm = ref({
  startDate: '',
  endDate: '',
  monthlyRent: 0,
  deposit: 0,
  paymentMethod: '月付',
  remark: ''
})

const bookingRules = {
  bookingTime: [
    { required: true, message: '请选择预约时间', trigger: 'change' }
  ],
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 签约表单验证规则
const leaseRules = {
  startDate: [
    { required: true, message: '请选择租期开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择租期结束日期', trigger: 'change' }
  ],
  deposit: [
    { required: true, message: '请输入押金金额', trigger: 'blur' }
  ],
  paymentMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}

// 解析房源图片
const houseImages = computed(() => {
  if (house.value && house.value.images) {
    return getImageUrls(house.value.images)
  }
  return []
})

// 加载房源详情
const loadHouseDetail = async () => {
  try {
    const res = await request.get(`/house/${route.params.id}`)
    if (res.code === 200) {
      house.value = res.data
      // 如果用户已登录，获取收藏状态
      if (userStore.isLoggedIn) {
        const favoriteRes = await request.get(`/favorite/check/${route.params.id}`)
        if (favoriteRes.code === 200) {
          house.value.isFavorite = favoriteRes.data
        }
      }
    } else {
      ElMessage.error(res.message || '获取房源详情失败')
    }
  } catch (error) {
    console.error('获取房源详情失败:', error)
    ElMessage.error('获取房源详情失败')
  }
}

// 处理预约看房
const handleBook = () => {
  // 检查用户是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!house.value) {
    ElMessage.warning('房源信息加载失败，请刷新页面重试')
    return
  }
  
  bookingDialogVisible.value = true
  // 初始化预设值
  if (userStore.userInfo) {
    bookingForm.value.contactName = userStore.userInfo.nickname || userStore.userInfo.username
    bookingForm.value.contactPhone = userStore.userInfo.phone || ''
  }
}

// 处理收藏/取消收藏
const handleFavorite = async () => {
  try {
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }

    if (house.value.isFavorite) {
      // 取消收藏
      const res = await request.delete(`/favorite/${house.value.id}`)
      if (res.code === 200) {
        ElMessage.success('已取消收藏')
        house.value.isFavorite = false
      }
    } else {
      // 添加收藏
      const res = await request.post(`/favorite/${house.value.id}`)
      if (res.code === 200) {
        ElMessage.success('收藏成功')
        house.value.isFavorite = true
      }
    }
  } catch (error) {
    // 如果是重复收藏的错误，更新状态为已收藏
    if (error.response?.data?.message?.includes('已收藏过该房源')) {
      ElMessage.warning('您已收藏过该房源')
      house.value.isFavorite = true
    } else {
      console.error('收藏操作失败:', error)
      ElMessage.error(error.response?.data?.message || '操作失败，请重试')
    }
  }
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 禁用非工作时间
const disabledHours = () => {
  const hours = []
  for (let i = 0; i < 24; i++) {
    if (i < 9 || i > 18) {
      hours.push(i)
    }
  }
  return hours
}

// 提交预约
const submitBooking = async () => {
  if (!bookingFormRef.value) return
  
  await bookingFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }

    if (!house.value) {
      ElMessage.warning('房源信息加载失败，请刷新页面重试')
      return
    }

    try {
      const res = await request.post('/booking', {
        userId: userStore.userInfo.id,
        houseId: house.value.id,
        bookingTime: bookingForm.value.bookingTime,
        contactName: bookingForm.value.contactName,
        contactPhone: bookingForm.value.contactPhone,
        remark: bookingForm.value.remark
      })
      
      if (res.code === 200) {
        ElMessage.success('预约成功')
        bookingDialogVisible.value = false
        bookingForm.value = { bookingTime: '', contactName: '', contactPhone: '', remark: '' }
      } else {
        ElMessage.error(res.message || '预约失败')
      }
    } catch (error) {
      console.error('预约失败:', error)
      ElMessage.error('预约失败')
    }
  })
}

// 处理申请签约
const handleLease = () => {
  // 检查用户是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!house.value) {
    ElMessage.warning('房源信息加载失败，请刷新页面重试')
    return
  }
  
  // 设置默认值
  leaseForm.value.monthlyRent = house.value.price
  leaseForm.value.deposit = house.value.deposit || house.value.price * 2 // 默认2个月租金作为押金
  leaseForm.value.startDate = new Date().toISOString().split('T')[0] // 今天
  leaseForm.value.endDate = new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toISOString().split('T')[0] // 1年后
  
  leaseDialogVisible.value = true
}

// 禁用签约开始日期（不能选择过去的日期）
const disabledStartDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 禁用签约结束日期（不能早于开始日期）
const disabledEndDate = (time) => {
  if (!leaseForm.value.startDate) {
    return time.getTime() < Date.now() - 8.64e7
  }
  const startDate = new Date(leaseForm.value.startDate)
  return time.getTime() < startDate.getTime()
}

// 提交签约申请
const submitLease = async () => {
  if (!leaseFormRef.value) return
  
  await leaseFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }

    if (!house.value) {
      ElMessage.warning('房源信息加载失败，请刷新页面重试')
      return
    }

    try {
      const res = await request.post('/lease-contract', {
        houseId: house.value.id,
        tenantId: userStore.userInfo.id,
        ownerId: house.value.ownerId,
        startDate: leaseForm.value.startDate,
        endDate: leaseForm.value.endDate,
        monthlyRent: leaseForm.value.monthlyRent,
        deposit: leaseForm.value.deposit,
        paymentMethod: leaseForm.value.paymentMethod,
        remark: leaseForm.value.remark
      })
      
      if (res.code === 200) {
        ElMessage.success('签约申请已提交，请等待处理')
        leaseDialogVisible.value = false
        leaseForm.value = { 
          startDate: '',
          endDate: '',
          monthlyRent: 0,
          deposit: 0,
          paymentMethod: '月付',
          remark: ''
        }
        // 跳转到用户中心的租约页面
        router.push('/contracts')
      } else {
        ElMessage.error(res.message || '签约申请提交失败')
      }
    } catch (error) {
      console.error('签约申请提交失败:', error)
      ElMessage.error(error.response?.data?.message || '签约申请提交失败，请重试')
    }
  })
}

onMounted(() => {
  loadHouseDetail()
})
</script>

<style scoped>
.house-detail {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}

.image-carousel {
  margin-bottom: 30px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-section,
.location-section,
.description-section,
.contact-section {
  margin-bottom: 30px;
}

h2 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #303133;
}

.description-section p {
  line-height: 1.6;
  color: #606266;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

:deep(.el-carousel__item) {
  border-radius: 6px;
  overflow: hidden;
}
</style> 