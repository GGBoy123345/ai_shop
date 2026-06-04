<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>轮播图管理</span>
          <el-button type="primary" @click="showDialog()">新增轮播图</el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="200">
          <template #default="{ row }">
            <el-image :src="row.imageUrl" style="width: 160px; height: 80px" fit="cover" :preview-src-list="[row.imageUrl]" />
          </template>
        </el-table-column>
        <el-table-column prop="linkUrl" label="跳转链接" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showDialog(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm title="确认删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑轮播图' : '新增轮播图'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="轮播图片" required>
          <el-upload
            :show-file-list="false"
            :http-request="handleImageUpload"
            accept="image/*"
          >
            <el-image v-if="form.imageUrl" :src="form.imageUrl" style="width: 320px; height: 120px" fit="cover" />
            <el-icon v-else style="width: 320px; height: 120px; border: 1px dashed #dcdfe6; display: flex; align-items: center; justify-content: center; font-size: 28px; color: #909399"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" placeholder="点击轮播图跳转的链接" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getBannerList, addBanner, updateBanner, deleteBanner } from '../../api/banner'
import { uploadImage } from '../../api/file'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editId = ref(null)
const uploadLoading = ref(false)
const form = ref({ imageUrl: '', linkUrl: '', sort: 0, status: 1 })

async function handleImageUpload(options) {
  uploadLoading.value = true
  try {
    const res = await uploadImage(options.file)
    form.value.imageUrl = res.url
    ElMessage.success('图片上传成功')
  } catch (e) {
    ElMessage.error('图片上传失败')
  } finally {
    uploadLoading.value = false
  }
}

async function loadList() {
  loading.value = true
  try {
    const res = await getBannerList()
    list.value = res || []
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function showDialog(row) {
  if (row) {
    editId.value = row.id
    form.value = { imageUrl: row.imageUrl, linkUrl: row.linkUrl, sort: row.sort, status: row.status }
  } else {
    editId.value = null
    form.value = { imageUrl: '', linkUrl: '', sort: 0, status: 1 }
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.value.imageUrl) {
    ElMessage.warning('请输入图片URL')
    return
  }
  submitting.value = true
  try {
    if (editId.value) {
      await updateBanner(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await addBanner(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row) {
  try {
    await updateBanner(row.id, { status: row.status === 1 ? 0 : 1 })
    ElMessage.success('操作成功')
    loadList()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(id) {
  try {
    await deleteBanner(id)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => loadList())
</script>
