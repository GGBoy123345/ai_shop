<template>
  <div class="products-page">
    <van-nav-bar title="我的商品" left-arrow @click-left="$router.back()">
      <template #right>
        <van-button size="small" type="primary" @click="$router.push('/merchant/product/edit')">发布商品</van-button>
      </template>
    </van-nav-bar>

    <van-tabs v-model:active="statusTab" @change="onTabChange">
      <van-tab title="全部" name="" />
      <van-tab title="在售" name="1" />
      <van-tab title="已下架" name="0" />
      <van-tab title="审核中" name="2" />
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadMore">
        <van-card v-for="item in products" :key="item.id"
          :title="item.title"
          :price="item.price"
          :thumb="item.mainImage || ''"
          :desc="item.subtitle || ''"
        >
          <template #footer>
            <van-tag v-if="item.status === 1" type="success">在售</van-tag>
            <van-tag v-else-if="item.status === 0" type="default">已下架</van-tag>
            <van-tag v-else-if="item.status === 2" type="warning">审核中</van-tag>
            <van-button size="mini" @click="$router.push(`/merchant/product/edit?id=${item.id}`)">编辑</van-button>
            <van-button v-if="item.status === 1" size="mini" type="warning" @click="toggleStatus(item, 0)">下架</van-button>
            <van-button v-else-if="item.status === 0" size="mini" type="success" @click="toggleStatus(item, 1)">上架</van-button>
            <van-button v-if="item.status === 0" size="mini" type="danger" @click="handleDelete(item)">删除</van-button>
          </template>
        </van-card>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import { getMerchantProducts, updateProductStatus, deleteProduct } from '../../api/merchant-product'

const products = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const statusTab = ref('')
const page = ref(1)

async function loadData(append = false) {
  loading.value = true
  try {
    const params = { page: page.value, size: 10 }
    if (statusTab.value !== '') params.status = statusTab.value
    const res = await getMerchantProducts(params)
    const records = res?.records || []
    if (append) {
      products.value.push(...records)
    } else {
      products.value = records
    }
    if (records.length < 10) finished.value = true
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
    refreshing.value = false
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
}

function onTabChange() {
  page.value = 1
  finished.value = false
  products.value = []
  loadData(false)
}

async function toggleStatus(item, status) {
  try {
    await updateProductStatus(item.id, status)
    item.status = status
    showToast(status === 1 ? '已上架' : '已下架')
  } catch (e) {
    showToast(e.message || '操作失败')
  }
}

async function handleDelete(item) {
  try {
    await showConfirmDialog({ title: '确认删除该商品？' })
    await deleteProduct(item.id)
    products.value = products.value.filter(p => p.id !== item.id)
    showToast('已删除')
  } catch (e) {
    // cancelled
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.products-page { min-height: 100vh; background: #f5f5f5; }
</style>
