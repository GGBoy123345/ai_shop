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
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
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

// GSAP动画：购物车商品入场
const playEnterAnimations = () => {
  nextTick(() => {
    // 导航栏滑入
    gsap.from('.van-nav-bar', {
      y: -50,
      opacity: 0,
      duration: 0.5,
      ease: 'power3.out'
    })

    // 购物车商品依次滑入
    gsap.from('.cart-item', {
      x: 80,
      opacity: 0,
      duration: 0.5,
      stagger: 0.1,
      ease: 'power2.out',
      delay: 0.2
    })

    // 底部结算栏弹入
    gsap.from('.van-submit-bar', {
      y: 60,
      opacity: 0,
      duration: 0.6,
      ease: 'back.out(1.5)',
      delay: 0.4
    })
  })
}

// 删除动画
const onDelete = async (id) => {
  try {
    // 找到要删除的元素并添加退出动画
    const items = document.querySelectorAll('.cart-item')
    const targetItem = cartItems.value.findIndex(item => item.id === id)
    if (targetItem >= 0 && items[targetItem]) {
      gsap.to(items[targetItem], {
        x: -300,
        opacity: 0,
        height: 0,
        padding: 0,
        margin: 0,
        duration: 0.4,
        ease: 'power2.in',
        onComplete: async () => {
          await deleteCartItem(id)
          cartItems.value = cartItems.value.filter(item => item.id !== id)
          checkedIds.value = checkedIds.value.filter(cid => cid !== id)
          showToast('已删除')
        }
      })
    } else {
      await deleteCartItem(id)
      cartItems.value = cartItems.value.filter(item => item.id !== id)
      checkedIds.value = checkedIds.value.filter(cid => cid !== id)
      showToast('已删除')
    }
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
    playEnterAnimations()
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
.cart-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 100px;
}
.cart-list { padding: 10px; }
.cart-item {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 12px;
  border-radius: 12px;
  margin-bottom: 10px;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.cart-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.item-img {
  width: 85px;
  height: 85px;
  border-radius: 8px;
  object-fit: cover;
  transition: transform 0.3s ease;
}
.item-img:hover { transform: scale(1.05); }
.item-img.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f5, #e8e8e8);
  color: #999;
  font-size: 12px;
}
.item-info { flex: 1; }
.item-title {
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  color: #333;
}
.item-price {
  color: #ee0a24;
  font-size: 16px;
  font-weight: bold;
  margin: 6px 0;
}
.delete-btn {
  height: 100%;
  border-radius: 0 12px 12px 0;
}
.van-submit-bar {
  border-radius: 12px 12px 0 0;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.08);
}
</style>
