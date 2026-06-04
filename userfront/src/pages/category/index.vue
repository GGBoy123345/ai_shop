<template>
  <div class="category-page">
    <div class="category-layout container">
      <!-- Sidebar -->
      <aside class="sidebar">
        <h2 class="sidebar-title">商品分类</h2>
        <nav class="sidebar-nav">
          <div v-for="cat in categories" :key="cat.id" class="sidebar-item" :class="{ active: activeId === cat.id }" @click="selectCategory(cat)">
            <span class="sidebar-item-text">{{ cat.name }}</span>
          </div>
        </nav>
      </aside>

      <!-- Main Content -->
      <div class="main-content">
        <!-- Search Bar -->
        <div class="search-row">
          <div class="search-box">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            <input v-model="keyword" type="text" placeholder="搜索商品..." class="search-input" @keyup.enter="onSearch" />
          </div>
        </div>

        <!-- Sub-categories -->
        <div v-if="subCategories.length" class="sub-categories">
          <div v-for="sub in subCategories" :key="sub.id" class="sub-cat-chip" @click="loadProducts(sub.id)">
            {{ sub.name }}
          </div>
        </div>

        <!-- Products Grid -->
        <div v-if="products.length" class="products-grid">
          <div v-for="item in products" :key="item.id" class="product-card" @click="$router.push(`/product/${item.id}`)">
            <div class="product-image">
              <img v-if="item.mainImage" :src="getThumbnailUrl(item.mainImage)" :alt="item.title" loading="lazy" />
              <div v-else class="product-image-placeholder">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>
              </div>
              <div class="product-tags">
                <span v-if="item.isHot" class="tag tag-hot">热销</span>
                <span v-if="item.isNew" class="tag tag-new">新品</span>
                <span v-if="item.isRecommend" class="tag tag-recommend">推荐</span>
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ item.title }}</h3>
              <span class="product-price">¥{{ Number(item.price).toFixed(2) }}</span>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="!loading && !products.length && !subCategories.length" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="1"><path d="M20.38 3.46L16 2 12 3.46 8 2 3.62 3.46a2 2 0 0 0-1.34 1.89v13.3a2 2 0 0 0 2.66 1.89L8 19l4-1.46L16 19l4.38-1.46a2 2 0 0 0 1.34-1.89V5.35a2 2 0 0 0-1.34-1.89z"/></svg>
          <p>选择分类浏览商品</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { gsap } from 'gsap'
import { getCategoryTree, getProductList } from '../../api/product'
import { getThumbnailUrl } from '../../utils/image'

const route = useRoute()
const keyword = ref('')
const categories = ref([])
const subCategories = ref([])
const products = ref([])
const activeId = ref(null)
const loading = ref(false)
let initialLoadDone = false

const selectCategory = (cat) => {
  activeId.value = cat.id
  products.value = []
  subCategories.value = cat.children || []
  // 不在 onMounted 的初始选择中播放动画，避免和 sidebar 入场动画冲突
  if (initialLoadDone) {
    animateContent()
  }
}

const loadProducts = async (categoryId) => {
  loading.value = true
  try {
    const res = await getProductList({ categoryId, page: 1, size: 24 })
    products.value = res?.records || []
    subCategories.value = []
    animateContent()
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

const onSearch = async () => {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    const res = await getProductList({ keyword: keyword.value, page: 1, size: 24 })
    products.value = res?.records || []
    subCategories.value = []
    animateContent()
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

const animateContent = () => {
  nextTick(() => {
    // 先杀死之前的动画，避免冲突
    gsap.killTweensOf('.sub-cat-chip, .product-card')
    const targets = document.querySelectorAll('.sub-cat-chip, .product-card')
    if (targets.length) {
      gsap.from(targets, {
        y: 30, opacity: 0, duration: 0.4, stagger: 0.04, ease: 'power2.out'
      })
    }
  })
}

onMounted(async () => {
  try {
    const res = await getCategoryTree()
    categories.value = res || []
    if (categories.value.length) {
      const queryId = route.query.id
      const cat = queryId
        ? categories.value.find(c => c.id == queryId) || categories.value[0]
        : categories.value[0]
      selectCategory(cat)
    }
  } catch (e) {
    // ignore
  }

  // 标记初始加载完成，之后的 selectCategory 调用才会播放动画
  initialLoadDone = true

  // sidebar 入场动画：先设置初始状态，再动画到正常状态
  nextTick(() => {
    const items = document.querySelectorAll('.sidebar-item')
    if (items.length) {
      gsap.from(items, {
        x: -20, opacity: 0, duration: 0.35, stagger: 0.03, ease: 'power2.out'
      })
    }
  })
})
</script>

<style scoped>
.category-page {
  padding: var(--space-xl) 0;
  min-height: calc(100vh - var(--header-height));
}

.category-layout {
  display: flex;
  gap: var(--space-xl);
}

/* Sidebar */
.sidebar {
  width: 220px;
  flex-shrink: 0;
  position: sticky;
  top: calc(var(--header-height) + var(--space-xl));
  align-self: flex-start;
  max-height: calc(100vh - var(--header-height) - var(--space-2xl));
  overflow-y: auto;
  padding: var(--space-md) 0;
}
.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-md);
  padding: 0 var(--space-lg);
}
.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.sidebar-item {
  padding: 12px var(--space-lg);
  font-size: 14px;
  color: var(--text-secondary);
  border-radius: 0;
  cursor: pointer;
  transition: color 0.2s ease, background-color 0.2s ease;
  position: relative;
}
.sidebar-item:hover {
  color: var(--text-primary);
  background: rgba(0,0,0,0.03);
}
.sidebar-item.active {
  color: var(--brand-start);
  background: rgba(102,126,234,0.06);
  font-weight: 500;
}
.sidebar-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--brand-gradient);
  border-radius: 0 2px 2px 0;
}

/* Main Content */
.main-content {
  flex: 1;
  min-width: 0;
}

/* Search */
.search-row {
  margin-bottom: var(--space-lg);
}
.search-box {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  background: var(--surface);
  border: 1px solid #e8e8e8;
  border-radius: var(--radius-full);
  padding: 10px var(--space-md);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}
.search-box:focus-within {
  border-color: var(--brand-start);
  box-shadow: 0 0 0 3px rgba(102,126,234,0.1);
}
.search-box svg {
  color: var(--text-muted);
  flex-shrink: 0;
}
.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  color: var(--text-primary);
  background: transparent;
}
.search-input::placeholder {
  color: var(--text-muted);
}

/* Sub-categories */
.sub-categories {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-sm);
  margin-bottom: var(--space-lg);
}
.sub-cat-chip {
  padding: 6px 16px;
  background: var(--surface);
  border: 1px solid #e8e8e8;
  border-radius: var(--radius-full);
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}
.sub-cat-chip:hover {
  border-color: var(--brand-start);
  color: var(--brand-start);
  background: rgba(102,126,234,0.04);
}

/* Products Grid */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: var(--space-md);
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
  height: 200px;
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
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: var(--space-sm);
  min-height: 40px;
}
.product-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--price-red);
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: var(--space-4xl) 0;
  color: var(--text-muted);
}
.empty-state p {
  margin-top: var(--space-md);
  font-size: 15px;
}

/* Responsive */
@media (max-width: 768px) {
  .category-layout {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
    position: static;
    max-height: none;
  }
  .sidebar-nav {
    flex-direction: row;
    flex-wrap: wrap;
    gap: var(--space-xs);
  }
  .sidebar-item {
    padding: 8px 14px;
    font-size: 13px;
  }
  .sidebar-item.active::before {
    display: none;
  }
}
</style>
