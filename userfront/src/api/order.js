import request from './request'

export const createOrder = (data) => request.post('/orders', data)
export const getOrderList = (params) => request.get('/orders', { params })
export const getOrderDetail = (id) => request.get(`/orders/${id}`)
export const cancelOrder = (id) => request.put(`/orders/${id}/cancel`)
export const confirmReceive = (id) => request.put(`/orders/${id}/confirm`)
export const deleteOrder = (id) => request.delete(`/orders/${id}`)
export const payOrder = (id) => request.post(`/orders/${id}/pay`)
