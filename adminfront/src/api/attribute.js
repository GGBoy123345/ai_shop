import request from './request'

export function getAttributeTemplates(params) {
  return request.get('/attribute-templates', { params })
}

export function getAttributeTemplatesByCategory(categoryId) {
  return request.get(`/attribute-templates/category/${categoryId}`)
}

export function addAttributeTemplate(data) {
  return request.post('/attribute-templates', data)
}

export function updateAttributeTemplate(id, data) {
  return request.put(`/attribute-templates/${id}`, data)
}

export function deleteAttributeTemplate(id) {
  return request.delete(`/attribute-templates/${id}`)
}

export function addAttributeOption(templateId, data) {
  return request.post(`/attribute-templates/${templateId}/options`, data)
}

export function deleteAttributeOption(id) {
  return request.delete(`/attribute-templates/options/${id}`)
}
