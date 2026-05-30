import request from './request'

export const applyMerchant = (data) => request.post('/merchants/apply', data)
export const getMyMerchant = () => request.get('/merchants/me')
export const updateMyMerchant = (data) => request.put('/merchants/me', data)
export const getMerchantDetail = (id) => request.get(`/merchants/${id}`)
