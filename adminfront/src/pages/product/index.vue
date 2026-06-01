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
        <el-table-column prop="title" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="90">
          <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="70" />
        <el-table-column prop="sales" label="销量" width="70" />
        <el-table-column prop="views" label="浏览" width="70" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'info' : 'warning'">
              {{ row.status === 1 ? '上架' : row.status === 0 ? '下架' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="标记" width="160">
          <template #default="{ row }">
            <el-tag v-if="row.isHot" type="danger" size="small" style="margin-right:4px;">热销</el-tag>
            <el-tag v-if="row.isNew" type="success" size="small" style="margin-right:4px;">新品</el-tag>
            <el-tag v-if="row.isRecommend" type="warning" size="small">推荐</el-tag>
            <span v-if="!row.isHot && !row.isNew && !row.isRecommend" style="color:#ccc;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button v-if="row.status === 2" type="success" size="small" @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="row.status === 2" type="danger" size="small" @click="handleAudit(row, 0)">拒绝</el-button>
            <el-button size="small" :type="row.isHot ? 'info' : 'danger'" @click="toggleField(row, 'hot')">
              {{ row.isHot ? '取消热销' : '设为热销' }}
            </el-button>
            <el-button size="small" :type="row.isRecommend ? 'info' : 'warning'" @click="toggleField(row, 'recommend')">
              {{ row.isRecommend ? '取消推荐' : '设为推荐' }}
            </el-button>
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
import { getProductList, auditProduct, setHot, setRecommend } from '../../api/product'

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

const toggleField = async (row, field) => {
  try {
    if (field === 'hot') {
      await setHot(row.id, row.isHot ? 0 : 1)
      row.isHot = row.isHot ? 0 : 1
    } else if (field === 'recommend') {
      await setRecommend(row.id, row.isRecommend ? 0 : 1)
      row.isRecommend = row.isRecommend ? 0 : 1
    }
    ElMessage.success('操作成功')
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

onMounted(loadProducts)
</script>
