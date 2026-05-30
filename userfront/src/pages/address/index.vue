<template>
  <div class="address-page">
    <van-nav-bar title="收货地址" left-arrow @click-left="$router.back()" />
    <van-empty v-if="!loading && addresses.length === 0" description="暂无收货地址" />
    <div class="address-list">
      <van-swipe-cell v-for="item in addresses" :key="item.id">
        <van-cell :title="item.receiverName + ' ' + item.receiverPhone"
          :label="item.province + item.city + item.district + item.detailAddress"
          @click="onEdit(item)">
          <template #right-icon>
            <van-tag v-if="item.isDefault === 1" type="primary" style="margin-right: 8px;">默认</van-tag>
            <van-tag v-else plain @click.stop="onSetDefault(item.id)">设为默认</van-tag>
          </template>
        </van-cell>
        <template #right>
          <van-button square type="danger" text="删除" @click="onDelete(item.id)" />
        </template>
      </van-swipe-cell>
    </div>
    <div style="padding: 16px;">
      <van-button type="primary" block round @click="showForm = true">新增地址</van-button>
    </div>

    <van-popup v-model:show="showForm" position="bottom" round style="padding: 20px;">
      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <van-field v-model="form.receiverName" label="收件人" placeholder="请输入收件人姓名"
            :rules="[{ required: true, message: '请输入收件人姓名' }]" />
          <van-field v-model="form.receiverPhone" label="手机号" placeholder="请输入手机号"
            :rules="[{ required: true, message: '请输入手机号' }]" />
          <van-field v-model="form.province" label="省份" placeholder="请输入省份" />
          <van-field v-model="form.city" label="城市" placeholder="请输入城市" />
          <van-field v-model="form.district" label="区县" placeholder="请输入区县" />
          <van-field v-model="form.detailAddress" label="详细地址" placeholder="请输入详细地址"
            :rules="[{ required: true, message: '请输入详细地址' }]" />
          <van-cell title="设为默认">
            <template #right-icon>
              <van-switch v-model="isDefaultSwitch" />
            </template>
          </van-cell>
        </van-cell-group>
        <div style="margin-top: 16px;">
          <van-button block type="primary" native-type="submit">保存</van-button>
        </div>
      </van-form>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from 'vant'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '../../api/address'

const addresses = ref([])
const loading = ref(false)
const showForm = ref(false)
const editingId = ref(null)
const form = ref({
  receiverName: '', receiverPhone: '',
  province: '', city: '', district: '', detailAddress: '', isDefault: 0
})
const isDefaultSwitch = computed({
  get: () => form.value.isDefault === 1,
  set: (val) => { form.value.isDefault = val ? 1 : 0 }
})

const loadAddresses = async () => {
  loading.value = true
  try {
    const res = await getAddressList()
    addresses.value = res || []
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

const onEdit = (item) => {
  editingId.value = item.id
  form.value = { ...item }
  showForm.value = true
}

const onSubmit = async () => {
  try {
    if (editingId.value) {
      await updateAddress(editingId.value, form.value)
    } else {
      await addAddress(form.value)
    }
    showToast('保存成功')
    showForm.value = false
    editingId.value = null
    form.value = { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '', isDefault: 0 }
    loadAddresses()
  } catch (e) {
    showToast(e.message || '保存失败')
  }
}

const onDelete = async (id) => {
  try {
    await deleteAddress(id)
    showToast('已删除')
    loadAddresses()
  } catch (e) {
    showToast('删除失败')
  }
}

const onSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    showToast('已设为默认')
    loadAddresses()
  } catch (e) {
    showToast('操作失败')
  }
}

onMounted(loadAddresses)
</script>

<style scoped>
.address-page { min-height: 100vh; background: #f5f5f5; }
.address-list { padding: 10px; }
</style>
