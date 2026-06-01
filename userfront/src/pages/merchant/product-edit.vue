<template>
  <div class="product-edit">
    <van-nav-bar :title="isEdit ? '编辑商品' : '发布商品'" left-arrow @click-left="$router.back()" />

    <van-form @submit="onSubmit">
      <van-cell-group inset title="基本信息" style="margin-top: 10px;">
        <van-field v-model="form.title" label="商品名称" placeholder="请输入商品名称" :rules="[{ required: true }]" />
        <van-field v-model="form.subtitle" label="副标题" placeholder="请输入副标题" />
        <van-field v-model="form.price" label="价格" type="number" placeholder="0.00" :rules="[{ required: true }]" />
        <van-field v-model="form.originalPrice" label="市场价" type="number" placeholder="0.00" />
        <van-field v-model="form.costPrice" label="成本价" type="number" placeholder="0.00" />
        <van-field v-model="form.weight" label="重量(kg)" type="number" placeholder="0.00" />
        <van-field label="商品视频" name="video">
          <template #input>
            <div class="video-upload-wrap">
              <van-uploader v-model="videoList" :max-count="1" :after-read="onVideoUpload" :before-delete="onVideoDelete" accept="video/*" />
              <div v-if="videoList.length > 0 && videoList[0].status === 'uploading'" class="upload-tip">上传中...</div>
            </div>
          </template>
        </van-field>
        <van-field label="商品主图" name="mainImage">
          <template #input>
            <van-uploader v-model="imageList" :max-count="1" :after-read="onImageUpload" :before-delete="onImageDelete" accept="image/*" />
          </template>
        </van-field>
        <van-field
          v-model="form.categoryId"
          label="商品分类"
          placeholder="请选择商品分类"
          readonly
          is-link
          @click="openCategoryPicker"
          :rules="[{ required: true, message: '请选择商品分类' }]"
        >
          <template #input>
            <span :style="{ color: selectedCategoryName === '请选择商品分类' ? '#c8c9cc' : '#323233' }">
              {{ selectedCategoryName }}
            </span>
          </template>
        </van-field>
      </van-cell-group>

      <!-- 商品属性（根据分类动态显示） -->
      <van-cell-group v-if="attributeTemplates.length > 0" inset title="商品属性" style="margin-top: 10px;">
        <template v-for="template in attributeTemplates" :key="template.id">
          <!-- text 类型 -->
          <van-field
            v-if="template.inputType === 'text'"
            v-model="attributes[template.id]"
            :label="template.name"
            :placeholder="'请输入' + template.name"
            :required="template.required === 1"
            :rules="template.required === 1 ? [{ required: true, message: '请输入' + template.name }] : []"
          />
          <!-- textarea 类型 -->
          <van-field
            v-else-if="template.inputType === 'textarea'"
            v-model="attributes[template.id]"
            :label="template.name"
            :placeholder="'请输入' + template.name"
            :required="template.required === 1"
            type="textarea"
            :rows="3"
            :rules="template.required === 1 ? [{ required: true, message: '请输入' + template.name }] : []"
          />
          <!-- select 类型 -->
          <van-field
            v-else-if="template.inputType === 'select'"
            :model-value="attributes[template.id]"
            :label="template.name"
            :placeholder="'请选择' + template.name"
            :required="template.required === 1"
            readonly
            is-link
            @click="openSelectPicker(template)"
          />
          <!-- multi_select 类型 -->
          <van-field
            v-else-if="template.inputType === 'multi_select'"
            :model-value="getMultiSelectDisplay(template.id)"
            :label="template.name"
            :placeholder="'请选择' + template.name"
            :required="template.required === 1"
            readonly
            is-link
            @click="openMultiSelectPopup(template)"
          >
            <template #input>
              <div class="multi-select-tags" v-if="attributes[template.id]">
                <van-tag v-for="val in attributes[template.id].split(',')" :key="val" type="primary" size="medium">{{ val }}</van-tag>
              </div>
              <span v-else style="color: #c8c9cc;">请选择{{ template.name }}</span>
            </template>
          </van-field>
        </template>
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

    <!-- 下拉选择弹窗 -->
    <van-popup v-model:show="showSelectPicker" position="bottom" round>
      <van-picker
        :columns="selectPickerColumns"
        @confirm="onSelectPickerConfirm"
        @cancel="showSelectPicker = false"
      />
    </van-popup>

    <!-- 多选弹窗 -->
    <van-popup v-model:show="showMultiSelectPopup" position="bottom" round style="max-height: 60%;">
      <div class="multi-select-popup">
        <div class="picker-header">
          <van-button size="small" @click="showMultiSelectPopup = false">取消</van-button>
          <span>{{ multiSelectTitle }}</span>
          <van-button size="small" type="primary" @click="confirmMultiSelect">确认</van-button>
        </div>
        <van-checkbox-group v-model="tempMultiSelectValues">
          <van-cell-group>
            <van-cell v-for="opt in multiSelectOptions" :key="opt.value" :title="opt.value" clickable
              @click="toggleMultiSelect(opt.value)">
              <template #right-icon>
                <van-checkbox :name="opt.value" />
              </template>
            </van-cell>
          </van-cell-group>
        </van-checkbox-group>
      </div>
    </van-popup>

    <!-- 分类选择弹窗 -->
    <van-popup v-model:show="showCategoryPicker" position="bottom" round style="height: 60%;">
      <div class="category-picker">
        <div class="picker-header">
          <van-button size="small" @click="showCategoryPicker = false">取消</van-button>
          <span>选择商品分类</span>
          <van-button size="small" type="primary" @click="confirmCategory">确认</van-button>
        </div>
        <van-tree-select
          :active-id="tempCategoryId"
          v-model:main-active-index="mainActiveIndex"
          :items="categoryItems"
          @click-nav="onNavClick"
          @click-item="onItemClick"
        />
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { addProduct, updateProduct, getProductDetail, getAttributeTemplates } from '../../api/merchant-product'
import { uploadImage, uploadVideo } from '../../api/file'
import { getCategoryTree } from '../../api/product'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const productId = ref(null)
const submitting = ref(false)
const imageList = ref([])
const videoList = ref([])
const categoryList = ref([])
const showCategoryPicker = ref(false)
const attributeTemplates = ref([])
const attributes = ref({})

