<template>
  <div class="order-list-page">
    <van-nav-bar title="我的订单" left-arrow @click-left="$router.back()" />
    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <van-tab title="全部" name="" />
      <van-tab title="待付款" name="0" />
      <van-tab title="待发货" name="1" />
      <van-tab title="待收货" name="2" />
      <van-tab title="已完成" name="3" />
    </van-tabs>
    <van-empty v-if="!loading && orders.length === 0" description="暂无订单" />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadOrders">
      <div class="order-card" v-for="order in orders" :key="order.id"
        @click="$router.push(`/order/${order.id}`)">
        <div class="order-header">
          <span class="order-no">订单号: {{ order.orderNo }}</span>
          <van-tag :type="statusTagType(order.status)">{{ statusText(order.status) }}</van-tag>
        </div>
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <img v-if="item.productImage" :src="item.productImage" class="item-img" />
            <div v-else class="item-img placeholder">暂无</div>
            <div class="item-info">
              <div class="item-title">{{ item.productTitle }}</div>
              <div class="item-price">¥{{ Number(item.price).toFixed(2) }} x{{ item.quantity }}</div>
            </div>
          </div>
        </div>
        <div class="order-footer">
          <span class="total">合计: ¥{{ Number(order.payAmount || order.totalAmount).toFixed(2) }}</span>
          <div class="actions">
            <van-button v-if="order.status === 0" size="small" type="default" @click.stop="onCancel(order.id)">取消</van-button>
            <van-button v-if="order.status === 0" size="small" type="danger">去付款</van-button>
            <van-button v-if="order.status === 2" size="small" type="primary" @click.stop="onConfirm(order.id)">确认收货</van-button>
          </div>
        </div>
      </div>
    </van-list>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { showToast } from 'vant'
import { getOrderList, cancelOrder, confirmReceive } from '../../api/order'

const route = useRoute()
const activeTab = ref(route.query.status || '')
const orders = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)

const statusText = (s) => ['待付款', '待发货', '待收货', '已完成', '已取消', '已退款'][s] || '未知'
const statusTagType = (s) => ['warning', 'primary', 'success', 'success', 'default', 'danger'][s] || 'default'

const loadOrders = async () => {
  try {
    const params = { page: page.value, size: 10 }
    if (activeTab.value !== '') params.status = activeTab.value
    const res = await getOrderList(params)
    const records = res?.records || []
    if (page.value === 1) {
      orders.value = records
    } else {
      orders.value.push(...records)
    }
    if (records.length < 10) finished.value = true
    page.value++
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
  }
}

const onTabChange = () => {
  page.value = 1
  orders.value = []
  finished.value = false
  loading.value = true
  loadOrders()
}

const onCancel = async (id) => {
  try {
    await cancelOrder(id)
    showToast('已取消')
    onTabChange()
  } catch (e) {
    showToast(e.message || '取消失败')
  }
}

const onConfirm = async (id) => {
  try {
    await confirmReceive(id)
    showToast('已确认收货')
    onTabChange()
  } catch (e) {
    showToast(e.message || '操作失败')
  }
}

onMounted(() => {
  if (route.query.status) activeTab.value = route.query.status
})
</script>

<style scoped>
.order-list-page { min-height: 100vh; background: #f5f5f5; }
.order-card { background: #fff; margin: 10px; border-radius: 8px; padding: 12px; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.order-no { font-size: 13px; color: #666; }
.order-item { display: flex; gap: 10px; margin-bottom: 8px; }
.item-img { width: 60px; height: 60px; border-radius: 4px; object-fit: cover; }
.item-img.placeholder { display: flex; align-items: center; justify-content: center; background: #eee; color: #999; font-size: 12px; }
.item-info { flex: 1; }
.item-title { font-size: 14px; }
.item-price { font-size: 13px; color: #666; margin-top: 4px; }
.order-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; padding-top: 10px; border-top: 1px solid #f0f0f0; }
.total { font-size: 15px; font-weight: bold; color: #ee0a24; }
.actions { display: flex; gap: 8px; }
</style>
