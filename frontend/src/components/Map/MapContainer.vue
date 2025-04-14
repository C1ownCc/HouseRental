<template>
  <div class="map-container" :style="{ height: height }">
    <div ref="mapRef" class="map-wrapper"></div>
    <div v-if="showToolbar" class="map-toolbar">
      <el-button-group>
        <el-button :icon="ZoomIn" @click="handleZoomIn">放大</el-button>
        <el-button :icon="ZoomOut" @click="handleZoomOut">缩小</el-button>
        <el-button :icon="Position" @click="handleLocate">定位</el-button>
      </el-button-group>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { ZoomIn, ZoomOut, Position } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  height: {
    type: String,
    default: '400px'
  },
  center: {
    type: Array,
    default: () => [116.397428, 39.90923]
  },
  zoom: {
    type: Number,
    default: 13
  },
  showToolbar: {
    type: Boolean,
    default: true
  },
  markers: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:center', 'marker-click'])

const mapRef = ref(null)
const map = ref(null)
const markerList = ref([])

// 初始化地图
const initMap = () => {
  if (!window.TMap) {
    ElMessage.error('地图加载失败')
    return
  }

  map.value = new window.TMap.Map(mapRef.value, {
    zoom: props.zoom,
    center: new window.TMap.LatLng(props.center[1], props.center[0]),
    pitch: 0,
    rotation: 0
  })

  // 添加比例尺控件
  map.value.addControl(new window.TMap.ScaleControl({
    position: window.TMap.ControlPosition.BOTTOM_RIGHT
  }))

  // 监听地图移动
  map.value.on('center_changed', () => {
    const center = map.value.getCenter()
    emit('update:center', [center.lng, center.lat])
  })

  // 初始化标记点
  updateMarkers()
}

// 更新标记点
const updateMarkers = () => {
  // 清除旧的标记点
  markerList.value.forEach(marker => {
    marker.setMap(null)
  })
  markerList.value = []

  // 添加新的标记点
  const markerLayer = new window.TMap.MultiMarker({
    map: map.value,
    styles: {
      default: new window.TMap.MarkerStyle({
        width: 32,
        height: 32,
        anchor: { x: 16, y: 32 },
        src: '/marker.png'
      })
    },
    geometries: props.markers.map(markerData => ({
      id: markerData.id || Math.random().toString(36).substr(2),
      position: new window.TMap.LatLng(markerData.position[1], markerData.position[0]),
      properties: {
        title: markerData.title,
        content: markerData.content,
        extData: markerData
      }
    }))
  })

  // 添加信息窗体
  const infoWindow = new window.TMap.InfoWindow({
    map: map.value,
    position: new window.TMap.LatLng(0, 0),
    offset: { x: 0, y: -32 }
  })

  // 添加点击事件
  markerLayer.on('click', (evt) => {
    const markerData = evt.geometry.properties.extData
    emit('marker-click', markerData)

    if (markerData.content) {
      infoWindow.setContent(markerData.content)
      infoWindow.setPosition(evt.geometry.position)
      infoWindow.open()
    }
  })

  // 添加鼠标移入移出事件
  markerLayer.on('mouseover', (evt) => {
    const markerData = evt.geometry.properties.extData
    if (markerData.content) {
      infoWindow.setContent(markerData.content)
      infoWindow.setPosition(evt.geometry.position)
      infoWindow.open()
    }
  })

  markerLayer.on('mouseout', () => {
    infoWindow.close()
  })

  markerList.value.push(markerLayer)

  // 如果有标记点，自动调整视野以包含所有标记点
  if (props.markers.length > 0) {
    const bounds = new window.TMap.LatLngBounds()
    props.markers.forEach(marker => {
      bounds.extend(new window.TMap.LatLng(marker.position[1], marker.position[0]))
    })
    map.value.fitBounds(bounds, {
      padding: 50
    })
  }
}

// 工具栏功能
const handleZoomIn = () => {
  map.value.setZoom(map.value.getZoom() + 1)
}

const handleZoomOut = () => {
  map.value.setZoom(map.value.getZoom() - 1)
}

const handleLocate = () => {
  if (!navigator.geolocation) {
    ElMessage.error('浏览器不支持定位功能')
    return
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      const center = new window.TMap.LatLng(
        position.coords.latitude,
        position.coords.longitude
      )
      map.value.setCenter(center)
      map.value.setZoom(15)
      emit('update:center', [center.lng, center.lat])
      ElMessage.success('定位成功')
    },
    () => {
      ElMessage.error('定位失败')
    },
    {
      enableHighAccuracy: true,
      timeout: 5000,
      maximumAge: 0
    }
  )
}

// 监听标记点变化
watch(() => props.markers, () => {
  if (map.value) {
    updateMarkers()
  }
}, { deep: true })

// 监听中心点变化
watch(() => props.center, (newCenter) => {
  if (map.value && newCenter) {
    map.value.setCenter(new window.TMap.LatLng(newCenter[1], newCenter[0]))
  }
})

// 监听缩放级别变化
watch(() => props.zoom, (newZoom) => {
  if (map.value) {
    map.value.setZoom(newZoom)
  }
})

onMounted(() => {
  // 确保地图 API 已加载
  if (window.TMap) {
    initMap()
  } else {
    const checkTMap = setInterval(() => {
      if (window.TMap) {
        clearInterval(checkTMap)
        initMap()
      }
    }, 100)
  }
})

onUnmounted(() => {
  if (map.value) {
    map.value.destroy()
  }
})
</script>

<style scoped lang="scss">
.map-container {
  position: relative;
  width: 100%;

  .map-wrapper {
    width: 100%;
    height: 100%;
  }

  .map-toolbar {
    position: absolute;
    top: 10px;
    left: 10px;
    z-index: 100;
    background-color: white;
    padding: 5px;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  }
}
</style> 