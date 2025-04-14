<template>
  <div class="user-center-container">
    <el-row :gutter="20">
      <!-- 左侧导航 -->
      <el-col :span="6">
        <el-card class="menu-card">
          <div class="user-info">
            <el-avatar :size="80">
              <template v-if="userInfo.avatar">
                <img :src="userInfo.avatar" />
              </template>
              <template v-else>
                <el-icon><User /></el-icon>
              </template>
            </el-avatar>
            <h3>{{ userInfo.nickname || userInfo.username }}</h3>
          </div>
          <el-menu
            :default-active="activeMenu"
            class="menu-list"
            @select="handleMenuSelect"
            :aria-hidden="false"
            role="navigation"
          >
            <el-menu-item index="profile" role="menuitem">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="password" role="menuitem">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
            <!-- 租客专属功能 -->
            <template v-if="userStore.userInfo.role === 'user'">
              <el-menu-item index="bookings" role="menuitem">
                <el-icon><Calendar /></el-icon>
                <span>我的预约</span>
              </el-menu-item>
              <el-menu-item index="favorites" role="menuitem">
                <el-icon><Star /></el-icon>
                <span>收藏房源</span>
              </el-menu-item>
            </template>
            <!-- 房东专属功能 -->
            <template v-if="userStore.userInfo.role === 'landlord' || userStore.userInfo.role === 'admin'">
              <el-menu-item index="houses" role="menuitem" @click="router.push('/landlord/houses')">
                <el-icon><House /></el-icon>
                <span>房源管理</span>
              </el-menu-item>
            </template>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="18">
        <el-card class="content-card">
          <!-- 个人信息 -->
          <div v-show="activeMenu === 'profile'">
            <h2>个人信息</h2>
            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="100px"
              class="profile-form"
            >
              <el-form-item label="头像">
                <el-upload
                  class="avatar-uploader"
                  action="/api/upload/image"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                  name="file"
                >
                  <template v-if="profileForm.avatar">
                    <img :src="profileForm.avatar" class="avatar" />
                  </template>
                  <template v-else>
                    <el-avatar :size="100">
                      <el-icon><User /></el-icon>
                    </el-avatar>
                  </template>
                </el-upload>
              </el-form-item>
              <el-form-item label="用户名" prop="username">
                <el-input v-model="profileForm.username" disabled />
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="手机号码" prop="phone">
                <el-input v-model="profileForm.phone" placeholder="请输入手机号码" />
              </el-form-item>
              <el-form-item label="电子邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="请输入电子邮箱" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSaveProfile" :loading="profileSaving">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 修改密码 -->
          <div v-show="activeMenu === 'password'">
            <h2>修改密码</h2>
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              class="password-form"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword" :loading="passwordChanging">
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 我的预约 -->
          <div v-if="activeMenu === 'bookings' && userStore.userInfo.role === 'user'">
            <h2>我的预约</h2>
            <el-table
              v-loading="bookingsLoading"
              :data="bookingsList"
              style="width: 100%"
            >
              <el-table-column label="房源信息" min-width="200">
                <template #default="{ row }">
                  <div class="house-info">
                    <el-image 
                      :src="`/uploads/${row.houseImages?.split(',')[0]}`" 
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
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="预约时间" width="160">
                <template #default="{ row }">
                  {{ formatDate(row.bookingTime) }}
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
              <el-table-column prop="username" label="预约人" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getBookingStatusType(row.status)">
                    {{ getBookingStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button
                    v-if="row.status === 0 || row.status === 1"
                    type="danger"
                    link
                    @click="handleCancelBooking(row)"
                  >
                    取消预约
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination">
              <el-pagination
                v-model:current-page="bookingsQuery.page"
                v-model:page-size="bookingsQuery.pageSize"
                :total="bookingsTotal"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next"
                @size-change="handleBookingsSizeChange"
                @current-change="handleBookingsPageChange"
              />
            </div>
          </div>

          <!-- 收藏房源 -->
          <div v-show="activeMenu === 'favorites'">
            <h2>收藏房源</h2>
            <template v-if="favoritesList.length > 0">
              <el-row :gutter="20" class="favorites-list">
                <el-col
                  v-for="item in favoritesList"
                  :key="item.id"
                  :xs="24"
                  :sm="12"
                  :md="8"
                  :lg="8"
                  :xl="6"
                >
                  <el-card class="house-card" :body-style="{ padding: '0px' }">
                    <el-image 
                      :src="item.houseCoverImage ? getImageUrl(item.houseCoverImage.split(',')[0]) : ''" 
                      class="house-cover" 
                      fit="cover"
                    />
                    <div class="house-info">
                      <h3 class="house-title">{{ item.houseTitle }}</h3>
                      <div class="house-price">
                        <span class="price">¥{{ item.housePrice }}</span>
                        <span class="unit">/月</span>
                      </div>
                      <div class="house-tags">
                        <el-tag size="small" type="info">{{ item.houseRoomCount }}室</el-tag>
                        <el-tag size="small" type="success">{{ item.houseArea }}㎡</el-tag>
                      </div>
                      <div class="house-actions">
                        <el-button type="primary" link @click="handleViewHouse(item.houseId)">
                          查看详情
                        </el-button>
                        <el-button type="danger" link @click="handleRemoveFavorite(item)">
                          取消收藏
                        </el-button>
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </template>
            <template v-else>
              <el-empty description="暂无收藏房源" />
            </template>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { getImageUrl } from '@/utils/image'
import { User, Lock, Calendar, Star, House, Plus, Picture } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = ref('profile')

// 处理菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
  // 切换菜单时加载对应数据
  if (index === 'bookings') {
    getBookingsList()
  } else if (index === 'favorites') {
    getFavoritesList()
  }
}

// 用户信息
const userInfo = reactive({
  username: '',
  nickname: '',
  avatar: ''
})

// 个人信息表单
const profileFormRef = ref(null)
const profileForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: ''
})

