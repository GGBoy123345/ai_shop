<template>
  <div class="create-order-page">
    <van-nav-bar title="确认订单" left-arrow @click-left="$router.back()" />

    <van-cell-group inset style="margin-top: 10px;" title="收货地址">
      <van-cell v-if="address" :title="address.receiverName + ' ' + address.receiverPhone"
        :label="address.province + address.city + address.district + address.detailAddress"
        is-link to="/address" />
      <van-cell v-else title="请选择收货地址" is-link to="/address" />
    </van-cell-group>

    <van-cell-group inset style="margin-top: 10px;" title="商品清单">
      <div v-for="item in cartItems" :key="item.id" class="order-item">
        <img v-if="item.productImage" :src="item.productImage" class="item-img" />
        <div v-else class="item-img placeholder">暂无</div>
        <div class="item-info">
          <div class="item-title">{{ item.productTitle || '商品' }}</div>
          <div class="item-price">¥{{ Number(item.price || 0).toFixed(2) }} x{{ item.quantity }}</div>
        </div>
      </div>
    </van-cell-group>

    <van-cell-group inset style="margin-top: 10px;">
      <van-field v-model="remark" label="订单备注" placeholder="选填" />
    </van-cell-group>

    <van-submit-bar :price="totalPrice" button-text="提交订单" @submit="onSubmit" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getAddressList } from '../../api/address'
import { getCartList } from '../../api/cart'
import { createOrder } from '../../api/order'

const router = useRouter()
const address = ref(null)
const cartItems = ref([])
const remark = ref('')

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + Number(item.price || 0) * item.quantity, 0) * 100
})

const onSubmit = async () => {
  if (!address.value) {
    showToast('请选择收货地址')
    return
  }
  if (cartItems.value.length === 0) {
    showToast('购物车为空')
    return
  }
  try {
    const items = cartItems.value.map(item => ({
      productId: item.productId,
      skuId: item.skuId,
      quantity: item.quantity
    }))
    await createOrder({
      addressId: address.value.id,
      remark: remark.value,
      items
    })
    showToast('下单成功')
    router.replace('/order')
  } catch (e) {
    showToast(e.message || '下单失败')
  }
}

onMounted(async () => {
  try {
    const [addrRes, cartRes] = await Promise.all([getAddressList(), getCartList()])
    const addrs = addrRes || []
    address.value = addrs.find(a => a.isDefault === 1) || addrs[0] || null
    cartItems.value = (cartRes || []).filter(item => item.checked === 1)
  } catch (e) {
    // ignore
  }
})
</script>

<style scoped>
.create-order-page { min-height: 100vh; background: #f5f5f5; padding-bottom: 70px; }
.order-item { display: flex; gap: 10px; padding: 10px 16px; }
.item-img { width: 60px; height: 60px; border-radius: 4px; object-fit: cover; }
.item-img.placeholder { display: flex; align-items: center; justify-content: center; background: #eee; color: #999; font-size: 12px; }
.item-info { flex: 1; }
.item-title { font-size: 14px; }
.item-price { font-size: 13px; color: #666; margin-top: 4px; }
</style>
