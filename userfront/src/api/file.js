import request from './request'

export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/files/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const uploadVideo = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/files/upload/video', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const uploadBatch = (files) => {
  const formData = new FormData()
  files.forEach(file => formData.append('files', file))
  return request.post('/files/upload/batch', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const getFileInfo = (id) => request.get(`/files/${id}`)
export const deleteFile = (id) => request.delete(`/files/${id}`)
