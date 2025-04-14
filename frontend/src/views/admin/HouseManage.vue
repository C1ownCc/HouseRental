<template>
  <div class="house-manage">
    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="searchForm.keyword" placeholder="标题/地址/描述" clearable />
      </el-form-item>
      <el-form-item label="区域">
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
      </el-form-item>
      <el-form-item label="房型">
        <el-select
          v-model="searchForm.type"
          placeholder="选择房型"
          clearable
          style="width: 120px"
        >
          <el-option
            v-for="item in typeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select
          v-model="searchForm.status"
          placeholder="选择状态"
          clearable
          style="width: 120px"
        >
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="operation-bar">
      <el-button type="primary" @click="handleAdd">新增房源</el-button>
      <el-button type="success" @click="handleBatchOnline">批量上架</el-button>
      <el-button type="warning" @click="handleBatchOffline">批量下架</el-button>
      <el-button type="danger" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="houseList"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="房源标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="area" label="面积(㎡)" width="100" />
      <el-table-column prop="price" label="租金(元/月)" width="120" />
      <el-table-column prop="deposit" label="押金(元)" width="100" />
      <el-table-column prop="roomCount" label="房型" width="100">
        <template #default="{ row }">
          {{ formatRoomType(row.roomCount) }}
        </template>
      </el-table-column>
      <el-table-column prop="address" label="地址" min-width="250" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.province }}{{ row.city }}{{ row.district }}{{ row.address }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ formatStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 0"
            type="success"
            size="small"
            @click="handleOnline(row.id)"
          >
            上架
          </el-button>
          <el-button
            v-else
            type="warning"
            size="small"
            @click="handleOffline(row.id)"
          >
            下架
          </el-button>
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">
            删除
          </el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="editMode ? '编辑房源' : '新增房源'"
      v-model="dialogVisible"
      width="80%"
      @close="handleDialogClose"
    >
      <el-form
        ref="houseFormRef"
        :model="houseForm"
        :rules="houseRules"
        label-width="100px"
        class="house-form"
      >
        <el-form-item label="房源标题" prop="title">
          <el-input v-model="houseForm.title" placeholder="请输入房源标题" />
        </el-form-item>

        <!-- 地址选择 -->
        <el-form-item label="所在区域" required>
          <el-cascader
            v-model="selectedArea"
            :options="areaOptions"
            :props="{
              expandTrigger: 'hover',
              value: 'value',
              label: 'label',
              children: 'children'
            }"
            @change="handleAreaChange"
            placeholder="请选择所在区域"
          />
        </el-form-item>

        <el-form-item label="详细地址" prop="address">
          <el-input 
            v-model="houseForm.address" 
            placeholder="请输入详细地址，如：xx小区xx栋xx单元xx号"
            @blur="handleAddressBlur"
          >
            <template #prepend>
              <span style="white-space: nowrap;">{{ houseForm.province }}{{ houseForm.city }}{{ houseForm.district }}</span>
            </template>
          </el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="房间数" prop="roomCount">
              <el-input-number v-model="houseForm.roomCount" :min="1" :max="10" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="厅室数" prop="hallCount">
              <el-input-number v-model="houseForm.hallCount" :min="0" :max="5" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="卫生间数" prop="bathroomCount">
              <el-input-number v-model="houseForm.bathroomCount" :min="1" :max="5" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="面积" prop="area">
              <el-input-number v-model="houseForm.area" :min="1" :precision="2" :step="0.5" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="租金" prop="price">
              <el-input-number v-model="houseForm.price" :min="1" :precision="2" :step="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="押金" prop="deposit">
              <el-input-number v-model="houseForm.deposit" :min="0" :precision="2" :step="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所在楼层" prop="floor">
              <el-input-number v-model="houseForm.floor" :min="1" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总楼层" prop="totalFloor">
              <el-input-number v-model="houseForm.totalFloor" :min="1" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="有无电梯" prop="hasElevator">
              <el-switch v-model="houseForm.hasElevator" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="朝向" prop="orientation">
              <el-select v-model="houseForm.orientation" placeholder="选择朝向" style="width: 100%">
                <el-option label="朝南" value="朝南" />
                <el-option label="朝北" value="朝北" />
                <el-option label="朝东" value="朝东" />
                <el-option label="朝西" value="朝西" />
                <el-option label="东南" value="东南" />
                <el-option label="西南" value="西南" />
                <el-option label="东北" value="东北" />
                <el-option label="西北" value="西北" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="装修情况" prop="decoration">
              <el-select v-model="houseForm.decoration" placeholder="选择装修情况" style="width: 100%">
                <el-option label="毛坯" value="毛坯" />
                <el-option label="简装" value="简装" />
                <el-option label="精装" value="精装" />
                <el-option label="豪装" value="豪装" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="配套设施" prop="facilities">
          <el-checkbox-group v-model="houseForm.facilities">
            <el-checkbox value="wifi">无线网络</el-checkbox>
            <el-checkbox value="tv">电视</el-checkbox>
            <el-checkbox value="ac">空调</el-checkbox>
            <el-checkbox value="washer">洗衣机</el-checkbox>
            <el-checkbox value="fridge">冰箱</el-checkbox>
            <el-checkbox value="microwave">微波炉</el-checkbox>
            <el-checkbox value="desk">书桌</el-checkbox>
            <el-checkbox value="wardrobe">衣柜</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="房源图片" prop="images">
          <el-upload
            v-model:file-list="houseForm.images"
            action="/api/upload/image"
            list-type="picture-card"
            :limit="10"
            :headers="uploadHeaders"
            name="file"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-exceed="handleUploadExceed"
            :on-remove="handleUploadRemove"
            :before-upload="beforeUpload"
            accept=".jpg,.jpeg,.png,.gif"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                只能上传jpg/png/gif文件，且不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="houseForm.contactName" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="houseForm.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="房源描述" prop="description">
          <el-input
            v-model="houseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入房源描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import { cascaderOptions } from '@/utils/area-data'
