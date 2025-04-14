<template>
  <div class="house-list">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <span>房源列表</span>
          <div class="search-form">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索房源"
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
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
              style="width: 300px"
            />
            <el-select v-model="searchForm.price" placeholder="租金" clearable style="width: 150px">
              <el-option label="全部价格" value="" />
              <el-option label="1000元以下" value="0-1000" />
              <el-option label="1000-2000元" value="1000-2000" />
              <el-option label="2000-3000元" value="2000-3000" />
              <el-option label="3000-5000元" value="3000-5000" />
              <el-option label="5000元以上" value="5000-999999" />
            </el-select>
            <el-select v-model="searchForm.type" placeholder="房型" clearable style="width: 150px">
              <el-option label="全部房型" value="" />
              <el-option label="一室" value="1" />
              <el-option label="两室" value="2" />
              <el-option label="三室" value="3" />
              <el-option label="四室及以上" value="4" />
            </el-select>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
        </div>
      </template>
      
      <el-row :gutter="20" v-if="houseList.length > 0">
        <el-col v-for="house in houseList" :key="house.id" :span="8" style="margin-bottom: 20px">
          <el-card :body-style="{ padding: '0px' }" shadow="hover" class="house-card">
            <img :src="getHouseImage(house.images)" class="house-image">
            <div class="house-info">
              <h3>{{ house.title }}</h3>
              <p class="price">¥{{ house.price }}/月</p>
              <p class="info">
                {{ house.area }}㎡ | {{ house.roomCount }}室{{ house.hallCount }}厅 | {{ house.orientation }}
              </p>
              <p class="address">{{ house.province }}{{ house.city }}{{ house.district }}{{ house.address }}</p>
              <div class="bottom">
                <el-button type="primary" link @click="viewDetail(house.id)">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-empty
        v-else
        description="暂无符合条件的房源"
        :image-size="200"
      >
        <template #image>
          <el-icon style="font-size: 80px; color: #909399"><House /></el-icon>
        </template>
        <el-button type="primary" @click="resetSearch">重置搜索条件</el-button>
      </el-empty>
      
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, House } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { getImageUrl } from '@/utils/image'
import { cascaderOptions } from '@/utils/area-data'
import { useHouseStore } from '@/store'

const router = useRouter()
const route = useRoute()
const houseStore = useHouseStore()
const houseList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 区域选项
const areaOptions = cascaderOptions

// 搜索表单
const searchForm = reactive({
  keyword: '',
  area: [],
  price: '',
  type: ''
})

// 初始化搜索表单
const initSearchForm = () => {
  // 优先从store中获取搜索条件
  const storeParams = houseStore.searchParams
  if (storeParams) {
    searchForm.keyword = storeParams.keyword || ''
    searchForm.area = storeParams.province && storeParams.city && storeParams.district 
      ? [storeParams.province, storeParams.city, storeParams.district] 
      : []
    searchForm.price = storeParams.price || ''
    searchForm.type = storeParams.type || ''
  } else {
    // 如果store中没有，则从路由查询参数中获取
    const { keyword, province, city, district, price, type } = route.query
    searchForm.keyword = keyword || ''
    searchForm.area = province && city && district ? [province, city, district] : []
    searchForm.price = price || ''
    searchForm.type = type || ''
  }
}

const loadHouseList = async () => {
  try {
    // 处理价格区间
    let minPrice, maxPrice
    if (searchForm.price) {
      [minPrice, maxPrice] = searchForm.price.split('-').map(Number)
    }

    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.keyword,
      province: searchForm.area?.length >= 1 ? searchForm.area[0] : undefined,
      city: searchForm.area?.length >= 2 ? searchForm.area[1] : undefined,
      district: searchForm.area?.length >= 3 ? searchForm.area[2] : undefined,
      minPrice,
      maxPrice,
      type: searchForm.type ? parseInt(searchForm.type) : undefined,
      ownerId: route.query.ownerId
    }

    // 移除值为undefined的参数
    Object.keys(params).forEach(key => {
      if (params[key] === undefined) {
        delete params[key]
      }
    })

    const res = await request.get('/house/public/list', { params })
    if (res.code === 200 && res.data.records && res.data.records.length > 0) {
      houseList.value = res.data.records.map(house => ({
        ...house,
        images: house.images ? house.images.split(',') : []
      }))
      total.value = res.data.total
    } else {
      houseList.value = []
      total.value = 0
      ElMessage.warning('暂无数据')
    }
  } catch (error) {
    console.error('获取房源列表失败:', error)
    ElMessage.error('获取房源列表失败')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadHouseList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadHouseList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadHouseList()
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.area = []
  searchForm.price = ''
  searchForm.type = ''
  currentPage.value = 1
  loadHouseList()
}

const viewDetail = (id) => {
  router.push(`/house/${id}`)
}

// 处理房源图片
const getHouseImage = (images) => {
  if (!images || images.length === 0) return ''
  return getImageUrl(images[0])
}

onMounted(() => {
  initSearchForm()
  loadHouseList()
})
</script>

<style scoped>
.house-list {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header span {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.search-form {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.house-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.house-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.house-image {
  width: 100%;
  height: 220px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.house-card:hover .house-image {
  transform: scale(1.05);
}

.house-info {
  padding: 20px;
}

h3 {
  margin: 10px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
  margin: 10px 0;
}

.info {
  color: #606266;
  font-size: 14px;
  margin: 8px 0;
}

.address {
  color: #909399;
  font-size: 14px;
  margin: 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bottom {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
  text-align: right;
}

.pagination {
  margin-top: 30px;
  text-align: center;
  padding: 20px 0;
}
</style> 