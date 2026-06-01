# 项目对齐与功能补全设计文档

## 1. 背景

项目开发过程中，实际实现与 `doc/` 目录下的设计文档、接口文档存在多处不一致。需要：
1. 修改设计文档，使其与实际实现一致
2. 补全缺失功能：公告管理、短信日志、模拟支付
3. 同步 SQL 文件

## 2. 设计文档修改清单

### 2.1 数据库设计文档.md

#### 用户表 (user) 对齐

| 设计文档字段 | 实际 SQL | 修改 |
|------------|---------|------|
| `username` VARCHAR(50) UNIQUE | 无 | 删除 |
| `email` VARCHAR(100) UNIQUE | 无 | 删除 |
| `phone` VARCHAR(20) UNIQUE | `phone` VARCHAR(20) NOT NULL UNIQUE | 保留，改为 NOT NULL |
| `birthday` DATE | 无 | 删除 |
| `last_login_time` DATETIME | 无 | 删除 |
| `last_login_ip` VARCHAR(50) | 无 | 删除 |
| `role` TINYINT (0/1/2) | `role` VARCHAR(20) DEFAULT 'user' | 改为 VARCHAR，值为 user/merchant/admin |

实际 user 表字段：id, phone, password, nickname, avatar, gender, role, status, deleted, create_time, update_time

#### 商家表 (merchant) 对齐

| 设计文档字段 | 实际 SQL | 修改 |
|------------|---------|------|
| `shop_logo` | `logo` | 改名 |
| `shop_banner` | 无 | 删除 |
| `shop_desc` TEXT | `description` TEXT | 改名 |
| `business_license` | `license_no` | 改名 |
| `id_card_front` | 无 | 删除 |
| `id_card_back` | 无 | 删除 |
| `contact_email` | 无 | 删除 |
| `province/city/district/address` | 无 | 删除 |
| `audit_time` | 无 | 删除 |
| `contact_name` | 无 | 新增（实际有） |

实际 merchant 表字段：id, user_id, shop_name, license_no, contact_phone, contact_name, description, logo, status, audit_remark, deleted, create_time, update_time

#### 订单表 (order) 对齐

| 设计文档字段 | 实际 SQL | 修改 |
|------------|---------|------|
| `freight_amount` | 无 | 删除 |
| `discount_amount` | 无 | 删除 |
| `coupon_id` | 无 | 删除 |
| `pay_type` TINYINT | `pay_method` VARCHAR(20) | 改名改类型 |
| `pay_trade_no` | 无 | 删除 |
| `receiver_name/phone/address` | 无 | 改为 `address_snapshot` JSON |
| `delivery_time` | `ship_time` | 改名 |
| `cancel_reason` | 无 | 删除 |
| `address_snapshot` JSON | 有 | 新增 |
| `remark` | 有 | 新增 |

实际 order 表字段：id, order_no, user_id, merchant_id, total_amount, pay_amount, status, address_snapshot, pay_method, pay_time, ship_time, receive_time, logistics_company, logistics_no, remark, deleted, create_time, update_time

#### 退款表 (refund) 对齐

| 设计文档字段 | 实际 SQL | 修改 |
|------------|---------|------|
| `order_item_id` | 无 | 删除 |
| `merchant_id` | 无 | 删除 |
| `refund_amount` | `amount` | 改名 |
| `refund_reason` | `reason` | 改名 |
| `refund_desc` TEXT | `description` TEXT | 改名 |
| `refund_images` JSON | `images` TEXT | 改名改类型 |
| `merchant_remark` | 无 | 删除 |
| `admin_remark` | 无 | 删除 |
| `reject_reason` | 有 | 新增 |

实际 refund 表字段：id, order_id, user_id, refund_no, amount, reason, description, images, status, reject_reason, deleted, create_time, update_time

#### Banner 表对齐

| 设计文档字段 | 实际 SQL | 修改 |
|------------|---------|------|
| `title` | 无 | 删除 |
| `image` | `image_url` | 改名 |
| `url` | `link_url` | 改名 |
| `url_type` | 无 | 删除 |
| `position` | 无 | 删除 |
| `sort_order` | `sort` | 改名 |
| `start_time/end_time` | 无 | 删除 |

