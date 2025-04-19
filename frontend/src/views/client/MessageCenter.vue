<template>
  <div class="message-center">
    <el-card class="message-card">
      <template #header>
        <div class="card-header">
          <span>消息中心</span>
          <div class="header-actions">
            <el-button type="primary" link @click="handleMarkAllAsRead" :disabled="!hasUnread">
              全部标记为已读
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部消息" name="all">
          <template v-if="messageList.length > 0">
            <div class="message-list">
              <div
                v-for="message in messageList"
                :key="message.id"
                class="message-item"
                :class="{ unread: message.status === 0 || message.readStatus === 0 }"
              >
                <div class="message-content">
                  <div class="message-header">
                    <span class="message-title">{{ message.title }}</span>
                    <span class="message-time">{{ formatDate(message.createTime) }}</span>
                  </div>
                  <div class="message-body">{{ message.content }}</div>
                  <div class="message-footer">
                    <el-tag size="small" :type="getMessageTypeTag(message.type)">
                      {{ getMessageTypeText(message.type) }}
                    </el-tag>
                    <div class="message-actions">
                      <el-button
                        v-if="message.status === 0 || message.readStatus === 0"
                        type="primary"
                        link
                        @click="handleMarkAsRead(message.id)"
                      >
                        标记为已读
                      </el-button>
                      <el-button type="danger" link @click="handleDelete(message.id)">
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
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
          </template>
          <el-empty v-else description="暂无消息">
            <template #image>
              <el-icon style="font-size: 80px; color: #909399"><Message /></el-icon>
            </template>
          </el-empty>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Message } from '@element-plus/icons-vue'
import request from '@/utils/request'

const activeTab = ref('all')
const messageList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const hasUnread = ref(false)

// 获取消息列表
const getMessageList = async () => {
  try {
    console.log('开始获取消息列表');
    const res = await request.get('/message/list', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    console.log('获取消息列表响应:', res);
    
    if (res.code === 200) {
      messageList.value = res.data.list || [];
      total.value = res.data.total || 0;
      hasUnread.value = res.data.unreadCount > 0;
      
      // 确保所有消息对象都有readStatus和status属性
      messageList.value.forEach(message => {
        // 如果readStatus不存在但status存在，同步两者
        if (message.readStatus === undefined && message.status !== undefined) {
          message.readStatus = message.status;
        }
        // 如果status不存在但readStatus存在，同步两者
        if (message.status === undefined && message.readStatus !== undefined) {
          message.status = message.readStatus;
        }
      });
      
      console.log('处理后的消息列表:', messageList.value);
    } else {
      console.error('获取消息列表失败，错误码:', res.code, '错误信息:', res.message);
      ElMessage.error(res.message || '获取消息列表失败')
    }
  } catch (error) {
    console.error('获取消息列表失败:', error)
    ElMessage.error('获取消息列表失败')
  }
}

// 标记消息为已读
const handleMarkAsRead = async (messageId) => {
  try {
    console.log('开始标记消息为已读, ID:', messageId);
    const res = await request.put(`/message/${messageId}/read`)
    console.log('标记已读返回结果:', res);
    
    if (res.code === 200) {
      // 直接更新本地状态，避免重新获取列表
      const message = messageList.value.find(item => item.id === messageId);
      if (message) {
        message.status = 1;
        message.readStatus = 1;
        // 检查是否还有未读消息
        const stillHasUnread = messageList.value.some(item => item.status === 0 || item.readStatus === 0);
        hasUnread.value = stillHasUnread;
      }
      
      ElMessage.success('已标记为已读')
    } else {
      ElMessage.error(res.message || '标记已读失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

// 标记所有消息为已读
const handleMarkAllAsRead = async () => {
  try {
    console.log('开始标记所有消息为已读');
    const res = await request.put('/message/read/all')
    console.log('标记全部已读返回结果:', res);
    
    if (res.code === 200) {
      // 直接更新本地状态，避免重新获取列表
      messageList.value.forEach(message => {
        message.status = 1;
        message.readStatus = 1;
      });
      hasUnread.value = false;
      
      ElMessage.success('已全部标记为已读')
    } else {
      ElMessage.error(res.message || '标记全部已读失败')
    }
  } catch (error) {
    console.error('标记全部已读失败:', error)
    ElMessage.error('标记全部已读失败')
  }
}

// 删除消息
const handleDelete = async (messageId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.delete(`/message/${messageId}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getMessageList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除消息失败:', error)
      ElMessage.error('删除消息失败')
    }
  }
}

// 处理分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  getMessageList()
}

// 处理页码改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  getMessageList()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 获取消息类型标签样式
const getMessageTypeTag = (type) => {
  const types = {
    1: 'info',    // 系统消息
    2: 'warning', // 预约消息
    3: 'success', // 收藏消息
    4: 'danger'   // 合同消息
  }
  return types[type] || 'info'
}

// 获取消息类型文本
const getMessageTypeText = (type) => {
  const texts = {
    1: '系统消息',
    2: '预约消息',
    3: '收藏消息',
    4: '合同消息'
  }
  return texts[type] || '未知类型'
}

onMounted(() => {
  getMessageList()
})
</script>

<style scoped>
.message-center {
  padding: 20px;
}

.message-card {
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 18px;
  font-weight: 600;
}

.message-list {
  margin-top: 20px;
}

.message-item {
  padding: 16px;
  border-radius: 8px;
  background-color: #fff;
  margin-bottom: 16px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.message-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message-item.unread {
  background-color: #f0f9ff;
  border-color: #91d5ff;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.message-time {
  font-size: 14px;
  color: #909399;
}

.message-body {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 12px 0;
}

.message-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.message-actions {
  display: flex;
  gap: 12px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 