import request from '@/utils/request'

// 获取房源列表
export function getHouseList(params) {
  return request({
    url: '/house/list',
    method: 'get',
    params
  })
}

// 获取房源详情
export function getHouseDetail(id) {
  return request({
    url: `/house/${id}`,
    method: 'get'
  })
}

// 收藏房源
export function favoriteHouse(id) {
  return request({
    url: `/house/favorite/${id}`,
    method: 'post'
  })
}

// 取消收藏
export function unfavoriteHouse(id) {
  return request({
    url: `/house/favorite/${id}`,
    method: 'delete'
  })
}

// 预约看房
export function bookHouse(data) {
  return request({
    url: '/booking/create',
    method: 'post',
    data
  })
}

// 获取房源评论
export function getHouseComments(id, params) {
  return request({
    url: `/house/${id}/comments`,
    method: 'get',
    params
  })
}

// 发表评论
export function addComment(id, data) {
  return request({
    url: `/house/${id}/comments`,
    method: 'post',
    data
  })
}

// 获取房源统计数据
export function getHouseStats() {
  return request({
    url: '/house/stats',
    method: 'get'
  })
}

// 搜索房源
export function searchHouses(params) {
  return request({
    url: '/house/search',
    method: 'get',
    params
  })
}

// 获取热门房源
export function getHotHouses() {
  return request({
    url: '/house/hot',
    method: 'get'
  })
}

// 获取推荐房源
export function getRecommendHouses() {
  return request({
    url: '/house/recommend',
    method: 'get'
  })
} 