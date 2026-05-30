<template>
  <div class="merchant-page">
    <van-nav-bar title="商家中心" left-arrow @click-left="$router.back()" />

    <div v-if="loading" style="text-align: center; padding: 40px;">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <template v-else-if="merchant">
      <!-- 审核中 -->
      <van-notice-bar v-if="merchant.status === 0" color="#e6a23c" background="#fdf6ec" left-icon="info-o" wrapable>
        您的入驻申请正在审核中，请耐心等待。
      </van-notice-bar>
      <!-- 已拒绝 -->
      <template v-else-if="merchant.status === 2">
        <van-notice-bar color="#f56c6c" background="#fef0f0" left-icon="close" wrapable>
          审核未通过：{{ merchant.auditRemark || '未说明原因' }}
        </van-notice-bar>
        <div style="padding: 16px;">
          <van-button round block type="primary" @click="$router.push('/merchant/apply')">重新申请</van-button>
        </div>
      </template>
      <!-- 已禁用 -->
      <van-notice-bar v-else-if="merchant.status === 3" color="#909399" background="#f4f4f5" left-icon="warning-o" wrapable>
        您的商家账号已被禁用，请联系管理员。
      </van-notice-bar>

      <!-- 已通过：显示店铺管理 -->
      <template v-if="merchant.status === 1">
        <van-cell-group inset style="margin-top: 10px">
          <van-cell icon="shop-o" :title="merchant.shopName" :label="'营业执照: ' + merchant.licenseNo" />
        </van-cell-group>
        <van-cell-group inset style="margin-top: 10px;">
          <van-cell title="编辑店铺信息" is-link @click="showEditDialog" />
          <van-cell title="商品管理" is-link to="/merchant/products" />
          <van-cell title="订单管理" is-link @click="showDeveloping" />
        </van-cell-group>
      </template>

      <!-- 待审核/已拒绝/已禁用：显示基本信息 -->
      <template v-if="merchant.status !== 1">
        <van-cell-group inset style="margin-top: 10px;">
          <van-cell title="店铺名称" :value="merchant.shopName" />
          <van-cell title="联系人" :value="merchant.contactName" />
          <van-cell title="联系电话" :value="merchant.contactPhone" />
          <van-cell title="店铺简介" :value="merchant.description || '暂无'" />
          <van-cell title="申请时间" :value="merchant.createTime" />
        </van-cell-group>
      </template>
    </template>

    <van-empty v-else description="您还不是商家">
      <van-button round type="primary" @click="$router.push('/merchant/apply')">申请入驻</van-button>
    </van-empty>

    <!-- 编辑店铺信息弹窗 -->
    <van-dialog v-model:show="editVisible" title="编辑店铺信息" show-cancel-button @confirm="saveEdit">
      <van-field v-model="editForm.shopName" label="店铺名称" placeholder="请输入店铺名称" />
      <van-field v-model="editForm.contactName" label="联系人" placeholder="请输入联系人" />
      <van-field v-model="editForm.contactPhone" label="联系电话" placeholder="请输入联系电话" />
      <van-field v-model="editForm.description" label="店铺简介" type="textarea" rows="3" placeholder="请输入店铺简介" />
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import { getMyMerchant, updateMyMerchant } from '../../api/merchant'

const merchant = ref(null)
const loading = ref(true)
const editVisible = ref(false)
const editForm = ref({ shopName: '', contactName: '', contactPhone: '', description: '' })

async function loadMerchant() {
  loading.value = true
  try {
    const res = await getMyMerchant()
    merchant.value = res
  } catch (e) {
    merchant.value = null
  } finally {
    loading.value = false
  }
}

function showEditDialog() {
  editForm.value = {
    shopName: merchant.value.shopName || '',
    contactName: merchant.value.contactName || '',
    contactPhone: merchant.value.contactPhone || '',
    description: merchant.value.description || ''
  }
  editVisible.value = true
}

async function saveEdit() {
  try {
    await updateMyMerchant(editForm.value)
    Object.assign(merchant.value, editForm.value)
    showToast('保存成功')
  } catch (e) {
    showToast('保存失败')
  }
}

function showDeveloping() {
  showToast('功能开发中')
}

onMounted(() => loadMerchant())
</script>

<style scoped>
.merchant-page { min-height: 100vh; background: #f5f5f5; }
</style>
