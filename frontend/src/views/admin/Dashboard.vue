<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总房源数</span>
              <el-icon><House /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.totalHouses }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>已上架房源</span>
              <el-icon><Check /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.onlineHouses }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总用户数</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.totalUsers }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>今日新增</span>
              <el-icon><Plus /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.todayNewHouses }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 合同统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总合同数</span>
              <el-icon><Document /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.totalContracts || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>已生效合同</span>
              <el-icon><Check /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.activeContracts || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>待签署合同</span>
              <el-icon><Loading /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ stats.pendingContracts || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>已收租金</span>
              <el-icon><Money /></el-icon>
            </div>
          </template>
          <div class="card-value">¥{{ stats.totalRent || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图表 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近7天趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="trendOption" autoresize />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>房型分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="roomTypeOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 合同和租金分布 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>合同状态分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="contractStatusOption" autoresize />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>每月租金收入</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="monthlyRentOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 价格分布 -->
    <el-row class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>租金分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="priceOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, provide } from 'vue'
import { House, Check, User, Plus, Document, Loading, Money } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart, { THEME_KEY } from 'vue-echarts'
import { ElMessage } from 'element-plus'

// 注册必要的组件
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 提供主题
provide(THEME_KEY, 'light')

// 统计数据
const stats = ref({
  totalHouses: 0,
  onlineHouses: 0,
  totalUsers: 0,
  todayNewHouses: 0
})

// 添加loading变量的定义
const loading = ref(false)

// 趋势数据
const trend = ref({
  dates: [],
  houses: [],
  users: []
})

// 分布数据
const distribution = ref({
  roomTypes: [],
  priceRanges: []
})

// 获取统计数据
const getStats = () => {
  loading.value = true
  
  // 设置默认值，避免显示0或者undefined
  stats.value = {
    totalHouses: 0,
    onlineHouses: 0,
    totalUsers: 0,
    todayNewHouses: 0,
    totalContracts: 0,
    activeContracts: 0,
    pendingContracts: 0,
    totalRent: 0
  }
  
  request.get('/admin/stats')
    .then(res => {
      console.log('基础统计数据:', res)
      if (res && res.data) {
        // 将返回的数据结构合并到stats对象中
        Object.assign(stats.value, res.data)
      }
    })
    .catch(error => {
      console.error('获取统计数据完整错误:', error)
      ElMessage.error('获取统计数据失败: ' + (error.message || '未知错误'))
    })
    .finally(() => {
      loading.value = false
    })

  // 获取合同状态分布数据
  request.get('/admin/contract/stats')
    .then(res => {
      console.log('合同统计数据:', res)
      if (res && res.data) {
        // 将返回的数据结构合并到stats对象中
        Object.assign(stats.value, res.data)
      }
    })
    .catch(error => {
      console.error('获取合同统计数据完整错误:', error)
      ElMessage.error('获取合同统计数据失败: ' + (error.message || '未知错误'))
    })
    
  // 获取每月租金收入数据
  request.get('/admin/contract/monthly-rent')
    .then(res => {
      console.log('月租金数据:', res)
      if (res && res.data && Array.isArray(res.data.data)) {
        const monthlyRentData = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]; // 初始化12个月的数据
        
        // 填充获取到的数据
        res.data.data.forEach(item => {
          if (item.month >= 1 && item.month <= 12) {
            monthlyRentData[item.month - 1] = item.amount || 0;
          }
        });
        
        // 更新图表数据
        monthlyRentOption.value.series[0].data = monthlyRentData;
      }
    })
    .catch(error => {
      console.error('获取月租金数据完整错误:', error)
    })
}

// 获取趋势数据
const getTrend = async () => {
  try {
    const res = await request.get('/api/dashboard/trend')
    if (res.code === 200) {
      trend.value = res.data
    }
  } catch (error) {
    console.error('获取趋势数据失败:', error)
  }
}

// 获取分布数据
const getDistribution = async () => {
  try {
    const res = await request.get('/api/dashboard/distribution')
    if (res.code === 200) {
      distribution.value = res.data
    }
  } catch (error) {
    console.error('获取分布数据失败:', error)
  }
}

// 初始化
onMounted(() => {
  getStats()
  getTrend()
  getDistribution()
})

// 趋势图表配置
const trendOption = computed(() => ({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['新增房源', '新增用户']
  },
  xAxis: {
    type: 'category',
    data: trend.value.dates
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '新增房源',
      type: 'line',
      data: trend.value.houses,
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      }
    },
    {
      name: '新增用户',
      type: 'line',
      data: trend.value.users,
      smooth: true,
      itemStyle: {
        color: '#67C23A'
      }
    }
  ]
}))

// 房型分布图表配置
const roomTypeOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  series: [
    {
      type: 'pie',
      radius: ['50%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: distribution.value.roomTypes
    }
  ]
}))

// 租金分布图表配置
const priceOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  xAxis: {
    type: 'category',
    data: distribution.value.priceRanges.map(item => item.name)
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      type: 'bar',
      data: distribution.value.priceRanges.map(item => item.value),
      itemStyle: {
        color: '#409EFF'
      }
    }
  ]
}))

// 合同状态分布图表配置
const contractStatusOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  series: [
    {
      type: 'pie',
      radius: ['50%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: stats.value.activeContracts, name: '已生效合同' },
        { value: stats.value.pendingContracts, name: '待签署合同' }
      ]
    }
  ]
}))

// 每月租金收入图表配置
const monthlyRentOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      type: 'bar',
      data: [12000, 20000, 15000, 25000, 18000, 30000, 22000, 35000, 28000, 40000, 32000, 50000],
      itemStyle: {
        color: '#409EFF'
      }
    }
  ]
}))
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card :deep(.card-header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-card .card-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  margin-top: 10px;
}

.chart-row {
  margin-top: 20px;
}

.chart-container {
  height: 300px;
  width: 100%;
}

:deep(.el-card__header) {
  padding: 10px 20px;
  border-bottom: 1px solid #ebeef5;
  box-sizing: border-box;
}
</style> 