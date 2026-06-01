import { createRouter, createWebHistory } from 'vue-router'
import { getStorage } from '../utils/storage'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/login/index.vue')
  },
  {
    path: '/',
    component: () => import('../layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../pages/dashboard/index.vue'), meta: { title: '数据概览' } },
      { path: 'user', name: 'User', component: () => import('../pages/user/index.vue'), meta: { title: '用户管理' } },
      { path: 'merchant', name: 'Merchant', component: () => import('../pages/merchant/index.vue'), meta: { title: '商家管理' } },
      { path: 'product', name: 'Product', component: () => import('../pages/product/index.vue'), meta: { title: '商品管理' } },
      { path: 'category', name: 'Category', component: () => import('../pages/category/index.vue'), meta: { title: '分类管理' } },
      { path: 'banner', name: 'Banner', component: () => import('../pages/banner/index.vue'), meta: { title: '轮播图管理' } },
      { path: 'notice', name: 'Notice', component: () => import('../pages/notice/index.vue'), meta: { title: '公告管理' } },
      { path: 'order', name: 'Order', component: () => import('../pages/order/index.vue'), meta: { title: '订单管理' } },
      { path: 'config', name: 'Config', component: () => import('../pages/config/index.vue'), meta: { title: '系统配置' } },
      { path: 'file', name: 'File', component: () => import('../pages/file/index.vue'), meta: { title: '文件管理' } },
      { path: 'notification', name: 'Notification', component: () => import('../pages/notification/index.vue'), meta: { title: '消息通知' } },
      { path: 'log', name: 'Log', component: () => import('../pages/log/index.vue'), meta: { title: '操作日志' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getStorage('admin_token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
