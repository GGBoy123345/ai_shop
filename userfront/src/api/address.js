import request from './request'

export const getAddressList = () => request.get('/addresses')
export const addAddress = (data) => request.post('/addresses', data)
export const updateAddress = (id, data) => request.put(`/addresses/${id}`, data)
export const deleteAddress = (id) => request.delete(`/addresses/${id}`)
export const setDefaultAddress = (id) => request.put(`/addresses/${id}/default`)
