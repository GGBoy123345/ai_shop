import request from './request'

export const getFavoriteList = (params) => request.get('/favorites', { params })
export const addFavorite = (productId) => request.post('/favorites', { productId })
export const removeFavorite = (productId) => request.delete(`/favorites/${productId}`)
export const checkFavorite = (productId) => request.get(`/favorites/check/${productId}`)
