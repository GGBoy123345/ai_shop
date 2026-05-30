import request from './request'

export function createNotification(data) {
  return request.post('/notify-svc/internal/notifications', data)
}

export function sendSms(data) {
  return request.post('/notify-svc/internal/notifications/sms/send', data)
}

export function sendEmail(data) {
  return request.post('/notify-svc/internal/notifications/emails/send', data)
}