import { getImageUrl, getImageUrls, removeUploadsPrefixFromList } from '@/utils/image'
import { debounce } from 'lodash'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const houseList = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const editMode = ref(false)
const houseFormRef = ref(null)
const userStore = useUserStore()

const searchForm = ref({
  keyword: '',
  area: [],
  type: undefined,
  status: undefined
})

const typeOptions = [
  { label: '一室', value: 1 },
  { label: '二室', value: 2 },
  { label: '三室', value: 3 },
  { label: '四室', value: 4 },
  { label: '五室及以上', value: 5 }
]

const statusOptions = [
  { label: '已下架', value: 0 },
  { label: '已上架', value: 1 }
]

const areaOptions = cascaderOptions

const selectedArea = ref([])

// 初始化表单数据
const initForm = () => {
  return {
    title: '',
    province: '',
    city: '',
    district: '',
    address: '',
    area: null,
    price: null,
    deposit: null,
    roomCount: null,
    hallCount: null,
    bathroomCount: null,
    floor: null,
    totalFloor: null,
    hasElevator: 1,
    orientation: '',
    decoration: '',
    facilities: [],
    description: '',
    contactName: '',
    contactPhone: '',
    images: [], // 初始化为空数组
    status: 1
  }
}

// 表单数据
const houseForm = ref(initForm())

