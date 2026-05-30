<template>
  <div class="favorite-page">
    <van-nav-bar title="我的收藏" left-arrow @click-left="$router.back()" />
    <van-empty v-if="!loading && favorites.length === 0" description="暂无收藏" />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadFavorites">
      <div class="goods-list">
        <div v-for="item in favorites" :key="item.id" class="goods-item"
          @click="$router.push(`/product/${item.productId}`)">
          <img v-if="item.productImage" :src="item.productImage" class="goods-img" />
          <div v-else class="goods-img placeholder">暂无</div>
          <div class="goods-info">
            <div class="goods-title">{{ item.productTitle || '商品' }}</div>
            <div class="goods-price">¥{{ Number(item.price || 0).toFixed(2) }}</div>
          </div>
          <van-button size="small" plain type="danger" @click.stop="onRemove(item.productId)">取消</van-button>
        </div>
      </div>
    </van-list>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import { getFavoriteList, removeFavorite } from '../../api/favorite'

const favorites = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)

const loadFavorites = async () => {
  try {
    const res = await getFavoriteList({ page: page.value, size: 10 })
    const records = res?.records || res || []
    if (page.value === 1) {
      favorites.value = records
    } else {
      favorites.value.push(...records)
    }
    if (records.length < 10) finished.value = true
    page.value++
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
  }
}

const onRemove = async (productId) => {
  try {
    await removeFavorite(productId)
    favorites.value = favorites.value.filter(f => f.productId !== productId)
    showToast('已取消收藏')
  } catch (e) {
    showToast('操作失败')
  }
}

onMounted(() => { loadFavorites() })
</script>

<style scoped>
.favorite-page { min-height: 100vh; background: #f5f5f5; }
.goods-list { padding: 10px; }
.goods-item { display: flex; align-items: center; background: #fff; padding: 10px; border-radius: 8px; margin-bottom: 10px; gap: 10px; }
.goods-img { width: 80px; height: 80px; border-radius: 4px; object-fit: cover; }
.goods-img.placeholder { display: flex; align-items: center; justify-content: center; background: #eee; color: #999; font-size: 12px; }
.goods-info { flex: 1; }
.goods-title { font-size: 14px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.goods-price { color: #ee0a24; font-size: 14px; font-weight: bold; margin-top: 4px; }
</style>
