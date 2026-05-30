import request from './request'

export function getProductList(params) { return request.get('/products', { params }) }
export function getProductDetail(id) { return request.get(`/products/${id}`) }
export function auditProduct(id, data) { return request.put(`/products/${id}/audit`, null, { params: data }) }
export function updateProductStatus(id, data) { return request.put(`/products/${id}/status`, null, { params: data }) }
