<template>
  <div class="product-edit">
    <van-nav-bar :title="isEdit ? '编辑商品' : '发布商品'" left-arrow @click-left="$router.back()" />

    <van-form @submit="onSubmit">
      <van-cell-group inset title="基本信息" style="margin-top: 10px;">
        <van-field v-model="form.title" label="商品名称" placeholder="请输入商品名称" :rules="[{ required: true }]" />
        <van-field v-model="form.subtitle" label="副标题" placeholder="请输入副标题" />
        <van-field v-model="form.price" label="价格" type="number" placeholder="0.00" :rules="[{ required: true }]" />
        <van-field label="商品主图" name="mainImage">
          <template #input>
            <van-uploader v-model="imageList" :max-count="1" :after-read="onImageUpload" :before-delete="onImageDelete" accept="image/*" />
          </template>
        </van-field>
        <van-field v-model="form.mainImage" label="主图地址" placeholder="上传后自动填入" readonly />
        <van-field v-model="form.categoryId" label="分类ID" type="number" placeholder="请输入分类ID" :rules="[{ required: true }]" />
      </van-cell-group>

      <van-cell-group inset title="商品描述" style="margin-top: 10px;">
        <van-field v-model="form.description" label="商品描述" type="textarea" rows="4" placeholder="请输入商品详细描述" />
      </van-cell-group>

      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit" :loading="submitting">
          {{ isEdit ? '保存修改' : '提交发布' }}
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { addProduct, updateProduct, getProductDetail } from '../../api/merchant-product'
import { uploadImage } from '../../api/file'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const productId = ref(null)
const submitting = ref(false)
const imageList = ref([])
const form = ref({
  title: '', subtitle: '', price: '', mainImage: '',
  categoryId: '', description: ''
})

async function onImageUpload(file) {
  file.status = 'uploading'
  file.message = '上传中...'
  try {
    const res = await uploadImage(file.file)
    form.value.mainImage = res.url
    file.status = 'done'
    file.message = ''
    showToast('上传成功')
  } catch (e) {
    file.status = 'failed'
    file.message = '上传失败'
    showToast('图片上传失败')
  }
}

function onImageDelete() {
  form.value.mainImage = ''
  imageList.value = []
  return true
}

onMounted(async () => {
  if (route.query.id) {
    isEdit.value = true
    productId.value = route.query.id
    try {
      const res = await getProductDetail(route.query.id)
      if (res) {
        form.value = {
          title: res.title || '',
          subtitle: res.subtitle || '',
          price: res.price || '',
          mainImage: res.mainImage || '',
          categoryId: res.categoryId || '',
          description: res.description || ''
        }
        if (res.mainImage) {
          imageList.value = [{ url: res.mainImage }]
        }
      }
    } catch (e) {
      showToast('加载失败')
    }
  }
})

async function onSubmit() {
  submitting.value = true
  try {
    const data = { ...form.value, price: Number(form.value.price), categoryId: Number(form.value.categoryId) }
    if (isEdit.value) {
      await updateProduct(productId.value, data)
      showToast('修改成功')
    } else {
      await addProduct(data)
      showToast('发布成功，等待审核')
    }
    router.back()
  } catch (e) {
    showToast(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.product-edit { min-height: 100vh; background: #f5f5f5; }
</style>
