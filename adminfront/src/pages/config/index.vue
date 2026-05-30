<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>系统配置</span>
          <el-button type="primary" @click="handleSave" :loading="saving">保存配置</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础配置" name="basic">
          <el-form :model="config" label-width="160px" style="max-width: 600px; margin-top: 16px">
            <el-form-item label="站点名称">
              <el-input v-model="config.siteName" />
            </el-form-item>
            <el-form-item label="站点Logo">
              <el-input v-model="config.siteLogo" placeholder="Logo图片URL" />
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="config.contactEmail" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="config.contactPhone" />
            </el-form-item>
            <el-form-item label="ICP备案号">
              <el-input v-model="config.icpNumber" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="交易配置" name="trade">
          <el-form :model="config" label-width="160px" style="max-width: 600px; margin-top: 16px">
            <el-form-item label="订单自动取消(分钟)">
              <el-input-number v-model="config.orderAutoCancelMinutes" :min="5" :max="120" />
            </el-form-item>
            <el-form-item label="自动确认收货(天)">
              <el-input-number v-model="config.autoConfirmDays" :min="1" :max="30" />
            </el-form-item>
            <el-form-item label="售后申请期限(天)">
              <el-input-number v-model="config.afterSaleDays" :min="1" :max="90" />
            </el-form-item>
            <el-form-item label="单笔订单上限(元)">
              <el-input-number v-model="config.maxOrderAmount" :min="100" :max="100000" :step="100" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="上传配置" name="upload">
          <el-form :model="config" label-width="160px" style="max-width: 600px; margin-top: 16px">
            <el-form-item label="单文件大小限制(MB)">
              <el-input-number v-model="config.maxFileSizeMB" :min="1" :max="50" />
            </el-form-item>
            <el-form-item label="允许的文件格式">
              <el-input v-model="config.allowedExtensions" placeholder="jpg,png,pdf,doc" />
            </el-form-item>
            <el-form-item label="存储方式">
              <el-select v-model="config.storageType">
                <el-option label="MinIO对象存储" value="minio" />
                <el-option label="本地存储" value="local" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('basic')
const saving = ref(false)

const config = ref({
  siteName: 'AI智能商城',
  siteLogo: '',
  contactEmail: 'admin@aimall.com',
  contactPhone: '400-123-4567',
  icpNumber: '京ICP备2026XXXXXX号',
  orderAutoCancelMinutes: 30,
  autoConfirmDays: 7,
  afterSaleDays: 15,
  maxOrderAmount: 50000,
  maxFileSizeMB: 10,
  allowedExtensions: 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx',
  storageType: 'minio'
})

async function handleSave() {
  saving.value = true
  try {
    // TODO: 对接后端系统配置API
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}
</script>
