/**
 * 验证手机号
 * @param {string} phone 手机号
 * @returns {boolean} 是否有效
 */
export function isValidPhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
}

/**
 * 验证邮箱
 * @param {string} email 邮箱
 * @returns {boolean} 是否有效
 */
export function isValidEmail(email) {
  return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)
}

/**
 * 验证密码强度
 * @param {string} password 密码
 * @returns {{valid: boolean, message: string}} 验证结果
 */
export function validatePassword(password) {
  if (password.length < 6) {
    return { valid: false, message: '密码长度不能小于6位' }
  }
  if (password.length > 20) {
    return { valid: false, message: '密码长度不能超过20位' }
  }
  return { valid: true, message: '密码格式正确' }
}

/**
 * 验证身份证号
 * @param {string} idCard 身份证号
 * @returns {boolean} 是否有效
 */
export function isValidIdCard(idCard) {
  return /^[1-9]\d{5}(19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dX]$/.test(idCard)
}

/**
 * 验证金额
 * @param {string|number} amount 金额
 * @returns {boolean} 是否有效
 */
export function isValidAmount(amount) {
  return /^\d+(\.\d{1,2})?$/.test(amount.toString())
}

/**
 * 验证URL
 * @param {string} url URL
 * @returns {boolean} 是否有效
 */
export function isValidUrl(url) {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}

/**
 * 验证中文姓名
 * @param {string} name 姓名
 * @returns {boolean} 是否有效
 */
export function isValidChineseName(name) {
  return /^[\u4e00-\u9fa5]{2,}$/.test(name)
}

/**
 * 验证房源标题
 * @param {string} title 标题
 * @returns {{valid: boolean, message: string}} 验证结果
 */
export function validateHouseTitle(title) {
  if (!title) {
    return { valid: false, message: '标题不能为空' }
  }
  if (title.length < 5) {
    return { valid: false, message: '标题长度不能小于5个字符' }
  }
  if (title.length > 50) {
    return { valid: false, message: '标题长度不能超过50个字符' }
  }
  return { valid: true, message: '标题格式正确' }
}

/**
 * 验证租金
 * @param {number} rent 租金
 * @returns {{valid: boolean, message: string}} 验证结果
 */
export function validateRent(rent) {
  if (!rent) {
    return { valid: false, message: '租金不能为空' }
  }
  if (rent < 0) {
    return { valid: false, message: '租金不能为负数' }
  }
  if (rent > 1000000) {
    return { valid: false, message: '租金不能超过100万' }
  }
  if (!isValidAmount(rent)) {
    return { valid: false, message: '租金格式不正确' }
  }
  return { valid: true, message: '租金格式正确' }
}