// 表单校验规则
const houseRules = {
  title: [{ required: true, message: '请输入房源标题', trigger: 'blur' }],
  province: [{ required: true, message: '请选择省份', trigger: 'change' }],
  city: [{ required: true, message: '请选择城市', trigger: 'change' }],
  district: [{ required: true, message: '请选择区域', trigger: 'change' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }],
  price: [{ required: true, message: '请输入租金', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 重置表单
const resetForm = () => {
  if (houseFormRef.value) {
    houseFormRef.value.resetFields()
  }
  selectedArea.value = []
  houseForm.value = initForm()
}

// 处理新增
const handleAdd = () => {
  editMode.value = false
  resetForm()
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  editMode.value = true
  nextTick(() => {
    if (houseFormRef.value) {
      houseFormRef.value.resetFields()
      Object.assign(houseForm.value, row)
      
      // 设置区域选择器的值
      selectedArea.value = [row.province, row.city, row.district]
      
      // 处理配套设施
      if (row.facilities) {
        try {
          houseForm.value.facilities = JSON.parse(row.facilities)
        } catch (e) {
          houseForm.value.facilities = []
        }
      }
      
      // 处理图片
      if (row.images) {
        const imageUrls = getImageUrls(row.images)
        houseForm.value.images = imageUrls.map(url => ({
          name: url.split('/').pop(),
          url: url
        }))
      } else {
        houseForm.value.images = []
      }
    }
  })
  dialogVisible.value = true
}

// 处理查看
const handleView = (row) => {
  // 跳转到房源详情页
  window.open(`/house/${row.id}`)
}

// 处理区域选择变化
const handleAreaChange = (value) => {
  if (value && value.length === 3) {
    houseForm.value.province = value[0]
    houseForm.value.city = value[1]
    houseForm.value.district = value[2]
    // 如果地址已经填写，则触发地址解析
    if (houseForm.value.address) {
      handleAddressBlur()
    }
  } else {
    houseForm.value.province = ''
    houseForm.value.city = ''
    houseForm.value.district = ''
  }
}

// 添加地址解析相关函数
const handleAddressBlur = debounce(async () => {
  if (!houseForm.value.address || !houseForm.value.province || !houseForm.value.city || !houseForm.value.district) {
    return
  }

  const fullAddress = `${houseForm.value.province}${houseForm.value.city}${houseForm.value.district}${houseForm.value.address}`
  
  try {
    // 检查百度地图 API 是否已加载
    if (!window.BMap) {
      console.error('百度地图 API 未加载')
      return
    }

    const myGeo = new BMap.Geocoder()
    const result = await new Promise((resolve, reject) => {
      myGeo.getPoint(fullAddress, (point) => {
        if (point) {
          resolve(point)
        } else {
          reject(new Error('地址解析失败'))
        }
      })
    })

    houseForm.value.latitude = result.lat
    houseForm.value.longitude = result.lng
  } catch (error) {
    // 只在调试时输出错误信息，不打扰用户
    console.error('地址解析失败:', error)
  }
}, 500)

// 提交表单前验证地址
const validateAddress = async () => {
  if (!houseForm.value.address || !houseForm.value.province || !houseForm.value.city || !houseForm.value.district) {
    return false
  }

  const fullAddress = `${houseForm.value.province}${houseForm.value.city}${houseForm.value.district}${houseForm.value.address}`
  
  try {
    // 检查百度地图 API 是否已加载
    if (!window.BMap) {
      throw new Error('百度地图 API 未加载')
    }

    const myGeo = new BMap.Geocoder()
    const result = await new Promise((resolve, reject) => {
      myGeo.getPoint(fullAddress, (point) => {
        if (point) {
          resolve(point)
        } else {
          reject(new Error('地址解析失败'))
        }
      })
    })

    houseForm.value.latitude = result.lat
    houseForm.value.longitude = result.lng
    return true
  } catch (error) {
    ElMessage.error('地址解析失败，请检查地址是否正确')
    return false
  }
}

// 获取房源列表
const getHouseList = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.value.keyword || undefined,
      province: searchForm.value.area?.[0] || undefined,
      city: searchForm.value.area?.[1] || undefined,
      district: searchForm.value.area?.[2] || undefined,
      type: typeof searchForm.value.type === 'number' ? searchForm.value.type : undefined,
      status: typeof searchForm.value.status === 'number' ? searchForm.value.status : undefined
    }
    
    const res = await request.get('/api/house/list', { params })
    
    if (res.code === 200 && res.data) {
      const { records, total: totalCount, current, size } = res.data
      houseList.value = records || []
      total.value = totalCount || 0
      currentPage.value = current || 1
      pageSize.value = size || 10
      
      if (houseList.value.length === 0 && total.value === 0) {
        ElMessage.warning('暂无符合条件的房源数据')
      }
    } else {
      throw new Error(res.message || '获取房源列表失败')
    }
  } catch (error) {
    console.error('获取房源列表失败:', error)
    ElMessage.error(error.message || '获取房源列表失败，请稍后重试')
    houseList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getHouseList()
}

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    keyword: '',
    area: [],
    type: undefined,
    status: undefined
  }
  currentPage.value = 1
  getHouseList()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  getHouseList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getHouseList()
}

