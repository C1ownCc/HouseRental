<template>
  <div class="empty-wrapper">
    <el-empty
      :image="image"
      :image-size="200"
      :description="description"
    >
      <template v-if="$slots.default" #default>
        <slot></slot>
      </template>
    </el-empty>
  </div>
</template>

<script setup>
defineProps({
  image: {
    type: String,
    default: undefined
  },
  description: {
    type: String,
    default: '暂无数据'
  }
})
</script>

<style scoped lang="scss">
.empty-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 20px;
}
</style> 