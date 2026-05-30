<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>商品管理</span>
          <el-radio-group v-model="statusFilter" @change="loadProducts">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="1">上架</el-radio-button>
            <el-radio-button label="0">下架</el-radio-button>
            <el-radio-button label="2">待审核</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="title" label="商品名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'info' : 'warning'">
              {{ row.status === 1 ? '上架' : row.status === 0 ? '下架' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 2" type="success" size="small" @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="row.status === 2" type="danger" size="small" @click="handleAudit(row, 0)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end;">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadProducts" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, auditProduct } from '../../api/product'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const statusFilter = ref('')

const loadProducts = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (statusFilter.value !== '') params.status = statusFilter.value
    const res = await getProductList(params)
    tableData.value = res?.records || []
    total.value = res?.total || 0
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleAudit = async (row, status) => {
  const action = status === 1 ? '通过' : '拒绝'
  try {
    const { value: remark } = await ElMessageBox.prompt(`请输入${action}备注`, `${action}审核`, {
      inputPlaceholder: '备注信息',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: status === 1 ? '审核通过' : ''
    })
    await auditProduct(row.id, { status, remark })
    ElMessage.success(`已${action}`)
    loadProducts()
  } catch (e) {
    if (e !== 'cancel' && e?.message) ElMessage.error(e.message)
  }
}

onMounted(loadProducts)
</script>
