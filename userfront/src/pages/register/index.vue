<template>
  <div class="register-page">
    <div class="logo">注册账号</div>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="form.phone" label="手机号" placeholder="请输入手机号" :rules="[{ required: true }]" />
        <van-field v-model="form.password" type="password" label="密码" placeholder="请输入密码" :rules="[{ required: true }]" />
        <van-field v-model="form.confirmPassword" type="password" label="确认密码" placeholder="请再次输入密码" :rules="[{ required: true }]" />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">注册</van-button>
      </div>
    </van-form>
    <div class="links">
      <router-link to="/login">已有账号？去登录</router-link>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { register } from '../../api/auth'
import { useUserStore } from '../../stores/user'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ phone: '', password: '', confirmPassword: '' })

// GSAP入场动画
onMounted(() => {
  const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })

  // Logo弹入
  tl.from('.logo', {
    scale: 0.5,
    opacity: 0,
    duration: 0.6,
    ease: 'back.out(1.7)'
  })

  // 表单滑入
  tl.from('.van-form', {
    y: 50,
    opacity: 0,
    duration: 0.5
  }, '-=0.2')

  // 表单项依次出现
  tl.from('.van-cell', {
    x: -30,
    opacity: 0,
    duration: 0.4,
    stagger: 0.12
  }, '-=0.3')

  // 按钮弹入
  tl.from('.van-button', {
    scale: 0.8,
    opacity: 0,
    duration: 0.4,
    ease: 'back.out(1.5)'
  }, '-=0.2')

  // 链接淡入
  tl.from('.links', {
    y: 15,
    opacity: 0,
    duration: 0.3
  }, '-=0.1')
})

async function onSubmit() {
  if (form.password !== form.confirmPassword) {
    // 密码不一致抖动动画
    gsap.to('.van-cell:last-child', {
      x: [-8, 8, -8, 8, 0],
      duration: 0.4,
      ease: 'power2.inOut'
    })
    showToast('两次密码不一致')
    return
  }
  try {
    const data = await register({ phone: form.phone, password: form.password })
    userStore.setToken(data.token)
    userStore.setUserInfo(data)

    // 注册成功动画
    gsap.to('.register-page', {
      scale: 1.05,
      opacity: 0,
      duration: 0.5,
      ease: 'power2.in',
      onComplete: () => {
        showToast('注册成功')
        router.replace('/')
      }
    })
  } catch (e) {
    // 错误抖动
    gsap.to('.van-form', {
      x: [-10, 10, -10, 10, 0],
      duration: 0.4,
      ease: 'power2.inOut'
    })
    showToast(e.message || '注册失败')
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-top: 60px;
  position: relative;
  overflow: hidden;
}

.register-page::before {
  content: '';
  position: absolute;
  top: 20%;
  right: -10%;
  width: 250px;
  height: 250px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
  animation: float 8s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

.logo {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 35px;
  position: relative;
  z-index: 1;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
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
