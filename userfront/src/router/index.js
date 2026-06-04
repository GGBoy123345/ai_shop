import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // Desktop layout — core editorial pages
  {
    path: '/',
    component: () => import('../layouts/DesktopLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../pages/home/index.vue') },
      { path: 'category', name: 'Category', component: () => import('../pages/category/index.vue') },
      { path: 'product/:id', name: 'ProductDetail', component: () => import('../pages/product/detail.vue') },
      { path: 'ai-compare', name: 'AiCompare', component: () => import('../pages/ai/compare.vue') },
      { path: 'ai-tryon', name: 'AiTryon', component: () => import('../pages/ai/tryon.vue') }
    ]
  },
  // Standalone pages (still use Vant for now)
  { path: '/login', name: 'Login', component: () => import('../pages/login/index.vue') },
  { path: '/register', name: 'Register', component: () => import('../pages/register/index.vue') },
  { path: '/search', name: 'Search', component: () => import('../pages/search/index.vue') },
  { path: '/cart', name: 'Cart', component: () => import('../pages/cart/index.vue') },
  { path: '/user', name: 'User', component: () => import('../pages/user/index.vue') },
  { path: '/address', name: 'Address', component: () => import('../pages/address/index.vue') },
  { path: '/favorite', name: 'Favorite', component: () => import('../pages/favorite/index.vue') },
  { path: '/order', name: 'OrderList', component: () => import('../pages/order/list.vue') },
  { path: '/order/create', name: 'CreateOrder', component: () => import('../pages/order/create.vue') },
  { path: '/order/:id', name: 'OrderDetail', component: () => import('../pages/order/detail.vue') },
  { path: '/notification', name: 'Notification', component: () => import('../pages/notification/index.vue') },
  { path: '/merchant/apply', name: 'MerchantApply', component: () => import('../pages/merchant/apply.vue') },
  { path: '/merchant', name: 'MerchantInfo', component: () => import('../pages/merchant/index.vue') },
  { path: '/merchant/products', name: 'MerchantProducts', component: () => import('../pages/merchant/products.vue') },
  { path: '/merchant/product/edit', name: 'MerchantProductEdit', component: () => import('../pages/merchant/product-edit.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

export default router
