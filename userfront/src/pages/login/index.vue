<template>
  <div class="login-page">
    <div class="logo">AI商城</div>
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
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { login } from '../../api/auth'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ phone: '', password: '' })

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
.login-page { min-height: 100vh; background: #fff; padding-top: 80px; }
.logo { text-align: center; font-size: 28px; font-weight: bold; color: #1989fa; margin-bottom: 40px; }
.links { text-align: center; margin-top: 20px; }
.links a { color: #1989fa; font-size: 14px; }
</style>