实际 banner 表字段：id, image_url, link_url, sort, status, deleted, create_time, update_time

#### 公告表 (notice) 对齐

设计文档字段：id, title, content, type, status, publish_time, create_time, update_time
实际 SQL 字段：id, title, content, status, deleted, create_time, update_time

修改：删除 `type` 和 `publish_time`，新增 `deleted`

#### 操作日志表 (operation_log) 对齐

| 设计文档字段 | 实际 SQL | 修改 |
|------------|---------|------|
| `user_id` | `operator_id` | 改名 |
| `username` | `operator_name` | 改名 |
| `operation` | `operation` | 保留 |
| `status` TINYINT | `result` TINYINT | 改名 |
| `duration` INT | 无 | 删除 |

#### 物流表 (logistics) 对齐

| 设计文档字段 | 实际 SQL | 修改 |
|------------|---------|------|
| `logistics_no` | `logistics_no` | 保留 |
| `status` TINYINT | `status` TINYINT | 保留 |
| `traces` JSON | `traces` JSON | 保留 |

#### 短信日志表 (sms_log) 对齐

| 设计文档字段 | 实际 SQL | 修改 |
|------------|---------|------|
| `content` VARCHAR(500) | `template_code` VARCHAR(50) | 改为模板编码 |
| `type` TINYINT | `params` VARCHAR(500) | 改为模板参数 |
| `platform` VARCHAR(20) | 无 | 删除 |
| `error_msg` | `error_msg` | 保留 |

实际 sms_log 表字段：id, phone, template_code, params, status, error_msg, create_time

#### 架构说明修改

- 删除分库设计说明，改为单库 `ai_mall`
- 删除 Elasticsearch 说明，标注搜索使用 MySQL
- 删除 RabbitMQ/Kafka 说明，标注当前未使用消息队列

### 2.2 后端架构设计文档.md

- 端口号对齐：gateway=8087, search=8085, file=8084（与实际 application.yaml 一致）
- 标注 AI 服务（ai-mall-ai）未实现
- 标注搜索使用 MySQL LIKE，非 Elasticsearch
- 标注短信/邮件为 Mock 实现

### 2.3 前端架构设计文档.md

- 项目结构改为实际的两个独立项目：`userfront/`（Vant）和 `adminfront/`（Element Plus）
- 删除 monorepo 和共享包的描述
- 路由表对齐实际路由

### 2.4 接口文档修改

#### 订单服务接口文档.md
- 删除支付模块（POST /api/payments, 回调接口）— 改为模拟支付
- 标注订单创建的价格计算为简化实现

#### 搜索服务接口文档.md
- 删除 ES 索引同步接口（POST/DELETE /internal/search/index/product）
- 标注搜索使用 MySQL LIKE

#### 通知服务接口文档.md
- 标注短信/邮件为 Mock 实现

#### AI 服务接口文档.md
- 标注整个服务未实现

---

## 3. 新增功能设计

### 3.1 公告管理 (Notice)

#### 数据库

使用已有的 `notice` 表：

```sql
CREATE TABLE `notice` (
    `id` BIGINT PRIMARY KEY,
    `title` VARCHAR(100) NOT NULL COMMENT '公告标题',
    `content` TEXT COMMENT '公告内容',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0下架 1上架',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='公告表';
```

#### 后端

**所属服务**：ai-mall-user（公告是系统管理功能，与 config/operation_log 同属管理模块）

**新增文件**：
- `entity/Notice.java` — 实体类
- `dto/NoticeDTO.java` — 创建/更新 DTO
- `mapper/NoticeMapper.java` — Mapper 接口
- `service/NoticeService.java` — 接口
- `service/impl/NoticeServiceImpl.java` — 实现

**在 AdminController 中新增端点**：

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/notices` | 公告列表（分页） |
| POST | `/api/admin/notices` | 创建公告 |
| PUT | `/api/admin/notices/{id}` | 更新公告 |
| DELETE | `/api/admin/notices/{id}` | 删除公告（逻辑删除） |
| PUT | `/api/admin/notices/{id}/status` | 上下架 |

**新增公开端点（用户端）**：

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/notices` | 获取已发布公告列表 |

