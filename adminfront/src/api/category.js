import request from './request'

export function getCategoryTree() { return request.get('/categories/tree') }
export function addCategory(data) { return request.post('/categories', data) }
export function updateCategory(id, data) { return request.put(`/categories/${id}`, data) }
export function deleteCategory(id) { return request.delete(`/categories/${id}`) }
