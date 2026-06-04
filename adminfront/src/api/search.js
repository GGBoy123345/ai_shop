import request from './request'

// 手动同步 ES 索引
export function syncEsIndex() {
  return request.post('/search/sync')
}
