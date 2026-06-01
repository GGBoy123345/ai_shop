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
      <div v-if="product.subtitle" class="subtitle">{{ product.subtitle }}</div>
      <div class="meta-row">
        <span>销量: {{ product.sales || 0 }}</span>
        <span>浏览: {{ product.views || 0 }}</span>
        <span v-if="product.weight">重量: {{ product.weight }}kg</span>
      </div>
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
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { gsap } from 'gsap'
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

// GSAP入场动画
const playEnterAnimations = () => {
  nextTick(() => {
    const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })

    // 图片区域缩放入场
    tl.from('.image-swipe, .product-image', {
      scale: 1.1,
      opacity: 0,
      duration: 0.8,
      ease: 'power2.out'
    })

    // 价格弹入效果
    tl.from('.price', {
      scale: 0.5,
      opacity: 0,
      duration: 0.5,
      ease: 'back.out(2)'
    }, '-=0.3')

    // 原价划线淡入
    tl.from('.original-price', {
      x: -20,
      opacity: 0,
      duration: 0.4
    }, '-=0.2')

    // 标题滑入
    tl.from('.title', {
      y: 20,
      opacity: 0,
      duration: 0.5
    }, '-=0.2')

    // 销量和元数据行
    tl.from('.sales, .subtitle, .meta-row', {
      y: 15,
      opacity: 0,
      duration: 0.4
    }, '-=0.2')

    // 功能单元格依次出现
    tl.from('.van-cell-group .van-cell', {
      x: -30,
      opacity: 0,
      duration: 0.4,
      stagger: 0.1
    }, '-=0.2')

    // 商品详情区域滑入
    tl.from('.desc-section', {
      y: 40,
      opacity: 0,
      duration: 0.6
    }, '-=0.2')

    // 底部操作栏弹入
    tl.from('.van-goods-action', {
      y: 80,
      opacity: 0,
      duration: 0.6,
      ease: 'back.out(1.5)'
    }, '-=0.3')
  })
}

onMounted(async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res || {}
    if (product.value.skus && product.value.skus.length) {
      selectedSku.value = product.value.skus[0].id
    }
    playEnterAnimations()
  } catch (e) {
    showToast('加载失败')
  }
})
</script>

<style scoped>
.product-detail { background: #f5f5f5; min-height: 100vh; padding-bottom: 70px; }
.image-swipe {
  height: 320px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}
.swipe-img {
  width: 100%;
  height: 320px;
  object-fit: cover;
  transition: transform 0.3s ease;
}
.swipe-img:active { transform: scale(0.98); }
.product-image.placeholder {
  height: 320px;
  background: linear-gradient(135deg, #f5f5f5, #e8e8e8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 16px;
}
.product-info {
  background: #fff;
  padding: 18px;
  position: relative;
}
.product-info::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 15px;
  right: 15px;
  height: 1px;
  background: linear-gradient(90deg, transparent, #eee, transparent);
}
.price {
  color: #ee0a24;
  font-size: 28px;
  font-weight: bold;
  display: inline-block;
}
.price::before {
  content: '¥';
  font-size: 18px;
  margin-right: 2px;
}
.original-price {
  font-size: 13px;
  color: #999;
  text-decoration: line-through;
  margin-top: 4px;
  display: inline-block;
  margin-left: 10px;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}
.title {
  font-size: 17px;
  margin-top: 12px;
  line-height: 1.5;
  color: #333;
  font-weight: 500;
}
.sales {
  font-size: 13px;
  color: #666;
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.sales::before {
  content: '🔥';
  font-size: 14px;
}
.subtitle {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
  line-height: 1.4;
}
.meta-row {
  font-size: 13px;
  color: #666;
  margin-top: 8px;
  display: flex;
  gap: 16px;
}
.van-cell-group {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.desc-section {
  background: #fff;
  margin-top: 12px;
  padding: 18px;
  border-radius: 12px;
  margin-left: 10px;
  margin-right: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.desc-title {
  font-size: 17px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}
.desc-title::before {
  content: '';
  width: 4px;
  height: 18px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 2px;
}
.desc-content {
  font-size: 14px;
  color: #555;
  line-height: 1.8;
}
.attr-list { max-height: 300px; overflow-y: auto; }
.sku-list { padding: 12px; max-height: 300px; overflow-y: auto; }
.sku-item {
  padding: 14px;
  margin-bottom: 10px;
  background: #f8f8f8;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}
.sku-item:hover {
  background: #f0f7ff;
  transform: translateX(4px);
}
.sku-item.active {
  border: 2px solid #1989fa;
  background: linear-gradient(135deg, #e8f4ff, #f0f7ff);
  box-shadow: 0 2px 12px rgba(25, 137, 250, 0.2);
}
.sku-price {
  color: #ee0a24;
  font-weight: bold;
  font-size: 16px;
}
.sku-stock {
  color: #999;
  font-size: 12px;
  margin-left: auto;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}
.van-goods-action {
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.08);
}
</style>
