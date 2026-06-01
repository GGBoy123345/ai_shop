<template>
  <div class="login-page">
    <div class="logo">
      <div class="logo-icon">🛒</div>
      <div class="logo-text">AI商城</div>
    </div>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="form.phone" label="手机号" placeholder="请输入手机号" :rules="[{ required: true }]" />
        <van-field v-model="form.password" type="password" label="密码" placeholder="请输入密码" :rules="[{ required: true }]" />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">登录</van-button>
      </div>
    </van-form>
    <div class="links">
      <router-link to="/register">注册账号</router-link>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { useUserStore } from '../../stores/user'
import { login } from '../../api/auth'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ phone: '', password: '' })
const logoRef = ref(null)
const formRef = ref(null)
const linksRef = ref(null)

// GSAP入场动画
onMounted(() => {
  const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })

  // Logo弹入效果
  tl.from('.logo', {
    scale: 0,
    opacity: 0,
    duration: 0.8,
    ease: 'back.out(1.7)'
  })

  // Logo文字逐字显示
  tl.from('.logo-text', {
    y: 20,
    opacity: 0,
    duration: 0.5,
    stagger: 0.05
  }, '-=0.3')

  // 表单从下方滑入
  tl.from('.van-form', {
    y: 60,
    opacity: 0,
    duration: 0.6
  }, '-=0.2')

  // 表单项依次出现
  tl.from('.van-cell', {
    x: -30,
    opacity: 0,
    duration: 0.4,
    stagger: 0.15
  }, '-=0.3')

  // 按钮弹入
  tl.from('.van-button', {
    scale: 0.8,
    opacity: 0,
    duration: 0.5,
    ease: 'back.out(1.5)'
  }, '-=0.2')

  // 链接淡入
  tl.from('.links', {
    y: 20,
    opacity: 0,
    duration: 0.4
  }, '-=0.2')
})

async function onSubmit() {
  try {
    const data = await login(form)
    userStore.setToken(data.token)
    userStore.setUserInfo(data)

    // 登录成功动画
    gsap.to('.login-page', {
      scale: 1.05,
      opacity: 0,
      duration: 0.5,
      ease: 'power2.in',
      onComplete: () => {
        showToast('登录成功')
        router.replace('/')
      }
    })
  } catch (e) {
    // 错误抖动动画
    gsap.to('.van-form', {
      x: [-10, 10, -10, 10, 0],
      duration: 0.4,
      ease: 'power2.inOut'
    })
    showToast(e.message || '登录失败')
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 80px;
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

.logo {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 10px;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.2));
}

.logo-text {
  font-size: 32px;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0,0,0,0.2);
  letter-spacing: 2px;
}

.van-form {
  position: relative;
  z-index: 1;
}

.van-cell-group {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.van-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.van-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.van-button:active {
  transform: translateY(0);
}

.links {
  text-align: center;
  margin-top: 24px;
  position: relative;
  z-index: 1;
}

.links a {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  text-decoration: none;
  transition: color 0.2s ease;
}

.links a:hover {
  color: #fff;
  text-decoration: underline;
}
</style>
