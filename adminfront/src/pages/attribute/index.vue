<template>
  <div class="attribute-manage">
    <div class="header">
      <h2>属性模板管理</h2>
      <div class="header-actions">
        <el-select v-model="selectedCategoryId" placeholder="选择分类筛选" clearable @change="loadTemplates">
          <el-option :value="null" label="全部分类" />
          <el-option
            v-for="item in flatCategoryList"
            :key="item.id"
            :value="item.id"
            :label="item.displayName"
          />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加属性模板
        </el-button>
      </div>
    </div>

    <el-table :data="templateList" border row-key="id" ref="tableRef">
      <el-table-column type="expand" width="50">
        <template #default="{ row }">
          <div class="option-manage" v-if="row.inputType === 'select' || row.inputType === 'multi_select'">
            <div class="option-header">
              <span>选项值管理（{{ row.name }}）</span>
            </div>
            <el-table :data="row.options || []" border size="small" v-if="row.options && row.options.length > 0">
              <el-table-column prop="value" label="选项值" />
              <el-table-column prop="sort" label="排序" width="100" />
              <el-table-column label="操作" width="100" align="center">
                <template #default="{ row: optRow }">
                  <el-button size="small" type="danger" text @click="handleDeleteOption(row, optRow)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="option-empty" v-else>暂无选项，请在下方添加</div>
            <div class="option-add">
              <el-input
                v-model="newOptionValue"
                placeholder="输入选项值，回车或点击添加"
                style="width: 240px"
                @keyup.enter="handleAddOption(row)"
              />
              <el-button type="primary" size="small" @click="handleAddOption(row)">添加选项</el-button>
            </div>
          </div>
          <div class="option-manage" v-else>
            <div class="option-empty">文本类型无需配置选项</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="categoryName" label="所属分类" width="150" />
      <el-table-column prop="name" label="属性名称" />
      <el-table-column prop="inputType" label="输入类型" width="120">
        <template #default="{ row }">
          <el-tag>{{ inputTypeMap[row.inputType] || row.inputType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="required" label="是否必填" width="100">
        <template #default="{ row }">
          <el-tag :type="row.required === 1 ? 'danger' : 'info'">
            {{ row.required === 1 ? '必填' : '选填' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.inputType === 'select' || row.inputType === 'multi_select'"
            size="small"
            type="success"
            @click="toggleExpand(row)"
          >选项</el-button>
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑属性模板' : '添加属性模板'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="item in flatCategoryList"
              :key="item.id"
              :value="item.id"
              :label="item.displayName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="属性名称" prop="name">
          <el-input v-model="form.name" placeholder="如：配料表、材质占比、适用人群" />
        </el-form-item>
        <el-form-item label="输入类型" prop="inputType">
          <el-select v-model="form.inputType">
            <el-option value="text" label="文本输入" />
            <el-option value="textarea" label="多行文本" />
            <el-option value="select" label="下拉选择" />
            <el-option value="multi_select" label="多选" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否必填">
          <el-switch v-model="form.required" :active-value="1" :inactive-value="0" />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAttributeTemplates, addAttributeTemplate, updateAttributeTemplate, deleteAttributeTemplate, addAttributeOption, deleteAttributeOption } from '../../api/attribute'
import { getCategoryTree } from '../../api/category'

const templateList = ref([])
const categoryList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const formRef = ref(null)
const selectedCategoryId = ref(null)
const tableRef = ref(null)
const newOptionValue = ref('')

const inputTypeMap = {
  text: '文本',
  textarea: '多行文本',
  select: '下拉选择',
  multi_select: '多选'
}

const form = ref({
  categoryId: null,
  name: '',
  inputType: 'text',
  required: 0,
  sort: 0
})

const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入属性名称', trigger: 'blur' }],
  inputType: [{ required: true, message: '请选择输入类型', trigger: 'change' }]
}

// 将树形分类转换为扁平列表
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
    }
  } catch (e) {
    console.error('加载分类失败:', e)
  }
}

async function loadTemplates() {
  try {
    const params = {
      page: 1,
      size: 100
    }
    const res = await getAttributeTemplates(params)
    if (res) {
      let list = res.records || res
      // 如果选择了分类，进行前端筛选
      if (selectedCategoryId.value) {
        list = list.filter(item => item.categoryId === selectedCategoryId.value)
      }
      // 填充分类名称
      const categoryMap = {}
      flatCategoryList.value.forEach(c => {
        categoryMap[c.id] = c.name
      })
      templateList.value = list.map(item => ({
        ...item,
        categoryName: categoryMap[item.categoryId] || '未知分类'
      }))
    }
  } catch (e) {
    ElMessage.error('加载属性模板失败')
  }
}

function handleAdd() {
  isEdit.value = false
  editId.value = null
  form.value = { categoryId: selectedCategoryId.value, name: '', inputType: 'text', required: 0, sort: 0 }
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  editId.value = row.id
  form.value = {
    categoryId: row.categoryId,
    name: row.name,
    inputType: row.inputType,
    required: row.required,
    sort: row.sort || 0
  }
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除属性模板"${row.name}"吗？`, '确认删除', {
      type: 'warning'
    })
    await deleteAttributeTemplate(row.id)
    ElMessage.success('删除成功')
    loadTemplates()
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

    if (isEdit.value) {
      await updateAttributeTemplate(editId.value, form.value)
      ElMessage.success('修改成功')
    } else {
      await addAttributeTemplate(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadTemplates()
  } catch (e) {
    if (e !== false) {
      ElMessage.error(e.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

function toggleExpand(row) {
  tableRef.value.toggleRowExpansion(row)
}

async function handleAddOption(templateRow) {
  const val = newOptionValue.value.trim()
  if (!val) {
    ElMessage.warning('请输入选项值')
    return
  }
  // 检查重复
  if (templateRow.options && templateRow.options.some(o => o.value === val)) {
    ElMessage.warning('该选项值已存在')
    return
  }
  try {
    await addAttributeOption(templateRow.id, {
      value: val,
      sort: (templateRow.options?.length || 0) + 1
    })
    ElMessage.success('添加成功')
    newOptionValue.value = ''
    await loadTemplates()
    // 重新展开该行
    const refreshed = templateList.value.find(t => t.id === templateRow.id)
    if (refreshed) {
      tableRef.value.toggleRowExpansion(refreshed, true)
    }
  } catch (e) {
    ElMessage.error(e.message || '添加失败')
  }
}

async function handleDeleteOption(templateRow, optRow) {
  try {
    await ElMessageBox.confirm(`确定要删除选项"${optRow.value}"吗？`, '确认删除', {
      type: 'warning'
    })
    await deleteAttributeOption(optRow.id)
    ElMessage.success('删除成功')
    await loadTemplates()
    // 重新展开该行
    const refreshed = templateList.value.find(t => t.id === templateRow.id)
    if (refreshed) {
      tableRef.value.toggleRowExpansion(refreshed, true)
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadCategories()
  loadTemplates()
})
</script>

<style scoped>
.attribute-manage {
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

.header-actions {
  display: flex;
  gap: 12px;
}

.option-manage {
  padding: 16px 20px;
}

.option-header {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #303133;
}

.option-add {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.option-empty {
  text-align: center;
  color: #909399;
  padding: 16px 0;
  font-size: 13px;
}
</style>
