<template>
  <div class="house-manage">
    <div class="header">
      <h2>我的房源</h2>
      <el-button type="primary" @click="handleAdd">发布新房源</el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="searchForm.keyword" placeholder="标题" clearable />
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

    <el-table :data="houseList" v-loading="loading" border>
      <el-table-column label="房源图片" width="120">
        <template #default="{ row }">
          <el-image 
            :src="getFirstImage(row.images)" 
            fit="cover"
            style="width: 80px; height: 80px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="price" label="价格">
        <template #default="{ row }">
          ¥{{ row.price }}/月
        </template>
      </el-table-column>
      <el-table-column prop="area" label="面积">
        <template #default="{ row }">
          {{ row.area }}㎡
        </template>
      </el-table-column>
      <el-table-column prop="roomCount" label="户型">
        <template #default="{ row }">
          {{ row.roomCount }}室{{ row.hallCount }}厅
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '已上架' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button-group>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              :type="row.status === 1 ? 'danger' : 'success'" 
              link 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑房源对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="form.id ? '编辑房源' : '发布新房源'"
      width="60%"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入房源标题" />
        </el-form-item>
        <el-form-item label="房源图片" prop="images">
          <el-upload
            action="/api/upload/image"
            list-type="picture-card"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :on-remove="handleRemove"
            :file-list="form.images"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :min="0" placeholder="月租金" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="面积" prop="area">
              <el-input-number v-model="form.area" :min="0" placeholder="平方米" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房间数" prop="roomCount">
              <el-input-number v-model="form.roomCount" :min="1" placeholder="卧室数量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="厅室数" prop="hallCount">
              <el-input-number v-model="form.hallCount" :min="0" placeholder="客厅数量" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="卫生间" prop="bathroomCount">
              <el-input-number v-model="form.bathroomCount" :min="1" placeholder="卫生间数量" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所在楼层" prop="floor">
              <el-input-number v-model="form.floor" :min="1" placeholder="所在楼层" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="总楼层" prop="totalFloor">
              <el-input-number v-model="form.totalFloor" :min="1" placeholder="总楼层" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电梯" prop="hasElevator">
              <el-radio-group v-model="form.hasElevator">
                <el-radio :label="1">有</el-radio>
                <el-radio :label="0">无</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="朝向" prop="orientation">
              <el-select v-model="form.orientation" placeholder="请选择朝向">
                <el-option label="东" value="东" />
                <el-option label="南" value="南" />
                <el-option label="西" value="西" />
                <el-option label="北" value="北" />
                <el-option label="东南" value="东南" />
                <el-option label="东北" value="东北" />
                <el-option label="西南" value="西南" />
                <el-option label="西北" value="西北" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="装修" prop="decoration">
              <el-select v-model="form.decoration" placeholder="请选择装修情况">
                <el-option label="毛坯" value="毛坯" />
                <el-option label="简装" value="简装" />
                <el-option label="精装" value="精装" />
                <el-option label="豪装" value="豪装" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="押金" prop="deposit">
              <el-input-number v-model="form.deposit" :min="0" placeholder="押金金额" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="配套设施" prop="facilities">
              <el-checkbox-group v-model="form.facilitiesList">
                <el-checkbox label="wifi">无线网络</el-checkbox>
                <el-checkbox label="tv">电视</el-checkbox>
                <el-checkbox label="washer">洗衣机</el-checkbox>
                <el-checkbox label="aircon">空调</el-checkbox>
                <el-checkbox label="fridge">冰箱</el-checkbox>
                <el-checkbox label="heater">热水器</el-checkbox>
                <el-checkbox label="bed">床</el-checkbox>
                <el-checkbox label="desk">书桌</el-checkbox>
                <el-checkbox label="wardrobe">衣柜</el-checkbox>
                <el-checkbox label="sofa">沙发</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="地址" prop="address">
              <el-input 
                v-model="form.address" 
                placeholder="请输入详细地址"
                @blur="handleAddressBlur"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
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
          </el-col>
        </el-row>
        <el-form-item label="房源描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            rows="4"
            placeholder="请输入房源详细描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getImageUrl, removeUploadsPrefix } from '@/utils/image'
