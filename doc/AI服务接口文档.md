# AI服务接口文档

> **状态：未实现。** ai-mall-ai 模块当前为空壳，仅包含 Application 类，无任何业务代码。以下接口均为设计规划，尚未开发。

> **服务名称**：AI服务（ai-mall-ai / ai-svc）
> **架构说明**：双层架构，Java适配层（ai-mall-ai, 8087）对外暴露RESTful接口，内部通过HTTP调用Python AI引擎（ai-svc, 8088）
> **基础路径**：`/api`（外部接口）、`/internal`（内部接口）
> **协议**：HTTP/HTTPS
> **数据格式**：JSON

---

## 架构概览

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│   前端 / 网关    │ ──>  │  ai-mall-ai     │ ──>  │    ai-svc       │
│  (gateway-svc)  │      │  Java适配层      │      │  Python AI引擎   │
│                 │ <──  │  :8087          │ <──  │  :8088          │
└─────────────────┘      └─────────────────┘      └─────────────────┘
     外部请求                参数校验/鉴权            LangChain + RAG
     /api/**               Feign序列化               ChromaDB + LLM
                           异常翻译                  知识库检索
```

**Java适配层（ai-mall-ai, 8087）职责**：
- 对外暴露RESTful接口，处理鉴权、参数校验、统一响应格式
- 将请求转发给Python AI引擎，封装响应结果
- 处理服务间调用（Feign），供product-svc等内部服务调用

**Python AI引擎（ai-svc, 8088）职责**：
- LangChain + RAG模式实现商品材质/成分知识问答
- ChromaDB向量数据库存储和检索知识库
- LLM生成通俗易懂的专业分析文本
- 商品向量化处理

---

## 统一响应格式

所有接口均返回以下统一格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 业务状态码，200 表示成功，其他为错误码 |
| message | String | 提示信息 |
| data | Object | 业务数据，具体结构见各接口说明 |

---

# 第一部分：外部接口（/api/**，Java适配层对外暴露）

> 以下接口通过Gateway网关对外暴露，需经过统一鉴权。
> AI分析类接口需要用户登录，知识库管理接口需要管理员权限。

---

## 一、商品AI分析模块

---

### 1. 商品材质/成分分析

根据商品ID获取AI生成的材质/成分专业分析。系统从商品属性表读取材质/成分信息，结合RAG知识库生成专业解读。支持用户追问。

**请求方式与URL**

`POST /api/ai/analyze/product`

**请求头**

| 参数名 | 是否必填 | 说明 |
|--------|----------|------|
| Content-Type | 是 | application/json |
| Authorization | 是 | Bearer {token}（需登录） |

**请求参数（Body）**

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| productId | Long | 是 | 商品ID |
| question | String | 否 | 用户追问，如"这个面料适合夏天穿吗？"，不传则返回默认分析 |

**请求示例**

```json
{
  "productId": 1001,
  "question": "这个面料适合夏天穿吗？"
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productId": 1001,
    "productName": "2024新款连衣裙 夏季清凉修身",
    "analysis": "这款连衣裙采用95%棉+5%氨纶的混纺面料，棉含量高使其具备优秀的透气性和吸湿性，非常适合夏季穿着。氨纶的加入提供了适度的弹性，使裙子更加贴合身形又不会束缚活动。面料克重约180g/m²，属于中薄款，手感柔软亲肤，不易起皱。建议搭配肤色或浅色内衣，避免透色问题。",
    "keyIngredients": [
      {
        "name": "棉",
        "percentage": "95%",
        "properties": ["透气", "吸湿", "亲肤", "不易过敏"]
      },
      {
        "name": "氨纶",
        "percentage": "5%",
        "properties": ["弹性好", "回复性佳", "耐磨"]
      }
    ],
    "careInstructions": "建议30度以下水温手洗或机洗，避免暴晒，悬挂晾干",
    "knowledgeSources": ["服装面料知识库", "棉纤维特性文档"],
    "generateTime": "2026-05-28 14:30:00"
  }
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| productId | Long | 商品ID |
| productName | String | 商品名称 |
| analysis | String | AI生成的专业分析文本，通俗易懂地解读材质/成分特性 |
| keyIngredients | Array | 关键成分/材质列表 |
| keyIngredients[].name | String | 成分/材质名称 |
| keyIngredients[].percentage | String | 含量百分比 |
| keyIngredients[].properties | Array[String] | 该成分的主要特性标签 |
| careInstructions | String | 洗涤/保养建议，可能为null |
| knowledgeSources | Array[String] | 分析所引用的知识库来源 |
| generateTime | String | 分析生成时间 |

**业务规则**

- 商品必须存在且状态为上架（status=1），已下架或已删除的商品无法分析
- 商品必须有关联的材质/成分属性，否则返回提示信息
- question 参数可选，不传时返回默认的全面分析；传入时AI会针对追问进行回答
- 分析结果基于RAG知识库生成，确保专业性

**错误码**

| 错误码 | 说明 |
|--------|------|
| 40101 | 未登录或Token已过期 |
| 90001 | 商品不存在 |
| 90002 | 商品已下架，无法分析 |
| 90003 | 商品缺少材质/成分属性信息 |
| 90004 | AI引擎调用超时，请稍后重试 |
| 90005 | AI引擎服务异常 |

---

### 2. 成分功效查询

查询指定成分/材质的详细功效信息。适用于化妆品成分、食品配料、服装面料等场景，基于知识库返回专业解读。

**请求方式与URL**

`POST /api/ai/analyze/ingredient`

**请求头**

| 参数名 | 是否必填 | 说明 |
|--------|----------|------|
| Content-Type | 是 | application/json |
| Authorization | 是 | Bearer {token}（需登录） |

**请求参数（Body）**

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| ingredient | String | 是 | 成分/材质名称，如"烟酰胺"、"纯棉"、"小麦粉" |
| category | String | 否 | 商品分类，用于限定查询范围。可选值：`服装`、`化妆品`、`食品`，不传则全库检索 |

**请求示例**

```json
{
  "ingredient": "烟酰胺",
  "category": "化妆品"
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "name": "烟酰胺",
    "category": "化妆品",
    "effects": [
      "美白提亮：抑制黑色素向角质层转运，减少色素沉着",
      "控油收毛孔：调节皮脂分泌，改善毛孔粗大",
      "抗衰老：促进胶原蛋白合成，改善细纹",
      "修复屏障：增强皮肤屏障功能，减少水分流失",
      "抗炎舒缓：减轻皮肤炎症反应，改善泛红"
    ],
    "mechanism": "烟酰胺是维生素B3的活性形式，通过抑制黑素小体从黑素细胞向角质形成细胞的转运来减少色素沉着。同时能促进神经酰胺合成，增强皮肤屏障。在控油方面，通过调节皮脂腺细胞的脂质合成来减少油脂分泌。",
    "suitableFor": [
      "肤色暗沉、有色斑困扰的人群",
      "油性肌肤、毛孔粗大的人群",
      "初老肌肤、有细纹的人群",
      "敏感肌修复期的人群"
    ],
    "usageNotes": [
      "建议从低浓度（2%）开始建立耐受，逐步提高浓度",
      "避免与酸类产品（如果酸、水杨酸）同时使用，可能引起刺激",
      "部分人群可能出现不耐受反应（泛红、刺痛），建议先做耳后测试",
      "孕妇及哺乳期女性建议咨询医生后使用",
      "开封后注意避光保存，避免氧化失效"
    ],
    "commonConcentration": "护肤品中常用浓度为2%-5%，美白产品通常为3%-5%，敏感肌适用浓度为2%-3%",
    "relatedIngredients": ["传明酸", "维C衍生物", "熊果苷"],
    "knowledgeSources": ["化妆品成分知识库", "皮肤科学文献"],
    "generateTime": "2026-05-28 14:35:00"
  }
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| name | String | 成分名称 |
| category | String | 所属分类 |
| effects | Array[String] | 功效列表，每项包含功效名称和具体说明 |
| mechanism | String | 作用机理，科学原理解释 |
| suitableFor | Array[String] | 适用人群列表 |
| usageNotes | Array[String] | 使用注意事项列表 |
| commonConcentration | String | 常用浓度范围说明 |
| relatedIngredients | Array[String] | 相关/类似功效的成分推荐 |
| knowledgeSources | Array[String] | 知识来源引用 |
| generateTime | String | 查询生成时间 |

**业务规则**

- ingredient 不能为空，长度不超过50个字符
- category 不在可选范围内时，按全库检索处理
- 查询结果基于知识库预置数据和LLM生成，确保专业性
- 知识库中不存在该成分时，返回LLM通用知识生成的结果，并标记knowledgeSources为"通用知识"

**错误码**

| 错误码 | 说明 |
|--------|------|
| 40101 | 未登录或Token已过期 |
| 90010 | 成分名称不能为空 |
| 90011 | 成分名称超出长度限制（最大50个字符） |
| 90004 | AI引擎调用超时，请稍后重试 |
| 90005 | AI引擎服务异常 |

---

### 3. 多商品对比分析

对多个商品进行AI对比分析，从材质、性价比、品质、功能等维度生成对比报告和推荐建议。

**请求方式与URL**

`POST /api/ai/analyze/compare`

**请求头**

| 参数名 | 是否必填 | 说明 |
|--------|----------|------|
| Content-Type | 是 | application/json |
| Authorization | 是 | Bearer {token}（需登录） |

**请求参数（Body）**

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| products | Array[Object] | 是 | 待对比的商品列表，最少2个，最多5个 |
| products[].productId | Long | 是 | 商品ID |
| products[].name | String | 否 | 商品名称，不传时由系统自动查询填充 |
| products[].price | BigDecimal | 否 | 商品价格，不传时由系统自动查询填充 |
| focus | String | 否 | 对比关注点。可选值：`性价比`、`品质`、`功能`，不传则综合对比 |
| question | String | 否 | 用户追问，如"哪款更适合送礼？" |

**请求示例**

```json
{
  "products": [
    {
      "productId": 1001,
      "name": "纯棉T恤A",
      "price": 199.00
    },
    {
      "productId": 1002,
      "name": "纯棉T恤B",
      "price": 129.00
    },
    {
      "productId": 1005,
      "name": "涤纶速干T恤C",
      "price": 89.00
    }
  ],
  "focus": "性价比",
  "question": "哪款更适合日常通勤穿？"
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "comparisonSummary": "三款T恤各有特色：纯棉T恤A（199元）棉含量最高达95%，面料质感最佳但价格偏高；纯棉T恤B（129元）棉含量80%，性价比较为均衡；涤纶速干T恤C（89元）价格最低，速干性能突出但透气性略逊。综合性价比来看，纯棉T恤B是最推荐的选择。",
    "comparisonTable": [
      {
        "productId": 1001,
        "productName": "纯棉T恤A",
        "price": 199.00,
        "materialScore": 9.2,
        "costScore": 7.0,
        "qualityScore": 9.0,
        "highlights": ["95%高含量纯棉", "面料手感柔软亲肤", "透气吸湿性优秀"]
      },
      {
        "productId": 1002,
        "productName": "纯棉T恤B",
        "price": 129.00,
        "materialScore": 8.0,
        "costScore": 8.5,
        "qualityScore": 8.0,
        "highlights": ["80%棉含量", "性价比均衡", "日常穿着舒适度好"]
      },
      {
        "productId": 1005,
        "productName": "涤纶速干T恤C",
        "price": 89.00,
        "materialScore": 6.5,
        "costScore": 9.0,
        "qualityScore": 7.0,
        "highlights": ["速干性能突出", "价格最低", "适合运动场景"]
      }
    ],
    "recommendation": "如果追求面料品质和穿着体验，推荐纯棉T恤A；如果注重性价比，推荐纯棉T恤B，80%棉含量已能满足日常舒适需求，价格比A款便宜70元；如果是运动场景或预算有限，涤纶速干T恤C的速干性能是不错的选择。日常通勤建议选择纯棉T恤B，兼顾舒适度和价格。",
    "recommendProductId": 1002,
    "focus": "性价比",
    "generateTime": "2026-05-28 14:40:00"
  }
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| comparisonSummary | String | 对比分析摘要，概述各商品特点 |
| comparisonTable | Array | 对比详情表 |
| comparisonTable[].productId | Long | 商品ID |
| comparisonTable[].productName | String | 商品名称 |
| comparisonTable[].price | BigDecimal | 商品价格 |
| comparisonTable[].materialScore | BigDecimal | 材质评分（1-10分） |
| comparisonTable[].costScore | BigDecimal | 性价比评分（1-10分） |
| comparisonTable[].qualityScore | BigDecimal | 品质评分（1-10分） |
| comparisonTable[].highlights | Array[String] | 该商品的亮点标签 |
| recommendation | String | AI生成的推荐建议，综合分析后给出结论 |
| recommendProductId | Long | AI推荐的最佳商品ID |
| focus | String | 本次对比的关注点 |
| generateTime | String | 分析生成时间 |

**业务规则**

- products 数组最少2个商品，最多5个商品
- products 中的 productId 不能重复
- 所有商品必须存在且为上架状态
- name 和 price 可不传，系统会自动从商品服务查询填充
- focus 影响评分权重：性价比侧重costScore，品质侧重qualityScore，功能侧重materialScore
- recommendProductId 从对比商品中选择综合最优的一个

**错误码**

| 错误码 | 说明 |
|--------|------|
| 40101 | 未登录或Token已过期 |
| 90020 | 对比商品数量不合法（最少2个，最多5个） |
| 90021 | 对比商品列表中存在重复商品 |
| 90022 | 对比商品列表不能为空 |
| 90001 | 商品不存在（附带具体productId） |
| 90002 | 商品已下架，无法对比（附带具体productId） |
| 90004 | AI引擎调用超时，请稍后重试 |
| 90005 | AI引擎服务异常 |

---

## 二、知识库管理模块

---

### 4. 知识库列表

分页查询AI知识库条目列表，支持按分类筛选。管理后台专用，用于查看和管理AI分析所依赖的知识库数据。

**请求方式与URL**

`GET /api/ai/knowledge`

**请求头**

| 参数名 | 是否必填 | 说明 |
|--------|----------|------|
| Authorization | 是 | Bearer {token}（管理员角色） |

**请求参数（Query）**

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页数量，默认10，最大100 |
| category | String | 否 | 知识分类筛选，可选值：`服装面料`、`化妆品成分`、`食品配料` |
| keyword | String | 否 | 搜索关键词，匹配知识条目标题和内容 |

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 156,
    "page": 1,
    "size": 10,
    "list": [
      {
        "id": 1,
        "title": "纯棉面料特性与保养",
        "category": "服装面料",
        "tags": ["棉", "天然纤维", "透气"],
        "vectorized": true,
        "vectorId": "vec_001",
        "source": "纺织材料学教材",
        "createTime": "2026-04-10 10:00:00",
        "updateTime": "2026-05-15 16:30:00"
      },
      {
        "id": 2,
        "title": "烟酰胺护肤功效详解",
        "category": "化妆品成分",
        "tags": ["烟酰胺", "美白", "维生素B3"],
        "vectorized": true,
        "vectorId": "vec_002",
        "source": "化妆品成分手册",
        "createTime": "2026-04-12 09:00:00",
        "updateTime": "2026-05-18 11:00:00"
      },
      {
        "id": 3,
        "title": "小麦粉营养成分分析",
        "category": "食品配料",
        "tags": ["小麦粉", "碳水化合物", "谷物"],
        "vectorized": false,
        "vectorId": null,
        "source": "食品营养学",
        "createTime": "2026-05-01 14:00:00",
        "updateTime": "2026-05-01 14:00:00"
      }
    ]
  }
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| total | Long | 符合条件的知识条目总数 |
| page | Integer | 当前页码 |
| size | Integer | 每页数量 |
| list | Array | 知识条目列表 |
| list[].id | Long | 知识条目ID |
| list[].title | String | 知识条目标题 |
| list[].category | String | 知识分类 |
| list[].tags | Array[String] | 标签列表 |
| list[].vectorized | Boolean | 是否已向量化（同步到向量数据库） |
| list[].vectorId | String | 向量数据库中的ID，未向量化时为null |
| list[].source | String | 知识来源 |
| list[].createTime | String | 创建时间 |
| list[].updateTime | String | 最后更新时间 |

**错误码**

| 错误码 | 说明 |
|--------|------|
| 40101 | 未登录或Token已过期 |
| 40302 | 无管理员权限 |

---

### 5. 同步知识库到向量数据库

触发知识库数据同步到ChromaDB向量数据库。支持按分类同步或全量同步。同步后AI分析接口才能基于最新知识库进行检索。

**请求方式与URL**

`POST /api/ai/knowledge/sync`

**请求头**

| 参数名 | 是否必填 | 说明 |
|--------|----------|------|
| Content-Type | 是 | application/json |
| Authorization | 是 | Bearer {token}（管理员角色） |

**请求参数（Body）**

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| category | String | 否 | 指定同步的知识分类，可选值：`服装面料`、`化妆品成分`、`食品配料`。不传则全量同步所有分类 |

**请求示例**

```json
{
  "category": "化妆品成分"
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "同步任务已提交",
  "data": {
    "taskId": "sync_task_20260528_001",
    "syncType": "INCREMENTAL",
    "category": "化妆品成分",
    "totalCount": 45,
    "status": "PROCESSING",
    "estimatedTime": "约2分钟",
    "createTime": "2026-05-28 14:50:00"
  }
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| taskId | String | 同步任务ID，可用于查询同步进度 |
| syncType | String | 同步类型：`FULL`（全量同步）、`INCREMENTAL`（增量同步） |
| category | String | 同步的分类，全量同步时为null |
| totalCount | Integer | 待同步的知识条目总数 |
| status | String | 任务状态：`PROCESSING`（处理中）、`COMPLETED`（已完成）、`FAILED`（失败） |
| estimatedTime | String | 预估完成时间 |
| createTime | String | 任务创建时间 |

**业务规则**

- 同步任务为异步执行，接口立即返回任务信息
- 全量同步会清空向量数据库中对应分类的旧数据后重新写入
- 增量同步仅同步新增或修改的知识条目
- 同步过程中，已向量化的知识条目仍可用于AI分析
- 重复提交同步请求时，如果上一次同步尚未完成，返回提示信息

**错误码**

| 错误码 | 说明 |
|--------|------|
| 40101 | 未登录或Token已过期 |
| 40302 | 无管理员权限 |
| 90030 | 该分类正在同步中，请勿重复提交 |
| 90031 | 知识库为空，无法同步 |
| 90005 | AI引擎服务异常 |

---

# 第二部分：内部接口（/internal/**，Feign调用）

> 以下接口仅供微服务内部调用（通过 OpenFeign），不对外暴露。
> 请求需在服务间通过 `X-Internal-Token` 请求头进行身份校验。
> 调用方主要为 product-svc（商品服务），在商品上架、属性变更等场景触发。

---

## 三、AI内部分析接口

---

### 6. 商品分析（内部调用）

供其他微服务调用的商品AI分析接口，不需要用户登录校验。主要用于商品详情页服务端渲染、缓存预热等场景。

**请求方式与URL**

`POST /internal/ai/analyze/product`

**请求头**

| 参数名 | 是否必填 | 说明 |
|--------|----------|------|
| Content-Type | 是 | application/json |
| X-Internal-Token | 是 | 内部服务调用令牌 |

**请求参数（Body）**

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| productId | Long | 是 | 商品ID |
| question | String | 否 | 分析提问，不传则返回默认全面分析 |

**请求示例**

```json
{
  "productId": 1001
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productId": 1001,
    "productName": "2024新款连衣裙 夏季清凉修身",
    "analysis": "这款连衣裙采用95%棉+5%氨纶的混纺面料，棉含量高使其具备优秀的透气性和吸湿性，非常适合夏季穿着。氨纶的加入提供了适度的弹性，使裙子更加贴合身形又不会束缚活动。",
    "keyIngredients": [
      {
        "name": "棉",
        "percentage": "95%",
        "properties": ["透气", "吸湿", "亲肤", "不易过敏"]
      },
      {
        "name": "氨纶",
        "percentage": "5%",
        "properties": ["弹性好", "回复性佳", "耐磨"]
      }
    ],
    "careInstructions": "建议30度以下水温手洗或机洗，避免暴晒，悬挂晾干",
    "knowledgeSources": ["服装面料知识库"],
    "generateTime": "2026-05-28 15:00:00"
  }
}
```

**响应字段说明**

> 响应结构与外部接口 `POST /api/ai/analyze/product` 完全一致，详见[接口1响应字段说明](#1-商品材质成分分析)。

**业务规则**

- 内部接口不做用户鉴权，但需校验 X-Internal-Token 的有效性
- 响应结构与外部接口一致，便于调用方统一处理
- 主要供 product-svc 在商品详情聚合时调用，生成分析缓存

**错误码**

| 错误码 | 说明 |
|--------|------|
| 40103 | 内部调用令牌无效 |
| 90001 | 商品不存在 |
| 90003 | 商品缺少材质/成分属性信息 |
| 90004 | AI引擎调用超时，请稍后重试 |
| 90005 | AI引擎服务异常 |

---

### 7. 商品对比（内部调用）

供其他微服务调用的商品对比分析接口。可用于推荐系统、相似商品对比等场景。

**请求方式与URL**

`POST /internal/ai/analyze/compare`

**请求头**

| 参数名 | 是否必填 | 说明 |
|--------|----------|------|
| Content-Type | 是 | application/json |
| X-Internal-Token | 是 | 内部服务调用令牌 |

**请求参数（Body）**

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| products | Array[Object] | 是 | 待对比的商品列表，最少2个，最多5个 |
| products[].productId | Long | 是 | 商品ID |
| products[].name | String | 否 | 商品名称 |
| products[].price | BigDecimal | 否 | 商品价格 |
| focus | String | 否 | 对比关注点：`性价比`、`品质`、`功能` |
| question | String | 否 | 追问 |

**请求示例**

```json
{
  "products": [
    {
      "productId": 1001,
      "name": "纯棉T恤A",
      "price": 199.00
    },
    {
      "productId": 1002,
      "name": "纯棉T恤B",
      "price": 129.00
    }
  ],
  "focus": "性价比"
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "comparisonSummary": "两款纯棉T恤对比：T恤A棉含量95%面料更优，T恤B棉含量80%性价比更高。",
    "comparisonTable": [
      {
        "productId": 1001,
        "productName": "纯棉T恤A",
        "price": 199.00,
        "materialScore": 9.2,
        "costScore": 7.0,
        "qualityScore": 9.0,
        "highlights": ["95%高含量纯棉", "面料手感柔软亲肤"]
      },
      {
        "productId": 1002,
        "productName": "纯棉T恤B",
        "price": 129.00,
        "materialScore": 8.0,
        "costScore": 8.5,
        "qualityScore": 8.0,
        "highlights": ["80%棉含量", "性价比均衡"]
      }
    ],
    "recommendation": "注重品质选T恤A，注重性价比选T恤B。",
    "recommendProductId": 1002,
    "focus": "性价比",
    "generateTime": "2026-05-28 15:05:00"
  }
}
```

**响应字段说明**

> 响应结构与外部接口 `POST /api/ai/analyze/compare` 完全一致，详见[接口3响应字段说明](#3-多商品对比分析)。

**业务规则**

- 内部接口不做用户鉴权，但需校验 X-Internal-Token 的有效性
- 响应结构与外部接口一致
- 主要供 product-svc 在商品推荐模块中调用

**错误码**

| 错误码 | 说明 |
|--------|------|
| 40103 | 内部调用令牌无效 |
| 90020 | 对比商品数量不合法（最少2个，最多5个） |
| 90022 | 对比商品列表不能为空 |
| 90001 | 商品不存在 |
| 90004 | AI引擎调用超时，请稍后重试 |
| 90005 | AI引擎服务异常 |

---

### 8. 商品向量化

将商品的材质/成分属性信息向量化并存储到ChromaDB向量数据库。商品上架时由product-svc异步触发，用于后续AI分析时的相似商品检索和知识匹配。

**请求方式与URL**

`POST /internal/ai/vectorize/product`

**请求头**

| 参数名 | 是否必填 | 说明 |
|--------|----------|------|
| Content-Type | 是 | application/json |
| X-Internal-Token | 是 | 内部服务调用令牌 |

**请求参数（Body）**

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| productId | Long | 是 | 商品ID |
| productName | String | 是 | 商品名称 |
| categoryId | Long | 是 | 分类ID |
| categoryName | String | 是 | 分类名称 |
| attributes | Array[Object] | 是 | 商品属性列表（材质/成分相关） |
| attributes[].name | String | 是 | 属性名称，如"材质"、"成分"、"配料" |
| attributes[].value | String | 是 | 属性值，如"95%棉+5%氨纶"、"烟酰胺、透明质酸" |

**请求示例**

```json
{
  "productId": 1001,
  "productName": "2024新款连衣裙 夏季清凉修身",
  "categoryId": 5,
  "categoryName": "连衣裙",
  "attributes": [
    {
      "name": "材质",
      "value": "95%棉+5%氨纶"
    },
    {
      "name": "衣长",
      "value": "70cm"
    },
    {
      "name": "袖长",
      "value": "短袖"
    }
  ]
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productId": 1001,
    "vectorId": "product_vec_1001",
    "status": "SUCCESS",
    "vectorizedAttributes": ["材质"],
    "skippedAttributes": ["衣长", "袖长"],
    "processTime": "2026-05-28 15:10:00"
  }
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
|------|------|------|
| productId | Long | 商品ID |
| vectorId | String | 在向量数据库中的唯一标识 |
| status | String | 向量化状态：`SUCCESS`（成功）、`PARTIAL`（部分成功）、`FAILED`（失败） |
| vectorizedAttributes | Array[String] | 成功向量化的属性名称列表 |
| skippedAttributes | Array[String] | 跳过的属性名称列表（非材质/成分类属性不参与向量化） |
| processTime | String | 处理完成时间 |

**业务规则**

- 仅对材质、成分、配料等与AI分析相关的属性进行向量化，其他属性（如衣长、尺码）自动跳过
- 同一商品重复调用时，会覆盖旧的向量数据（幂等操作）
- 向量化为异步处理，接口返回仅代表任务已提交
- 商品下架时应调用删除向量接口（可选），避免无效检索
- 该接口由 product-svc 在商品上架事件中通过MQ异步调用

**错误码**

| 错误码 | 说明 |
|--------|------|
| 40103 | 内部调用令牌无效 |
| 90040 | 商品ID不能为空 |
| 90041 | 商品属性列表不能为空 |
| 90042 | 向量化处理失败 |
| 90005 | AI引擎服务异常 |

---

# 附录

## 附录A：错误码汇总

### 通用错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录） |
| 403 | 禁止访问（权限不足） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 业务错误码 - AI商品分析（90001~90009）

| 错误码 | 说明 |
|--------|------|
| 90001 | 商品不存在 |
| 90002 | 商品已下架，无法分析 |
| 90003 | 商品缺少材质/成分属性信息 |
| 90004 | AI引擎调用超时，请稍后重试 |
| 90005 | AI引擎服务异常 |

### 业务错误码 - AI成分查询（90010~90019）

| 错误码 | 说明 |
|--------|------|
| 90010 | 成分名称不能为空 |
| 90011 | 成分名称超出长度限制（最大50个字符） |

### 业务错误码 - AI商品对比（90020~90029）

| 错误码 | 说明 |
|--------|------|
| 90020 | 对比商品数量不合法（最少2个，最多5个） |
| 90021 | 对比商品列表中存在重复商品 |
| 90022 | 对比商品列表不能为空 |

### 业务错误码 - 知识库管理（90030~90039）

| 错误码 | 说明 |
|--------|------|
| 90030 | 该分类正在同步中，请勿重复提交 |
| 90031 | 知识库为空，无法同步 |

### 业务错误码 - 商品向量化（90040~90049）

| 错误码 | 说明 |
|--------|------|
| 90040 | 商品ID不能为空 |
| 90041 | 商品属性列表不能为空 |
| 90042 | 向量化处理失败 |

### 权限与认证错误码

| 错误码 | 说明 |
|--------|------|
| 40101 | 未登录或Token已过期 |
| 40103 | 内部调用令牌无效 |
| 40302 | 无管理员权限 |

---

## 附录B：接口总览

### 外部接口（/api/**）

| 序号 | 接口名称 | 请求方式 | URL | 是否需登录 | 说明 |
|------|----------|----------|-----|-----------|------|
| 1 | 商品材质/成分分析 | POST | /api/ai/analyze/product | 是 | 根据商品ID生成材质/成分专业分析 |
| 2 | 成分功效查询 | POST | /api/ai/analyze/ingredient | 是 | 查询指定成分的详细功效信息 |
| 3 | 多商品对比分析 | POST | /api/ai/analyze/compare | 是 | 多商品AI对比分析与推荐 |
| 4 | 知识库列表 | GET | /api/ai/knowledge | 是（管理员） | 分页查询知识库条目 |
| 5 | 同步知识库 | POST | /api/ai/knowledge/sync | 是（管理员） | 触发知识库同步到向量数据库 |

### 内部接口（/internal/**）

| 序号 | 接口名称 | 请求方式 | URL | 调用方 | 说明 |
|------|----------|----------|-----|--------|------|
| 6 | 商品分析（内部） | POST | /internal/ai/analyze/product | product-svc | 内部调用，无需用户登录 |
| 7 | 商品对比（内部） | POST | /internal/ai/analyze/compare | product-svc | 内部调用商品对比分析 |
| 8 | 商品向量化 | POST | /internal/ai/vectorize/product | product-svc | 商品上架时异步触发向量化 |

---

## 附录C：调用时序说明

### 商品详情页AI分析流程

```
用户浏览商品详情页
       │
       ▼
  前端调用 POST /api/ai/analyze/product
       │
       ▼
  gateway-svc 路由 → ai-mall-ai (8087)
       │
       ▼
  ai-mall-ai 参数校验 + 鉴权
       │
       ▼
  ai-mall-ai 调用 product-svc 获取商品属性
       │
       ▼
  ai-mall-ai HTTP调用 ai-svc (8088)
       │
       ▼
  ai-svc 执行 RAG 流程:
    1. 从 ChromaDB 检索相关知识
    2. 结合商品属性 + 知识库上下文
    3. LLM 生成专业分析文本
       │
       ▼
  返回分析结果 → 前端展示
```

### 商品上架向量化流程

```
商家上架商品
       │
       ▼
  product-svc 更新商品状态为上架
       │
       ▼
  product-svc 发送MQ消息（商品上架事件）
       │
       ▼
  ai-mall-ai 消费MQ消息
       │
       ▼
  ai-mall-ai 调用 POST /internal/ai/vectorize/product
       │
       ▼
  ai-svc 提取材质/成分属性
       │
       ▼
  ai-svc 文本向量化 → 存入 ChromaDB
       │
       ▼
  向量化完成，后续分析可检索
```

---

## 附录D：技术说明

### RAG（检索增强生成）流程

1. **知识库构建**：将材质/成分专业数据（面料特性、化妆品成分功效、食品配料营养等）进行文本分块（Chunking），通过Embedding模型生成向量，存入ChromaDB
2. **检索阶段**：用户发起分析请求时，将商品属性信息作为Query，从ChromaDB中检索Top-K条最相关的知识片段
3. **生成阶段**：将检索到的知识片段作为上下文（Context），连同商品属性和用户问题一起送入LLM，生成通俗易懂的专业分析文本

### 向量数据库说明

- **存储引擎**：ChromaDB（本地部署）
- **Embedding模型**：text2vec系列中文模型
- **分块策略**：按段落分块，每块约500 tokens，重叠50 tokens
- **检索参数**：Top-K = 5，相似度阈值 = 0.7
