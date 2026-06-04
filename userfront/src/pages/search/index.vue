<template>
  <div class="search-page">
    <van-search v-model="keyword" placeholder="搜索商品" show-action @search="onSearch" @focus="showPanel = true">
      <template #action>
        <span @click="onSearch">搜索</span>
      </template>
    </van-search>

    <!-- 搜索面板 -->
    <div v-if="showPanel && !searched" class="search-panel">
      <!-- 搜索历史 -->
      <div v-if="historyList.length > 0" class="panel-section">
        <div class="section-header">
          <span>搜索历史</span>
          <van-icon name="delete-o" @click="onClearHistory" />
        </div>
        <div class="tag-list">
          <van-tag v-for="item in historyList" :key="item.id" plain size="medium" @click="onTagClick(item.keyword)">
            {{ item.keyword }}
          </van-tag>
        </div>
      </div>

      <!-- 热门搜索 -->
      <div class="panel-section">
        <div class="section-header">
          <span>热门搜索</span>
        </div>
        <div class="tag-list">
          <van-tag v-for="item in hotList" :key="item.keyword" plain type="danger" size="medium"
            @click="onTagClick(item.keyword)">
            {{ item.keyword }}
          </van-tag>
        </div>
      </div>
    </div>

    <!-- 搜索建议 -->
    <van-cell-group v-if="keyword && showPanel && !searched">
      <van-cell v-for="s in suggestions" :key="s" :title="s" icon="search" @click="onTagClick(s)" />
    </van-cell-group>

    <!-- 搜索结果 -->
    <div v-if="searched" class="search-results">
      <van-empty v-if="!loading && results.length === 0" description="暂无搜索结果" />
      <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadMore">
        <div class="product-card" v-for="item in results" :key="item.id"
          @click="$router.push(`/product/${item.id}`)">
          <img v-if="item.mainImage" v-lazy="getThumbnailUrl(item.mainImage)" class="product-img" />
          <div v-else class="product-img placeholder">暂无</div>
          <div class="product-info">
            <div class="product-name" v-html="item.name"></div>
            <div class="product-subtitle" v-if="item.subtitle" v-html="item.subtitle"></div>
            <div class="product-price">¥{{ Number(item.price).toFixed(2) }}</div>
            <div class="product-sales" v-if="item.sales">已售{{ item.sales }}</div>
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { searchProducts, getHotKeywords, searchSuggest, getSearchHistory, clearSearchHistory } from '../../api/search'
import { getThumbnailUrl } from '../../utils/image'
import { showToast } from 'vant'

const router = useRouter()
const keyword = ref('')
const showPanel = ref(true)
const searched = ref(false)
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const results = ref([])
const historyList = ref([])
const hotList = ref([])
const suggestions = ref([])

let debounceTimer = null

watch(keyword, (val) => {
  if (!val) {
    suggestions.value = []
    searched.value = false
    showPanel.value = true
    return
  }
  // 联想搜索：每次输入变化都触发，300ms 防抖
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(async () => {
    try {
      suggestions.value = await searchSuggest(val)
    } catch (e) {
      suggestions.value = []
    }
  }, 300)
})

const onSearch = () => {
  if (!keyword.value.trim()) return
  showPanel.value = false
  searched.value = true
  page.value = 1
  results.value = []
  finished.value = false
  loading.value = true
  loadMore()
}

const loadMore = async () => {
  try {
    const res = await searchProducts({ keyword: keyword.value, page: page.value, size: 10 })
    const list = res?.list || []
    if (page.value === 1) {
      results.value = list
    } else {
      results.value.push(...list)
    }
    if (list.length < 10) finished.value = true
    page.value++
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
  }
}

const onTagClick = (kw) => {
  keyword.value = kw
  onSearch()
}

const onClearHistory = async () => {
  try {
    await clearSearchHistory()
    historyList.value = []
    showToast('已清空')
  } catch (e) {
    showToast('清空失败')
  }
}

onMounted(async () => {
  try {
    const [history, hot] = await Promise.all([getSearchHistory(), getHotKeywords()])
    historyList.value = history || []
    hotList.value = hot || []
  } catch (e) {
    // ignore
  }
})
</script>

<style scoped>
.search-page { min-height: 100vh; background: #f5f5f5; }
.search-panel { padding: 10px 16px; }
.panel-section { margin-bottom: 16px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-size: 14px; font-weight: bold; }
.tag-list { display: flex; flex-wrap: wrap; gap: 8px; }
.product-card { display: flex; gap: 10px; background: #fff; margin: 10px; border-radius: 8px; padding: 10px; }
.product-img { width: 100px; height: 100px; border-radius: 4px; object-fit: cover; }
.product-img.placeholder { display: flex; align-items: center; justify-content: center; background: #eee; color: #999; font-size: 12px; }
.product-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.product-name { font-size: 14px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-name :deep(em), .product-subtitle :deep(em) { font-style: normal; color: #ee0a24; }
.product-subtitle { font-size: 12px; color: #999; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { font-size: 16px; font-weight: bold; color: #ee0a24; }
.product-sales { font-size: 12px; color: #999; }
</style>
