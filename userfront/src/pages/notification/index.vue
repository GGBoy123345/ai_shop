<template>
  <div class="notification-page">
    <van-nav-bar title="消息通知" left-arrow @click-left="$router.back()">
      <template #right>
        <span @click="handleMarkAllRead" style="font-size: 14px; color: #1989fa">全部已读</span>
      </template>
    </van-nav-bar>

    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <van-tab title="全部" name="all" />
      <van-tab title="未读" name="unread" />
      <van-tab title="系统" name="system" />
      <van-tab title="订单" name="order" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadMore"
      >
        <van-cell-group v-if="list.length > 0" inset style="margin-top: 10px">
          <van-swipe-cell v-for="item in list" :key="item.id">
            <van-cell
              :title="item.title"
              :label="item.content"
              @click="handleRead(item)"
            >
              <template #value>
                <div style="text-align: right">
                  <div style="font-size: 12px; color: #999">{{ formatTime(item.createTime) }}</div>
                  <van-tag v-if="!item.isRead" type="danger" size="small" style="margin-top: 4px">未读</van-tag>
                </div>
              </template>
            </van-cell>
            <template #right>
              <van-button square type="danger" text="删除" @click="handleDelete(item.id)" />
            </template>
          </van-swipe-cell>
        </van-cell-group>

        <van-empty v-else-if="finished" description="暂无消息" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import { getNotifications, markRead, markAllRead, deleteNotification } from '../../api/notification'

const activeTab = ref('all')
const list = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const page = ref(1)
const size = 10

function getParams() {
  const params = { page: page.value, size }
  if (activeTab.value === 'unread') {
    params.isRead = false
  } else if (activeTab.value === 'system' || activeTab.value === 'order') {
    params.type = activeTab.value
  }
  return params
}

async function loadData(append = false) {
  loading.value = true
  try {
    const res = await getNotifications(getParams())
    const records = res.records || []
    if (append) {
      list.value.push(...records)
    } else {
      list.value = records
    }
    finished.value = list.value.length >= res.total
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value++
  loadData(true)
}

function onRefresh() {
  page.value = 1
  finished.value = false
  loadData(false)
  refreshing.value = false
}

function onTabChange() {
  page.value = 1
  finished.value = false
  list.value = []
  loadData(false)
}

async function handleRead(item) {
  if (!item.isRead) {
    try {
      await markRead(item.id)
      item.isRead = true
    } catch (e) {
      // ignore
    }
  }
}

async function handleMarkAllRead() {
  try {
    await markAllRead()
    list.value.forEach(item => { item.isRead = true })
    showToast('已全部标为已读')
  } catch (e) {
    showToast('操作失败')
  }
}

async function handleDelete(id) {
  try {
    await showConfirmDialog({ title: '确认删除该消息？' })
    await deleteNotification(id)
    list.value = list.value.filter(item => item.id !== id)
    showToast('已删除')
  } catch (e) {
    // cancelled
  }
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${date.getMonth() + 1}-${date.getDate()}`
}

onMounted(() => {
  loadData(false)
})
</script>

<style scoped>
.notification-page {
  min-height: 100vh;
  background: #f5f5f5;
}
</style>
