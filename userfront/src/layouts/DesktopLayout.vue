<template>
  <div class="desktop-layout">
    <header class="header" :class="{ scrolled: isScrolled }">
      <div class="header-inner">
        <router-link to="/" class="logo">
          <span class="logo-icon">🛒</span>
          <span class="logo-text">AI商城</span>
        </router-link>

        <nav class="nav">
          <router-link to="/" class="nav-link" :class="{ active: route.name === 'Home' }">首页</router-link>
          <router-link to="/category" class="nav-link" :class="{ active: route.name === 'Category' }">分类</router-link>
          <router-link to="/ai-compare" class="nav-link ai-link" :class="{ active: route.name === 'AiCompare' }">
            <span class="ai-badge">AI</span> 智能对比
          </router-link>
          <router-link to="/ai-tryon" class="nav-link ai-link" :class="{ active: route.name === 'AiTryon' }">
            <span class="ai-badge">AI</span> 线上试衣
          </router-link>
        </nav>

        <div class="header-actions">
          <div class="search-trigger" @click="openSearch">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
          </div>
          <router-link to="/cart" class="cart-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
            </svg>
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount > 99 ? '99+' : cartCount }}</span>
          </router-link>
          <div v-if="userStore.isLoggedIn" class="user-menu" @click="showUserMenu = !showUserMenu">
            <div class="user-avatar">
              {{ (userStore.userInfo?.nickname || 'U').charAt(0) }}
            </div>
            <transition name="dropdown">
              <div v-if="showUserMenu" class="user-dropdown">
                <router-link to="/user" class="dropdown-item" @click="showUserMenu = false">个人中心</router-link>
                <router-link to="/order" class="dropdown-item" @click="showUserMenu = false">我的订单</router-link>
                <router-link to="/favorite" class="dropdown-item" @click="showUserMenu = false">我的收藏</router-link>
                <div class="dropdown-divider"></div>
                <div class="dropdown-item" @click="handleLogout">退出登录</div>
              </div>
            </transition>
          </div>
          <router-link v-else to="/login" class="login-btn">登录</router-link>
        </div>
      </div>
    </header>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <span class="logo-icon">🛒</span>
          <span class="logo-text">AI商城</span>
          <p class="footer-tagline">智能购物，品质生活</p>
        </div>
        <div class="footer-links">
          <div class="footer-col">
            <h4>购物指南</h4>
            <a href="#">购物流程</a>
            <a href="#">配送方式</a>
            <a href="#">售后服务</a>
          </div>
          <div class="footer-col">
            <h4>AI功能</h4>
            <router-link to="/ai-compare">智能对比</router-link>
            <router-link to="/ai-tryon">线上试衣</router-link>
          </div>
          <div class="footer-col">
            <h4>关于我们</h4>
            <a href="#">公司介绍</a>
            <a href="#">联系客服</a>
            <a href="#">商务合作</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2026 AI商城 · 毕业设计作品</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isScrolled = ref(false)
const showUserMenu = ref(false)
const cartCount = ref(0)

function handleScroll() {
  isScrolled.value = window.scrollY > 10
}

function openSearch() {
  router.push('/search')
}

function handleLogout() {
  userStore.logout()
  showUserMenu.value = false
  router.push('/')
}

function handleClickOutside(e) {
  if (showUserMenu.value && !e.target.closest('.user-menu')) {
    showUserMenu.value = false
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.desktop-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Header */
.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: var(--surface);
  height: var(--header-height);
  transition: box-shadow 0.3s ease, background-color 0.3s ease;
}
.header.scrolled {
  box-shadow: var(--shadow-header);
}

.header-inner {
  max-width: var(--content-max);
  margin: 0 auto;
  padding: 0 var(--space-lg);
  height: 100%;
  display: flex;
  align-items: center;
  gap: var(--space-xl);
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  flex-shrink: 0;
}
.logo-icon {
  font-size: 24px;
}
.logo-text {
  font-size: 20px;
  font-weight: 700;
  background: var(--brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1px;
}

/* Navigation */
.nav {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  flex: 1;
}
.nav-link {
  padding: var(--space-sm) var(--space-md);
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  border-radius: var(--radius-md);
  transition: color 0.2s ease, background-color 0.2s ease;
  white-space: nowrap;
}
.nav-link:hover {
  color: var(--text-primary);
  background: rgba(0,0,0,0.03);
}
.nav-link.active {
  color: var(--brand-start);
  background: rgba(102,126,234,0.06);
}
.ai-link {
  display: flex;
  align-items: center;
  gap: 4px;
}
.ai-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--brand-gradient);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 5px;
  border-radius: var(--radius-sm);
  letter-spacing: 0.5px;
}

