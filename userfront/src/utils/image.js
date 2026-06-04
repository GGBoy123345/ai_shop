/**
 * 获取缩略图 URL
 * 规则：原图 abc.jpg → abc_thumb.jpg
 * 如果 URL 无效或已经是缩略图，返回原 URL
 */
export function getThumbnailUrl(url) {
  if (!url || typeof url !== 'string') return url
  // 已经是缩略图，不再转换
  if (url.includes('_thumb.')) return url
  // 替换扩展名前的部分为 _thumb
  return url.replace(/\.(\w+)$/, '_thumb.$1')
}
