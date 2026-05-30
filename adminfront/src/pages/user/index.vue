<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>用户管理</span>
          <el-input v-model="keyword" placeholder="搜索手机号/昵称" style="width: 250px;" clearable
            @clear="loadUsers" @keyup.enter="loadUsers" />
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : row.role === 'merchant' ? 'warning' : ''">
              {{ row.role === 'admin' ? '管理员' : row.role === 'merchant' ? '商家' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="(val) => handleStatusChange(row, val)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end;">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadUsers" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus } from '../../api/user'

const keyword = ref('')
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList({ page: currentPage.value, size: pageSize.value, keyword: keyword.value || undefined })
    tableData.value = res?.records || []
    total.value = res?.total || 0
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row, val) => {
  const status = val ? 1 : 0
  try {
    await ElMessageBox.confirm(`确定${status === 0 ? '禁用' : '启用'}该用户？`, '提示', { type: 'warning' })
    await updateUserStatus(row.id, { status })
    row.status = status
    ElMessage.success('操作成功')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

onMounted(loadUsers)
</script>
