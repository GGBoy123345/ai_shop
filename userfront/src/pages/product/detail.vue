<template>
  <div class="product-detail">
    <div class="detail-layout container">
      <!-- Image Gallery -->
      <div class="gallery">
        <div class="gallery-main">
          <img v-if="currentImage" :src="currentImage" :alt="product.title" class="gallery-img" />
          <div v-else class="gallery-placeholder">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>
            <span>暂无图片</span>
          </div>
        </div>
        <div v-if="imageList.length > 1" class="gallery-thumbs">
          <div v-for="(img, idx) in imageList" :key="idx" class="thumb" :class="{ active: currentImage === img }" @click="currentImage = img">
            <img :src="img" :alt="`商品图 ${idx + 1}`" />
          </div>
        </div>
      </div>

      <!-- Purchase Panel -->
      <div class="purchase-panel">
        <div class="panel-sticky">
          <h1 class="product-title">{{ product.title || '加载中...' }}</h1>
          <p v-if="product.subtitle" class="product-subtitle">{{ product.subtitle }}</p>

          <div class="price-block">
            <span class="price">¥{{ Number(product.price || 0).toFixed(2) }}</span>
            <span v-if="product.originalPrice" class="original-price">¥{{ Number(product.originalPrice).toFixed(2) }}</span>
          </div>

          <div class="meta-row">
            <span class="meta-item">销量 {{ product.sales || 0 }}</span>
            <span class="meta-item">浏览 {{ product.views || 0 }}</span>
            <span v-if="product.weight" class="meta-item">重量 {{ product.weight }}kg</span>
          </div>

          <!-- SKU Selector -->
          <div v-if="product.skus && product.skus.length" class="sku-section">
            <h3 class="panel-label">选择规格</h3>
            <div class="sku-options">
              <button v-for="sku in product.skus" :key="sku.id" class="sku-btn" :class="{ active: selectedSku === sku.id }" @click="selectedSku = sku.id">
                <span class="sku-name">{{ sku.attributes || '默认' }}</span>
                <span class="sku-price">¥{{ Number(sku.price).toFixed(2) }}</span>
              </button>
            </div>
          </div>

          <!-- Attributes -->
          <div v-if="product.attributes && product.attributes.length" class="attrs-section">
            <h3 class="panel-label">商品参数</h3>
            <div class="attrs-grid">
              <div v-for="attr in product.attributes" :key="attr.templateId" class="attr-item">
                <span class="attr-name">{{ attr.templateName }}</span>
                <span class="attr-value">{{ attr.value }}</span>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="action-row">
            <button class="btn-cart" @click="onAddCart">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
              加入购物车
            </button>
            <button class="btn-buy" @click="onBuyNow">立即购买</button>
          </div>

          <!-- AI Compare CTA -->
          <div class="ai-cta" @click="$router.push('/ai-compare')">
            <div class="ai-cta-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="8" height="18" rx="2"/><rect x="14" y="3" width="8" height="18" rx="2"/></svg>
            </div>
            <div class="ai-cta-text">
              <span class="ai-cta-title">AI 智能对比</span>
              <span class="ai-cta-desc">与其他商品对比参数，获取AI推荐</span>
            </div>
            <span class="ai-cta-arrow">→</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Description Section -->
    <div v-if="product.description" class="description-section container">
      <h2 class="section-title section-accent">商品详情</h2>
      <div class="description-content">{{ product.description }}</div>
    </div>

    <!-- Toast -->
    <transition name="fade">
      <div v-if="toast" class="toast" :class="toast.type">{{ toast.message }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { getProductDetail } from '../../api/product'
import { addToCart } from '../../api/cart'

const route = useRoute()
const router = useRouter()
const product = ref({})
const selectedSku = ref(null)
const currentImage = ref('')
const toast = ref(null)

const imageList = computed(() => {
  if (product.value.images) {
    try { return JSON.parse(product.value.images) } catch { return [] }
  }
  return product.value.mainImage ? [product.value.mainImage] : []
})

function showToast(message, type = 'success') {
  toast.value = { message, type }
  setTimeout(() => { toast.value = null }, 2500)
}

const onAddCart = async () => {
  try {
    await addToCart({
      productId: product.value.id,
      skuId: selectedSku.value,
      quantity: 1
    })
    showToast('已加入购物车')
  } catch (e) {
    showToast(e.message || '加入失败', 'error')
  }
}

const onBuyNow = async () => {
  await onAddCart()
  router.push('/cart')
}

const playEnterAnimations = () => {
  nextTick(() => {
    const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })

    tl.from('.gallery', { x: -40, opacity: 0, duration: 0.7 })
    tl.from('.purchase-panel', { x: 40, opacity: 0, duration: 0.7 }, '-=0.5')
    tl.from('.product-title', { y: 20, opacity: 0, duration: 0.5 }, '-=0.3')
    tl.from('.price-block', { scale: 0.9, opacity: 0, duration: 0.5, ease: 'back.out(1.5)' }, '-=0.2')
    tl.from('.sku-section, .attrs-section', { y: 20, opacity: 0, duration: 0.4, stagger: 0.1 }, '-=0.2')
    tl.from('.action-row', { y: 20, opacity: 0, duration: 0.4 }, '-=0.1')
    tl.from('.ai-cta', { y: 15, opacity: 0, duration: 0.4 }, '-=0.1')
    tl.from('.description-section', { y: 40, opacity: 0, duration: 0.6 }, '-=0.2')
  })
}

