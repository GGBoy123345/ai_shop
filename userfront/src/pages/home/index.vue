<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-inner container">
        <div class="hero-content">
          <h1 class="hero-title">
            <span class="hero-line">AI驱动的</span>
            <span class="hero-line hero-accent">智能购物体验</span>
          </h1>
          <p class="hero-subtitle">智能对比分析，线上试衣预览，让每一次购买决策都有据可依</p>
          <div class="hero-actions">
            <button class="btn-primary" @click="$router.push('/category')">开始选购</button>
            <button class="btn-ghost" @click="$router.push('/ai-compare')">了解AI功能</button>
          </div>
        </div>
        <div class="hero-visual">
          <div class="hero-glow"></div>
          <div class="hero-card-stack">
            <div class="hero-card hc-1">
              <div class="hc-img"></div>
              <div class="hc-text"></div>
            </div>
            <div class="hero-card hc-2">
              <div class="hc-img"></div>
              <div class="hc-text"></div>
            </div>
            <div class="hero-card hc-3">
              <div class="hc-img"></div>
              <div class="hc-text"></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- AI Features -->
    <section class="ai-section container">
      <div class="section-header">
        <h2 class="section-title section-accent">AI 智能功能</h2>
        <p class="section-desc">两大AI核心能力，重新定义在线购物</p>
      </div>
      <div class="ai-grid">
        <div class="ai-card" @click="$router.push('/ai-compare')">
          <div class="ai-card-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="2" y="3" width="8" height="18" rx="2"/><rect x="14" y="3" width="8" height="18" rx="2"/>
              <path d="M6 8h0M6 12h0M6 16h0M18 8h0M18 12h0M18 16h0"/>
            </svg>
          </div>
          <h3 class="ai-card-title">AI 智能对比</h3>
          <p class="ai-card-desc">选择多款商品，AI自动分析参数差异，给出专业推荐和理由，告别选择困难</p>
          <span class="ai-card-link">开始对比 →</span>
        </div>
        <div class="ai-card" @click="$router.push('/ai-tryon')">
          <div class="ai-card-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20.38 3.46L16 2 12 3.46 8 2 3.62 3.46a2 2 0 0 0-1.34 1.89v13.3a2 2 0 0 0 2.66 1.89L8 19l4-1.46L16 19l4.38-1.46a2 2 0 0 0 1.34-1.89V5.35a2 2 0 0 0-1.34-1.89z"/>
              <line x1="12" y1="2" x2="12" y2="17.54"/>
            </svg>
          </div>
          <h3 class="ai-card-title">线上试衣间</h3>
          <p class="ai-card-desc">输入身材参数，AI生成穿着效果预览，买前先看上身效果，减少退换烦恼</p>
          <span class="ai-card-link">立即体验 →</span>
        </div>
      </div>
    </section>

    <!-- Banner Carousel -->
    <section v-if="banners.length" class="banner-section container">
      <div class="banner-carousel">
        <div class="banner-track" :style="{ transform: `translateX(-${currentBanner * 100}%)` }">
          <div v-for="item in banners" :key="item.id" class="banner-slide">
            <img :src="item.imageUrl" :alt="item.title || 'Banner'" class="banner-img" />
          </div>
        </div>
        <div v-if="banners.length > 1" class="banner-dots">
          <button v-for="(_, i) in banners" :key="i" class="banner-dot" :class="{ active: currentBanner === i }" @click="currentBanner = i"></button>
        </div>
      </div>
    </section>

    <!-- Categories -->
    <section class="categories-section container">
      <div class="section-header">
        <h2 class="section-title section-accent">商品分类</h2>
      </div>
      <div class="category-grid">
        <div v-for="cat in categories" :key="cat.id" class="category-card" @click="$router.push({ path: '/category', query: { id: cat.id } })">
          <div class="category-icon">{{ cat.name.charAt(0) }}</div>
          <span class="category-name">{{ cat.name }}</span>
        </div>
      </div>
    </section>

    <!-- Recommended Products -->
    <section class="products-section container">
      <div class="section-header">
        <h2 class="section-title section-accent">推荐商品</h2>
      </div>
      <div class="products-grid">
        <div v-for="item in products" :key="item.id" class="product-card" @click="$router.push(`/product/${item.id}`)">
          <div class="product-image">
            <img v-if="item.mainImage" :src="getThumbnailUrl(item.mainImage)" :alt="item.title" loading="lazy" />
            <div v-else class="product-image-placeholder">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>
            </div>
            <div class="product-tags">
              <span v-if="item.isHot" class="tag tag-hot">热销</span>
              <span v-if="item.isNew" class="tag tag-new">新品</span>
              <span v-if="item.isRecommend" class="tag tag-recommend">推荐</span>
            </div>
          </div>
          <div class="product-info">
            <h3 class="product-title">{{ item.title }}</h3>
            <div class="product-price-row">
              <span class="product-price">¥{{ Number(item.price).toFixed(2) }}</span>
              <span v-if="item.originalPrice" class="product-original">¥{{ Number(item.originalPrice).toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Load More -->
      <div v-if="!finished" class="load-more">
        <button class="btn-load-more" :disabled="loading" @click="loadProducts">
          {{ loading ? '加载中...' : '加载更多' }}
        </button>
      </div>
      <div v-else-if="products.length" class="load-more">
        <span class="no-more">已展示全部商品</span>
      </div>

      <!-- Empty State -->
      <div v-if="!loading && !products.length" class="empty-state">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="1"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
        <p>暂无商品</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { getCategoryTree, getProductList, getBanners } from '../../api/product'
import { getThumbnailUrl } from '../../utils/image'

const router = useRouter()
const banners = ref([])
const categories = ref([])
const products = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const currentBanner = ref(0)
let bannerTimer = null

const loadProducts = async () => {
  if (loading.value || finished.value) return
  loading.value = true
  try {
    const res = await getProductList({ page: page.value, size: 12 })
    const records = res?.records || []
    if (page.value === 1) {
      products.value = records
    } else {
      products.value.push(...records)
    }
    if (records.length < 12) finished.value = true
    page.value++
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
  }
}

// GSAP Animations
const playEnterAnimations = () => {
  nextTick(() => {
    const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })

    // Hero entrance
    tl.from('.hero-title .hero-line', {
      y: 60, opacity: 0, duration: 0.8, stagger: 0.15
    })
    tl.from('.hero-subtitle', { y: 30, opacity: 0, duration: 0.6 }, '-=0.3')
    tl.from('.hero-actions', { y: 30, opacity: 0, duration: 0.5 }, '-=0.2')

    // Hero visual cards
    tl.from('.hero-card', {
      x: 80, opacity: 0, rotation: 8, duration: 0.7, stagger: 0.12, ease: 'back.out(1.4)'
    }, '-=0.4')

    // AI section
    tl.from('.ai-card', {
      y: 50, opacity: 0, duration: 0.6, stagger: 0.15
    }, '-=0.3')

    // Categories
    tl.from('.category-card', {
      scale: 0.8, opacity: 0, duration: 0.4, stagger: 0.05, ease: 'back.out(1.5)'
    }, '-=0.2')

    // Products
    tl.from('.product-card', {
      y: 40, opacity: 0, duration: 0.5, stagger: 0.06
    }, '-=0.2')
  })
}