// 表格选择
const handleSelectionChange = (val) => {
  selectedRows.value = val
}

// 状态处理
const getStatusType = (status) => {
  const map = {
    0: 'info',    // 已下架
    1: 'success', // 已上架
    2: 'warning'  // 已出租
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '已下架',
    1: '已上架',
    2: '已出租'
  }
  return map[status] || '未知'
}

// 上架/下架处理
const handleOnline = async (id) => {
  try {
    const res = await request.put(`/api/house/${id}/online`)
    if (res.code === 200) {
      ElMessage.success('上架成功')
      getHouseList()
    } else {
      ElMessage.error(res.message || '上架失败')
    }
  } catch (error) {
    console.error('上架失败:', error)
    ElMessage.error('上架失败')
  }
}

const handleOffline = async (id) => {
  try {
    const res = await request.put(`/api/house/${id}/offline`)
    if (res.code === 200) {
      ElMessage.success('下架成功')
      getHouseList()
    } else {
      ElMessage.error(res.message || '下架失败')
    }
  } catch (error) {
    console.error('下架失败:', error)
    ElMessage.error('下架失败')
  }
}

// 批量操作
const handleBatchOnline = async () => {
  if (!selectedRows.value.length) {
    ElMessage.warning('请选择要上架的房源')
    return
  }
  try {
    const ids = selectedRows.value.map(item => item.id)
    const res = await request.post('/api/house/batch-online', ids)
    if (res.code === 200) {
      ElMessage.success('批量上架成功')
      getHouseList()
    } else {
      ElMessage.error(res.message || '批量上架失败')
    }
  } catch (error) {
    console.error('批量上架失败:', error)
    ElMessage.error('批量上架失败')
  }
}

const handleBatchOffline = async () => {
  if (!selectedRows.value.length) {
    ElMessage.warning('请选择要下架的房源')
    return
  }
  try {
    const ids = selectedRows.value.map(item => item.id)
    const res = await request.post('/api/house/batch-offline', ids)
    if (res.code === 200) {
      ElMessage.success('批量下架成功')
      getHouseList()
    } else {
      ElMessage.error(res.message || '批量下架失败')
    }
  } catch (error) {
    console.error('批量下架失败:', error)
    ElMessage.error('批量下架失败')
  }
}

