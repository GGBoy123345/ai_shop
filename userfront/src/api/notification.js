import request from './request'

export function getNotifications(params) {
  return request.get('/notify-svc/api/notifications', { params })
}

export function getUnreadCount() {
  return request.get('/notify-svc/api/notifications/unread-count')
}

export function markRead(id) {
  return request.put(`/notify-svc/api/notifications/${id}/read`)
}

export function markAllRead() {
  return request.put('/notify-svc/api/notifications/read-all')
}

export function deleteNotification(id) {
  return request.delete(`/notify-svc/api/notifications/${id}`)
}
