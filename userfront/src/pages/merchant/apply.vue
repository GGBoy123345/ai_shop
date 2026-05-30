<template>
  <div class="merchant-apply">
    <van-nav-bar title="商家入驻" left-arrow @click-left="$router.back()" />

    <van-form @submit="onSubmit" v-if="!merchant">
      <van-cell-group inset style="margin-top: 10px">
        <van-field v-model="form.shopName" label="店铺名称" placeholder="请输入店铺名称" :rules="[{ required: true }]" />
        <van-field v-model="form.contactName" label="联系人" placeholder="请输入联系人姓名" />
        <van-field v-model="form.contactPhone" label="联系电话" placeholder="请输入联系电话" :rules="[{ required: true }]" />
        <van-field v-model="form.licenseNo" label="营业执照号" placeholder="请输入营业执照编号" :rules="[{ required: true }]" />
        <van-field label="店铺Logo" name="logo">
          <template #input>
            <van-uploader v-model="logoList" :max-count="1" :after-read="onLogoUpload" :before-delete="onLogoDelete" accept="image/*" />
          </template>
        </van-field>
        <van-field v-model="form.logo" label="Logo地址" placeholder="上传后自动填入" readonly />
        <van-field v-model="form.description" label="店铺简介" type="textarea" rows="3" placeholder="请输入店铺简介" />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit" :loading="submitting">提交申请</van-button>
      </div>
    </van-form>

    <div v-else style="padding: 20px;">
      <van-notice-bar v-if="merchant.status === 0" color="#e6a23c" background="#fdf6ec" left-icon="info-o" wrapable>
        您的入驻申请正在审核中，请耐心等待。
      </van-notice-bar>
      <van-notice-bar v-else-if="merchant.status === 1" color="#67c23a" background="#f0f9eb" left-icon="passed" wrapable>
        您的入驻申请已通过！
      </van-notice-bar>
      <van-notice-bar v-else-if="merchant.status === 2" color="#f56c6c" background="#fef0f0" left-icon="close" wrapable>
        审核未通过：{{ merchant.auditRemark || '未说明原因' }}
      </van-notice-bar>
      <van-notice-bar v-else-if="merchant.status === 3" color="#909399" background="#f4f4f5" left-icon="warning-o" wrapable>
        您的商家账号已被禁用。
      </van-notice-bar>

      <van-cell-group inset style="margin-top: 16px">
        <van-cell title="店铺名称" :value="merchant.shopName" />
        <van-cell title="联系人" :value="merchant.contactName" />
        <van-cell title="联系电话" :value="merchant.contactPhone" />
        <van-cell title="营业执照号" :value="merchant.licenseNo" />
        <van-cell title="店铺简介" :value="merchant.description || '无'" />
        <van-cell title="申请时间" :value="merchant.createTime" />
      </van-cell-group>

      <div v-if="merchant.status === 2" style="margin: 16px;">
        <van-button round block type="primary" @click="resetForm">重新申请</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { applyMerchant, getMyMerchant } from '../../api/merchant'
import { uploadImage } from '../../api/file'

const router = useRouter()
const merchant = ref(null)
const submitting = ref(false)
const logoList = ref([])
const form = ref({
  shopName: '', contactName: '', contactPhone: '',
  licenseNo: '', logo: '', description: ''
})

async function onLogoUpload(file) {
  file.status = 'uploading'
  file.message = '上传中...'
  try {
    const res = await uploadImage(file.file)
    form.value.logo = res.url
    file.status = 'done'
    file.message = ''
    showToast('上传成功')
  } catch (e) {
    file.status = 'failed'
    file.message = '上传失败'
    showToast('图片上传失败')
  }
}

function onLogoDelete() {
  form.value.logo = ''
  logoList.value = []
  return true
}

async function loadMerchant() {
  try {
    const res = await getMyMerchant()
    merchant.value = res
  } catch (e) {
    // 没有商家信息，显示申请表单
  }
}

async function onSubmit() {
  submitting.value = true
  try {
    await applyMerchant(form.value)
    showToast('申请提交成功')
    loadMerchant()
  } catch (e) {
    showToast(e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  merchant.value = null
}

onMounted(() => loadMerchant())
</script>

<style scoped>
.merchant-apply { min-height: 100vh; background: #f5f5f5; }
</style>
