<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>商家管理</span>
          <el-radio-group v-model="statusFilter" @change="loadMerchants">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="0">待审核</el-radio-button>
            <el-radio-button label="1">已通过</el-radio-button>
            <el-radio-button label="2">已拒绝</el-radio-button>
            <el-radio-button label="3">已禁用</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="shopName" label="店铺名称" />
        <el-table-column prop="contactName" label="联系人" />
        <el-table-column prop="contactPhone" label="联系电话" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : row.status === 2 ? 'danger' : 'info'">
              {{ ['待审核', '已通过', '已拒绝', '已禁用'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" size="small" @click="handleAudit(row, 1)">通过</el-button>
              <el-button type="danger" size="small" @click="handleAudit(row, 2)">拒绝</el-button>
            </template>
            <template v-else-if="row.status === 1">
              <el-button type="warning" size="small" @click="handleStatus(row, 3)">禁用</el-button>
            </template>
            <template v-else-if="row.status === 3">
              <el-button type="success" size="small" @click="handleStatus(row, 1)">启用</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end;">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadMerchants" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMerchantList, auditMerchant, updateMerchantStatus } from '../../api/merchant'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const statusFilter = ref('')

const loadMerchants = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (statusFilter.value !== '') params.status = statusFilter.value
    const res = await getMerchantList(params)
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
    const { value: remark } = await ElMessageBox.prompt(`请输入${action}备注（可选）`, `${action}审核`, {
      inputPlaceholder: '备注信息',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: status === 1 ? '审核通过' : ''
    })
    await auditMerchant(row.id, { status, remark })
    ElMessage.success(`已${action}`)
    loadMerchants()
  } catch (e) {
    if (e !== 'cancel' && e?.message) ElMessage.error(e.message)
  }
}

const handleStatus = async (row, status) => {
  const action = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确认${action}该商家？`, '提示', { type: 'warning' })
    await updateMerchantStatus(row.id, { status })
    ElMessage.success(`已${action}`)
    loadMerchants()
  } catch (e) {
    if (e !== 'cancel' && e?.message) ElMessage.error(e.message)
  }
}

onMounted(loadMerchants)
</script>
