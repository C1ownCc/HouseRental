/**
 * 处理图片URL，确保URL包含/uploads前缀
 * @param {string} url 原始URL
 * @returns {string} 处理后的URL
 */
export const getImageUrl = (url) => {
  if (!url) return ''
  return url.startsWith('/uploads') ? url : `/uploads${url}`
}

/**
 * 处理图片URL列表，确保每个URL都包含/uploads前缀
 * @param {string} urls 逗号分隔的URL字符串
 * @returns {string[]} 处理后的URL数组
 */
export const getImageUrls = (urls) => {
  if (!urls) return []
  return urls.split(',')
    .filter(Boolean)
    .map(url => getImageUrl(url))
}

/**
 * 从URL中移除/uploads前缀，用于提交到后端
 * @param {string} url 原始URL
 * @returns {string} 处理后的URL
 */
export const removeUploadsPrefix = (url) => {
  if (!url) return ''
  return url.replace(/^\/uploads/, '')
}

/**
 * 从URL列表中移除/uploads前缀，用于提交到后端
 * @param {string[]} urls URL数组
 * @returns {string} 处理后的URL字符串
 */
export const removeUploadsPrefixFromList = (urls) => {
  if (!Array.isArray(urls)) return ''
  return urls
    .map(url => removeUploadsPrefix(url))
    .filter(Boolean)
    .join(',')
} 