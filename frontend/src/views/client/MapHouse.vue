<template>
  <div class="map-house">
    <div class="search-bar">
      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索房源"
        clearable
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-cascader
        v-model="searchForm.area"
        :options="cascaderOptions"
        placeholder="选择区域"
        clearable
        class="area-select"
      />
      <el-select v-model="searchForm.price" placeholder="租金范围" clearable class="price-select">
        <el-option label="2000元以下" value="0-2000" />
        <el-option label="2000-4000元" value="2000-4000" />
        <el-option label="4000-6000元" value="4000-6000" />
        <el-option label="6000-8000元" value="6000-8000" />
        <el-option label="8000元以上" value="8000-999999" />
      </el-select>
      <el-select v-model="searchForm.type" placeholder="房型" clearable class="type-select">
        <el-option label="一室" value="1" />
        <el-option label="二室" value="2" />
        <el-option label="三室" value="3" />
        <el-option label="四室" value="4" />
        <el-option label="五室及以上" value="5" />
      </el-select>
      <div class="search-buttons">
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="RefreshLeft" @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="map-container">
      <div id="map-house" class="map"></div>

      <div class="house-list">
        <div class="house-list-header">
          <span class="house-count">找到 {{ houseList.length }} 套房源</span>
          <el-radio-group v-model="sortType" size="small">
            <el-radio-button label="default">默认</el-radio-button>
            <el-radio-button label="price_asc">价格从低到高</el-radio-button>
            <el-radio-button label="price_desc">价格从高到低</el-radio-button>
          </el-radio-group>
        </div>
        <div v-if="houseList.length > 0" class="house-items">
          <div
            v-for="house in sortedHouseList"
            :key="house.id"
            class="house-item"
            :class="{ active: activeHouseId === house.id }"
            @click="handleHouseClick(house)"
          >
            <el-image :src="getFirstImage(house.images)" class="house-image" fit="cover">
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="house-info">
              <h3 class="house-title">{{ house.title }}</h3>
              <div class="house-tags">
                <el-tag size="small" type="info">{{ house.roomCount }}室{{ house.hallCount }}厅</el-tag>
                <el-tag size="small" type="info">{{ house.area }}㎡</el-tag>
                <el-tag size="small" type="info">{{ house.district }}</el-tag>
              </div>
              <p class="house-address">
                <el-icon><Location /></el-icon>
                {{ house.address }}
              </p>
              <div class="house-bottom">
                <span class="house-price">¥{{ house.price }}<span class="price-unit">/月</span></span>
                <el-button type="primary" size="small" class="view-detail-btn" @click.stop="handleViewDetail(house)">
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无房源" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Picture, Location, RefreshLeft } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getImageUrl } from '@/utils/image'
import { cascaderOptions } from '@/utils/area-data'

const router = useRouter()
const map = ref(null)
const markers = ref([])
const houseList = ref([])
const activeHouseId = ref(null)
const searchForm = ref({
  keyword: '',
  area: [],
  price: '',
  type: ''
})

// 添加排序功能
const sortType = ref('default')
const sortedHouseList = computed(() => {
  const list = [...houseList.value]
  switch (sortType.value) {
    case 'price_asc':
      return list.sort((a, b) => a.price - b.price)
    case 'price_desc':
      return list.sort((a, b) => b.price - a.price)
    default:
      return list
  }
})

// 初始化百度地图
const initMap = () => {
  // 创建地图实例
  map.value = new BMap.Map('map-house')
  
  // 创建点坐标
  const point = new BMap.Point(116.404, 39.915) // 默认北京市中心
  
  // 初始化地图，设置中心点坐标和地图级别
  map.value.centerAndZoom(point, 12)
  
  // 开启鼠标滚轮缩放
  map.value.enableScrollWheelZoom(true)
  
  // 添加地图控件
  map.value.addControl(new BMap.NavigationControl())
  map.value.addControl(new BMap.ScaleControl())
  
  // 添加地图事件监听
  map.value.addEventListener('dragend', handleMapChange)
  map.value.addEventListener('zoomend', handleMapChange)
}

// 处理地图视野变化
const handleMapChange = () => {
  // 获取当前地图视野范围
  const bounds = map.value.getBounds()
  const sw = bounds.getSouthWest()
  const ne = bounds.getNorthEast()
  
  // 根据视野范围获取房源
  loadHouseList({
    swLat: sw.lat,
    swLng: sw.lng,
    neLat: ne.lat,
    neLng: ne.lng
  })
}

// 加载房源列表
const loadHouseList = async (bounds) => {
  try {
    // 处理价格区间
    let minPrice, maxPrice
    if (searchForm.value.price) {
      [minPrice, maxPrice] = searchForm.value.price.split('-').map(Number)
    }

    const params = {
      keyword: searchForm.value.keyword,
      province: searchForm.value.area?.[0],
      city: searchForm.value.area?.[1],
      district: searchForm.value.area?.[2],
      minPrice,
      maxPrice,
      type: searchForm.value.type,
      ...bounds
    }

    const res = await request.get('/house/map', { params })
    if (res.code === 200) {
      houseList.value = res.data
      updateMarkers()
    }
  } catch (error) {
    console.error('获取房源列表失败:', error)
    ElMessage.error('获取房源列表失败')
  }
}

