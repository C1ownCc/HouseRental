<template>
  <div class="payment-success">
    <el-card>
      <div class="success-icon">
        <el-icon class="icon"><CircleCheckFilled /></el-icon>
      </div>
      <h1 class="success-title">支付成功</h1>
      <div class="payment-info" v-if="paymentDetail">
        <el-descriptions :column="1" border class="info-box">
          <el-descriptions-item label="合同编号">{{ paymentDetail.contract?.contractNumber }}</el-descriptions-item>
          <el-descriptions-item label="支付周期">
            {{ formatDate(paymentDetail.payment?.periodStart) }} 至 {{ formatDate(paymentDetail.payment?.periodEnd) }}
          </el-descriptions-item>
          <el-descriptions-item label="支付金额">¥{{ paymentDetail.payment?.amount }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ paymentDetail.payment?.paymentMethod }}</el-descriptions-item>
          <el-descriptions-item label="交易流水号">{{ paymentDetail.payment?.transactionNo }}</el-descriptions-item>
          <el-descriptions-item label="支付日期">{{ formatDate(paymentDetail.payment?.paymentDate) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div class="actions">
        <el-button type="primary" @click="goToPaymentList">返回账单列表</el-button>
        <el-button @click="goToContractDetail">查看合同详情</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const paymentDetail = ref(null)
const loading = ref(false)

// 获取支付详情
const getPaymentDetail = async () => {
  const paymentId = route.params.paymentId
  if (!paymentId) {
    ElMessage.error('无效的支付记录ID')
    router.push('/contracts')
    return
  }
  
  loading.value = true
  try {
    const res = await request.get(`/rent-payment/${paymentId}`)
    if (res.code === 200) {
      paymentDetail.value = res.data
    } else {
      ElMessage.error(res.message || '获取支付详情失败')
      router.push('/contracts')
    }
  } catch (error) {
    console.error('获取支付详情失败:', error)
    ElMessage.error('获取支付详情失败')
    router.push('/contracts')
  } finally {
    loading.value = false
  }
}

// 返回账单列表
const goToPaymentList = () => {
  if (paymentDetail.value && paymentDetail.value.payment) {
    router.push(`/contract/${paymentDetail.value.payment.contractId}/payments`)
  } else {
    router.push('/contracts')
  }
}

// 查看合同详情
const goToContractDetail = () => {
  if (paymentDetail.value && paymentDetail.value.payment) {
    router.push(`/contract/${paymentDetail.value.payment.contractId}`)
  } else {
    router.push('/contracts')
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  getPaymentDetail()
})
</script>

<style scoped>
.payment-success {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.el-card {
  width: 100%;
  max-width: 600px;
}

.success-icon {
  text-align: center;
  margin: 20px 0;
}

.icon {
  font-size: 80px;
  color: #67c23a;
}

.success-title {
  text-align: center;
  font-size: 24px;
  color: #67c23a;
  margin-bottom: 30px;
}

.payment-info {
  margin: 20px 0 30px;
}

.info-box {
  margin: 0 auto;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 30px 0 10px;
}
</style> 