// 删除处理
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该房源吗？', '提示', {
      type: 'warning'
    })
    const res = await request.delete(`/api/house/${id}`)
    console.log('删除响应:', res)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      houseList.value = houseList.value.filter(item => item.id !== id)
      total.value = total.value - 1
    } else {
      throw new Error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (!selectedRows.value.length) {
    ElMessage.warning('请选择要删除的房源')
    return
  }
  try {
    await ElMessageBox.confirm('确定要删除选中的房源吗？', '提示', {
      type: 'warning'
    })
    const ids = selectedRows.value.map(item => item.id)
    const res = await request.post('/api/house/batch-delete', ids)
    console.log('批量删除响应:', res)
    if (res.code === 200) {
      ElMessage.success('批量删除成功')
      const deletedIds = new Set(ids)
      houseList.value = houseList.value.filter(item => !deletedIds.has(item.id))
      total.value = total.value - ids.length
      selectedRows.value = []
    } else {
      throw new Error(res.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 处理图片上传
const handleUploadSuccess = (response, uploadFile) => {
    console.log('上传成功响应:', response)
    if (response.code === 200) {
        // 使用工具函数处理图片URL
        uploadFile.url = getImageUrl(response.data)
        ElMessage.success('图片上传成功')
    } else {
        // 上传失败，移除文件
        const fileList = houseForm.value.images || []
        const index = fileList.findIndex(item => item.uid === uploadFile.uid)
        if (index > -1) {
            fileList.splice(index, 1)
        }
        ElMessage.error(response.message || '图片上传失败')
    }
}

// 处理表单提交
const submitForm = async () => {
    if (!houseFormRef.value) return
    
    await houseFormRef.value.validate(async (valid, fields) => {
        if (valid) {
            try {
                submitting.value = true
                
                // 在提交前验证地址
                console.log('开始验证地址...')
                const addressValid = await validateAddress()
                if (!addressValid) {
                    ElMessage.error('地址验证失败，请确保地址信息完整且正确')
                    return
                }
                console.log('地址验证成功')
                
                const formData = { ...houseForm.value }
                
                // 处理配套设施，转为JSON字符串
                formData.facilities = JSON.stringify(formData.facilities || [])
                
                // 处理图片，将文件列表转换为URL字符串
                console.log('原始图片数据:', formData.images)
                if (Array.isArray(formData.images)) {
                    formData.images = removeUploadsPrefixFromList(
                        formData.images.map(file => file.url)
                    )
                }
                console.log('最终图片数据:', formData.images)
                
                // 设置房东ID
                const userInfoStr = localStorage.getItem('userInfo')
                if (!userInfoStr) {
                    ElMessage.error('用户信息获取失败，请重新登录')
                    return
                }
                const userInfo = JSON.parse(userInfoStr)
                if (!userInfo.id) {
                    ElMessage.error('用户ID获取失败，请重新登录')
                    return
                }
                formData.ownerId = userInfo.id

                // 打印提交的数据，用于调试
                console.log('提交的表单数据:', formData)
                
                let res
                try {
                    if (editMode.value) {
                        res = await request.put(`/api/house/${formData.id}`, formData)
                    } else {
                        res = await request.post('/api/house', formData)
                    }
                    
                    // 检查响应数据
                    if (!res) {
                        throw new Error('服务器响应为空')
                    }
                    
                    if (res.code === 200) {
                        ElMessage.success(editMode.value ? '编辑成功' : '新增成功')
                        dialogVisible.value = false
                        getHouseList()
                    } else {
                        throw new Error(res.message || '操作失败')
                    }
                } catch (error) {
                    console.error('API请求失败:', error)
                    ElMessage.error(error.message || (editMode.value ? '编辑失败' : '新增失败'))
                } finally {
                    submitting.value = false
                }
            } catch (error) {
                console.error('表单提交失败:', error)
                ElMessage.error(error.message || (editMode.value ? '编辑失败' : '新增失败'))
            }
        } else {
            console.log('表单验证失败:', fields)
        }
    })
}

// 处理对话框关闭
const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

// 处理图片上传
const handleUploadError = (error, uploadFile) => {
  console.error('上传错误:', error)
  // 上传失败，移除文件
  const fileList = houseForm.value.images || []
  const index = fileList.findIndex(item => item.uid === uploadFile.uid)
  if (index > -1) {
    fileList.splice(index, 1)
  }
  ElMessage.error('图片上传失败：' + (error.message || '未知错误'))
}

const handleUploadRemove = (uploadFile) => {
  // 从图片列表中移除图片
  const fileList = houseForm.value.images || []
  const index = fileList.findIndex(item => item.url === uploadFile.url)
  if (index > -1) {
    fileList.splice(index, 1)
  }
}

const handleUploadExceed = () => {
  ElMessage.warning('最多只能上传10张图片')
}

const beforeUpload = (file) => {
  const isImage = /\.(jpg|jpeg|png|gif)$/i.test(file.name)
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传jpg/jpeg/png/gif格式的图片!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB!')
    return false
  }
  return true
}

const submitting = ref(false)

// 上传组件的请求头
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? {
    Authorization: `Bearer ${token}`
  } : {}
})

// 添加房型和状态的格式化函数
const formatRoomType = (type) => {
  const option = typeOptions.find(item => item.value === type)
  return option ? option.label : '未知'
}

const formatStatus = (status) => {
  const option = statusOptions.find(item => item.value === status)
  return option ? option.label : '未知'
}

// 初始化
onMounted(() => {
  getHouseList()
})
</script>

<style scoped>
.house-manage {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

.operation-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.house-form {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 20px;
}

.dialog-footer {
  padding-top: 20px;
  text-align: right;
}
</style> 