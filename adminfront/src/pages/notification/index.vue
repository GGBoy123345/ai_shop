<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>消息通知</span>
          <el-button type="primary" @click="showSendDialog">发送通知</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="发送站内信" name="notification" />
        <el-tab-pane label="发送短信" name="sms" />
        <el-tab-pane label="发送邮件" name="email" />
        <el-tab-pane label="短信日志" name="smsLogs" />
      </el-tabs>

      <!-- 站内信表单 -->
      <el-form v-if="activeTab === 'notification'" :model="notifForm" label-width="100px" style="max-width: 600px; margin-top: 20px">
        <el-form-item label="用户ID" required>
          <el-input v-model="notifForm.userId" placeholder="请输入接收用户ID" />
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="notifForm.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="notifForm.content" type="textarea" :rows="4" placeholder="请输入通知内容" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="notifForm.type" placeholder="请选择通知类型">
            <el-option label="系统通知" value="system" />
            <el-option label="订单通知" value="order" />
            <el-option label="促销通知" value="promotion" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="sendNotification" :loading="sending">发送</el-button>
        </el-form-item>
      </el-form>

      <!-- 短信表单 -->
      <el-form v-if="activeTab === 'sms'" :model="smsForm" label-width="100px" style="max-width: 600px; margin-top: 20px">
        <el-form-item label="手机号" required>
          <el-input v-model="smsForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="短信内容" required>
          <el-input v-model="smsForm.content" type="textarea" :rows="4" placeholder="请输入短信内容" />
        </el-form-item>
        <el-form-item label="模板编码">
          <el-input v-model="smsForm.templateCode" placeholder="可选，短信模板编码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="sendSms" :loading="sending">发送</el-button>
        </el-form-item>
      </el-form>

      <!-- 邮件表单 -->
      <el-form v-if="activeTab === 'email'" :model="emailForm" label-width="100px" style="max-width: 600px; margin-top: 20px">
        <el-form-item label="收件邮箱" required>
          <el-input v-model="emailForm.to" placeholder="请输入收件邮箱" />
        </el-form-item>
        <el-form-item label="邮件主题" required>
          <el-input v-model="emailForm.subject" placeholder="请输入邮件主题" />
        </el-form-item>
        <el-form-item label="邮件内容" required>
          <el-input v-model="emailForm.content" type="textarea" :rows="6" placeholder="请输入邮件内容" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="sendEmail" :loading="sending">发送</el-button>
        </el-form-item>
      </el-form>

      <!-- 短信日志 -->
      <div v-if="activeTab === 'smsLogs'" style="margin-top: 20px">
        <div style="display: flex; gap: 10px; margin-bottom: 16px">
          <el-input v-model="smsLogSearch.phone" placeholder="按手机号筛选" style="width: 220px" clearable />
          <el-button type="primary" @click="loadSmsLogs">搜索</el-button>
        </div>
        <el-table :data="smsLogs" v-loading="smsLogLoading" stripe>
          <el-table-column prop="phone" label="手机号" width="150" />
          <el-table-column prop="templateCode" label="模板编码" width="180" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'info'">
                {{ row.status === 1 ? '成功' : row.status === 2 ? '失败' : '发送中' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" />
        </el-table>
        <el-pagination
          style="margin-top: 16px; justify-content: flex-end"
          v-model:current-page="smsLogPage.current"
          v-model:page-size="smsLogPage.size"
          :total="smsLogPage.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadSmsLogs"
          @current-change="loadSmsLogs"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createNotification, sendSms as sendSmsApi, sendEmail as sendEmailApi, getSmsLogs } from '../../api/notification'

const activeTab = ref('notification')
const sending = ref(false)

const notifForm = reactive({
  userId: '',
  title: '',
  content: '',
  type: 'system'
})

const smsForm = reactive({
  phone: '',
  content: '',
  templateCode: ''
})

const emailForm = reactive({
  to: '',
  subject: '',
  content: ''
})

const smsLogs = ref([])
const smsLogLoading = ref(false)
const smsLogSearch = reactive({ phone: '' })
const smsLogPage = reactive({ current: 1, size: 10, total: 0 })

async function loadSmsLogs() {
  smsLogLoading.value = true
  try {
    const res = await getSmsLogs({ page: smsLogPage.current, size: smsLogPage.size, phone: smsLogSearch.phone || undefined })
    smsLogs.value = res?.records || []
    smsLogPage.total = res?.total || 0
  } catch (e) {
    smsLogs.value = []
  } finally {
    smsLogLoading.value = false
  }
}

async function sendNotification() {
  if (!notifForm.userId || !notifForm.title || !notifForm.content) {
    ElMessage.warning('请填写必填项')
    return
  }
  sending.value = true
  try {
    await createNotification({
      userId: Number(notifForm.userId),
      title: notifForm.title,
      content: notifForm.content,
      type: notifForm.type
    })
    ElMessage.success('通知发送成功')
    notifForm.userId = ''
    notifForm.title = ''
    notifForm.content = ''
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

async function sendSms() {
  if (!smsForm.phone || !smsForm.content) {
    ElMessage.warning('请填写必填项')
    return
  }
  sending.value = true
  try {
    await sendSmsApi({ phone: smsForm.phone, content: smsForm.content, templateCode: smsForm.templateCode })
    ElMessage.success('短信发送成功（Mock）')
    smsForm.phone = ''
    smsForm.content = ''
    smsForm.templateCode = ''
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

async function sendEmail() {
  if (!emailForm.to || !emailForm.subject || !emailForm.content) {
    ElMessage.warning('请填写必填项')
    return
  }
  sending.value = true
  try {
    await sendEmailApi({ to: emailForm.to, subject: emailForm.subject, content: emailForm.content })
    ElMessage.success('邮件发送成功（Mock）')
    emailForm.to = ''
    emailForm.subject = ''
    emailForm.content = ''
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

watch(activeTab, (val) => {
  if (val === 'smsLogs') loadSmsLogs()
})

function showSendDialog() {
  activeTab.value = 'notification'
}
</script>
