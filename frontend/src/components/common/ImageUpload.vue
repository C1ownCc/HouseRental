<template>
  <div class="image-upload">
    <el-upload
      :action="action"
      :headers="headers"
      :multiple="multiple"
      :limit="limit"
      :file-list="fileList"
      :list-type="listType"
      :before-upload="handleBeforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-exceed="handleExceed"
      :on-remove="handleRemove"
      :class="{ 'hide-upload': fileList.length >= limit }"
    >
      <template #trigger>
        <el-button type="primary" :icon="Plus">{{ buttonText }}</el-button>
      </template>
      <template #tip>
        <div class="upload-tip" v-if="tip">{{ tip }}</div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  value: {
    type: [String, Array],
    default: ''
  },
  action: {
    type: String,
    required: true
  },
  multiple: {
    type: Boolean,
    default: false
  },
  limit: {
    type: Number,
    default: 1
  },
  fileSize: {
    type: Number,
    default: 5 // 默认5MB
  },
  tip: {
    type: String,
    default: ''
  },
  listType: {
    type: String,
    default: 'picture-card'
  },
  buttonText: {
    type: String,
    default: '上传图片'
  }
})

const emit = defineEmits(['update:value', 'change'])

// 计算文件列表
const fileList = computed(() => {
  if (!props.value) return []
  if (typeof props.value === 'string') {
    return [{ url: props.value, name: '图片' }]
  }
  return props.value.map(url => ({ url, name: '图片' }))
})

// 请求头（可以添加token等认证信息）
const headers = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

// 上传前验证
const handleBeforeUpload = (file) => {
  // 验证文件类型
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }

  // 验证文件大小
  const isLtSize = file.size / 1024 / 1024 < props.fileSize
  if (!isLtSize) {
    ElMessage.error(`图片大小不能超过 ${props.fileSize}MB！`)
    return false
  }

  return true
}

// 上传成功
const handleSuccess = (response, file) => {
  if (response.code === 200) {
    const url = response.data.url
    if (props.multiple) {
      const value = props.value ? [...props.value, url] : [url]
      emit('update:value', value)
    } else {
      emit('update:value', url)
    }
    emit('change', url)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleError = () => {
  ElMessage.error('上传失败，请重试')
}

// 超出限制
const handleExceed = () => {
  ElMessage.warning(`最多只能上传 ${props.limit} 张图片`)
}

// 移除图片
const handleRemove = (file) => {
  if (props.multiple) {
    const value = props.value.filter(url => url !== file.url)
    emit('update:value', value)
  } else {
    emit('update:value', '')
  }
  emit('change', '')
}
</script>

<style scoped lang="scss">
.image-upload {
  :deep(.el-upload--picture-card) {
    width: 150px;
    height: 150px;
    line-height: 150px;
  }

  :deep(.el-upload-list--picture-card .el-upload-list__item) {
    width: 150px;
    height: 150px;
  }

  .upload-tip {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-top: 5px;
  }

  &.hide-upload {
    :deep(.el-upload--picture-card) {
      display: none;
    }
  }
}
</style> 