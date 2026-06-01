import request from './request'

// 获取用户和商家统计
export function getUserMerchantStats() {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

// 获取商品总数
export function getProductCount() {
  return request({
    url: '/products/count',
    method: 'get'
  })
}

// 获取订单总数
export function getOrderCount() {
  return request({
    url: '/orders/count',
    method: 'get'
  })
}

// 获取近7天订单趋势
export function getOrderTrend() {
  return request({
    url: '/orders/trend',
    method: 'get'
  })
}

// 获取近7天销售额趋势
export function getSalesTrend() {
  return request({
    url: '/orders/sales-trend',
    method: 'get'
  })
}

// 获取商品分类统计
export function getCategoryStats() {
  return request({
    url: '/categories/stats',
    method: 'get'
  })
}
