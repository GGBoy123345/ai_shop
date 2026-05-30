const PREFIX = 'ai_mall_'

export const getStorage = (key) => {
  const val = localStorage.getItem(PREFIX + key)
  try { return JSON.parse(val) } catch { return val }
}

export const setStorage = (key, val) => {
  localStorage.setItem(PREFIX + key, JSON.stringify(val))
}

export const removeStorage = (key) => {
  localStorage.removeItem(PREFIX + key)
}