在 UserController 或新建 NoticeController 中添加。需要在网关白名单中放行 GET /api/notices。

#### 前端

**管理后台** (`adminfront/`)：
- 新增 `pages/notice/index.vue` — 公告管理页面（表格 + CRUD 对话框）
- 在侧边栏菜单添加"公告管理"入口
- 在 `api/` 目录新增 notice.js

**用户端** (`userfront/`)：
- 在首页添加公告滚动展示区域
- 在 `api/` 目录新增 notice.js

---

### 3.2 短信日志 (SmsLog)

#### 数据库

使用已有的 `sms_log` 表：

```sql
CREATE TABLE `sms_log` (
    `id` BIGINT PRIMARY KEY,
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `template_code` VARCHAR(50) COMMENT '模板编码',
    `params` VARCHAR(500) COMMENT '模板参数',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0发送中 1成功 2失败',
    `error_msg` VARCHAR(500) COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB COMMENT='短信日志表';
```

#### 后端

**所属服务**：ai-mall-notify

**新增文件**：
- `entity/SmsLog.java`
- `mapper/SmsLogMapper.java`

**修改 InternalNotificationController.sendSms()**：
- 发送短信时同时写入 sms_log 记录
- Mock 成功时 status=1，失败时 status=2

**新增管理后台查询端点**（在 NotificationController 中）：

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/notifications/sms-logs` | 短信日志列表（分页，按手机号/状态筛选） |

#### 前端

**管理后台** (`adminfront/`)：
- 在通知管理页面新增"短信日志"标签页
- 展示：手机号、模板、状态、时间

---

### 3.3 模拟支付 (Mock Payment)

#### 设计思路

不对接真实支付 SDK，模拟以下流程：
1. 用户下单后进入"待付款"状态
2. 用户点击"去付款"→ 调用模拟支付接口 → 订单直接变为"待发货"
3. 记录支付方式和支付时间

#### 后端

**所属服务**：ai-mall-order

**在 OrderController 中新增端点**：

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/orders/{id}/pay` | 模拟支付（直接标记已支付） |

**实现逻辑**：
1. 校验订单存在且属于当前用户
2. 校验订单状态为"待付款"(0)
3. 更新订单状态为"待发货"(1)
4. 设置 pay_method = "mock"，pay_time = now()
5. 返回成功

#### 前端

**用户端** (`userfront/`)：
- 订单列表页"去付款"按钮绑定点击事件，调用支付接口
- 订单详情页"去付款"按钮同理
- 支付成功后跳转到订单详情页

---

## 4. SQL 文件同步

将设计文档中的表结构变更同步到 `backend/aiShopBackend/sql/ai_mall.sql`，确保 SQL 文件与设计文档一致。

主要变更：
- notice 表：与设计文档对齐（已一致）
- sms_log 表：与设计文档对齐（已一致）
- 其他表：按设计文档修改后的结构更新

---

## 5. 网关白名单更新

新增需要放行的端点：
- `GET /api/notices` — 用户端获取公告列表

---

## 6. 涉及文件清单

### 设计文档修改
- `doc/数据库设计文档.md`
- `doc/后端架构设计文档.md`
- `doc/前端架构设计文档.md`
- `doc/订单服务接口文档.md`
- `doc/搜索服务接口文档.md`
- `doc/通知服务接口文档.md`
- `doc/AI服务接口文档.md`

### 后端新增/修改
- `ai-mall-user`: Notice 实体/Mapper/Service/DTO, AdminController 新增公告端点, NoticeController（公开端点）
- `ai-mall-notify`: SmsLog 实体/Mapper, InternalNotificationController 修改, NotificationController 新增日志端点
- `ai-mall-order`: OrderController 新增模拟支付端点
- `ai-mall-gateway`: AuthFilter 白名单新增 GET /api/notices
- `sql/ai_mall.sql`: 同步表结构

### 前端新增/修改
- `adminfront/`: 公告管理页面, 短信日志标签页, 侧边栏菜单, API 文件
- `userfront/`: 首页公告展示, 订单支付按钮事件绑定, API 文件
