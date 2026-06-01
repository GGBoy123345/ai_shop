import request from './request'

export function getNoticeList(params) { return request.get('/admin/notices', { params }) }
export function addNotice(data) { return request.post('/admin/notices', data) }
export function updateNotice(id, data) { return request.put(`/admin/notices/${id}`, data) }
export function deleteNotice(id) { return request.delete(`/admin/notices/${id}`) }
export function updateNoticeStatus(id, status) { return request.put(`/admin/notices/${id}/status`, null, { params: { status } }) }