// 表单校验规则
const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 保存状态
const profileSaving = ref(false)

// 密码修改表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码修改表单校验规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 密码修改状态
const passwordChanging = ref(false)

// 处理密码修改
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    passwordChanging.value = true
    
    const res = await request.put('/user/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      // 清空表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      // 重置表单校验状态
      passwordFormRef.value.resetFields()
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.message || '修改失败，请重试')
  } finally {
    passwordChanging.value = false
  }
}

// 预约列表相关
const bookingsLoading = ref(false)
const bookingsList = ref([])
const bookingsTotal = ref(0)
const bookingsQuery = reactive({
  page: 1,
  pageSize: 10
})

// 收藏列表相关
const favoritesLoading = ref(false)
const favoritesList = ref([])
const favoritesTotal = ref(0)
const favoritesQuery = reactive({
  page: 1,
  pageSize: 12
})

// 获取用户信息
const getUserInfo = async () => {
  try {
    const userStore = useUserStore()
    const success = await userStore.getCurrentUser()
    if (success) {
      // 更新表单数据和左侧显示的用户信息
      Object.assign(profileForm, userStore.userInfo)
      Object.assign(userInfo, userStore.userInfo)
    } else {
      throw new Error('获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error(error.message || '获取用户信息失败')
    // 如果获取信息失败，跳转到登录页
    router.push('/login')
  }
}

// 头像上传
const handleAvatarSuccess = async (response) => {
  if (response.code === 200) {
    console.log('头像上传成功，路径:', response.data)
    console.log('response', response);
    
    try {
      const res = await request.put('/user/profile', {
        ...profileForm,
        avatar: response.data  // 使用返回的data字段
      })
      if (res.code === 200) {
        ElMessage.success('头像更新成功')
        // 更新表单中的头像
        profileForm.avatar = response.data
        // 更新用户信息
        const userStore = useUserStore()
        userStore.setUserInfo(res.data)
        // 更新左侧显示的头像
        Object.assign(userInfo, res.data)
      } else {
        ElMessage.error(res.message || '更新用户信息失败')
      }
    } catch (error) {
      console.error('更新用户信息失败:', error)
      ElMessage.error('更新用户信息失败')
    }
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传头像图片只能是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 保存个人信息
const handleSaveProfile = async () => {
  if (!profileFormRef.value) return

  try {
    await profileFormRef.value.validate()
      profileSaving.value = true
    
    const res = await request.put('/user/profile', profileForm)
    if (res.code === 200) {
      ElMessage.success('个人信息更新成功')
      // 更新用户信息
      const userStore = useUserStore()
      userStore.setUserInfo(res.data)
    }
      } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error(error.message || '更新失败，请重试')
      } finally {
        profileSaving.value = false
      }
}

// 获取预约列表
const getBookingsList = async () => {
  try {
    bookingsLoading.value = true
    const res = await request.get('/booking/list', {
      params: bookingsQuery
    })
    console.log("res", res);
    
    if (res.code === 200) {
      bookingsList.value = res.data.list
      bookingsTotal.value = res.data.total
    }
  } catch (error) {
    console.error('获取预约列表失败:', error)
    ElMessage.error(error.message || '获取预约列表失败')
  } finally {
    bookingsLoading.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 获取预约状态类型
const getBookingStatusType = (status) => {
  const types = {
    0: 'warning',   // 待确认
    1: 'success',   // 已确认
    2: 'info',      // 已取消
    3: 'primary',   // 已完成
    4: 'danger'     // 已拒绝
  }
  return types[status] || 'info'
}

// 获取预约状态文本
const getBookingStatusText = (status) => {
  const texts = {
    0: '待确认',
    1: '已确认',
    2: '已取消',
    3: '已完成',
    4: '已拒绝'
  }
  return texts[status] || '未知'
}

// 取消预约
const handleCancelBooking = async (booking) => {
  try {
    const res = await request.put(`/booking/${booking.id}/cancel`)
    if (res.code === 200) {
      ElMessage.success('预约已取消')
      getBookingsList()
    }
  } catch (error) {
    console.error('取消预约失败:', error)
    ElMessage.error(error.message || '取消预约失败')
  }
}

// 获取收藏列表
const getFavoritesList = async () => {
  try {
    favoritesLoading.value = true
    const res = await request.get('/favorite/list', {
      params: favoritesQuery
    })
    if (res.code === 200) {
      favoritesList.value = res.data.list
      favoritesTotal.value = res.data.total
      console.log('收藏列表:', favoritesList.value)
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error(error.message || '获取收藏列表失败')
  } finally {
    favoritesLoading.value = false
  }
}

// 取消收藏
const handleRemoveFavorite = async (item) => {
  try {
    const res = await request.delete(`/favorite/${item.houseId}`)
    if (res.code === 200) {
      ElMessage.success('已取消收藏')
      getFavoritesList()
    }
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error(error.message || '取消收藏失败')
  }
}

// 查看房源
const handleViewHouse = (houseId) => {
  router.push(`/house/${houseId}`)
}

// 分页相关
const handleBookingsSizeChange = (val) => {
  bookingsQuery.pageSize = val
  bookingsQuery.page = 1
  getBookingsList()
}

const handleBookingsPageChange = (val) => {
  bookingsQuery.page = val
  getBookingsList()
}

const handleFavoritesSizeChange = (val) => {
  favoritesQuery.pageSize = val
  favoritesQuery.page = 1
  getFavoritesList()
}

const handleFavoritesPageChange = (val) => {
  favoritesQuery.page = val
  getFavoritesList()
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount && amount !== 0) return ''
  return amount.toLocaleString('zh-CN', {
    style: 'currency',
    currency: 'CNY',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 格式化面积
const formatArea = (area) => {
  if (!area && area !== 0) return ''
  return `${area}㎡`
}

// 在 mounted 时只加载用户信息
onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.user-center-container {
  padding: 20px;
}

.menu-card {
  height: 100%;
}

.user-info {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.user-info h3 {
  margin: 10px 0;
  font-size: 16px;
  color: #333;
}

.menu-list {
  border-right: none;
}

.content-card {
  min-height: 500px;
}

.content-card h2 {
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
}

.profile-form,
.password-form {
  max-width: 500px;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.favorites-list {
  margin: 0 -10px;
}

.favorites-list .el-col {
  padding: 10px;
}

.house-card {
  height: 100%;
  transition: all 0.3s;
}

.house-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.house-cover {
  width: 100%;
  height: 200px;
  display: block;
  object-fit: cover;
}

.house-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.house-title {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  height: 44px;
}

.house-price {
  margin: 12px 0;
}

.house-price .price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.house-price .unit {
  font-size: 14px;
  color: #909399;
  margin-left: 4px;
}

.house-tags {
  margin: 12px 0;
}

.house-tags .el-tag + .el-tag {
  margin-left: 8px;
}

.house-actions {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.house-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.house-image {
  width: 120px;
  height: 80px;
  border-radius: 4px;
}

.house-detail {
  flex: 1;
}

.house-detail h4 {
  margin: 0 0 8px;
  font-size: 14px;
  color: #333;
}

.house-detail p {
  margin: 0;
  font-size: 13px;
  color: #666;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 24px;
}

.house-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.house-image {
  width: 120px;
  height: 80px;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.house-detail {
  flex: 1;
  min-width: 0; /* 防止文本溢出 */
}

.house-detail h4 {
  margin: 0 0 8px;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.house-detail p {
  margin: 0;
  font-size: 13px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.house-detail .created-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.house-detail .remark {
  color: #666;
  font-size: 13px;
  margin-top: 4px;
}
</style> 