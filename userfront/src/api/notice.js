import request from './request'

export const getNotices = (params) => request.get('/notices', { params })
