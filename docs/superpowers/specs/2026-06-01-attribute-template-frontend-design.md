# 属性模板前端对接设计文档

## 背景

当前系统的属性模板基础设施（数据库表、后端 API）已完整，但前端存在三个关键缺陷：

1. 管理端无法管理 `attribute_option` 选项值（后端 API 已有，前端未对接）
2. 商户端商品编辑页将所有属性渲染为文本框，忽略 `inputType` 类型
3. 用户端商品详情页不展示商品属性参数

## 目标

- 管理员可以为 `select`/`multi_select` 类型的属性模板配置选项值
- 商户编辑商品时，根据属性模板的 `inputType` 渲染对应的输入控件
- 用户浏览商品详情时，可以看到商品参数信息

## 不改动的部分

- 数据库表结构（`attribute_template`、`attribute_option`、`product_attribute`）
- 后端 API（模板 CRUD、选项管理、商品属性存储/读取）
- 分类继承逻辑（子分类自动继承父分类属性）

---

## 模块一：管理端选项值管理

### 修改文件

- `adminfront/src/pages/attribute/index.vue`

### 设计

在现有属性模板表格中，为 `select` 和 `multi_select` 类型的模板增加选项管理功能：

- 每行增加"管理选项"操作按钮，仅当 `inputType` 为 `select` 或 `multi_select` 时显示
- 点击后展开 el-table 的 expand 行，展示该模板下的选项列表
- 选项列表以表格形式展示：选项值、排序、操作（删除）
- 底部有"添加选项"区域：输入框 + 添加按钮
- 调用已有 API：`addAttributeOption(templateId, { value, sort })` 和 `deleteAttributeOption(optionId)`

### 交互流程

1. 管理员在属性模板列表中点击"管理选项"
2. 该行展开，显示已有的选项列表
3. 输入新选项值，点击"添加"按钮
4. 选项出现在列表中，可随时删除

---

## 模块二：商户端动态属性控件

### 修改文件

- `userfront/src/pages/merchant/product-edit.vue`

### 设计

将属性模板的渲染逻辑从统一文本框改为根据 `inputType` 分发到不同控件：

| inputType | 渲染控件 | 数据结构 |
|-----------|----------|----------|
| `text` | `van-field` 文本框 | `attributes[templateId]` = 字符串 |
| `textarea` | `van-field` type="textarea" | `attributes[templateId]` = 字符串 |
| `select` | `van-field` readonly + `van-picker` 弹出选择 | `attributes[templateId]` = 选中的 option value 字符串 |
| `multi_select` | `van-field` readonly + 多选弹窗 + `van-tag` 展示已选 | `attributes[templateId]` = 逗号分隔的多个 value 字符串 |

### 控件交互

**select 类型：**
- 点击 field 弹出 `van-picker`，选项列表来自模板的 `options` 数组
- 选中后显示选中值，存储为字符串

**multi_select 类型：**
- 点击 field 弹出 `van-popup`，内含 `van-checkbox-group`
- 已选项以 `van-tag` 形式展示在 field 内
- 存储为逗号分隔的字符串（如 `"S,M,L"`）

### 必填校验

- 提交时检查所有 `required=1` 的属性是否有值
- 缺失时 `showToast` 提示具体属性名称
- 校验通过后再提交

---

## 模块三：用户端商品参数展示

### 修改文件

- `userfront/src/pages/product/detail.vue`

### 设计

在商品详情页的商品描述区域上方，新增"商品参数"折叠展示区：

- 使用 `van-collapse` 折叠面板，标题"商品参数"
- 内容以 key-value 列表展示：每行一个属性，左侧属性名（灰色），右侧属性值
- 数据来源：`getProductDetail` 返回的 `attributes` 数组，每项已有 `templateName` 和 `value`
- 如果商品没有属性数据，不显示此区域

### 展示格式

```
商品参数
  材料    纯棉
  尺码    M
  版型    宽松
  领型    圆领
```

---

## 数据流

```
管理员在管理端创建属性模板 → 配置选项值（select/multi_select类型）
                                    ↓
商户选择商品分类 → 后端返回该分类的属性模板（含 options）
                                    ↓
商户根据模板填写属性 → 提交时存储到 product_attribute 表
                                    ↓
用户浏览商品 → getProductDetail 返回 attributes（含 templateName + value）
                                    ↓
商品详情页展示属性参数
```

---

## API 使用清单

| 模块 | API | 状态 |
|------|-----|------|
| 管理端选项管理 | `POST /api/attribute-templates/{templateId}/options` | 已有，前端新增调用 |
| 管理端选项删除 | `DELETE /api/attribute-templates/options/{id}` | 已有，前端新增调用 |
| 商户端获取模板 | `GET /api/attribute-templates/category/{categoryId}` | 已有，返回值增加 options 使用 |
| 商户端提交属性 | 随商品 `POST/PUT` 一起提交 | 已有，无需改动 |
| 用户端查看详情 | `GET /api/products/{id}` | 已有，返回值含 attributes |
