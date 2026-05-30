import request from './request'

export const applyRefund = (data) => request.post('/refunds', data)
export const getRefundList = (params) => request.get('/refunds', { params })
export const getRefundDetail = (id) => request.get(`/refunds/${id}`)
