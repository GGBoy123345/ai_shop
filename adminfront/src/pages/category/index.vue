<template>
  <div class="category-manage">
    <div class="header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加分类
      </el-button>
    </div>

    <el-table :data="categoryList" row-key="id" border default-expand-all>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column label="属性数量" width="100">
        <template #default="{ row }">
          <el-tag>{{ row.attributeCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="800" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" @click="handleManageAttr(row)">属性管理</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '添加分类'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="父级分类">
          <el-select v-model="form.parentId" placeholder="请选择父级分类（留空为顶级分类）" clearable>
            <el-option :value="0" label="顶级分类" />
            <el-option
              v-for="item in flatCategoryList"
              :key="item.id"
              :value="item.id"
              :label="item.displayName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 属性管理对话框 -->
    <el-dialog v-model="attrDialogVisible" :title="`管理 ${currentCategory.name} 的属性`" width="700px">
      <div class="attr-header">
        <el-button type="primary" size="small" @click="handleAddAttr">
          <el-icon><Plus /></el-icon>
          添加属性
        </el-button>
      </div>

      <el-table :data="attrList" border style="margin-top: 10px;">
        <el-table-column prop="name" label="属性名称" />
        <el-table-column prop="inputType" label="输入类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ inputTypeMap[row.inputType] || row.inputType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="required" label="必填" width="80">
          <template #default="{ row }">
            <el-tag :type="row.required === 1 ? 'danger' : 'info'" size="small">
              {{ row.required === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" width="150">
          <template #default="{ row, $index }">
            <el-button size="small" @click="handleEditAttr(row, $index)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteAttr(row, $index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="attrDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAttrs" :loading="savingAttrs">保存属性</el-button>
      </template>
    </el-dialog>

    <!-- 添加/编辑属性对话框 -->
    <el-dialog v-model="attrFormVisible" :title="isEditAttr ? '编辑属性' : '添加属性'" width="500px" append-to-body>
      <el-form :model="attrForm" :rules="attrRules" ref="attrFormRef" label-width="100px">
        <el-form-item label="属性名称" prop="name">
          <el-input v-model="attrForm.name" placeholder="如：配料表、材质占比、适用人群" />
        </el-form-item>
        <el-form-item label="输入类型" prop="inputType">
          <el-select v-model="attrForm.inputType">
            <el-option value="text" label="文本输入" />
            <el-option value="textarea" label="多行文本" />
            <el-option value="select" label="下拉选择" />
            <el-option value="multi_select" label="多选" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否必填">
          <el-switch v-model="attrForm.required" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="attrForm.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="attrFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAttrSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategoryTree, addCategory, updateCategory, deleteCategory } from '../../api/category'
import { getAttributeTemplatesByCategory, addAttributeTemplate, updateAttributeTemplate, deleteAttributeTemplate } from '../../api/attribute'

const categoryList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const formRef = ref(null)

// 属性管理相关
const attrDialogVisible = ref(false)
const currentCategory = ref({})
const attrList = ref([])
const savingAttrs = ref(false)

// 属性表单相关
const attrFormVisible = ref(false)
const isEditAttr = ref(false)
const editAttrIndex = ref(-1)
const attrFormRef = ref(null)
const attrForm = ref({
  name: '',
  inputType: 'text',
  required: 0,
  sort: 0
})

const inputTypeMap = {
  text: '文本',
  textarea: '多行文本',
  select: '下拉选择',
  multi_select: '多选'
}

const form = ref({
  parentId: null,
  name: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const attrRules = {
  name: [{ required: true, message: '请输入属性名称', trigger: 'blur' }],
  inputType: [{ required: true, message: '请选择输入类型', trigger: 'change' }]
}

// 将树形结构转换为扁平列表
const flatCategoryList = computed(() => {
  const result = []
  function flatten(list, level = 0) {
    for (const item of list) {
      result.push({
        id: item.id,
        name: item.name,
        displayName: '　'.repeat(level) + (level > 0 ? '└ ' : '') + item.name
      })
      if (item.children && item.children.length > 0) {
        flatten(item.children, level + 1)
      }
    }
  }
  flatten(categoryList.value)
  return result
})

async function loadCategories() {
  try {
    const res = await getCategoryTree()
    if (res) {
      categoryList.value = res
      // 加载每个分类的属性数量
      await loadAttributeCounts()
    }
  } catch (e) {
    ElMessage.error('加载分类列表失败')
  }
}

// 加载每个分类的属性数量
async function loadAttributeCounts() {
  const allIds = flatCategoryList.value.map(c => c.id)
  for (const id of allIds) {
    try {
      const res = await getAttributeTemplatesByCategory(id)
      const count = res ? res.length : 0
      // 更新分类列表中的属性数量
      updateCategoryAttrCount(categoryList.value, id, count)
    } catch (e) {
      // 忽略错误
    }
  }
}

function updateCategoryAttrCount(list, id, count) {
  for (const item of list) {
    if (item.id === id) {
      item.attributeCount = count
      return true
    }
    if (item.children && updateCategoryAttrCount(item.children, id, count)) {
      return true
    }
  }
  return false
}

function handleAdd() {
  isEdit.value = false
  editId.value = null
  form.value = { parentId: null, name: '', sort: 0 }
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  editId.value = row.id
  form.value = {
    parentId: row.parentId || null,
    name: row.name,
    sort: row.sort || 0
  }
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除分类"${row.name}"吗？删除后不可恢复。`, '确认删除', {
      type: 'warning'
    })
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    submitting.value = true
    const data = { ...form.value }
    if (!data.parentId || data.parentId === 0) {
      data.parentId = 0
    }

    if (isEdit.value) {
      await updateCategory(editId.value, data)
      ElMessage.success('修改成功')
    } else {
      await addCategory(data)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadCategories()
  } catch (e) {
    if (e !== false) {
      ElMessage.error(e.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// ==================== 属性管理 ====================

async function handleManageAttr(row) {
  currentCategory.value = row
  attrDialogVisible.value = true
  // 加载该分类的属性模板
  try {
    const res = await getAttributeTemplatesByCategory(row.id)
    attrList.value = res || []
  } catch (e) {
    ElMessage.error('加载属性失败')
    attrList.value = []
  }
}

function handleAddAttr() {
  isEditAttr.value = false
  editAttrIndex.value = -1
  attrForm.value = { name: '', inputType: 'text', required: 0, sort: 0 }
  attrFormVisible.value = true
}

function handleEditAttr(row, index) {
  isEditAttr.value = true
  editAttrIndex.value = index
  attrForm.value = { ...row }
  attrFormVisible.value = true
}

function handleDeleteAttr(row, index) {
  attrList.value.splice(index, 1)
}

function handleAttrSubmit() {
  attrFormRef.value.validate((valid) => {
    if (valid) {
      if (isEditAttr.value) {
        // 编辑
        attrList.value[editAttrIndex.value] = { ...attrForm.value }
      } else {
        // 添加
        attrList.value.push({ ...attrForm.value })
      }
      attrFormVisible.value = false
    }
  })
}

async function handleSaveAttrs() {
  savingAttrs.value = true
  try {
    // 获取原有的属性（用于判断哪些需要删除）
    const oldAttrs = await getAttributeTemplatesByCategory(currentCategory.value.id) || []

    // 先删除所有原有属性
    for (const old of oldAttrs) {
      try {
        await deleteAttributeTemplate(old.id)
      } catch (e) {
        // 忽略删除错误
      }
    }

    // 添加新属性
    for (const attr of attrList.value) {
      await addAttributeTemplate({
        categoryId: currentCategory.value.id,
        name: attr.name,
        inputType: attr.inputType,
        required: attr.required,
        sort: attr.sort || 0
      })
    }

    ElMessage.success('属性保存成功')
    attrDialogVisible.value = false
    loadCategories() // 刷新属性数量
  } catch (e) {
    ElMessage.error('保存失败：' + (e.message || '未知错误'))
  } finally {
    savingAttrs.value = false
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-manage {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
}

.attr-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 10px;
}
</style>