const animateNewProducts = (count) => {
  nextTick(() => {
    const items = document.querySelectorAll('.product-card')
    const newItems = Array.from(items).slice(-count)
    if (newItems.length) {
      gsap.from(newItems, { y: 40, opacity: 0, duration: 0.5, stagger: 0.06, ease: 'power2.out' })
    }
  })
}

// Banner auto-play
function startBannerTimer() {
  if (banners.value.length <= 1) return
  bannerTimer = setInterval(() => {
    currentBanner.value = (currentBanner.value + 1) % banners.value.length
  }, 4000)
}

onMounted(async () => {
  try {
    const [bannerRes, catRes] = await Promise.all([getBanners(), getCategoryTree()])
    banners.value = bannerRes || []
    categories.value = (catRes || []).slice(0, 8)
    startBannerTimer()
  } catch (e) {
    console.error('首页数据加载失败:', e.message)
  }
  await loadProducts()
  playEnterAnimations()
})

onUnmounted(() => {
  if (bannerTimer) clearInterval(bannerTimer)
})
</script>

<style scoped>
.home-page {
  background: var(--page-bg);
}

/* ============================================
   Hero Section
   ============================================ */
.hero {
  background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
  padding: var(--space-4xl) 0;
  overflow: hidden;
  position: relative;
}
.hero::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(102,126,234,0.15) 0%, transparent 70%);
  pointer-events: none;
}
.hero-inner {
  display: flex;
  align-items: center;
  gap: var(--space-3xl);
  position: relative;
  z-index: 1;
}
.hero-content {
  flex: 1;
  min-width: 0;
}
.hero-title {
  font-size: clamp(2rem, 4vw, 3.2rem);
  font-weight: 700;
  line-height: 1.2;
  color: #fff;
  margin-bottom: var(--space-lg);
}
.hero-line {
  display: block;
}
.hero-accent {
  background: linear-gradient(135deg, #667eea, #a78bfa, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.hero-subtitle {
  font-size: 18px;
  color: rgba(255,255,255,0.6);
  line-height: 1.6;
  max-width: 480px;
  margin-bottom: var(--space-xl);
}
.hero-actions {
  display: flex;
  gap: var(--space-md);
}

/* Buttons */
.btn-primary {
  padding: 14px 32px;
  background: linear-gradient(135deg, var(--brand-start), var(--brand-end));
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-full);
  box-shadow: 0 4px 15px rgba(102,126,234,0.35);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.btn-primary:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 25px rgba(102,126,234,0.45);
}
.btn-primary:active {
  transform: scale(0.98);
}
.btn-ghost {
  padding: 14px 32px;
  background: transparent;
  color: rgba(255,255,255,0.8);
  font-size: 16px;
  font-weight: 500;
  border-radius: var(--radius-full);
  border: 1px solid rgba(255,255,255,0.2);
  transition: all 0.3s ease;
}
.btn-ghost:hover {
  background: rgba(255,255,255,0.08);
  border-color: rgba(255,255,255,0.4);
}