import { cascaderOptions } from '@/utils/area-data'
import { debounce } from 'lodash-es'

const loading = ref(false)
const houseList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)

// 表单数据
const form = ref({
  id: null,
  title: '',
  images: [],
  price: null,
  deposit: null,
  area: null,
  roomCount: null,
  hallCount: null,
  bathroomCount: 1,
  floor: null,
  totalFloor: null,
  hasElevator: 0,
  orientation: '',
  decoration: '简装',
  facilities: '',
  facilitiesList: [],
  contactName: '',
  contactPhone: '',
  address: '',
  province: '',
  city: '',
  district: '',
  description: '',
  status: 1,
  latitude: null,
  longitude: null
})

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入房源标题', trigger: 'blur' }],
  images: [{ required: true, message: '请上传房源图片', trigger: 'change' }],
  price: [{ required: true, message: '请输入租金', trigger: 'blur' }],
  deposit: [{ required: true, message: '请输入押金', trigger: 'blur' }],
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }],
  roomCount: [{ required: true, message: '请输入房间数', trigger: 'blur' }],
  hallCount: [{ required: true, message: '请输入厅室数', trigger: 'blur' }],
  bathroomCount: [{ required: true, message: '请输入卫生间数量', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  totalFloor: [{ required: true, message: '请输入总楼层', trigger: 'blur' }],
  orientation: [{ required: true, message: '请选择朝向', trigger: 'change' }],
  decoration: [{ required: true, message: '请选择装修情况', trigger: 'change' }],
  contactName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  description: [{ required: true, message: '请输入房源描述', trigger: 'blur' }]
}

// 上传相关
const uploadHeaders = computed(() => ({
  Authorization: 'Bearer ' + localStorage.getItem('token')
}))

// 搜索表单数据
const searchForm = ref({
  keyword: '',
  type: undefined,
  status: undefined
})

// 房型选项
const typeOptions = [
  { label: '一室', value: 1 },
  { label: '二室', value: 2 },
  { label: '三室', value: 3 },
  { label: '四室', value: 4 },
  { label: '五室及以上', value: 5 }
]

// 状态选项
const statusOptions = [
  { label: '已下架', value: 0 },
  { label: '已上架', value: 1 }
]

// 区域选择器数据
const areaOptions = cascaderOptions
const selectedArea = ref([])

// 处理区域选择变化
const handleAreaChange = (value) => {
  if (value && value.length === 3) {
    form.value.province = value[0]
    form.value.city = value[1]
    form.value.district = value[2]
  } else {
    form.value.province = ''
    form.value.city = ''
    form.value.district = ''
  }
}

// 获取房源列表
const loadHouseList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.value.keyword || undefined,
      type: typeof searchForm.value.type === 'number' ? searchForm.value.type : undefined,
      status: typeof searchForm.value.status === 'number' ? searchForm.value.status : undefined
    }
    
    const res = await request.get('/house/owner', { params })
    if (res.code === 200) {
      houseList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取房源列表失败:', error)
    ElMessage.error('获取房源列表失败')
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

// 处理图片上传成功
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.value.images.push({
      url: getImageUrl(response.data),
      name: response.data.split('/').pop(),
      status: 'success'
    })
  } else {
    ElMessage.error('上传失败')
  }
}

// 处理图片删除
const handleRemove = (file) => {
  const index = form.value.images.findIndex(img => img.url === file.url)
  if (index !== -1) {
    form.value.images.splice(index, 1)
  }
}

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 添加房源
const handleAdd = () => {
  form.value = {
    id: null,
    title: '',
    images: [],
    price: null,
    deposit: null,
    area: null,
    roomCount: null,
    hallCount: null,
    bathroomCount: 1,
    floor: null,
    totalFloor: null,
    hasElevator: 0,
    orientation: '',
    decoration: '简装',
    facilities: '',
    facilitiesList: [],
    contactName: '',
    contactPhone: '',
    address: '',
    province: '',
    city: '',
    district: '',
    description: '',
    status: 1,
    latitude: null,
    longitude: null
  }
  selectedArea.value = []
  dialogVisible.value = true
}

