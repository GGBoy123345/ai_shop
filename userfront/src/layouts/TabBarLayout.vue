<template>
  <div class="tabbar-layout">
    <div class="content">
      <router-view />
    </div>
    <van-tabbar :model-value="activeTab" @change="onTabChange">
      <van-tabbar-item icon="home-o" name="Home">首页</van-tabbar-item>
      <van-tabbar-item icon="apps-o" name="Category">分类</van-tabbar-item>
      <van-tabbar-item icon="shopping-cart-o" name="Cart" :badge="cartCount || ''">购物车</van-tabbar-item>
      <van-tabbar-item icon="user-o" name="User">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const cartCount = ref(0)

const tabRoutes = ['Home', 'Category', 'Cart', 'User']
const activeTab = computed(() => {
  return tabRoutes.includes(route.name) ? route.name : 'Home'
})

function onTabChange(name) {
  router.push({ name })
}
</script>

<style scoped>
.tabbar-layout {
  padding-bottom: 50px;
}
.content {
  min-height: calc(100vh - 50px);
}
</style>
