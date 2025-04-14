import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import relativeTime from 'dayjs/plugin/relativeTime'

// 设置语言为中文
dayjs.locale('zh-cn')
// 加载相对时间插件
dayjs.extend(relativeTime)

/**
 * 格式化日期
 * @param {string|Date} date 日期
 * @param {string} format 格式化模式，默认 YYYY-MM-DD HH:mm:ss
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  return dayjs(date).format(format)
}

/**
 * 获取相对时间
 * @param {string|Date} date 日期
 * @returns {string} 相对时间，如"3小时前"
 */
export function fromNow(date) {
  if (!date) return ''
  return dayjs(date).fromNow()
}

/**
 * 获取日期范围
 * @param {number} days 天数
 * @returns {[Date, Date]} 开始日期和结束日期
 */
export function getDateRange(days) {
  const end = dayjs()
  const start = dayjs().subtract(days, 'day')
  return [start.toDate(), end.toDate()]
}

/**
 * 判断日期是否过期
 * @param {string|Date} date 日期
 * @returns {boolean} 是否过期
 */
export function isExpired(date) {
  if (!date) return true
  return dayjs(date).isBefore(dayjs())
}

/**
 * 获取两个日期之间的天数
 * @param {string|Date} start 开始日期
 * @param {string|Date} end 结束日期
 * @returns {number} 天数
 */
export function getDaysBetween(start, end) {
  if (!start || !end) return 0
  return dayjs(end).diff(dayjs(start), 'day')
}

/**
 * 格式化时间段
 * @param {number} minutes 分钟数
 * @returns {string} 格式化后的时间段，如"2小时30分钟"
 */
export function formatDuration(minutes) {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours ? hours + '小时' : ''}${mins ? mins + '分钟' : ''}`
} 