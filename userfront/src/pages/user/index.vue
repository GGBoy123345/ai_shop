<template>
  <div class="user-page">
    <div class="user-header">
      <div class="avatar">{{ userStore.userInfo?.nickname?.charAt(0) || '?' }}</div>
      <div class="info" v-if="userStore.isLoggedIn">
        <div class="name">{{ userStore.userInfo?.nickname }}</div>
        <div class="phone">{{ userStore.userInfo?.phone }}</div>
      </div>
      <div class="info" v-else>
        <router-link to="/login" class="login-link">登录/注册</router-link>
      </div>
    </div>
    <van-cell-group inset class="order-nav">
      <van-grid :column-num="4">
        <van-grid-item icon="clock-o" text="待付款" @click="$router.push('/order?status=0')" />
        <van-grid-item icon="logistics" text="待发货" @click="$router.push('/order?status=1')" />
        <van-grid-item icon="send-gift-o" text="待收货" @click="$router.push('/order?status=2')" />
        <van-grid-item icon="checked" text="已完成" @click="$router.push('/order?status=3')" />
      </van-grid>
    </van-cell-group>
    <van-cell-group inset style="margin-top: 10px;">
      <van-cell title="收货地址" is-link to="/address" />
      <van-cell title="我的收藏" is-link to="/favorite" />
      <van-cell title="商家中心" is-link to="/merchant" />
      <van-cell title="消息通知" is-link to="/notification" />
    </van-cell-group>
    <div v-if="userStore.isLoggedIn" style="margin-top: 20px; padding: 0 16px;">
      <van-button block plain type="danger" @click="onLogout">退出登录</van-button>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useUserStore } from '../../stores/user'
import { getUserInfo } from '../../api/user'
import { logout } from '../../api/auth'

const userStore = useUserStore()
const router = useRouter()

const onLogout = async () => {
  try {
    await logout()
  } catch (e) {
    // ignore
  }
  userStore.logout()
  showToast('已退出')
  router.push('/login')
}

onMounted(async () => {
  if (userStore.isLoggedIn) {
    try {
      const res = await getUserInfo()
      if (res) {
        userStore.setUserInfo(res)
      }
    } catch (e) {
      // ignore
    }
  }
})
</script>

<style scoped>
.user-page { min-height: 100vh; background: #f5f5f5; }
.user-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px 20px; display: flex; align-items: center; color: #fff; }
.avatar { width: 60px; height: 60px; border-radius: 50%; background: rgba(255,255,255,0.3); display: flex; align-items: center; justify-content: center; margin-right: 15px; font-size: 24px; font-weight: bold; }
.info .name { font-size: 18px; font-weight: bold; }
.info .phone { font-size: 14px; opacity: 0.8; margin-top: 4px; }
.login-link { color: #fff; font-size: 18px; }
.order-nav { margin-top: 10px; }
</style>
