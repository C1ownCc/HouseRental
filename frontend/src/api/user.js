import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

// 获取用户收藏列表
export function getFavorites(params) {
  return request({
    url: '/user/favorites',
    method: 'get',
    params
  })
}

// 获取用户预约列表
export function getBookings(params) {
  return request({
    url: '/user/bookings',
    method: 'get',
    params
  })
}

// 获取用户消息列表
export function getMessages(params) {
  return request({
    url: '/user/messages',
    method: 'get',
    params
  })
}

// 标记消息为已读
export function readMessage(id) {
  return request({
    url: `/user/messages/${id}/read`,
    method: 'put'
  })
}

// 删除消息
export function deleteMessage(id) {
  return request({
    url: `/user/messages/${id}`,
    method: 'delete'
  })
}

// 获取热门房东列表
export function getPopularLandlords() {
  return request({
    url: '/user/popular-landlords',
    method: 'get'
  })
} 