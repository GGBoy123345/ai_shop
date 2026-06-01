<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #409EFF;">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">用户总数</div>
            <div class="stat-value">{{ stats.userCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #67C23A;">
            <el-icon><Shop /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">商家总数</div>
            <div class="stat-value">{{ stats.merchantCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #E6A23C;">
            <el-icon><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">商品总数</div>
            <div class="stat-value">{{ stats.productCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #F56C6C;">
            <el-icon><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">订单总数</div>
            <div class="stat-value">{{ stats.orderCount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近7天订单趋势</span>
              <el-radio-group v-model="orderChartType" size="small">
                <el-radio-button label="line">折线图</el-radio-button>
                <el-radio-button label="bar">柱状图</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="orderChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近7天销售额趋势</span>
            </div>
          </template>
          <div ref="salesChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card>
          <template #header>商品分类分布</template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近订单</span>
              <el-button type="primary" link @click="$router.push('/order')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" stripe style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="productName" label="商品" min-width="120" show-overflow-tooltip />
            <el-table-column prop="amount" label="金额" width="100">
              <template #default="{ row }">
                <span style="color: #F56C6C; font-weight: bold;">¥{{ row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { User, Shop, Goods, List } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getUserMerchantStats,
  getProductCount,
  getOrderCount,
  getOrderTrend,
  getSalesTrend,
  getCategoryStats
} from '@/api/statistics'

// 统计数据
const stats = ref({
  userCount: 0,
  merchantCount: 0,
  productCount: 0,
  orderCount: 0
})

// 图表类型切换
const orderChartType = ref('line')

// 图表DOM引用
const orderChartRef = ref(null)
const salesChartRef = ref(null)
const categoryChartRef = ref(null)

// 图表实例
let orderChart = null
let salesChart = null
let categoryChart = null

// 最近订单（模拟数据）
const recentOrders = ref([
  { orderNo: 'ORD20260601001', productName: 'iPhone 16 Pro Max', amount: '9999.00', status: 0, createTime: '2026-06-01 14:30:00' },
  { orderNo: 'ORD20260601002', productName: 'MacBook Pro 16寸', amount: '18999.00', status: 1, createTime: '2026-06-01 13:20:00' },
  { orderNo: 'ORD20260601003', productName: 'AirPods Pro 3', amount: '1899.00', status: 2, createTime: '2026-06-01 12:15:00' },
  { orderNo: 'ORD20260601004', productName: 'iPad Air 6', amount: '4799.00', status: 3, createTime: '2026-06-01 11:00:00' },
  { orderNo: 'ORD20260601005', productName: 'Apple Watch Ultra 3', amount: '6299.00', status: 4, createTime: '2026-06-01 10:45:00' }
])

// 订单状态映射
const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待付款', 1: '待发货', 2: '已发货', 3: '已完成', 4: '已取消' }
  return texts[status] || '未知'
}

// 生成近7天日期
const getLast7Days = () => {
  const dates = []
  for (let i = 6; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
  }
  return dates
}

// 加载统计数据
const loadStats = async () => {
  try {
    const [userMerchantRes, productRes, orderRes] = await Promise.all([
      getUserMerchantStats(),
      getProductCount(),
      getOrderCount()
    ])

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

// 初始化订单趋势图
const initOrderChart = async () => {
  if (!orderChartRef.value) return

  orderChart = echarts.init(orderChartRef.value)

  // 模拟数据，实际应从API获取
  let dates = getLast7Days()
  let orderData = [12, 18, 15, 22, 28, 25, 32]

  try {
    const res = await getOrderTrend()
    if (res && res.dates && res.counts) {
      dates = res.dates
      orderData = res.counts
    }
  } catch (e) {
    console.log('使用模拟订单趋势数据')
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '订单数'
    },
    series: [{
      name: '订单数',
      type: orderChartType.value,
      smooth: true,
      data: orderData,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      },
      lineStyle: {
        width: 2,
        color: '#409EFF'
      },
      itemStyle: {
        color: '#409EFF'
      }
    }]
  }

  orderChart.setOption(option)
}

// 初始化销售额趋势图
const initSalesChart = async () => {
  if (!salesChartRef.value) return

  salesChart = echarts.init(salesChartRef.value)

  // 模拟数据
  let dates = getLast7Days()
  let salesData = [8500, 12800, 9600, 15200, 18900, 14500, 21000]

  try {
    const res = await getSalesTrend()
    if (res && res.dates && res.amounts) {
      dates = res.dates
      salesData = res.amounts
    }
  } catch (e) {
    console.log('使用模拟销售额趋势数据')
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br />销售额: ¥{c}'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '销售额(元)',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [{
      name: '销售额',
      type: 'line',
      smooth: true,
      data: salesData,
      markPoint: {
        data: [
          { type: 'max', name: '最大值' },
          { type: 'min', name: '最小值' }
        ]
      },
      markLine: {
        data: [{ type: 'average', name: '平均值' }]
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(103, 194, 58, 0.5)' },
          { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
        ])
      },
      lineStyle: {
        width: 2,
        color: '#67C23A'
      },
      itemStyle: {
        color: '#67C23A'
      }
    }]
  }

  salesChart.setOption(option)
}

// 初始化分类统计图
const initCategoryChart = async () => {
  if (!categoryChartRef.value) return

  categoryChart = echarts.init(categoryChartRef.value)

  // 模拟数据
  let categoryData = [
    { value: 35, name: '手机数码' },
    { value: 28, name: '电脑办公' },
    { value: 18, name: '家用电器' },
    { value: 12, name: '服饰鞋包' },
    { value: 7, name: '食品生鲜' }
  ]

  try {
    const res = await getCategoryStats()
    if (res && Array.isArray(res)) {
      categoryData = res
    }
  } catch (e) {
    console.log('使用模拟分类统计数据')
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center'
    },
    series: [{
      name: '商品分类',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: categoryData
    }]
  }

  categoryChart.setOption(option)
}

// 监听图表类型变化
watch(orderChartType, () => {
  if (orderChart) {
    orderChart.dispose()
    initOrderChart()
  }
})

// 窗口大小变化时重绘图表
const handleResize = () => {
  orderChart?.resize()
  salesChart?.resize()
  categoryChart?.resize()
}

onMounted(async () => {
  await loadStats()
  await nextTick()
  initOrderChart()
  initSalesChart()
  initCategoryChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  orderChart?.dispose()
  salesChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 0;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 20px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.stat-icon .el-icon {
  font-size: 28px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
