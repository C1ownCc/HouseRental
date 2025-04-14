export * from './date'
export * from './validate'
export * from './storage'

/**
 * 格式化金额
 * @param {number} amount 金额
 * @param {number} decimals 小数位数
 * @returns {string} 格式化后的金额
 */
export function formatAmount(amount, decimals = 2) {
  if (!amount && amount !== 0) return ''
  return Number(amount).toFixed(decimals).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

/**
 * 格式化面积
 * @param {number} area 面积
 * @returns {string} 格式化后的面积
 */
export function formatArea(area) {
  if (!area && area !== 0) return ''
  return `${Number(area).toFixed(1)}㎡`
}

/**
 * 格式化房型
 * @param {number} rooms 房间数
 * @param {number} halls 厅数
 * @returns {string} 格式化后的房型
 */
export function formatHouseType(rooms, halls) {
  if (!rooms && rooms !== 0) return ''
  return `${rooms}室${halls ? halls + '厅' : ''}`
}

/**
 * 深拷贝
 * @param {any} obj 要拷贝的对象
 * @returns {any} 拷贝后的对象
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  const clone = Array.isArray(obj) ? [] : {}
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      clone[key] = deepClone(obj[key])
    }
  }
  return clone
}

/**
 * 防抖函数
 * @param {Function} fn 要执行的函数
 * @param {number} delay 延迟时间（毫秒）
 * @returns {Function} 防抖后的函数
 */
export function debounce(fn, delay) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 节流函数
 * @param {Function} fn 要执行的函数
 * @param {number} delay 延迟时间（毫秒）
 * @returns {Function} 节流后的函数
 */
export function throttle(fn, delay) {
  let timer = null
  let start = Date.now()
  return function (...args) {
    const current = Date.now()
    const remaining = delay - (current - start)
    if (timer) clearTimeout(timer)
    if (remaining <= 0) {
      fn.apply(this, args)
      start = Date.now()
    } else {
      timer = setTimeout(() => {
        fn.apply(this, args)
        start = Date.now()
      }, remaining)
    }
  }
} 