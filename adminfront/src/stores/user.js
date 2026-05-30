import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getStorage, setStorage, removeStorage } from '../utils/storage'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(getStorage('admin_token') || '')
  const userInfo = ref(getStorage('admin_info') || null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(t) {
    token.value = t
    setStorage('admin_token', t)
  }

  function setUserInfo(info) {
    userInfo.value = info
    setStorage('admin_info', info)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    removeStorage('admin_token')
    removeStorage('admin_info')
  }

  return { token, userInfo, isLoggedIn, setToken, setUserInfo, logout }
})
