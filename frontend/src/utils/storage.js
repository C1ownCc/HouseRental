/**
 * 存储数据到localStorage
 * @param {string} key 键
 * @param {any} value 值
 * @param {number} [expireHours] 过期时间（小时）
 */
export function setStorage(key, value, expireHours) {
  const data = {
    value,
    time: Date.now()
  }
  if (expireHours) {
    data.expire = expireHours * 60 * 60 * 1000
  }
  localStorage.setItem(key, JSON.stringify(data))
}

/**
 * 从localStorage获取数据
 * @param {string} key 键
 * @param {any} defaultValue 默认值
 * @returns {any} 存储的值
 */
export function getStorage(key, defaultValue = null) {
  const dataStr = localStorage.getItem(key)
  if (!dataStr) return defaultValue

  try {
    const data = JSON.parse(dataStr)
    if (data.expire) {
      if (Date.now() - data.time > data.expire) {
        localStorage.removeItem(key)
        return defaultValue
      }
    }
    return data.value
  } catch {
    return defaultValue
  }
}

/**
 * 从localStorage移除数据
 * @param {string} key 键
 */
export function removeStorage(key) {
  localStorage.removeItem(key)
}

/**
 * 清空localStorage
 */
export function clearStorage() {
  localStorage.clear()
}

/**
 * 存储数据到sessionStorage
 * @param {string} key 键
 * @param {any} value 值
 */
export function setSessionStorage(key, value) {
  sessionStorage.setItem(key, JSON.stringify(value))
}

/**
 * 从sessionStorage获取数据
 * @param {string} key 键
 * @param {any} defaultValue 默认值
 * @returns {any} 存储的值
 */
export function getSessionStorage(key, defaultValue = null) {
  const value = sessionStorage.getItem(key)
  if (value === null) return defaultValue
  try {
    return JSON.parse(value)
  } catch {
    return value
  }
}

/**
 * 从sessionStorage移除数据
 * @param {string} key 键
 */
export function removeSessionStorage(key) {
  sessionStorage.removeItem(key)
}

/**
 * 清空sessionStorage
 */
export function clearSessionStorage() {
  sessionStorage.clear()
}