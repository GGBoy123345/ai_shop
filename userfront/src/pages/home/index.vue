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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
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
    } else {
      products.value.push(...records)
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

onMounted(async () => {
  try {
    const [bannerRes, catRes] = await Promise.all([getBanners(), getCategoryTree()])
    banners.value = bannerRes || []
    categories.value = (catRes || []).slice(0, 8)
  } catch (e) {
    console.error('首页数据加载失败:', e.message)
  }
})
</script>

<style scoped>
.home-page { background: #f5f5f5; padding-bottom: 60px; }
.search-bar { position: sticky; top: 0; z-index: 10; }
.banner { height: 180px; margin: 10px; border-radius: 8px; overflow: hidden; }
.banner-img { width: 100%; height: 180px; object-fit: cover; }
.category-nav { margin: 10px; border-radius: 8px; overflow: hidden; }
.section { margin: 10px; }
.section-title { font-size: 16px; font-weight: bold; margin-bottom: 10px; }
.goods-list { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
.goods-item { background: #fff; border-radius: 8px; overflow: hidden; }
.goods-image { width: 100%; height: 150px; object-fit: cover; }
.goods-image.placeholder { display: flex; align-items: center; justify-content: center; background: #eee; color: #999; font-size: 12px; }
.goods-info { padding: 8px; }
.goods-title { font-size: 14px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.goods-price { color: #ee0a24; font-size: 16px; font-weight: bold; margin-top: 4px; }
</style>