/* Header Actions */
.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  flex-shrink: 0;
}

.search-trigger {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  color: var(--text-secondary);
  cursor: pointer;
  transition: color 0.2s ease, background-color 0.2s ease;
}
.search-trigger:hover {
  color: var(--text-primary);
  background: rgba(0,0,0,0.04);
}

.cart-btn {
  position: relative;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  color: var(--text-secondary);
  transition: color 0.2s ease, background-color 0.2s ease;
}
.cart-btn:hover {
  color: var(--text-primary);
  background: rgba(0,0,0,0.04);
}
.cart-badge {
  position: absolute;
  top: 0;
  right: -2px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: var(--price-red);
  color: #fff;
  font-size: 10px;
  font-weight: 600;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.user-menu {
  position: relative;
}
.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-full);
  background: var(--brand-gradient);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}
.user-avatar:hover {
  transform: scale(1.05);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 160px;
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-dropdown);
  padding: var(--space-xs) 0;
  z-index: 200;
}
.dropdown-item {
  display: block;
  padding: 10px var(--space-md);
  font-size: 14px;
  color: var(--text-primary);
  transition: background-color 0.15s ease;
  cursor: pointer;
}
.dropdown-item:hover {
  background: rgba(0,0,0,0.03);
}
.dropdown-divider {
  height: 1px;
  background: #f0f0f0;
  margin: var(--space-xs) 0;
}

.dropdown-enter-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.dropdown-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.login-btn {
  padding: 6px 16px;
  font-size: 14px;
  font-weight: 500;
  color: var(--brand-start);
  border: 1px solid var(--brand-start);
  border-radius: var(--radius-full);
  transition: all 0.2s ease;
}
.login-btn:hover {
  background: var(--brand-start);
  color: #fff;
}

/* Main Content */
.main {
  flex: 1;
}

/* Footer */
.footer {
  background: #1a1a2e;
  color: rgba(255,255,255,0.7);
  padding: var(--space-3xl) 0 var(--space-xl);
  margin-top: var(--space-4xl);
}
.footer-inner {
  max-width: var(--content-max);
  margin: 0 auto;
  padding: 0 var(--space-lg);
}
.footer-brand {
  margin-bottom: var(--space-xl);
}
.footer-brand .logo-icon {
  font-size: 20px;
}
.footer-brand .logo-text {
  font-size: 18px;
}
.footer-tagline {
  font-size: 14px;
  margin-top: var(--space-sm);
  color: rgba(255,255,255,0.4);
}
.footer-links {
  display: flex;
  gap: var(--space-3xl);
  margin-bottom: var(--space-xl);
}
.footer-col h4 {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: var(--space-md);
}
.footer-col a {
  display: block;
  font-size: 13px;
  color: rgba(255,255,255,0.5);
  padding: 4px 0;
  transition: color 0.2s ease;
}
.footer-col a:hover {
  color: rgba(255,255,255,0.9);
}
.footer-bottom {
  border-top: 1px solid rgba(255,255,255,0.08);
  padding-top: var(--space-lg);
  font-size: 13px;
  color: rgba(255,255,255,0.3);
}

/* Responsive */
@media (max-width: 768px) {
  .nav { display: none; }
  .footer-links { flex-direction: column; gap: var(--space-lg); }
}
</style>
