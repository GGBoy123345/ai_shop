import request from './request'

export function getBannerList(params) { return request.get('/banners', { params }) }
export function addBanner(data) { return request.post('/banners', data) }
export function updateBanner(id, data) { return request.put(`/banners/${id}`, data) }
export function deleteBanner(id) { return request.delete(`/banners/${id}`) }
