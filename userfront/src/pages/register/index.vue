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
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../../api/auth'
import { useUserStore } from '../../stores/user'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ phone: '', password: '', confirmPassword: '' })

async function onSubmit() {
  if (form.password !== form.confirmPassword) {
    showToast('两次密码不一致')
    return
  }
  try {
    const data = await register({ phone: form.phone, password: form.password })
    userStore.setToken(data.token)
    userStore.setUserInfo(data)
    showToast('注册成功')
    router.replace('/')
  } catch (e) {
    showToast(e.message || '注册失败')
  }
}
</script>

<style scoped>
.register-page { min-height: 100vh; background: #fff; padding-top: 60px; }
.logo { text-align: center; font-size: 24px; font-weight: bold; margin-bottom: 30px; }
.links { text-align: center; margin-top: 20px; }
.links a { color: #1989fa; font-size: 14px; }
</style>
