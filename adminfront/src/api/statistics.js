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
