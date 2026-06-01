import request from './request'

export function getProductList(params) { return request.get('/products', { params }) }
export function getProductDetail(id) { return request.get(`/products/${id}`) }
export function auditProduct(id, data) { return request.put(`/products/${id}/audit`, null, { params: data }) }
export function updateProductStatus(id, data) { return request.put(`/products/${id}/status`, null, { params: data }) }
export function setHot(id, value) { return request.put(`/products/${id}/hot`, null, { params: { value } }) }
export function setNew(id, value) { return request.put(`/products/${id}/new`, null, { params: { value } }) }
export function setRecommend(id, value) { return request.put(`/products/${id}/recommend`, null, { params: { value } }) }
export function setSortOrder(id, value) { return request.put(`/products/${id}/sort`, null, { params: { value } }) }
