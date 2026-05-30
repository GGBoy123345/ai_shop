import request from './request'

export const getUserInfo = () => request.get('/users/me')
export const updateUserInfo = (data) => request.put('/users/me', data)
