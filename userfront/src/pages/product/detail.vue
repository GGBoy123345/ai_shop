<template>
  <div class="product-detail">
    <van-nav-bar title="商品详情" left-arrow @click-left="$router.back()" />
    <van-swipe v-if="imageList.length" :autoplay="3000" indicator-color="white" class="image-swipe">
      <van-swipe-item v-for="(img, idx) in imageList" :key="idx">
        <img :src="img" class="swipe-img" />
      </van-swipe-item>
    </van-swipe>
    <div v-else class="product-image placeholder">暂无图片</div>
    <div class="product-info">
      <div class="price">¥{{ Number(product.price || 0).toFixed(2) }}</div>
      <div v-if="product.originalPrice" class="original-price">原价: ¥{{ Number(product.originalPrice).toFixed(2) }}</div>
      <div class="title">{{ product.title || '加载中...' }}</div>
      <div class="sales">销量: {{ product.sales || 0 }}</div>
    </div>
    <van-cell-group inset style="margin-top: 10px;">
      <van-cell v-if="product.attributes && product.attributes.length" title="商品属性" is-link
        @click="showAttrs = true" />
      <van-cell v-if="product.skus && product.skus.length" title="选择规格" is-link
        @click="showSku = true" />
    </van-cell-group>
    <div v-if="product.description" class="desc-section">
      <div class="desc-title">商品详情</div>
      <div class="desc-content">{{ product.description }}</div>
    </div>

    <van-action-sheet v-model:show="showAttrs" title="商品属性">
      <div class="attr-list">
        <van-cell v-for="attr in product.attributes" :key="attr.templateId"
          :title="attr.templateName" :value="attr.value" />
      </div>
    </van-action-sheet>

    <van-action-sheet v-model:show="showSku" title="选择规格">
      <div class="sku-list">
        <div v-for="sku in product.skus" :key="sku.id" class="sku-item"
          :class="{ active: selectedSku === sku.id }" @click="selectedSku = sku.id">
          <span>{{ sku.attributes || '默认' }}</span>
          <span class="sku-price">¥{{ Number(sku.price).toFixed(2) }}</span>
          <span class="sku-stock">库存: {{ sku.stock }}</span>
        </div>
      </div>
      <div style="padding: 10px;">
        <van-button type="danger" block @click="onAddCart">确定</van-button>
      </div>
    </van-action-sheet>

    <van-goods-action>
      <van-goods-action-icon icon="chat-o" text="客服" />
      <van-goods-action-icon icon="cart-o" text="购物车" :badge="cartCount || ''" to="/cart" />
      <van-goods-action-button type="warning" text="加入购物车" @click="onAddCart" />
      <van-goods-action-button type="danger" text="立即购买" @click="onBuyNow" />
    </van-goods-action>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getProductDetail } from '../../api/product'
import { addToCart } from '../../api/cart'

const route = useRoute()
const router = useRouter()
const product = ref({})
const showAttrs = ref(false)
const showSku = ref(false)
const selectedSku = ref(null)
const cartCount = ref(0)

const imageList = computed(() => {
  if (product.value.images) {
    try { return JSON.parse(product.value.images) } catch { return [] }
  }
  return product.value.mainImage ? [product.value.mainImage] : []
})

const onAddCart = async () => {
  try {
    await addToCart({
      productId: product.value.id,
      skuId: selectedSku.value,
      quantity: 1
    })
    cartCount.value++
    showToast('已加入购物车')
    showSku.value = false
  } catch (e) {
    showToast(e.message || '加入失败')
  }
}

const onBuyNow = async () => {
  await onAddCart()
  router.push('/cart')
}

onMounted(async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res || {}
    if (product.value.skus && product.value.skus.length) {
      selectedSku.value = product.value.skus[0].id
    }
  } catch (e) {
    showToast('加载失败')
  }
})
</script>

<style scoped>
.product-detail { background: #f5f5f5; min-height: 100vh; padding-bottom: 60px; }
.image-swipe { height: 300px; }
.swipe-img { width: 100%; height: 300px; object-fit: cover; }
.product-image.placeholder { height: 300px; background: #eee; display: flex; align-items: center; justify-content: center; color: #999; }
.product-info { background: #fff; padding: 15px; }
.price { color: #ee0a24; font-size: 24px; font-weight: bold; }
.original-price { font-size: 13px; color: #999; text-decoration: line-through; margin-top: 4px; }
.title { font-size: 16px; margin-top: 8px; }
.sales { font-size: 13px; color: #999; margin-top: 4px; }
.desc-section { background: #fff; margin-top: 10px; padding: 15px; }
.desc-title { font-size: 16px; font-weight: bold; margin-bottom: 10px; }
.desc-content { font-size: 14px; color: #333; line-height: 1.6; }
.attr-list { max-height: 300px; overflow-y: auto; }
.sku-list { padding: 10px; max-height: 300px; overflow-y: auto; }
.sku-item { padding: 12px; margin-bottom: 8px; background: #f5f5f5; border-radius: 8px; display: flex; align-items: center; gap: 10px; cursor: pointer; }
.sku-item.active { border: 2px solid #1989fa; background: #e8f4ff; }
.sku-price { color: #ee0a24; font-weight: bold; }
.sku-stock { color: #999; font-size: 12px; margin-left: auto; }
</style>
