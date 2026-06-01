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
import { onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { showToast } from 'vant'
import { useUserStore } from '../../stores/user'
import { getUserInfo } from '../../api/user'
import { logout } from '../../api/auth'

const userStore = useUserStore()
const router = useRouter()

// GSAP入场动画
const playEnterAnimations = () => {
  nextTick(() => {
    const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })

    // 头部区域淡入并轻微上移
    tl.from('.user-header', {
      y: -30,
      opacity: 0,
      duration: 0.6
    })

    // 头像弹入
    tl.from('.avatar', {
      scale: 0,
      opacity: 0,
      duration: 0.5,
      ease: 'back.out(2)'
    }, '-=0.3')

    // 用户信息滑入
    tl.from('.info', {
      x: -30,
      opacity: 0,
      duration: 0.4
    }, '-=0.2')

    // 订单导航网格依次出现
    tl.from('.order-nav .van-grid-item', {
      y: 30,
      opacity: 0,
      duration: 0.4,
      stagger: 0.08
    }, '-=0.2')

    // 功能菜单滑入
    tl.from('.van-cell-group:last-of-type .van-cell', {
      x: -40,
      opacity: 0,
      duration: 0.4,
      stagger: 0.1
    }, '-=0.2')

    // 退出按钮淡入
    tl.from('.van-button', {
      y: 20,
      opacity: 0,
      duration: 0.4
    }, '-=0.2')
  })
}

const onLogout = async () => {
  // 退出动画
  gsap.to('.user-page', {
    scale: 0.95,
    opacity: 0,
    duration: 0.4,
    ease: 'power2.in',
    onComplete: async () => {
      try {
        await logout()
      } catch (e) {
        // ignore
      }
      userStore.logout()
      showToast('已退出')
      router.push('/login')
    }
  })
}

onMounted(async () => {
  playEnterAnimations()
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
.user-page {
  min-height: 100vh;
  background: #f5f5f5;
}
.user-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 35px 20px;
  display: flex;
  align-items: center;
  color: #fff;
  position: relative;
  overflow: hidden;
}
.user-header::before {
  content: '';
  position: absolute;
  top: -30%;
  right: -10%;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}
.user-header::after {
  content: '';
  position: absolute;
  bottom: -40%;
  left: 10%;
  width: 150px;
  height: 150px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}
.avatar {
  width: 65px;
  height: 65px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 26px;
  font-weight: bold;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  border: 3px solid rgba(255, 255, 255, 0.4);
  transition: transform 0.3s ease;
}
.avatar:hover { transform: scale(1.05) rotate(5deg); }
.info { position: relative; z-index: 1; }
.info .name { font-size: 20px; font-weight: bold; letter-spacing: 1px; }
.info .phone { font-size: 14px; opacity: 0.8; margin-top: 6px; }
.login-link {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}
.order-nav {
  margin-top: 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}
.van-cell-group {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.van-cell {
  transition: background 0.2s ease;
}
.van-cell:active {
  background: #f5f5f5;
}
.van-button {
  border-radius: 8px;
  font-weight: 600;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.van-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(238, 10, 36, 0.3);
}
</style>