// 更新地图标记点
const updateMarkers = () => {
  // 清除现有标记点
  markers.value.forEach(marker => {
    map.value.removeOverlay(marker)
  })
  markers.value = []

  // 添加新的标记点
  houseList.value.forEach(house => {
    if (house.latitude && house.longitude) {
      const point = new BMap.Point(house.longitude, house.latitude)
      const marker = new BMap.Marker(point)
      
      // 创建信息窗口
      const infoWindow = new BMap.InfoWindow(`
        <div class="map-info-window">
          <h4>${house.title}</h4>
          <p>${house.roomCount}室${house.hallCount}厅 | ${house.area}㎡</p>
          <p class="price">¥${house.price}/月</p>
        </div>
      `)
      
      // 添加标记点点击事件
      marker.addEventListener('click', () => {
        map.value.openInfoWindow(infoWindow, point)
        activeHouseId.value = house.id
      })
      
      map.value.addOverlay(marker)
      markers.value.push(marker)
    }
  })
}

// 处理房源点击
const handleHouseClick = (house) => {
  activeHouseId.value = house.id
  if (house.latitude && house.longitude) {
    const point = new BMap.Point(house.longitude, house.latitude)
    map.value.panTo(point)
  }
}

// 处理查看详情
const handleViewDetail = (house) => {
  router.push(`/house/${house.id}`)
}

// 处理搜索
const handleSearch = async () => {
  // 如果选择了地区，先定位到对应地区
  if (searchForm.value.area?.length > 0) {
    const address = searchForm.value.area.join('')
    const myGeo = new BMap.Geocoder()
    try {
      const result = await new Promise((resolve, reject) => {
        myGeo.getPoint(address, (point) => {
          if (point) {
            resolve(point)
          } else {
            reject(new Error('未找到该地址'))
          }
        })
      })
      
      // 根据选择的行政级别设置缩放级别
      let zoomLevel = 12 // 默认城市级别
      if (searchForm.value.area.length === 1) { // 省级
        zoomLevel = 8
      } else if (searchForm.value.area.length === 2) { // 市级
        zoomLevel = 10
      } else if (searchForm.value.area.length === 3) { // 区级
        zoomLevel = 13
      }
      
      // 平滑移动到目标位置并设置缩放级别
      map.value.centerAndZoom(result, zoomLevel)
    } catch (error) {
      console.error('地址解析失败:', error)
      ElMessage.warning('地址解析失败，将使用当前地图范围搜索')
    }
  }
  
  // 搜索当前地图范围内的房源
  handleMapChange()
}

// 监听地区选择变化
watch(() => searchForm.value.area, (newValue) => {
  if (newValue?.length > 0) {
    handleSearch()
  }
})

// 处理重置
const handleReset = () => {
  searchForm.value = {
    keyword: '',
    area: [],
    price: '',
    type: ''
  }
  handleMapChange()
}

// 获取房源第一张图片
const getFirstImage = (images) => {
  if (!images) return ''
  const imageList = images.split(',')
  return getImageUrl(imageList[0])
}

onMounted(() => {
  // 检查百度地图API是否已加载，如果已加载则立即初始化
  if (window.BMap) {
    initMap();
    handleMapChange();
  } else {
    // 定义回调函数，等待API加载完成
    const checkBMap = setInterval(() => {
      if (window.BMap) {
        clearInterval(checkBMap);
        initMap();
        handleMapChange();
      }
    }, 100);
    
    // 设置超时，防止无限等待
    setTimeout(() => {
      clearInterval(checkBMap);
      if (!window.BMap) {
        ElMessage.error('百度地图加载失败，请刷新页面重试');
      }
    }, 10000);
  }
})

onUnmounted(() => {
  // 清理地图实例
  if (map.value) {
    map.value.clearOverlays()
    map.value = null
  }
})
</script>

<style scoped>
.map-house {
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.search-bar {
  padding: 16px 24px;
  background: #fff;
  display: flex;
  gap: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  align-items: center;
}

.search-input {
  width: 280px;
}

.area-select {
  width: 200px;
}

.price-select,
.type-select {
  width: 140px;
}

.search-buttons {
  display: flex;
  gap: 8px;
}

.map-container {
  flex: 1;
  display: flex;
  position: relative;
  margin: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.map {
  flex: 1;
  height: 100%;
}

.house-list {
  width: 380px;
  background: #fff;
  border-left: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
}

.house-list-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.house-count {
  font-size: 14px;
  color: #606266;
}

.house-items {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.house-item {
  display: flex;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 16px;
  border: 1px solid #f0f0f0;
  background: #fff;
  position: relative;
}

.house-item:hover,
.house-item.active {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.house-image {
  width: 140px;
  height: 100px;
  border-radius: 4px;
  margin-right: 16px;
  object-fit: cover;
}

.house-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.house-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.house-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.house-address {
  margin: 8px 0;
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.house-bottom {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.house-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.price-unit {
  font-size: 14px;
  font-weight: normal;
  margin-left: 2px;
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

:deep(.map-info-window) {
  padding: 12px;
  max-width: 300px;
}

:deep(.map-info-window h4) {
  margin: 0 0 8px;
  font-size: 16px;
  color: #303133;
}

:deep(.map-info-window p) {
  margin: 4px 0;
  font-size: 13px;
  color: #606266;
}

:deep(.map-info-window .price) {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
  margin-top: 8px;
}
</style> 