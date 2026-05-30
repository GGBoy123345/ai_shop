<template>
  <div class="cart-page">
    <van-nav-bar title="购物车" />
    <van-empty v-if="!loading && cartItems.length === 0" description="购物车是空的" image="search">
      <van-button round type="primary" to="/">去逛逛</van-button>
    </van-empty>
    <div v-else class="cart-list">
      <van-checkbox-group v-model="checkedIds">
        <van-swipe-cell v-for="item in cartItems" :key="item.id">
          <div class="cart-item">
            <van-checkbox :name="item.id" />
            <img v-if="item.productImage" :src="item.productImage" class="item-img" />
            <div v-else class="item-img placeholder">暂无</div>
            <div class="item-info">
              <div class="item-title">{{ item.productTitle || '商品' }}</div>
              <div class="item-price">¥{{ Number(item.price || 0).toFixed(2) }}</div>
              <van-stepper v-model="item.quantity" :min="1" :max="99" @change="onQuantityChange(item)" />
            </div>
          </div>
          <template #right>
            <van-button square type="danger" text="删除" class="delete-btn" @click="onDelete(item.id)" />
          </template>
        </van-swipe-cell>
      </van-checkbox-group>
    </div>
    <van-submit-bar v-if="cartItems.length > 0" :price="totalPrice" button-text="去结算" @submit="onSubmit">
      <van-checkbox v-model="allChecked" @click="toggleAll">全选</van-checkbox>
    </van-submit-bar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getCartList, updateCartQuantity, deleteCartItem } from '../../api/cart'
import { getStorage } from '../../utils/storage'

const router = useRouter()

const cartItems = ref([])
const checkedIds = ref([])
const loading = ref(false)

const allChecked = computed({
  get: () => checkedIds.value.length === cartItems.value.length && cartItems.value.length > 0,
  set: () => {}
})

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => checkedIds.value.includes(item.id))
    .reduce((sum, item) => sum + Number(item.price || 0) * item.quantity, 0) * 100
})

const toggleAll = () => {
  if (allChecked.value) {
    checkedIds.value = []
  } else {
    checkedIds.value = cartItems.value.map(item => item.id)
  }
}

const onQuantityChange = async (item) => {
  try {
    await updateCartQuantity(item.id, item.quantity)
  } catch (e) {
    showToast('更新失败')
  }
}

const onDelete = async (id) => {
  try {
    await deleteCartItem(id)
    cartItems.value = cartItems.value.filter(item => item.id !== id)
    checkedIds.value = checkedIds.value.filter(cid => cid !== id)
    showToast('已删除')
  } catch (e) {
    showToast('删除失败')
  }
}

const onSubmit = () => {
  if (checkedIds.value.length === 0) {
    showToast('请选择商品')
    return
  }
  router.push('/order/create')
}

onMounted(async () => {
  if (!getStorage('token')) {
    showToast('请先登录')
    router.replace('/login')
    return
  }
  loading.value = true
  try {
    const res = await getCartList()
    cartItems.value = res || []
    checkedIds.value = cartItems.value.map(item => item.id)
  } catch (e) {
    if (e?.response?.status === 401) {
      showToast('请先登录')
      router.replace('/login')
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.cart-page { min-height: 100vh; background: #f5f5f5; padding-bottom: 100px; }
.cart-list { padding: 10px; }
.cart-item { display: flex; align-items: center; background: #fff; padding: 10px; border-radius: 8px; margin-bottom: 10px; gap: 10px; }
.item-img { width: 80px; height: 80px; border-radius: 4px; object-fit: cover; }
.item-img.placeholder { display: flex; align-items: center; justify-content: center; background: #eee; color: #999; font-size: 12px; }
.item-info { flex: 1; }
.item-title { font-size: 14px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.item-price { color: #ee0a24; font-size: 14px; font-weight: bold; margin: 4px 0; }
.delete-btn { height: 100%; }
</style>
