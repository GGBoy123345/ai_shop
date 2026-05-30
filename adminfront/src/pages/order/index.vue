<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>订单管理</span>
          <el-radio-group v-model="statusFilter" @change="loadOrders">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="0">待付款</el-radio-button>
            <el-radio-button label="1">待发货</el-radio-button>
            <el-radio-button label="2">待收货</el-radio-button>
            <el-radio-button label="3">已完成</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">¥{{ Number(row.totalAmount).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="100" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="primary" size="small" @click="handleShip(row)">发货</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end;">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadOrders" />
      </div>
    </el-card>

    <el-dialog v-model="shipDialogVisible" title="发货" width="400px">
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="物流公司">
          <el-input v-model="shipForm.logisticsCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="物流单号">
          <el-input v-model="shipForm.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderList, shipOrder } from '../../api/order'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const statusFilter = ref('')
const shipDialogVisible = ref(false)
const shipForm = ref({ logisticsCompany: '', logisticsNo: '' })
const currentShipOrder = ref(null)

const statusText = (s) => ['待付款', '待发货', '待收货', '已完成', '已取消', '已退款'][s] || '未知'
const statusTagType = (s) => ['warning', 'primary', 'success', 'success', 'info', 'danger'][s] || 'default'

const loadOrders = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (statusFilter.value !== '') params.status = statusFilter.value
    const res = await getOrderList(params)
    tableData.value = res?.records || []
    total.value = res?.total || 0
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleShip = (row) => {
  currentShipOrder.value = row
  shipForm.value = { logisticsCompany: '', logisticsNo: '' }
  shipDialogVisible.value = true
}

const confirmShip = async () => {
  if (!shipForm.value.logisticsCompany || !shipForm.value.logisticsNo) {
    ElMessage.warning('请填写物流信息')
    return
  }
  try {
    await shipOrder(currentShipOrder.value.id, shipForm.value)
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    loadOrders()
  } catch (e) {
    ElMessage.error(e.message || '发货失败')
  }
}

onMounted(loadOrders)
</script>
