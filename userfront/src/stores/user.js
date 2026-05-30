import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getStorage, setStorage, removeStorage } from '../utils/storage'

export const useUserStore = defineStore('user', () => {
  const token = ref(getStorage('token') || '')
  const userInfo = ref(getStorage('userInfo') || null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(t) {
    token.value = t
    setStorage('token', t)
  }

  function setUserInfo(info) {
    userInfo.value = info
    setStorage('userInfo', info)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    removeStorage('token')
    removeStorage('userInfo')
  }

  return { token, userInfo, isLoggedIn, setToken, setUserInfo, logout }
})
