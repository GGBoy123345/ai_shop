<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">AI商城管理后台</h2>
      <el-form :model="form" @submit.prevent="onSubmit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="管理员账号" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit" :loading="loading" style="width: 100%;">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '../../stores/user'
import { adminLogin } from '../../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const adminStore = useAdminStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function onSubmit() {
  loading.value = true
  try {
    const data = await adminLogin(form)
    adminStore.setToken(data.token)
    adminStore.setUserInfo(data)
    ElMessage.success('登录成功')
    router.replace('/dashboard')
  } catch (e) {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container { height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-card { width: 400px; padding: 20px; }
.title { text-align: center; margin-bottom: 30px; color: #333; }
</style>
