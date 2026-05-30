import request from './request'

export const searchProducts = (params) => request.get('/search', { params })
export const getHotKeywords = () => request.get('/search/hot')
export const searchSuggest = (keyword) => request.get('/search/suggest', { params: { keyword } })
export const getSearchHistory = () => request.get('/search/history')
export const clearSearchHistory = () => request.delete('/search/history')