// 编辑房源
const handleEdit = (row) => {
  form.value = { ...row }
  // 处理图片
  form.value.images = row.images ? row.images.split(',').map(url => ({
    url: getImageUrl(url),
    name: url.split('/').pop(),
    status: 'success'
  })) : []
  // 处理设施
  form.value.facilitiesList = row.facilities ? row.facilities.split(',') : []
  // 设置区域选择器的值
  selectedArea.value = [row.province, row.city, row.district]
  dialogVisible.value = true
}

// 处理地址解析
const handleAddressBlur = debounce(async () => {
  if (!form.value.address || !form.value.province || !form.value.city || !form.value.district) {
    return
  }

  const fullAddress = `${form.value.province}${form.value.city}${form.value.district}${form.value.address}`
  
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

    form.value.latitude = result.lat
    form.value.longitude = result.lng
  } catch (error) {
    // 只在调试时输出错误信息，不打扰用户
    console.error('地址解析失败:', error)
  }
}, 500)

// 提交表单前验证地址
const validateAddress = async () => {
  if (!form.value.address || !form.value.province || !form.value.city || !form.value.district) {
    return false
  }

  const fullAddress = `${form.value.province}${form.value.city}${form.value.district}${form.value.address}`
  
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

    form.value.latitude = result.lat
    form.value.longitude = result.lng
    return true
  } catch (error) {
    ElMessage.error('地址解析失败，请检查地址是否正确')
    return false
  }
}

// 修改提交表单方法
const handleSubmit = async () => {
  if (!formRef.value) {
    console.error('表单引用不存在')
    return
  }
  
  try {
    // 添加表单验证结果的调试信息
    const valid = await formRef.value.validate().catch(err => {
      console.error('表单验证失败:', err)
      return false
    })
    
    if (!valid) {
      ElMessage.error('请填写完整所有必填项')
      return
    }
    
    // 在提交前验证地址
    console.log('开始验证地址...')
    const addressValid = await validateAddress()
    if (!addressValid) {
      ElMessage.error('地址验证失败，请确保地址信息完整且正确')
      return
    }
    console.log('地址验证成功')
    
    // 验证图片
    if (!form.value.images || form.value.images.length === 0) {
      ElMessage.error('请至少上传一张房源图片')
      return
    }
    
    // 处理设施列表
    const facilities = form.value.facilitiesList.join(',')
    
    // 构建提交数据
    const submitData = {
      ...form.value,
      facilities,
      // 确保图片数据正确处理
      images: form.value.images.map(img => removeUploadsPrefix(img.url)).join(',')
    }
    
    console.log('准备提交的数据:', submitData)
    loading.value = true
    
    // 发送请求
    const url = submitData.id ? `/house/${submitData.id}` : '/house'
    const method = submitData.id ? 'put' : 'post'
    const res = await request[method](url, submitData)
    
    if (res.code === 200) {
      ElMessage.success(submitData.id ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadHouseList()
    } else {
      throw new Error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '提交失败，请检查表单数据是否完整')
  } finally {
    loading.value = false
  }
}

// 切换房源状态
const handleToggleStatus = async (row) => {
  try {
    const res = await request.put(`/house/${row.id}/${row.status === 1 ? 'offline' : 'online'}`)
    if (res.code === 200) {
      ElMessage.success('操作成功')
      loadHouseList()
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除房源
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该房源吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/house/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadHouseList()
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val
  loadHouseList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadHouseList()
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadHouseList()
}

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    keyword: '',
    type: undefined,
    status: undefined
  }
  currentPage.value = 1
  loadHouseList()
}

onMounted(() => {
  loadHouseList()
})
</script>

<style scoped>
.house-manage {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style> 