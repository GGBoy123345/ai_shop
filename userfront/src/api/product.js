import request from './request'

export const getProductList = (params) => request.get('/products', { params })
export const getProductDetail = (id) => request.get(`/products/${id}`)
export const getCategoryTree = () => request.get('/categories/tree')
export const getBanners = () => request.get('/banners')
