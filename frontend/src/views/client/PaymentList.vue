<template>
  <div class="payment-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>租金账单</span>
          <div>
            <el-button type="primary" size="small" @click="goBack">返回合同详情</el-button>
          </div>
        </div>
      </template>
      
      <div class="contract-info" v-if="contract">
        <p><strong>合同编号：</strong>{{ contract.contractNumber }}</p>
        <p><strong>租期：</strong>{{ formatDate(contract.startDate) }} 至 {{ formatDate(contract.endDate) }}</p>
        <p><strong>月租金：</strong>¥{{ contract.monthlyRent }}</p>
        <p><strong>支付方式：</strong>{{ contract.paymentMethod }}</p>
      </div>
      
      <el-table :data="paymentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="periodStart" label="账单周期" min-width="250">
          <template #default="scope">
            {{ formatDate(scope.row.periodStart) }} 至 {{ formatDate(scope.row.periodEnd) }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="150">
          <template #default="scope">
            ¥{{ scope.row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getPaymentStatusType(scope.row.paymentStatus)">
              {{ getPaymentStatusText(scope.row.paymentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentDate" label="支付日期" width="150">
          <template #default="scope">
            {{ scope.row.paymentDate && scope.row.paymentDate !== '1970-01-01' ? formatDate(scope.row.paymentDate) : '未支付' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.paymentStatus === 0" 
              type="primary" 
              size="small" 
              @click="handlePay(scope.row)"
              :disabled="loading"
            >
              支付
            </el-button>
            <el-button 
              v-else-if="scope.row.paymentStatus === 1" 
              type="info" 
              size="small" 
              @click="viewPaymentDetail(scope.row.id)"
              :disabled="loading"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 支付对话框 -->
    <el-dialog v-model="payDialogVisible" title="支付租金" width="40%">
      <div class="payment-info">
        <p><strong>支付周期：</strong>{{ formatDate(currentPayment?.periodStart) }} 至 {{ formatDate(currentPayment?.periodEnd) }}</p>
        <p><strong>支付金额：</strong>¥{{ currentPayment?.amount }}</p>
      </div>
      
      <el-tabs v-model="paymentTab">
        <el-tab-pane label="扫码支付" name="qrcode">
          <div class="qrcode-container">
            <div class="qrcode-box">
              <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=house-rental-payment-${currentPayment?.id}-${currentPayment?.amount}`" alt="支付二维码" />
            </div>
            <div class="qrcode-tips">
              <p>请使用微信或支付宝扫描二维码完成支付</p>
              <p>支付金额: <span class="highlight">¥{{ currentPayment?.amount }}</span></p>
              <el-button type="primary" @click="simulatePayment">模拟支付完成</el-button>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="手动录入" name="manual">
          <el-form :model="payForm" :rules="payRules" ref="payFormRef" label-width="100px">
            <el-form-item label="支付方式" prop="paymentMethod">
              <el-select v-model="payForm.paymentMethod" placeholder="请选择支付方式">
                <el-option label="支付宝" value="支付宝" />
                <el-option label="微信" value="微信" />
                <el-option label="银行转账" value="银行转账" />
                <el-option label="现金" value="现金" />
              </el-select>
            </el-form-item>
            <el-form-item label="交易流水号" prop="transactionNo">
              <el-input v-model="payForm.transactionNo" placeholder="请输入交易流水号" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="payForm.remark" type="textarea" rows="3" placeholder="请输入备注信息" />
            </el-form-item>
          </el-form>
          <div class="dialog-footer">
            <el-button @click="payDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmPay">确认支付</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
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
const paymentList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 支付相关
const payDialogVisible = ref(false)
const payFormRef = ref(null)
const currentPayment = ref(null)
const payForm = ref({
  paymentMethod: '',
  transactionNo: '',
  remark: ''
})

const paymentTab = ref('qrcode')

const payRules = {
  paymentMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ],
  transactionNo: [
    { required: true, message: '请输入交易流水号', trigger: 'blur' }
  ]
}

// 获取合同信息
const getContractDetail = async () => {
  const contractId = route.params.id
  if (!contractId) {
    ElMessage.error('无效的合同ID')
    router.push('/contracts')
    return
  }
  
  try {
    const res = await request.get(`/lease-contract/${contractId}`)
    if (res.code === 200) {
      contract.value = res.data
    } else {
      ElMessage.error(res.message || '获取合同详情失败')
    }
  } catch (error) {
    console.error('获取合同详情失败:', error)
    ElMessage.error('获取合同详情失败')
  }
}

// 获取支付列表
const getPaymentList = async () => {
  const contractId = route.params.id
  if (!contractId) {
    ElMessage.error('无效的合同ID')
    router.push('/contracts')
    return
  }
  
  loading.value = true
  try {
    const res = await request.get(`/rent-payment/contract/${contractId}`, {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })
    
    if (res.code === 200) {
      paymentList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取支付记录失败')
    }
  } catch (error) {
    console.error('获取支付记录失败:', error)
    ElMessage.error('获取支付记录失败')
  } finally {
    loading.value = false
  }
}

// 处理支付
const handlePay = (payment) => {
  currentPayment.value = payment
  // 重置支付表单
  payForm.value = {
    paymentMethod: '微信',
    transactionNo: '',
    remark: ''
  }
  // 默认显示扫码支付选项卡
  paymentTab.value = 'qrcode'
  payDialogVisible.value = true
}

// 生成随机流水号
const generateTransactionNo = () => {
  const prefix = 'HR'
  const date = new Date()
  const dateStr = date.getFullYear().toString().slice(2) + 
                 (date.getMonth() + 1).toString().padStart(2, '0') + 
                 date.getDate().toString().padStart(2, '0')
  const random = Math.floor(Math.random() * 1000000).toString().padStart(6, '0')
  return `${prefix}${dateStr}${random}`
}

// 模拟扫码支付完成
const simulatePayment = async () => {
  loading.value = true
  
  try {
    // 生成随机交易流水号
    const transactionNo = generateTransactionNo()
    
    // 构建支付数据
    const payload = {
      paymentMethod: '微信扫码',
      remark: '通过扫码完成支付'
    }
    
    // 发送支付请求
    const res = await request.put(`/rent-payment/confirm/${currentPayment.value.id}`, payload, {
      params: {
        transactionNo: transactionNo
      }
    })
    
    if (res.code === 200) {
      ElMessage.success(`支付成功，流水号: ${transactionNo}`)
      payDialogVisible.value = false
      // 跳转到支付成功页面
      router.push(`/payment/success/${currentPayment.value.id}`)
    } else {
      ElMessage.error(res.message || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
  } finally {
    loading.value = false
  }
}

// 确认支付
const confirmPay = async () => {
  if (!payFormRef.value) return
  
  await payFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }

    loading.value = true
    try {
      // 保存支付方式
      const payload = {
        paymentMethod: payForm.value.paymentMethod,
        remark: payForm.value.remark
      }
      
      const res = await request.put(`/rent-payment/confirm/${currentPayment.value.id}`, payload, {
        params: {
          transactionNo: payForm.value.transactionNo
        }
      })
      
      if (res.code === 200) {
        ElMessage.success('支付成功')
        payDialogVisible.value = false
        // 跳转到支付成功页面
        router.push(`/payment/success/${currentPayment.value.id}`)
      } else {
        ElMessage.error(res.message || '支付失败')
      }
    } catch (error) {
      console.error('支付失败:', error)
      ElMessage.error('支付失败')
    } finally {
      loading.value = false
    }
  })
}

// 查看支付详情
const viewPaymentDetail = (paymentId) => {
  // 实际项目中可以跳转到支付详情页或打开详情对话框
  ElMessage.info('查看支付ID: ' + paymentId + ' 的详情')
}

// 返回合同详情
const goBack = () => {
  router.push(`/contract/${route.params.id}`)
}

// 获取支付状态文本
const getPaymentStatusText = (status) => {
  const statusMap = {
    0: '未支付',
    1: '已支付',
    2: '已逾期'
  }
  return statusMap[status] || '未知状态'
}

// 获取支付状态标签类型
const getPaymentStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 分页
const handleSizeChange = (size) => {
  pageSize.value = size
  getPaymentList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  getPaymentList()
}

onMounted(() => {
  getContractDetail()
  getPaymentList()
})
</script>

<style scoped>
.payment-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contract-info {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  line-height: 1.6;
}

.contract-info p {
  margin: 5px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.payment-info {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  line-height: 1.6;
}

.payment-info p {
  margin: 5px 0;
}

.qrcode-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.qrcode-box {
  width: 200px;
  height: 200px;
  background-color: #fff;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 20px;
}

.qrcode-box img {
  width: 100%;
  height: 100%;
}

.qrcode-tips {
  flex: 1;
}

.qrcode-tips p {
  margin: 5px 0;
}

.highlight {
  color: #409eff;
}

.dialog-footer {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 