onMounted(async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res || {}
    if (imageList.value.length) {
      currentImage.value = imageList.value[0]
    }
    if (product.value.skus && product.value.skus.length) {
      selectedSku.value = product.value.skus[0].id
    }
    playEnterAnimations()
  } catch (e) {
    showToast('加载失败', 'error')
  }
})
</script>

<style scoped>
.product-detail {
  padding: var(--space-xl) 0 var(--space-4xl);
}

.detail-layout {
  display: flex;
  gap: var(--space-2xl);
  align-items: flex-start;
}

/* Gallery */
.gallery {
  flex: 1;
  min-width: 0;
  position: sticky;
  top: calc(var(--header-height) + var(--space-xl));
}
.gallery-main {
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: #f8f8f8;
  box-shadow: var(--shadow-card);
}
.gallery-img {
  width: 100%;
  height: 480px;
  object-fit: cover;
  display: block;
}
.gallery-placeholder {
  width: 100%;
  height: 480px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  color: var(--text-muted);
  font-size: 14px;
  background: linear-gradient(135deg, #f5f5f5, #eee);
}
.gallery-thumbs {
  display: flex;
  gap: var(--space-sm);
  margin-top: var(--space-md);
}
.thumb {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.2s ease;
}
.thumb.active {
  border-color: var(--brand-start);
}
.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Purchase Panel */
.purchase-panel {
  width: 420px;
  flex-shrink: 0;
}
.panel-sticky {
  position: sticky;
  top: calc(var(--header-height) + var(--space-xl));
}
.product-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  margin-bottom: var(--space-sm);
}
.product-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: var(--space-lg);
}

/* Price */
.price-block {
  display: flex;
  align-items: baseline;
  gap: var(--space-md);
  margin-bottom: var(--space-md);
  padding: var(--space-md);
  background: linear-gradient(135deg, rgba(238,10,36,0.04), rgba(238,10,36,0.01));
  border-radius: var(--radius-md);
}
.price {
  font-size: 28px;
  font-weight: 700;
  color: var(--price-red);
}
.original-price {
  font-size: 14px;
  color: var(--text-muted);
  text-decoration: line-through;
}

/* Meta */
.meta-row {
  display: flex;
  gap: var(--space-lg);
  margin-bottom: var(--space-xl);
}
.meta-item {
  font-size: 13px;
  color: var(--text-muted);
}

/* Panel Label */
.panel-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-md);
}

/* SKU */
.sku-section {
  margin-bottom: var(--space-xl);
}
.sku-options {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-sm);
}
.sku-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 10px 16px;
  background: var(--surface);
  border: 1px solid #e8e8e8;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
}
.sku-btn:hover {
  border-color: var(--brand-start);
}
.sku-btn.active {
  border-color: var(--brand-start);
  background: rgba(102,126,234,0.04);
  box-shadow: 0 0 0 2px rgba(102,126,234,0.15);
}
.sku-name {
  font-size: 13px;
  color: var(--text-primary);
}
.sku-price {
  font-size: 12px;
  color: var(--price-red);
  font-weight: 500;
}

/* Attributes */
.attrs-section {
  margin-bottom: var(--space-xl);
}
.attrs-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-sm);
}
.attr-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  background: #f8f8f8;
  border-radius: var(--radius-md);
  font-size: 13px;
}
.attr-name {
  color: var(--text-muted);
}
.attr-value {
  color: var(--text-primary);
  font-weight: 500;
}

/* Actions */
.action-row {
  display: flex;
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
}
.btn-cart {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  padding: 14px;
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-start);
  background: transparent;
  border: 2px solid var(--brand-start);
  border-radius: var(--radius-full);
  transition: all 0.3s ease;
}
.btn-cart:hover {
  background: rgba(102,126,234,0.06);
}
.btn-buy {
  flex: 1;
  padding: 14px;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  background: var(--brand-gradient);
  border-radius: var(--radius-full);
  box-shadow: 0 4px 15px rgba(102,126,234,0.35);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.btn-buy:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102,126,234,0.45);
}
.btn-buy:active {
  transform: scale(0.98);
}

/* AI CTA */
.ai-cta {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md);
  background: rgba(102,126,234,0.04);
  border: 1px solid rgba(102,126,234,0.15);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s ease;
}
.ai-cta:hover {
  background: rgba(102,126,234,0.08);
  border-color: rgba(102,126,234,0.3);
}
.ai-cta-icon {
  width: 40px;
  height: 40px;
  background: var(--brand-gradient);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.ai-cta-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.ai-cta-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}
.ai-cta-desc {
  font-size: 12px;
  color: var(--text-muted);
}
.ai-cta-arrow {
  font-size: 16px;
  color: var(--brand-start);
  transition: transform 0.2s ease;
}
.ai-cta:hover .ai-cta-arrow {
  transform: translateX(4px);
}

/* Description */
.description-section {
  margin-top: var(--space-3xl);
  padding: var(--space-xl);
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}
.description-content {
  margin-top: var(--space-lg);
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.8;
  max-width: 75ch;
}

/* Toast */
.toast {
  position: fixed;
  top: calc(var(--header-height) + var(--space-md));
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: var(--radius-full);
  font-size: 14px;
  font-weight: 500;
  z-index: 1000;
  box-shadow: var(--shadow-dropdown);
}
.toast.success {
  background: var(--success);
  color: #fff;
}
.toast.error {
  background: var(--danger);
  color: #fff;
}

/* Responsive */
@media (max-width: 900px) {
  .detail-layout {
    flex-direction: column;
  }
  .gallery {
    position: static;
  }
  .gallery-img, .gallery-placeholder {
    height: 320px;
  }
  .purchase-panel {
    width: 100%;
  }
  .panel-sticky {
    position: static;
  }
}
</style>
