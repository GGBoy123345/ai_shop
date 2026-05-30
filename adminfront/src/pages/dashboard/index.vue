<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>用户总数</template>
          <div class="stat-value">{{ stats.userCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>商家总数</template>
          <div class="stat-value">{{ stats.merchantCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>商品总数</template>
          <div class="stat-value">{{ stats.productCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>订单总数</template>
          <div class="stat-value">{{ stats.orderCount }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>近7天订单趋势</template>
          <div class="chart-placeholder">图表区域（ECharts）</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>近7天销售额</template>
          <div class="chart-placeholder">图表区域（ECharts）</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserMerchantStats, getProductCount, getOrderCount } from '@/api/statistics'

const stats = ref({
  userCount: 0,
  merchantCount: 0,
  productCount: 0,
  orderCount: 0
})

const loadStats = async () => {
  try {
    // 并行请求所有统计数据
    const [userMerchantRes, productRes, orderRes] = await Promise.all([
      getUserMerchantStats(),
      getProductCount(),
      getOrderCount()
    ])

    // request.js 拦截器已经解包了响应，直接返回 data
    stats.value = {
      userCount: Number(userMerchantRes.userCount) || 0,
      merchantCount: Number(userMerchantRes.merchantCount) || 0,
      productCount: Number(productRes) || 0,
      orderCount: Number(orderRes) || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.stat-value { font-size: 28px; font-weight: bold; color: #409EFF; text-align: center; }
.chart-placeholder { height: 300px; display: flex; align-items: center; justify-content: center; color: #999; background: #f5f5f5; border-radius: 4px; }
</style>
