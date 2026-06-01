<template>
  <div class="category-page">
    <van-search v-model="keyword" placeholder="搜索商品" shape="round" @search="onSearch" />
    <div class="category-content">
      <div class="left-nav">
        <div v-for="cat in categories" :key="cat.id" class="nav-item"
          :class="{ active: activeId === cat.id }" @click="selectCategory(cat)">
          {{ cat.name }}
        </div>
      </div>
      <div class="right-content">
        <div v-if="subCategories.length" class="category-grid">
          <div v-for="sub in subCategories" :key="sub.id" class="category-item"
            @click="loadProducts(sub.id)">
            <div class="icon">{{ sub.name.charAt(0) }}</div>
            <div class="name">{{ sub.name }}</div>
          </div>
        </div>
        <div v-if="products.length" class="goods-list">
          <div v-for="item in products" :key="item.id" class="goods-item"
            @click="$router.push(`/product/${item.id}`)">
            <img v-if="item.mainImage" :src="item.mainImage" class="goods-img" />
            <div v-else class="goods-img placeholder">暂无</div>
            <div class="goods-info">
              <div class="goods-title">{{ item.title }}</div>
              <div class="goods-price">¥{{ Number(item.price).toFixed(2) }}</div>
            </div>
          </div>
        </div>
        <van-empty v-if="!subCategories.length && !products.length" description="暂无数据" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { gsap } from 'gsap'
import { getCategoryTree, getProductList } from '../../api/product'

const route = useRoute()
const keyword = ref('')
const categories = ref([])
const subCategories = ref([])
const products = ref([])
const activeId = ref(null)

// GSAP动画：左侧导航入场
const playNavAnimations = () => {
  nextTick(() => {
    // 搜索栏滑入
    gsap.from('.van-search', {
      y: -40,
      opacity: 0,
      duration: 0.5,
      ease: 'power3.out'
    })

    // 左侧导航依次出现
    gsap.from('.nav-item', {
      x: -50,
      opacity: 0,
      duration: 0.4,
      stagger: 0.06,
      ease: 'power2.out',
      delay: 0.2
    })
  })
}

// GSAP动画：子分类/商品列表入场
const playContentAnimations = () => {
  nextTick(() => {
    // 分类网格依次弹入
    gsap.from('.category-item', {
      scale: 0.5,
      opacity: 0,
      duration: 0.4,
      stagger: 0.05,
      ease: 'back.out(1.5)'
    })

    // 商品列表滑入
    gsap.from('.goods-item', {
      y: 40,
      opacity: 0,
      duration: 0.5,
      stagger: 0.08,
      ease: 'power2.out'
    })
  })
}

const selectCategory = (cat) => {
  activeId.value = cat.id
  products.value = []
  subCategories.value = cat.children || []
  playContentAnimations()
}

const loadProducts = async (categoryId) => {
  try {
    const res = await getProductList({ categoryId, page: 1, size: 20 })
    products.value = res?.records || []
    subCategories.value = []
    playContentAnimations()
  } catch (e) {
    // ignore
  }
}

const onSearch = async () => {
  if (!keyword.value.trim()) return
  try {
    const res = await getProductList({ keyword: keyword.value, page: 1, size: 20 })
    products.value = res?.records || []
    subCategories.value = []
    playContentAnimations()
  } catch (e) {
    // ignore
  }
}

onMounted(async () => {
  try {
    const res = await getCategoryTree()
    categories.value = res || []
    playNavAnimations()
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
})
</script>

<style scoped>
.category-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}
.category-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}
.left-nav {
  width: 100px;
  background: #fff;
  overflow-y: auto;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
}
.nav-item {
  padding: 16px 10px;
  text-align: center;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
  color: #666;
}
.nav-item:hover {
  background: #f8f8f8;
  color: #333;
}
.nav-item.active {
  background: #fff;
  color: #1989fa;
  font-weight: bold;
  border-left-color: #1989fa;
}
.right-content {
  flex: 1;
  background: #fff;
  overflow-y: auto;
  padding: 12px;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}
.category-item {
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}
.category-item:hover { transform: translateY(-3px); }
.category-item .icon {
  width: 55px;
  height: 55px;
  background: linear-gradient(135deg, #f0f7ff, #e8f4ff);
  border-radius: 12px;
  margin: 0 auto 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #1989fa;
  box-shadow: 0 2px 8px rgba(25, 137, 250, 0.15);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.category-item:hover .icon {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(25, 137, 250, 0.25);
}
.category-item .name {
  font-size: 12px;
  color: #555;
}
.goods-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-top: 12px;
}
.goods-item {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.goods-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}
.goods-img {
  width: 100%;
  height: 130px;
  object-fit: cover;
  transition: transform 0.3s ease;
}
.goods-item:hover .goods-img { transform: scale(1.05); }
.goods-img.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f5, #e8e8e8);
  color: #999;
  font-size: 12px;
}
.goods-info { padding: 10px; }
.goods-title {
  font-size: 13px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  color: #333;
}
.goods-price {
  color: #ee0a24;
  font-size: 15px;
  font-weight: bold;
  margin-top: 6px;
}
</style>
