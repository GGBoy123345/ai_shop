import axios from 'axios'
import { getStorage, removeStorage } from '../utils/storage'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = getStorage('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  res => {
    const { code, message, data } = res.data
    if (code === 200) return data
    return Promise.reject(new Error(message))
  },
  error => {
    if (error.response?.status === 401) {
      if (router.currentRoute.value.path !== '/login') {
        removeStorage('token')
        removeStorage('userInfo')
        router.replace('/login')
      }
    }
    return Promise.reject(error)
  }
)

export default request