/* Hero Visual — Decorative Card Stack */
.hero-visual {
  flex-shrink: 0;
  width: 380px;
  height: 340px;
  position: relative;
}
.hero-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(102,126,234,0.25) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}
.hero-card-stack {
  position: relative;
  width: 100%;
  height: 100%;
}
.hero-card {
  position: absolute;
  width: 200px;
  background: rgba(255,255,255,0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: var(--radius-lg);
  padding: 12px;
  transition: transform 0.4s ease;
}
.hero-card:hover {
  transform: translateY(-8px) scale(1.03) !important;
}
.hc-1 {
  top: 20px;
  left: 10px;
  transform: rotate(-5deg);
  z-index: 1;
}
.hc-2 {
  top: 60px;
  left: 100px;
  transform: rotate(2deg);
  z-index: 2;
}
.hc-3 {
  top: 140px;
  left: 40px;
  transform: rotate(-2deg);
  z-index: 3;
}
.hc-img {
  width: 100%;
  height: 120px;
  background: linear-gradient(135deg, rgba(102,126,234,0.3), rgba(118,75,162,0.3));
  border-radius: var(--radius-md);
  margin-bottom: 10px;
}
.hc-text {
  height: 12px;
  width: 70%;
  background: rgba(255,255,255,0.15);
  border-radius: var(--radius-sm);
}

/* ============================================
   Section Headers
   ============================================ */
.section-header {
  margin-bottom: var(--space-xl);
}
.section-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}
.section-desc {
  font-size: 15px;
  color: var(--text-secondary);
  margin-top: var(--space-sm);
}

/* ============================================
   AI Features
   ============================================ */