// 下拉选择器
const showSelectPicker = ref(false)
const selectPickerColumns = ref([])
const currentSelectTemplate = ref(null)

// 多选弹窗
const showMultiSelectPopup = ref(false)
const multiSelectTitle = ref('')
const multiSelectOptions = ref([])
const tempMultiSelectValues = ref([])
const currentMultiSelectTemplate = ref(null)

const form = ref({
  title: '', subtitle: '', price: '', originalPrice: '', costPrice: '',
  weight: '', video: '', mainImage: '',
  categoryId: '', description: ''
})

// 将树形分类转换为 TreeSelect 需要的格式
const categoryItems = computed(() => {
  return convertToTreeSelectItems(categoryList.value)
})

// 选中的分类名称
const selectedCategoryName = computed(() => {
  if (!form.value.categoryId) return '请选择商品分类'
  return findCategoryName(categoryList.value, form.value.categoryId) || '请选择商品分类'
})

function convertToTreeSelectItems(tree) {
  return tree.map(item => ({
    text: item.name,
    id: item.id,
    children: (item.children && item.children.length > 0)
      ? convertToTreeSelectItems(item.children)
      : undefined
  }))
}

function findCategoryName(tree, id) {
  for (const item of tree) {
    if (item.id === id) return item.name
    if (item.children) {
      const found = findCategoryName(item.children, id)
      if (found) return found
    }
  }
  return null
}

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

async function onVideoUpload(file) {
  file.status = 'uploading'
  file.message = '上传中...'
  try {
    const res = await uploadVideo(file.file)
    form.value.video = res.url
    file.status = 'done'
    file.message = ''
    showToast('视频上传成功')
  } catch (e) {
    file.status = 'failed'
    file.message = '上传失败'
    showToast('视频上传失败')
  }
}

function onVideoDelete() {
  form.value.video = ''
  videoList.value = []
  return true
}

const mainActiveIndex = ref(0)
const tempCategoryId = ref(null)

function onNavClick(index) {
  mainActiveIndex.value = index
}

function onItemClick(item) {
  tempCategoryId.value = item.id
}

function confirmCategory() {
  if (tempCategoryId.value) {
    form.value.categoryId = tempCategoryId.value
  }
  showCategoryPicker.value = false
}

// 打开分类选择弹窗
function openCategoryPicker() {
  tempCategoryId.value = form.value.categoryId || null
  showCategoryPicker.value = true
}

