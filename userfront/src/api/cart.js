import request from './request'

export const getCartList = () => request.get('/carts')
export const addToCart = (data) => request.post('/carts', data)
export const updateCartQuantity = (id, quantity) => request.put(`/carts/${id}/quantity`, { quantity })
export const deleteCartItem = (id) => request.delete(`/carts/${id}`)
