<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>操作日志</span>
          <div>
            <el-select v-model="filterModule" placeholder="筛选模块" clearable style="width: 140px; margin-right: 8px">
              <el-option label="用户模块" value="用户模块" />
              <el-option label="商品模块" value="商品模块" />
              <el-option label="订单模块" value="订单模块" />
              <el-option label="商家模块" value="商家模块" />
              <el-option label="文件模块" value="文件模块" />
            </el-select>
            <el-button type="danger" @click="handleClear" :disabled="tableData.length === 0">清空日志</el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="module" label="模块" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.module }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作" min-width="180" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="status" label="结果" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>

      <div style="margin-top: 16px; display: flex; justify-content: flex-end;">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadLogs"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const filterModule = ref('')

const filteredData = computed(() => {
  if (!filterModule.value) return tableData.value
  return tableData.value.filter(item => item.module === filterModule.value)
})

const mockLogs = [
  { id: 1, module: '用户模块', operation: '用户登录', operatorName: 'admin', ip: '192.168.1.100', status: 1, createTime: '2026-05-29 10:23:15' },
  { id: 2, module: '商品模块', operation: '审核商品通过 #1024', operatorName: 'admin', ip: '192.168.1.100', status: 1, createTime: '2026-05-29 10:25:30' },
  { id: 3, module: '商家模块', operation: '审核商家入驻申请 #5', operatorName: 'admin', ip: '192.168.1.100', status: 1, createTime: '2026-05-29 10:30:00' },
  { id: 4, module: '订单模块', operation: '订单发货 #20260529001', operatorName: 'admin', ip: '192.168.1.100', status: 1, createTime: '2026-05-29 11:00:00' },
  { id: 5, module: '文件模块', operation: '删除文件 #88', operatorName: 'admin', ip: '192.168.1.100', status: 1, createTime: '2026-05-29 11:15:00' },
  { id: 6, module: '商品模块', operation: '批量上架商品', operatorName: 'admin', ip: '192.168.1.100', status: 1, createTime: '2026-05-29 14:00:00' },
  { id: 7, module: '用户模块', operation: '重置用户密码 #3', operatorName: 'admin', ip: '192.168.1.100', status: 1, createTime: '2026-05-29 14:20:00' },
  { id: 8, module: '商家模块', operation: '禁用商家 #2', operatorName: 'admin', ip: '192.168.1.100', status: 1, createTime: '2026-05-29 15:00:00' },
]

async function loadLogs() {
  loading.value = true
  try {
    // TODO: 对接后端操作日志API
    await new Promise(resolve => setTimeout(resolve, 300))
    tableData.value = mockLogs
    total.value = mockLogs.length
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

async function handleClear() {
  try {
    await ElMessageBox.confirm('确认清空所有操作日志？', '警告', { type: 'warning' })
    tableData.value = []
    total.value = 0
    ElMessage.success('已清空')
  } catch {
    // 取消
  }
}

onMounted(() => loadLogs())
</script>
