import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getHouseList as getHouseListApi,
  getHouseDetail as getHouseDetailApi,
  favoriteHouse as favoriteHouseApi,
  unfavoriteHouse as unfavoriteHouseApi,
  bookHouse as bookHouseApi,
  getHouseComments as getHouseCommentsApi,
  addComment as addCommentApi,
  getHotHouses as getHotHousesApi,
  getRecommendHouses as getRecommendHousesApi,
  searchHouses as searchHousesApi
} from '@/api'

export const useHouseStore = defineStore('house', () => {
  // 状态
  const houseList = ref([])
  const total = ref(0)
  const loading = ref(false)
  const currentHouse = ref(null)
  const hotHouses = ref([])
  const recommendHouses = ref([])

  // 获取房源列表
  const getHouseList = async (params) => {
    loading.value = true
    try {
      const data = await getHouseListApi(params)
      houseList.value = data.list
      total.value = data.total
    } catch (error) {
      console.error('获取房源列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取房源详情
  const getHouseDetail = async (id) => {
    try {
      const data = await getHouseDetailApi(id)
      currentHouse.value = data
      return data
    } catch (error) {
      console.error('获取房源详情失败:', error)
      return null
    }
  }

  // 收藏房源
  const favoriteHouse = async (id) => {
    try {
      await favoriteHouseApi(id)
      if (currentHouse.value && currentHouse.value.id === id) {
        currentHouse.value.isFavorite = true
      }
      return true
    } catch (error) {
      console.error('收藏失败:', error)
      return false
    }
  }

  // 取消收藏
  const unfavoriteHouse = async (id) => {
    try {
      await unfavoriteHouseApi(id)
      if (currentHouse.value && currentHouse.value.id === id) {
        currentHouse.value.isFavorite = false
      }
      return true
    } catch (error) {
      console.error('取消收藏失败:', error)
      return false
    }
  }

  // 预约看房
  const bookHouse = async (bookingForm) => {
    try {
      await bookHouseApi(bookingForm)
      return true
    } catch (error) {
      console.error('预约失败:', error)
      return false
    }
  }

  // 获取房源评论
  const getHouseComments = async (id, params) => {
    try {
      const data = await getHouseCommentsApi(id, params)
      return data
    } catch (error) {
      console.error('获取评论失败:', error)
      return { list: [], total: 0 }
    }
  }

  // 发表评论
  const addComment = async (id, commentData) => {
    try {
      await addCommentApi(id, commentData)
      return true
    } catch (error) {
      console.error('发表评论失败:', error)
      return false
    }
  }

  // 获取热门房源
  const getHotHouses = async () => {
    try {
      const data = await getHotHousesApi()
      hotHouses.value = data
    } catch (error) {
      console.error('获取热门房源失败:', error)
      hotHouses.value = []
    }
  }

  // 获取推荐房源
  const getRecommendHouses = async () => {
    try {
      const data = await getRecommendHousesApi()
      recommendHouses.value = data
    } catch (error) {
      console.error('获取推荐房源失败:', error)
      recommendHouses.value = []
    }
  }

  // 搜索房源
  const searchHouses = async (params) => {
    loading.value = true
    try {
      const data = await searchHousesApi(params)
      houseList.value = data.list
      total.value = data.total
    } catch (error) {
      console.error('搜索房源失败:', error)
      houseList.value = []
      total.value = 0
    } finally {
      loading.value = false
    }
  }

  return {
    houseList,
    total,
    loading,
    currentHouse,
    hotHouses,
    recommendHouses,
    getHouseList,
    getHouseDetail,
    favoriteHouse,
    unfavoriteHouse,
    bookHouse,
    getHouseComments,
    addComment,
    getHotHouses,
    getRecommendHouses,
    searchHouses
  }
}) 