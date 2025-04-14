<template>
  <div class="home">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-content">
        <h1>找到您理想的住所</h1>
        <div class="search-box">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入地址、小区名称"
            class="search-input"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-cascader
            v-model="searchForm.area"
            :options="areaOptions"
            :props="{
              expandTrigger: 'hover',
              value: 'value',
              label: 'label',
              children: 'children'
            }"
            placeholder="选择省/市/区"
            clearable
            class="search-input"
          />
          <el-select v-model="searchForm.price" placeholder="租金" clearable>
            <el-option label="全部价格" value="" />
            <el-option label="1000元以下" value="0-1000" />
            <el-option label="1000-2000元" value="1000-2000" />
            <el-option label="2000-3000元" value="2000-3000" />
            <el-option label="3000-5000元" value="3000-5000" />
            <el-option label="5000元以上" value="5000-999999" />
          </el-select>
          <el-select v-model="searchForm.type" placeholder="房型" clearable>
            <el-option label="全部房型" value="" />
            <el-option label="一室" value="1" />
            <el-option label="两室" value="2" />
            <el-option label="三室" value="3" />
            <el-option label="四室及以上" value="4" />
          </el-select>
          <el-button type="primary" @click="handleSearch" :loading="searching">搜索房源</el-button>
        </div>
      </div>
    </div>

    <!-- 推荐房源 -->
    <div class="recommend-section">
      <div class="section-title">
        <h2>热门房源推荐</h2>
        <router-link to="/house-list" class="more-link">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      <div class="house-list">
        <el-row :gutter="20">
          <el-col :span="6" v-for="house in recommendHouses" :key="house.id">
            <el-card class="house-card" :body-style="{ padding: '0px' }">
              <img :src="getHouseImage(house.images)" class="house-image">
              <div class="house-info">
                <h3 class="house-title">{{ house.title }}</h3>
                <p class="house-address">
                  <el-icon><Location /></el-icon>
                  {{ house.address }}
                </p>
                <div class="house-tags">
                  <el-tag size="small" v-for="tag in house.tags" :key="tag">{{ tag }}</el-tag>
                </div>
                <div class="house-price">
                  <span class="price">¥{{ house.price }}</span>
                  <span class="unit">/月</span>
                </div>
                <div class="house-actions">
                  <el-button type="primary" link @click="handleViewHouse(house.id)">
                    查看详情
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 热门房东 -->
    <div class="landlord-section">
      <div class="section-title">
        <h2>热门房东推荐</h2>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="landlord in popularLandlords" :key="landlord.id">
          <el-card class="landlord-card">
            <div class="landlord-avatar">
              <el-avatar :size="80" :src="landlord.avatar || '/avatar.png'" />
            </div>
            <div class="landlord-info">
              <h3>{{ landlord.nickname || landlord.username }}</h3>
              <p class="landlord-contact">
                <el-icon><Phone /></el-icon>
                {{ landlord.phone }}
              </p>
              <p class="landlord-contact">
                <el-icon><Message /></el-icon>
                {{ landlord.email }}
              </p>
              <el-button type="primary" @click="handleViewLandlord(landlord.id)">
                查看房源
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { useHouseStore } from '@/store'
import { Location, List, User, Message, Calendar, View, Search, ArrowRight, Picture, House, Phone } from '@element-plus/icons-vue'
import { getImageUrl } from '@/utils/image'
import { cascaderOptions } from '@/utils/area-data'
import { getPopularLandlords } from '@/api/user'

const router = useRouter()
const houseStore = useHouseStore()

// 搜索状态
const searching = ref(false)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  area: [],
  price: '',
  type: ''
})

// 区域选项
const areaOptions = cascaderOptions

// 推荐房源数据
const recommendHouses = ref([])

// 热门房东数据
const popularLandlords = ref([])

// 处理搜索
const handleSearch = async () => {
  searching.value = true
  try {
    const searchParams = {
      keyword: searchForm.keyword,
      province: searchForm.area[0],
      city: searchForm.area[1],
      district: searchForm.area[2],
      price: searchForm.price,
      type: searchForm.type
    }
    houseStore.setSearchParams(searchParams)
    await router.push({
      path: '/house-list',
      query: searchParams
    })
  } finally {
    searching.value = false
  }
}

