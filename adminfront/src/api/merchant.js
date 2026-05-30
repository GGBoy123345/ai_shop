import request from './request'

export const getMerchantList = (params) => request.get('/merchants', { params })
export const auditMerchant = (id, data) => request.put(`/merchants/${id}/audit`, data)
export const updateMerchantStatus = (id, data) => request.put(`/merchants/${id}/status`, data)
