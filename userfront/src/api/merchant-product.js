import request from './request'

export const getMerchantProducts = (params) => request.get('/products/merchant', { params })
export const addProduct = (data) => request.post('/products', data)
export const updateProduct = (id, data) => request.put(`/products/${id}`, data)
export const updateProductStatus = (id, status) => request.put(`/products/${id}/status`, null, { params: { status } })
export const deleteProduct = (id) => request.delete(`/products/${id}`)
export const getProductDetail = (id) => request.get(`/products/${id}`)
export const getCategoryTree = () => request.get('/categories/tree')
export const getAttributeTemplates = (categoryId) => request.get(`/attribute-templates/category/${categoryId}`)
export const getProductSkus = (productId) => request.get(`/products/${productId}/skus`)
export const addSku = (productId, data) => request.post('/skus', data, { params: { productId } })
export const updateSku = (id, data) => request.put(`/skus/${id}`, data)
export const deleteSku = (id) => request.delete(`/skus/${id}`)
