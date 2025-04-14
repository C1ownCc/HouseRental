import { defineStore } from 'pinia'
import { ref } from 'vue'

// 用户状态管理
export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({})

  // 设置token
  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 清除token
  function clearToken() {
    token.value = ''
    localStorage.removeItem('token')
  }

  // 设置用户信息
  function setUserInfo(info) {
    userInfo.value = info
  }

  // 清除用户信息
  function clearUserInfo() {
    userInfo.value = {}
  }

  return {
    token,
    userInfo,
    setToken,
    clearToken,
    setUserInfo,
    clearUserInfo
  }
})

// 房源状态管理
export const useHouseStore = defineStore('house', () => {
  const houseList = ref([])
  const currentHouse = ref({})
  const searchParams = ref({
    keyword: '',
    area: '',
    price: '',
    type: ''
  })

  // 设置房源列表
  function setHouseList(list) {
    houseList.value = list
  }

  // 设置当前查看的房源
  function setCurrentHouse(house) {
    currentHouse.value = house
  }

  // 设置搜索参数
  function setSearchParams(params) {
    searchParams.value = { ...searchParams.value, ...params }
  }

  return {
    houseList,
    currentHouse,
    searchParams,
    setHouseList,
    setCurrentHouse,
    setSearchParams
  }
})
