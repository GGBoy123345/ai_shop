<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>文件管理</span>
          <el-upload
            :show-file-list="false"
            :http-request="handleUpload"
            accept="image/*,.pdf,.doc,.docx,.xls,.xlsx"
            multiple
          >
            <el-button type="primary">上传文件</el-button>
          </el-upload>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="originalName" label="文件名" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="isImage(row.type) ? 'success' : 'info'" size="small">
              {{ isImage(row.type) ? '图片' : '文档' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="extension" label="格式" width="80" />
        <el-table-column label="大小" width="100">
          <template #default="{ row }">{{ formatSize(row.size) }}</template>
        </el-table-column>
        <el-table-column prop="uploaderId" label="上传者ID" width="100" />
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="preview(row)">预览</el-button>
            <el-popconfirm title="确认删除该文件？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" size="small" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end;">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadFiles" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const isImage = (type) => type && type.startsWith('image/')

const formatSize = (bytes) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(2) + ' MB'
}

const loadFiles = async () => {
  loading.value = true
  try {
    const { getFileList } = await import('../../api/file')
    const res = await getFileList({ page: currentPage.value, size: pageSize.value })
    tableData.value = res?.records || []
    total.value = res?.total || 0
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const preview = (row) => {
  if (row.url) window.open(row.url, '_blank')
}

const handleDelete = async (id) => {
  try {
    const { deleteFile } = await import('../../api/file')
    await deleteFile(id)
    ElMessage.success('删除成功')
    loadFiles()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

const handleUpload = async (options) => {
  try {
    const { uploadFile } = await import('../../api/file')
    await uploadFile(options.file)
    ElMessage.success('上传成功')
    loadFiles()
  } catch (e) {
    ElMessage.error(e.message || '上传失败')
  }
}

onMounted(loadFiles)
</script>
