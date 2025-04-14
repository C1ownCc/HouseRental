<template>
  <div v-if="visible" class="loading-wrapper" :class="{ fullscreen }">
    <div class="loading-content">
      <el-icon class="loading-icon" :size="size">
        <Loading />
      </el-icon>
      <p v-if="text" class="loading-text">{{ text }}</p>
    </div>
  </div>
</template>

<script setup>
import { Loading } from '@element-plus/icons-vue'

defineProps({
  visible: {
    type: Boolean,
    default: true
  },
  fullscreen: {
    type: Boolean,
    default: false
  },
  size: {
    type: Number,
    default: 30
  },
  text: {
    type: String,
    default: '加载中...'
  }
})
</script>

<style scoped lang="scss">
.loading-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;

  &.fullscreen {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.9);
    z-index: 9999;
  }

  .loading-content {
    text-align: center;

    .loading-icon {
      animation: rotating 2s linear infinite;
      color: var(--el-color-primary);
    }

    .loading-text {
      margin-top: 10px;
      color: var(--el-text-color-secondary);
      font-size: 14px;
    }
  }
}

@keyframes rotating {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style> 