// --- 下拉选择 ---
function openSelectPicker(template) {
  currentSelectTemplate.value = template
  selectPickerColumns.value = (template.options || []).map(opt => ({
    text: opt.value,
    value: opt.value
  }))
  showSelectPicker.value = true
}

function onSelectPickerConfirm({ selectedValues }) {
  if (currentSelectTemplate.value) {
    attributes.value[currentSelectTemplate.value.id] = selectedValues[0]
  }
  showSelectPicker.value = false
}

// --- 多选 ---
function openMultiSelectPopup(template) {
  currentMultiSelectTemplate.value = template
  multiSelectTitle.value = template.name
  multiSelectOptions.value = template.options || []
  const currentVal = attributes.value[template.id]
  tempMultiSelectValues.value = currentVal ? currentVal.split(',') : []
  showMultiSelectPopup.value = true
}

function toggleMultiSelect(value) {
  const idx = tempMultiSelectValues.value.indexOf(value)
  if (idx > -1) {
    tempMultiSelectValues.value.splice(idx, 1)
  } else {
    tempMultiSelectValues.value.push(value)
  }
}

function confirmMultiSelect() {
  if (currentMultiSelectTemplate.value) {
    attributes.value[currentMultiSelectTemplate.value.id] = tempMultiSelectValues.value.join(',')
  }
  showMultiSelectPopup.value = false
}

function getMultiSelectDisplay(templateId) {
  return attributes.value[templateId] || ''
}

// 加载分类对应的属性模板
async function loadAttributeTemplates(categoryId) {
  if (!categoryId) {
    attributeTemplates.value = []
    attributes.value = {}
    return
  }
  try {
    const res = await getAttributeTemplates(categoryId)
    if (res) {
      attributeTemplates.value = res
      // 初始化属性值对象
      const newAttrs = {}
      res.forEach(t => {
        // 保留已有的属性值
        newAttrs[t.id] = attributes.value[t.id] || ''
      })
      attributes.value = newAttrs
    }
  } catch (e) {
    console.error('加载属性模板失败:', e)
    attributeTemplates.value = []
    attributes.value = {}
  }
}

// 监听分类变化，自动加载属性模板
watch(() => form.value.categoryId, (newCategoryId) => {
  loadAttributeTemplates(newCategoryId)
})

onMounted(async () => {
  // 加载分类列表
  try {
    const res = await getCategoryTree()
    if (res) {
      categoryList.value = res
    }
  } catch (e) {
    console.error('加载分类失败:', e)
  }
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
          originalPrice: res.originalPrice || '',
          costPrice: res.costPrice || '',
          weight: res.weight || '',
          video: res.video || '',
          mainImage: res.mainImage || '',
          categoryId: res.categoryId != null ? String(res.categoryId) : '',
          description: res.description || ''
        }
        if (res.mainImage) {
          imageList.value = [{ url: res.mainImage }]
        }
        if (res.video) {
          videoList.value = [{ url: res.video }]
        }
        // 加载已有的属性值
        if (res.attributes && res.attributes.length > 0) {
          const attrs = {}
          res.attributes.forEach(attr => {
            attrs[attr.templateId] = attr.value
          })
          attributes.value = attrs
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
    // 校验分类是否已选择
    if (!form.value.categoryId) {
      showToast('请选择商品分类')
      submitting.value = false
      return
    }

    // 校验必填属性
    for (const template of attributeTemplates.value) {
      if (template.required === 1) {
        const val = attributes.value[template.id]
        if (!val || !val.trim()) {
          showToast(`请填写${template.name}`)
          submitting.value = false
          return
        }
      }
    }

    // 构建属性数组
    const attributeList = Object.entries(attributes.value)
      .filter(([_, value]) => value && value.trim())
      .map(([templateId, value]) => ({
        templateId: String(templateId),
        value: value.trim()
      }))

    const data = {
      ...form.value,
      price: Number(form.value.price),
      categoryId: String(form.value.categoryId),
      attributes: attributeList
    }

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

.category-picker {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebedf0;
}

.picker-header span {
  font-size: 16px;
  font-weight: 500;
}

.category-picker :deep(.van-tree-select) {
  flex: 1;
}

.video-upload-wrap {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.multi-select-popup {
  display: flex;
  flex-direction: column;
  max-height: 60vh;
}

.multi-select-popup .van-cell-group {
  flex: 1;
  overflow-y: auto;
}

.multi-select-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  min-height: 24px;
  align-items: center;
}
</style>