// 获取推荐房源
const getRecommendHouses = async () => {
  try {
    const res = await request.get('/house/recommend')
    recommendHouses.value = res.data
  } catch (error) {
    console.error('获取推荐房源失败:', error)
  }
}

// 获取热门房东
const getPopularLandlordsList = async () => {
  try {
    const res = await getPopularLandlords()
    popularLandlords.value = res.data
  } catch (error) {
    console.error('获取热门房东失败:', error)
  }
}

// 处理房源图片
const getHouseImage = (images) => {
  if (!images) return ''
  const firstImage = images.split(',')[0]
  return getImageUrl(firstImage)
}

// 处理查看房源详情
const handleViewHouse = (houseId) => {
  router.push(`/house/${houseId}`)
}

// 处理查看房东房源
const handleViewLandlord = (landlordId) => {
  router.push({
    path: '/house-list',
    query: { ownerId: landlordId }
  })
}

onMounted(() => {
  getRecommendHouses()
  getPopularLandlordsList()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.search-section {
  height: 600px;
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), 
              url('/images/banner.jpeg');
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  text-align: center;
  position: relative;
  margin-bottom: 40px;
}

.search-content {
  width: 800px;
  padding: 0 20px;
}

.search-content h1 {
  font-size: 42px;
  font-weight: 600;
  margin-bottom: 30px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.search-box {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  padding: 20px;
  border-radius: 12px;
  display: flex;
  gap: 12px;
  align-items: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.search-input {
  width: 220px !important;
}

.recommend-section,
.landlord-section {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 0 20px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #ebeef5;
}

.section-title h2 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  position: relative;
}

.section-title h2::after {
  content: '';
  position: absolute;
  bottom: -17px;
  left: 0;
  width: 60px;
  height: 2px;
  background-color: #409EFF;
}

.more-link {
  color: #409EFF;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.house-list {
  padding: 20px;
}

.house-card {
  transition: all 0.3s ease;
  margin-bottom: 20px;
  border: none;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.house-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.house-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.house-info {
  padding: 20px;
}

.house-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 10px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.house-address {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 12px;
}

.house-tags {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.house-price {
  margin-bottom: 12px;
}

.price {
  font-size: 24px;
  font-weight: 600;
  color: #f56c6c;
}

.unit {
  font-size: 14px;
  color: #909399;
}

.house-actions {
  display: flex;
  justify-content: flex-end;
}

.landlord-card {
  text-align: center;
  padding: 30px 20px;
  transition: all 0.3s ease;
  border: none;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.landlord-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.landlord-avatar {
  margin-bottom: 20px;
}

.landlord-info h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #303133;
}

.landlord-contact {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #606266;
  margin-bottom: 10px;
}

:deep(.el-input__wrapper),
:deep(.el-select),
:deep(.el-cascader) {
  width: 180px !important;
}

:deep(.el-input__wrapper),
:deep(.el-select .el-input__wrapper),
:deep(.el-cascader .el-input__wrapper) {
  --el-input-bg-color: rgba(255, 255, 255, 0.9);
  height: 40px;
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-button--primary) {
  height: 40px;
  padding: 0 25px;
  font-weight: 500;
}

:deep(.el-avatar) {
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 响应式布局调整 */
@media screen and (max-width: 1200px) {
  .search-content {
    width: 100%;
    max-width: 900px;
  }
  
  .search-section {
    height: 500px;
  }
  
  .search-content h1 {
    font-size: 36px;
  }
}

@media screen and (max-width: 768px) {
  .search-section {
    height: auto;
    min-height: 400px;
    padding: 40px 0;
  }
  
  .search-content h1 {
    font-size: 28px;
  }
  
  .search-box {
    flex-direction: column;
    padding: 20px;
    width: 90%;
    margin: 0 auto;
  }
  
  .search-input,
  :deep(.el-select),
  :deep(.el-cascader) {
    width: 100% !important;
  }
  
  :deep(.el-button) {
    width: 100%;
  }
}
</style> 