.ai-section {
  padding: var(--space-4xl) var(--space-lg) 0;
}
.ai-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-lg);
}
.ai-card {
  background: var(--surface);
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: relative;
  overflow: hidden;
}
.ai-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--brand-gradient);
  opacity: 0;
  transition: opacity 0.3s ease;
}
.ai-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}
.ai-card:hover::before {
  opacity: 1;
}
.ai-card-icon {
  width: 64px;
  height: 64px;
  background: rgba(102,126,234,0.08);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--brand-start);
  margin-bottom: var(--space-md);
}
.ai-card-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
}
.ai-card-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: var(--space-md);
}
.ai-card-link {
  font-size: 14px;
  font-weight: 500;
  color: var(--brand-start);
  transition: letter-spacing 0.2s ease;
}
.ai-card:hover .ai-card-link {
  letter-spacing: 1px;
}

/* ============================================
   Banner
   ============================================ */
.banner-section {
  padding: var(--space-2xl) var(--space-lg) 0;
}
.banner-carousel {
  position: relative;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}
.banner-track {
  display: flex;
  transition: transform 0.5s ease;
}
.banner-slide {
  min-width: 100%;
}
.banner-img {
  width: 100%;
  height: 280px;
  object-fit: cover;
}
.banner-dots {
  position: absolute;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}
.banner-dot {
  width: 8px;
  height: 8px;
  border-radius: var(--radius-full);
  background: rgba(255,255,255,0.4);
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
}
.banner-dot.active {
  background: #fff;
  width: 24px;
}

/* ============================================
   Categories
   ============================================ */
.categories-section {
  padding: var(--space-3xl) var(--space-lg) 0;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: var(--space-md);
}
.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-lg) var(--space-md);
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.category-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}
.category-icon {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, rgba(102,126,234,0.1), rgba(118,75,162,0.1));
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  color: var(--brand-start);
  transition: transform 0.3s ease;
}
.category-card:hover .category-icon {
  transform: scale(1.1);
}
.category-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

/* ============================================
   Products Grid
   ============================================ */
.products-section {
  padding: var(--space-3xl) var(--space-lg) 0;
}
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: var(--space-lg);
}
.product-card {
  background: var(--surface);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}
.product-image {
  position: relative;
  height: 220px;
  overflow: hidden;
  background: #f8f8f8;
}
.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}
.product-card:hover .product-image img {
  transform: scale(1.05);
}
.product-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f5, #eee);
}
.product-tags {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 4px;
}
.product-info {
  padding: var(--space-md);
}
.product-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: var(--space-sm);
  min-height: 42px;
}
.product-price-row {
  display: flex;
  align-items: baseline;
  gap: var(--space-sm);
}
.product-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--price-red);
}
.product-original {
  font-size: 13px;
  color: var(--text-muted);
  text-decoration: line-through;
}

/* ============================================
   Load More & Empty
   ============================================ */
.load-more {
  text-align: center;
  padding: var(--space-2xl) 0;
}
.btn-load-more {
  padding: 12px 40px;
  font-size: 14px;
  font-weight: 500;
  color: var(--brand-start);
  background: transparent;
  border: 1px solid var(--brand-start);
  border-radius: var(--radius-full);
  transition: all 0.3s ease;
}
.btn-load-more:hover:not(:disabled) {
  background: var(--brand-start);
  color: #fff;
}
.btn-load-more:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.no-more {
  font-size: 14px;
  color: var(--text-muted);
}

.empty-state {
  text-align: center;
  padding: var(--space-4xl) 0;
  color: var(--text-muted);
}
.empty-state p {
  margin-top: var(--space-md);
  font-size: 15px;
}

/* ============================================
   Responsive
   ============================================ */
@media (max-width: 768px) {
  .hero-inner {
    flex-direction: column;
    text-align: center;
  }
  .hero-subtitle { max-width: none; }
  .hero-actions { justify-content: center; }
  .hero-visual { display: none; }
  .ai-grid { grid-template-columns: 1fr; }
  .products-grid { grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); }
}
</style>
