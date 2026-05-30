<template>
  <div class="order-detail-page">
    <van-nav-bar title="订单详情" left-arrow @click-left="$router.back()" />
    <van-loading v-if="loading" style="text-align: center; padding: 40px;" />
    <template v-else-if="order">
      <div class="status-bar">
        <span class="status-text">{{ statusText(order.status) }}</span>
      </div>
      <van-cell-group inset style="margin-top: 10px;">
        <van-cell title="订单号" :value="order.orderNo" />
        <van-cell title="下单时间" :value="order.createTime" />
        <van-cell v-if="order.payTime" title="支付时间" :value="order.payTime" />
        <van-cell v-if="order.shipTime" title="发货时间" :value="order.shipTime" />
      </van-cell-group>

      <van-cell-group inset style="margin-top: 10px;" title="商品信息">
        <div v-for="item in order.items" :key="item.id" class="order-item">
          <img v-if="item.productImage" :src="item.productImage" class="item-img" />
          <div v-else class="item-img placeholder">暂无</div>
          <div class="item-info">
            <div class="item-title">{{ item.productTitle }}</div>
            <div class="item-price">¥{{ Number(item.price).toFixed(2) }} x{{ item.quantity }}</div>
          </div>
        </div>
      </van-cell-group>

      <van-cell-group inset style="margin-top: 10px;">
        <van-cell title="商品总额" :value="'¥' + Number(order.totalAmount).toFixed(2)" />
        <van-cell title="实付金额" :value="'¥' + Number(order.payAmount || order.totalAmount).toFixed(2)" />
        <van-cell v-if="order.remark" title="备注" :value="order.remark" />
      </van-cell-group>

      <div v-if="order.logisticsCompany" style="margin-top: 10px;">
        <van-cell-group inset title="物流信息">
          <van-cell title="物流公司" :value="order.logisticsCompany" />
          <van-cell title="物流单号" :value="order.logisticsNo" />
        </van-cell-group>
      </div>

      <div class="bottom-actions">
        <van-button v-if="order.status === 0" type="danger" size="small" @click="onCancel">取消订单</van-button>
        <van-button v-if="order.status === 0" type="primary" size="small">去付款</van-button>
        <van-button v-if="order.status === 2" type="primary" size="small" @click="onConfirm">确认收货</van-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getOrderDetail, cancelOrder, confirmReceive } from '../../api/order'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)

const statusText = (s) => ['待付款', '待发货', '待收货', '已完成', '已取消', '已退款'][s] || '未知'

const onCancel = async () => {
  try {
    await cancelOrder(order.value.id)
    showToast('已取消')
    router.back()
  } catch (e) {
    showToast(e.message || '取消失败')
  }
}

const onConfirm = async () => {
  try {
    await confirmReceive(order.value.id)
    showToast('已确认收货')
    router.back()
  } catch (e) {
    showToast(e.message || '操作失败')
  }
}

onMounted(async () => {
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res || {}
  } catch (e) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.order-detail-page { min-height: 100vh; background: #f5f5f5; padding-bottom: 70px; }
.status-bar { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; color: #fff; }
.status-text { font-size: 20px; font-weight: bold; }
.order-item { display: flex; gap: 10px; padding: 10px 16px; }
.item-img { width: 60px; height: 60px; border-radius: 4px; object-fit: cover; }
.item-img.placeholder { display: flex; align-items: center; justify-content: center; background: #eee; color: #999; font-size: 12px; }
.item-info { flex: 1; }
.item-title { font-size: 14px; }
.item-price { font-size: 13px; color: #666; margin-top: 4px; }
.bottom-actions { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; padding: 10px 16px; display: flex; justify-content: flex-end; gap: 10px; box-shadow: 0 -2px 10px rgba(0,0,0,0.05); }
</style>
