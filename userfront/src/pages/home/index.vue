<template>
  <div class="home-page">
    <div class="search-bar">
      <van-search v-model="keyword" placeholder="搜索商品" shape="round" @search="onSearch" />
    </div>
    <van-swipe :autoplay="3000" indicator-color="white" class="banner">
      <van-swipe-item v-for="item in banners" :key="item.id">
        <img :src="item.imageUrl" class="banner-img" />
      </van-swipe-item>
    </van-swipe>
    <van-grid :column-num="4" :border="false" class="category-nav">
      <van-grid-item v-for="cat in categories" :key="cat.id" :text="cat.name"
        @click="$router.push({ path: '/category', query: { id: cat.id } })" />
    </van-grid>
    <div class="section">
      <div class="section-title">推荐商品</div>
      <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadProducts">
        <div class="goods-list">
          <div v-for="item in products" :key="item.id" class="goods-item"
            @click="$router.push(`/product/${item.id}`)">
            <img v-if="item.mainImage" :src="item.mainImage" class="goods-image" />
            <div v-else class="goods-image placeholder">暂无图片</div>
            <div class="goods-info">
              <div class="goods-title">{{ item.title }}</div>
              <div class="goods-price">¥{{ Number(item.price).toFixed(2) }}</div>
            </div>
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { getCategoryTree, getProductList, getBanners } from '../../api/product'

const router = useRouter()
const keyword = ref('')
const banners = ref([])
const categories = ref([])
const products = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)

const onSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/category', query: { keyword: keyword.value } })
  }
}

const loadProducts = async () => {
  try {
    const res = await getProductList({ page: page.value, size: 10 })
    const records = res?.records || []
    if (page.value === 1) {
      products.value = records
      animateProducts()
    } else {
      products.value.push(...records)
      // 新加载的商品也添加动画
      nextTick(() => {
        const items = document.querySelectorAll('.goods-item')
        const newItems = Array.from(items).slice(-records.length)
        gsap.from(newItems, {
          y: 40,
          opacity: 0,
          duration: 0.5,
          stagger: 0.08,
          ease: 'power2.out'
        })
      })
    }
    if (records.length < 10) {
      finished.value = true
    }
    page.value++
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
  }
}

// GSAP动画：入场动画
const playEnterAnimations = () => {
  nextTick(() => {
    // 搜索栏从上方滑入
    gsap.from('.search-bar', {
      y: -60,
      opacity: 0,
      duration: 0.6,
      ease: 'power3.out'
    })

    // Banner淡入并轻微缩放
    gsap.from('.banner', {
      scale: 0.9,
      opacity: 0,
      duration: 0.8,
      delay: 0.2,
      ease: 'back.out(1.2)'
    })

    // 分类导航依次出现
    gsap.from('.van-grid-item', {
      y: 30,
      opacity: 0,
      duration: 0.5,
      stagger: 0.08,
      delay: 0.4,
      ease: 'power2.out'
    })

    // 推荐商品标题滑入
    gsap.from('.section-title', {
      x: -50,
      opacity: 0,
      duration: 0.6,
      delay: 0.6,
      ease: 'power2.out'
    })
  })
}

// 商品卡片入场动画
const animateProducts = () => {
  nextTick(() => {
    gsap.from('.goods-item', {
      y: 50,
      opacity: 0,
      duration: 0.6,
      stagger: 0.1,
      ease: 'power2.out'
    })
  })
}

onMounted(async () => {
  try {
    const [bannerRes, catRes] = await Promise.all([getBanners(), getCategoryTree()])
    banners.value = bannerRes || []
    categories.value = (catRes || []).slice(0, 8)
    playEnterAnimations()
  } catch (e) {
    console.error('首页数据加载失败:', e.message)
  }
})
</script>

<style scoped>
.home-page { background: #f5f5f5; padding-bottom: 60px; }
.search-bar { position: sticky; top: 0; z-index: 10; }
.banner { height: 180px; margin: 10px; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); }
.banner-img { width: 100%; height: 180px; object-fit: cover; transition: transform 0.3s ease; }
.banner-img:hover { transform: scale(1.02); }
.category-nav { margin: 10px; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); }
.section { margin: 10px; }
.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}
.section-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 2px;
}
.goods-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}
.goods-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  display: flex;
  flex-direction: column;
}
.goods-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}
.goods-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  transition: transform 0.4s ease;
}
.goods-item:hover .goods-image { transform: scale(1.05); }
.goods-image.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f5, #e8e8e8);
  color: #999;
  font-size: 12px;
}
.goods-info {
  padding: 10px;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.goods-title {
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
  color: #333;
  min-height: 42px;
  max-height: 42px;
}
.goods-price {
  color: #ee0a24;
  font-size: 18px;
  font-weight: bold;
  margin-top: auto;
  padding-top: 8px;
}
</style>
