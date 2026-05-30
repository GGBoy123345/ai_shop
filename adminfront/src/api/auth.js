import request from './request'

export const adminLogin = (data) => request.post('/admin/login', data)
