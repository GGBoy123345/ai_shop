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
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCategoryTree, getProductList } from '../../api/product'

const route = useRoute()
const keyword = ref('')
const categories = ref([])
const subCategories = ref([])
const products = ref([])
const activeId = ref(null)

const selectCategory = (cat) => {
  activeId.value = cat.id
  products.value = []
  subCategories.value = cat.children || []
}

const loadProducts = async (categoryId) => {
  try {
    const res = await getProductList({ categoryId, page: 1, size: 20 })
    products.value = res?.records || []
    subCategories.value = []
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
  } catch (e) {
    // ignore
  }
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
})
</script>

<style scoped>
.category-page { height: 100vh; display: flex; flex-direction: column; }
.category-content { flex: 1; display: flex; overflow: hidden; }
.left-nav { width: 90px; background: #f5f5f5; overflow-y: auto; }
.nav-item { padding: 15px 10px; text-align: center; font-size: 14px; cursor: pointer; }
.nav-item.active { background: #fff; color: #1989fa; font-weight: bold; }
.right-content { flex: 1; background: #fff; overflow-y: auto; padding: 10px; }
.category-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }
.category-item { text-align: center; cursor: pointer; }
.category-item .icon { width: 50px; height: 50px; background: #f5f5f5; border-radius: 8px; margin: 0 auto 5px; display: flex; align-items: center; justify-content: center; font-size: 18px; color: #1989fa; }
.category-item .name { font-size: 12px; }
.goods-list { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; margin-top: 10px; }
.goods-item { background: #fff; border-radius: 8px; overflow: hidden; cursor: pointer; }
.goods-img { width: 100%; height: 120px; object-fit: cover; }
.goods-img.placeholder { display: flex; align-items: center; justify-content: center; background: #eee; color: #999; font-size: 12px; }
.goods-info { padding: 6px; }
.goods-title { font-size: 13px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.goods-price { color: #ee0a24; font-size: 14px; font-weight: bold; margin-top: 2px; }
</style>
