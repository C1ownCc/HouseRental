<template>
  <div class="contract-detail">
    <el-card v-if="contract" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>租约详情</span>
          <div>
            <el-button type="primary" size="small" @click="goBack">返回列表</el-button>
            <el-button 
              v-if="contract.status === 1" 
              type="success" 
              size="small" 
              @click="signContract"
              :disabled="loading"
            >
              签署合同
            </el-button>
            <el-button 
              v-if="contract.status === 3" 
              type="warning" 
              size="small" 
              @click="viewPayments"
              :disabled="loading"
            >
              查看账单
            </el-button>
            <el-button 
              v-if="contract.status === 3" 
              type="danger" 
              size="small" 
              @click="terminateContract"
              :disabled="loading"
            >
              终止合同
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 合同基本信息 -->
      <div class="section">
        <h2>合同信息</h2>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="合同编号">{{ contract.contractNumber }}</el-descriptions-item>
          <el-descriptions-item label="合同状态">
            <el-tag :type="getStatusType(contract.status)">
              {{ getStatusText(contract.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="租期开始">{{ formatDate(contract.startDate) }}</el-descriptions-item>
          <el-descriptions-item label="租期结束">{{ formatDate(contract.endDate) }}</el-descriptions-item>
          <el-descriptions-item label="月租金">¥{{ contract.monthlyRent }}</el-descriptions-item>
          <el-descriptions-item label="押金">¥{{ contract.deposit }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ contract.paymentMethod }}</el-descriptions-item>
          <el-descriptions-item label="签约日期">{{ contract.signDate ? formatDateTime(contract.signDate) : '未签约' }}</el-descriptions-item>
          <el-descriptions-item label="签署状态" :span="2">
            <el-space>
              <el-tag :type="contract.tenantSign === 1 ? 'success' : 'info'">
                租户{{ contract.tenantSign === 1 ? '已签署' : '未签署' }}
              </el-tag>
              <el-tag :type="contract.ownerSign === 1 ? 'success' : 'info'">
                房东{{ contract.ownerSign === 1 ? '已签署' : '未签署' }}
              </el-tag>
            </el-space>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ contract.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <!-- 房源信息 -->
      <div class="section" v-if="house">
        <h2>房源信息</h2>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="房源标题">{{ house.title }}</el-descriptions-item>
          <el-descriptions-item label="房源状态">
            <el-tag :type="house.status === 1 ? 'success' : (house.status === 2 ? 'warning' : 'info')">
              {{ house.status === 0 ? '下架' : (house.status === 1 ? '上架' : '已出租') }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="房源面积">{{ house.area }}㎡</el-descriptions-item>
          <el-descriptions-item label="房源户型">{{ house.roomCount }}室{{ house.hallCount }}厅{{ house.bathroomCount }}卫</el-descriptions-item>
          <el-descriptions-item label="所在楼层">{{ house.floor }}层</el-descriptions-item>
          <el-descriptions-item label="总楼层">{{ house.totalFloor }}层</el-descriptions-item>
          <el-descriptions-item label="房屋朝向">{{ house.orientation }}</el-descriptions-item>
          <el-descriptions-item label="装修情况">{{ house.decoration }}</el-descriptions-item>
          <el-descriptions-item label="详细地址" :span="2">{{ house.address }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <!-- 房东信息 -->
      <div class="section" v-if="owner">
        <h2>房东信息</h2>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="房东姓名">{{ owner.nickname || owner.username }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ house?.contactPhone || '未提供' }}</el-descriptions-item>
          <el-descriptions-item label="联系邮箱">{{ owner.email || '未提供' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <!-- 合同条款 -->
      <div class="section">
        <h2>合同条款</h2>
        <div class="terms">
          <p><strong>第一条 租赁物业</strong></p>
          <p>出租方同意将位于{{ house?.address }}的房屋出租给承租方。</p>
          
          <p><strong>第二条 租赁期限</strong></p>
          <p>租赁期从{{ formatDate(contract.startDate) }}起至{{ formatDate(contract.endDate) }}止，共计{{ getDurationMonths() }}个月。</p>
          
          <p><strong>第三条 租金及支付方式</strong></p>
          <p>月租金为人民币{{ contract.monthlyRent }}元，按{{ contract.paymentMethod }}支付。</p>
          
          <p><strong>第四条 押金</strong></p>
          <p>承租方应支付押金人民币{{ contract.deposit }}元，该押金将在租赁期满且承租方无违约的情况下退还。</p>
          
          <p><strong>第五条 房屋交付及设施设备</strong></p>
          <p>出租方应保证所交付的房屋完好，基本设备齐全可用。</p>
          
          <p><strong>第六条 维修责任</strong></p>
          <p>正常使用下的房屋及设施维修由出租方负责；因承租方使用不当造成的损坏由承租方负责维修或赔偿。</p>
          
          <p><strong>第七条 转租限制</strong></p>
          <p>未经出租方书面同意，承租方不得将房屋转租给第三方。</p>
          
          <p><strong>第八条 合同终止</strong></p>
          <p>租赁期满，本合同自然终止。如一方提前终止合同，应提前一个月通知对方并征得对方同意。</p>
          
          <p><strong>第九条 违约责任</strong></p>
          <p>任何一方违反本合同约定，应承担相应的违约责任。</p>
          
          <p><strong>第十条 合同效力</strong></p>
          <p>本合同自双方签字或盖章之日起生效。</p>
        </div>
      </div>

      <!-- 终止合同对话框 -->
<el-dialog v-model="terminateDialogVisible" title="终止合同" width="30%">
  <el-form :model="terminateForm" :rules="terminateRules" ref="terminateFormRef" label-width="100px">
    <el-form-item label="终止原因" prop="remark">
      <el-input v-model="terminateForm.remark" type="textarea" rows="4" placeholder="请输入终止合同的原因" />
    </el-form-item>
  </el-form>
  <template #footer>
    <span class="dialog-footer">
      <el-button @click="terminateDialogVisible = false">取消</el-button>
      <el-button type="danger" @click="confirmTerminate">确认终止</el-button>
    </span>
  </template>
</el-dialog> 
      
      <!-- 签署确认区域 -->
      <div class="sign-section" v-if="contract.status === 1">
        <el-alert
          title="请认真阅读合同条款，签署后将具有法律效力。"
          type="warning"
          :closable="false"
          show-icon
        />
        <div class="sign-action">
          <el-checkbox v-model="readAndAgree">我已阅读并同意以上合同条款</el-checkbox>
          <el-button type="primary" @click="signContract" :disabled="!readAndAgree || loading">签署合同</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const contract = ref(null)
const house = ref(null)
const owner = ref(null)
const readAndAgree = ref(false)

// 终止合同相关
const terminateDialogVisible = ref(false)
const terminateFormRef = ref(null)
const terminateForm = ref({
  remark: ''
})

const terminateRules = {
  remark: [
    { required: true, message: '请输入终止合同的原因', trigger: 'blur' },
    { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
  ]
}

// 获取合同详情
const getContractDetail = async (isRetry = false) => {
  const contractId = route.params.id
  if (!contractId) {
    ElMessage.error('无效的合同ID')
    router.push('/contracts')
    return
  }
  
  loading.value = true
  try {
    const res = await request.get(`/lease-contract/${contractId}`)
    if (res.code === 200) {
      contract.value = res.data
      
      // 获取房源信息
      if (contract.value.houseId) {
        try {
          const houseRes = await request.get(`/house/${contract.value.houseId}`)
          if (houseRes.code === 200) {
            house.value = houseRes.data
          }
        } catch (houseError) {
          console.error('获取房源信息失败:', houseError)
        }
      }
      
      // 获取房东信息 - 发现没有/user/{id}接口，暂时跳过此步骤
      // 考虑到后端没有提供获取单个用户的接口，这里暂时设置一个默认值
      owner.value = {
        username: '房东信息不可用',
        nickname: '房东信息不可用',
        email: '暂无'
      }
    } else {
      ElMessage.error(res.message || '获取合同详情失败')
      setTimeout(() => {
        router.push('/contracts')
      }, 1500)
    }
  } catch (error) {
    console.error('获取合同详情失败:', error)
    
    // 防止重复弹窗
    if (!isRetry) {
      // 使用更详细的错误信息
      const errorMessage = error.displayMessage || error.response?.data?.message || error.message || '系统错误，请稍后重试'
      
      // 提供更多信息和重试选项
      ElMessageBox.confirm(
        `获取合同详情失败: ${errorMessage}，是否重试？`,
        '错误',
        {
          confirmButtonText: '重试',
          cancelButtonText: '返回列表',
          type: 'error'
        }
      ).then(() => {
        // 用户选择重试，使用isRetry标记避免递归
        getContractDetail(true)
      }).catch(() => {
        // 用户选择返回
        router.push('/contracts')
      })
    }
  } finally {
    loading.value = false
  }
}

// 签署合同
const signContract = () => {
  if (contract.value.status !== 1) {
    ElMessage.warning('当前合同状态不允许签署')
    return
  }
  
  if (!readAndAgree.value) {
    ElMessage.warning('请先阅读并同意合同条款')
    return
  }
  
  ElMessageBox.confirm('确认签署此租赁合同？签署后将具有法律效力。', '确认签署', {
    confirmButtonText: '确认签署',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      const res = await request.put(`/lease-contract/tenant-sign/${contract.value.id}`)
      if (res.code === 200) {
        ElMessage.success('合同签署成功')
        getContractDetail(true) // 使用isRetry参数重新加载
      } else {
        ElMessage.error(res.message || '合同签署失败')
      }
    } catch (error) {
      console.error('合同签署失败:', error)
      ElMessage.error('合同签署失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 取消签署
  })
}

// 查看租金账单
const viewPayments = () => {
  router.push(`/contract/${contract.value.id}/payments`)
}

// 打开终止合同对话框
const terminateContract = () => {
  if (contract.value.status !== 3) {
    ElMessage.warning('只有生效中的合同才能终止')
    return
  }
  
  terminateForm.value.remark = ''
  terminateDialogVisible.value = true
}

// 确认终止合同
const confirmTerminate = async () => {
  if (!terminateFormRef.value) return
  
  await terminateFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }

    loading.value = true
    try {
      const res = await request.put(`/lease-contract/terminate/${contract.value.id}`, {}, {
        params: {
          remark: terminateForm.value.remark
        }
      })
      
      if (res.code === 200) {
        ElMessage.success('合同已终止')
        terminateDialogVisible.value = false
        getContractDetail(true) // 重新加载详情
      } else {
        ElMessage.error(res.message || '终止合同失败')
      }
    } catch (error) {
      console.error('终止合同失败:', error)
      ElMessage.error('终止合同失败')
    } finally {
      loading.value = false
    }
  })
}

// 返回列表
const goBack = () => {
  router.push('/contracts')
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '草稿',
    1: '待租户签署',
    2: '待房东签署',
    3: '已生效',
    4: '已终止',
    5: '已到期'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'info',
    1: 'warning',
    2: 'warning',
    3: 'success',
    4: 'danger',
    5: 'info'
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return `${formatDate(dateTimeStr)} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 计算租期月数
const getDurationMonths = () => {
  if (!contract.value || !contract.value.startDate || !contract.value.endDate) return ''
  
  const start = new Date(contract.value.startDate)
  const end = new Date(contract.value.endDate)
  
  const months = (end.getFullYear() - start.getFullYear()) * 12 + end.getMonth() - start.getMonth()
  const days = end.getDate() - start.getDate()
  
  return days >= 0 ? months : months - 1
}

onMounted(() => {
  getContractDetail()
})
</script>

<style scoped>
.contract-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section {
  margin-bottom: 30px;
}

h2 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
}

.terms {
  line-height: 1.8;
  color: #606266;
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.terms p {
  margin-bottom: 10px;
}

.sign-section {
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.sign-action {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 添加终止合同对话框 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

