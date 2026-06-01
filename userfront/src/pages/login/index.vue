<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- Logo区域 -->
    <div class="logo-section">
      <div class="logo-icon">🛒</div>
      <div class="logo-text">AI商城</div>
      <div class="logo-subtitle">智能购物，品质生活</div>
    </div>

    <!-- 表单区域 -->
    <div class="form-section">
      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <div class="input-wrapper">
            <van-field
              v-model="form.phone"
              label="手机号"
              placeholder="请输入手机号"
              :rules="[{ required: true }]"
              @focus="onInputFocus"
              @blur="onInputBlur"
            />
          </div>
          <div class="input-wrapper">
            <van-field
              v-model="form.password"
              type="password"
              label="密码"
              placeholder="请输入密码"
              :rules="[{ required: true }]"
              @focus="onInputFocus"
              @blur="onInputBlur"
            />
          </div>
        </van-cell-group>
        <div class="button-wrapper">
          <van-button round block type="primary" native-type="submit" class="login-btn">
            登录
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 底部链接 -->
    <div class="links-section">
      <router-link to="/register" class="link-item">注册账号</router-link>
      <span class="link-divider">|</span>
      <router-link to="/forgot" class="link-item">忘记密码</router-link>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { useUserStore } from '../../stores/user'
import { login } from '../../api/auth'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ phone: '', password: '' })

// 输入框聚焦放大
const onInputFocus = (e) => {
  const wrapper = e.target.closest('.input-wrapper')
  if (wrapper) {
    gsap.to(wrapper, { scale: 1.02, duration: 0.25, ease: 'power2.out' })
  }
}

// 输入框失焦恢复
const onInputBlur = (e) => {
  const wrapper = e.target.closest('.input-wrapper')
  if (wrapper) {
    gsap.to(wrapper, { scale: 1, duration: 0.25, ease: 'power2.out' })
  }
}

// GSAP统一入场动画
onMounted(() => {
  const allElements = '.logo-section, .logo-icon, .logo-text, .logo-subtitle, .form-section, .input-wrapper, .button-wrapper, .links-section'

  gsap.fromTo(allElements,
    { x: 40, opacity: 0 },
    { x: 0, opacity: 1, duration: 0.9, ease: 'power2.out' }
  )
})

async function onSubmit() {
  try {
    const data = await login(form)
    userStore.setToken(data.token)
    userStore.setUserInfo(data)
    showToast('登录成功')
    router.replace('/')
  } catch (e) {
    showToast(e.message || '登录失败')
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(145deg, #f8f9ff 0%, #f0f2ff 50%, #e8ebff 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -80px;
  animation: float-slow 8s ease-in-out infinite;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -60px;
  left: -60px;
  animation: float-slow 6s ease-in-out infinite reverse;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 40%;
  left: 60%;
  animation: float-slow 10s ease-in-out infinite;
}

@keyframes float-slow {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(10px, -15px) rotate(2deg); }
  66% { transform: translate(-8px, 12px) rotate(-1deg); }
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 50px;
  position: relative;
  z-index: 1;
}

.logo-icon {
  font-size: 56px;
  margin-bottom: 16px;
  filter: drop-shadow(0 8px 16px rgba(102, 126, 234, 0.3));
  display: inline-block;
}

.logo-text {
  font-size: 36px;
  font-weight: 700;
  color: #2d3748;
  letter-spacing: 3px;
  margin-bottom: 8px;
}

.logo-subtitle {
  font-size: 14px;
  color: #718096;
  letter-spacing: 2px;
  font-weight: 400;
}

/* 表单区域 */
.form-section {
  width: 100%;
  max-width: 380px;
  position: relative;
  z-index: 1;
}

.van-cell-group {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  overflow: visible;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
}

.input-wrapper {
  border-radius: 12px;
  border: 2px solid transparent;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  margin: 8px;
  background: #fff;
  transform-origin: center;
}

.input-wrapper:focus-within {
  border-color: rgba(102, 126, 234, 0.4);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-wrapper:first-child {
  margin-top: 12px;
}

.input-wrapper:last-child {
  margin-bottom: 12px;
}

.input-wrapper :deep(.van-cell) {
  background: transparent;
  padding: 14px 16px;
}

.input-wrapper :deep(.van-field__label) {
  color: #4a5568;
  font-weight: 500;
  width: 60px;
}

.input-wrapper :deep(.van-field__control) {
  color: #2d3748;
  font-size: 15px;
}

.input-wrapper :deep(.van-field__control::placeholder) {
  color: #a0aec0;
}

.button-wrapper {
  margin: 24px 16px 16px;
}

.login-btn {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.35);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: center;
}

.login-btn:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
  background: linear-gradient(135deg, #5a6fe6 0%, #6a3f9a 100%);
}

.login-btn:active {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
}

/* 底部链接 */
.links-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  position: relative;
  z-index: 1;
}

.link-item {
  color: #667eea;
  font-size: 14px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
  position: relative;
}

.link-item::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  transition: width 0.3s ease;
  border-radius: 1px;
}

.link-item:hover {
  color: #5a6fe6;
}

.link-item:hover::after {
  width: 100%;
}

.link-divider {
  color: #cbd5e0;
  font-size: 12px;
}

/* 响应式调整 */
@media (max-width: 400px) {
  .logo-text {
    font-size: 28px;
  }

  .logo-icon {
    font-size: 48px;
  }

  .form-section {
    max-width: 100%;
  }
}
</style>
