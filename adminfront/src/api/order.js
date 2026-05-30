import request from './request'

export function getOrderList(params) { return request.get('/orders', { params }) }
export function getOrderDetail(id) { return request.get(`/orders/${id}`) }
export function shipOrder(id, data) { return request.put(`/orders/${id}/ship`, data) }
export function getOrderStatistics() { return request.get('/orders/statistics') }
