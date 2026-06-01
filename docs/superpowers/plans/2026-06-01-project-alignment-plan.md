# 项目对齐与功能补全实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修改设计文档使其与实际实现对齐，补全公告管理、短信日志、模拟支付三个缺失功能，同步SQL文件。

**Architecture:** 分三阶段执行：第一阶段修改设计文档（纯文档工作），第二阶段补全后端功能（Java/Spring Boot），第三阶段补全前端页面（Vue3/Vant/Element Plus）。每个阶段独立可交付。

**Tech Stack:** Spring Boot 3.2.5, MyBatis-Plus 3.5.5, Vue 3, Vant 4, Element Plus, MySQL 8.0

---

## 第一阶段：修改设计文档

### Task 1: 修改数据库设计文档

**Files:**
- Modify: `doc/数据库设计文档.md`

- [ ] **Step 1: 修改用户表 (user) 定义**

打开 `doc/数据库设计文档.md`，找到 `#### 2.1.1 用户表 (user)` 部分，将字段表替换为：

```markdown
| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | - | 主键(雪花算法) |
| phone | VARCHAR(20) | 是 | - | 手机号(唯一) |
| password | VARCHAR(100) | 是 | - | 密码(BCrypt加密) |
| nickname | VARCHAR(50) | 否 | - | 昵称 |
| avatar | VARCHAR(255) | 否 | - | 头像URL |
| gender | TINYINT | 否 | 0 | 性别: 0-未知, 1-男, 2-女 |
| role | VARCHAR(20) | 是 | 'user' | 角色: user/merchant/admin |
| status | TINYINT | 是 | 1 | 状态: 0-禁用, 1-正常 |
| deleted | TINYINT | 是 | 0 | 逻辑删除: 0-未删除, 1-已删除 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引：**
- PRIMARY KEY (id)
- UNIQUE KEY (phone)
- INDEX (role)
```

同步修改底部的建表 SQL：

```sql
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY,
    `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色: user/merchant/admin',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_phone` (`phone`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB COMMENT='用户表';
```

- [ ] **Step 2: 修改商家表 (merchant) 定义**

找到 `#### 2.1.2 商家信息表 (merchant)` 部分，将字段表替换为：

```markdown
| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | - | 主键(雪花算法) |
| user_id | BIGINT | 是 | - | 关联用户ID |
| shop_name | VARCHAR(100) | 是 | - | 店铺名称 |
| license_no | VARCHAR(50) | 否 | - | 营业执照编号 |
| contact_phone | VARCHAR(20) | 否 | - | 联系电话 |
| contact_name | VARCHAR(50) | 否 | - | 联系人 |
| description | TEXT | 否 | - | 店铺描述 |
| logo | VARCHAR(255) | 否 | - | 店铺Logo |
| status | TINYINT | 是 | 0 | 状态: 0-待审核, 1-已通过, 2-已拒绝, 3-已关闭 |
| audit_remark | VARCHAR(500) | 否 | - | 审核备注 |
| deleted | TINYINT | 是 | 0 | 逻辑删除 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引：**
- PRIMARY KEY (id)
- UNIQUE KEY (user_id)
- INDEX (status)
```

同步修改底部建表 SQL。

- [ ] **Step 3: 修改订单表 (order) 定义**

找到 `#### 2.3.1 订单表 (order)` 部分，将字段表替换为：

```markdown
| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | - | 主键(雪花算法) |
| order_no | VARCHAR(32) | 是 | - | 订单号(唯一) |
| user_id | BIGINT | 是 | - | 用户ID |
| merchant_id | BIGINT | 是 | - | 商家ID |
| total_amount | DECIMAL(10,2) | 是 | - | 总金额 |
| pay_amount | DECIMAL(10,2) | 否 | - | 实付金额 |
| status | TINYINT | 是 | 0 | 状态: 0-待付款, 1-待发货, 2-待收货, 3-已完成, 4-已取消, 5-已退款 |
| address_snapshot | JSON | 否 | - | 地址快照 |
| pay_method | VARCHAR(20) | 否 | - | 支付方式: alipay/wechat/mock |
| pay_time | DATETIME | 否 | - | 支付时间 |
| ship_time | DATETIME | 否 | - | 发货时间 |
| receive_time | DATETIME | 否 | - | 收货时间 |
| logistics_company | VARCHAR(50) | 否 | - | 物流公司 |
| logistics_no | VARCHAR(50) | 否 | - | 物流单号 |
| remark | VARCHAR(500) | 否 | - | 备注 |
| deleted | TINYINT | 是 | 0 | 逻辑删除 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引：**
- PRIMARY KEY (id)
- UNIQUE KEY (order_no)
- INDEX (user_id)
- INDEX (merchant_id)
- INDEX (status)
- INDEX (create_time)
```

- [ ] **Step 4: 修改退款表 (refund) 定义**

找到退款表部分，替换为：

```markdown
| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | - | 主键 |
| refund_no | VARCHAR(32) | 是 | - | 退款单号(唯一) |
| order_id | BIGINT | 是 | - | 订单ID |
| user_id | BIGINT | 是 | - | 用户ID |
| amount | DECIMAL(10,2) | 是 | - | 退款金额 |
| reason | VARCHAR(500) | 否 | - | 退款原因 |
| description | TEXT | 否 | - | 详细描述 |
| images | TEXT | 否 | - | 凭证图片(JSON数组) |
| status | TINYINT | 是 | 0 | 状态: 0-待审核, 1-已同意, 2-已拒绝, 3-已完成 |
| reject_reason | VARCHAR(500) | 否 | - | 拒绝原因 |
| deleted | TINYINT | 是 | 0 | 逻辑删除 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
```

- [ ] **Step 5: 修改 Banner 表定义**

找到轮播图表部分，替换为：

```markdown
| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | - | 主键 |
| image_url | VARCHAR(500) | 是 | - | 图片URL |
| link_url | VARCHAR(500) | 否 | - | 跳转链接 |
| sort | INT | 是 | 0 | 排序序号 |
| status | TINYINT | 是 | 1 | 状态: 0-禁用, 1-正常 |
| deleted | TINYINT | 是 | 0 | 逻辑删除 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
```

- [ ] **Step 6: 修改公告表 (notice) 定义**

找到公告表部分，替换为：

```markdown
| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | - | 主键 |
| title | VARCHAR(100) | 是 | - | 公告标题 |
| content | TEXT | 否 | - | 公告内容 |
| status | TINYINT | 是 | 1 | 状态: 0-下架, 1-上架 |
| deleted | TINYINT | 是 | 0 | 逻辑删除 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
```

- [ ] **Step 7: 修改操作日志表 (operation_log) 定义**

找到操作日志表部分，替换为：

```markdown
| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | - | 主键 |
| module | VARCHAR(50) | 否 | - | 操作模块 |
| operation | VARCHAR(100) | 否 | - | 操作描述 |
| method | VARCHAR(10) | 否 | - | 请求方式 |
| url | VARCHAR(500) | 否 | - | 请求URL |
| operator_id | BIGINT | 否 | - | 操作人ID |
| operator_name | VARCHAR(50) | 否 | - | 操作人名称 |
| ip | VARCHAR(50) | 否 | - | 操作IP |
| params | TEXT | 否 | - | 请求参数 |
| result | TINYINT | 是 | 1 | 结果: 0-失败, 1-成功 |
| error_msg | TEXT | 否 | - | 错误信息 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
```

- [ ] **Step 8: 修改短信日志表 (sms_log) 定义**

找到短信日志表部分，替换为：

```markdown
| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | 是 | - | 主键 |
| phone | VARCHAR(20) | 是 | - | 手机号 |
| template_code | VARCHAR(50) | 否 | - | 模板编码 |
| params | VARCHAR(500) | 否 | - | 模板参数 |
| status | TINYINT | 是 | 0 | 状态: 0-发送中, 1-成功, 2-失败 |
| error_msg | VARCHAR(500) | 否 | - | 错误信息 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
```

- [ ] **Step 9: 添加架构说明**

在文档开头 `1.1 技术架构` 表格后添加：

```markdown
> **注意：** 实际实现采用单库架构（ai_mall），未采用分库策略。搜索服务使用 MySQL LIKE 查询，未集成 Elasticsearch。短信和邮件服务当前为 Mock 实现。
```

- [ ] **Step 10: 提交**

```bash
git add doc/数据库设计文档.md
git commit -m "docs: 对齐数据库设计文档与实际实现"
```

---

### Task 2: 修改后端架构设计文档

**Files:**
- Modify: `doc/后端架构设计文档.md`

- [ ] **Step 1: 修改端口号**

找到微服务列表表格，将端口号修改为：
- gateway-svc: 8087（非 8080）
- search-svc: 8085（非 8084）
- file-svc: 8084（非 8085）
- ai-mall-ai: 未配置端口

- [ ] **Step 2: 标注未实现的服务**

在 AI 服务部分添加：

```markdown
> **状态：未实现。** ai-mall-ai 模块当前为空壳，仅包含 Application 类，无任何业务代码。
```

- [ ] **Step 3: 标注搜索实现方式**

在搜索服务部分添加：

```markdown
> **注意：** 当前搜索使用 MySQL LIKE 查询实现，未集成 Elasticsearch。
```

- [ ] **Step 4: 提交**

```bash
git add doc/后端架构设计文档.md
git commit -m "docs: 对齐后端架构设计文档与实际实现"
```

---

### Task 3: 修改前端架构设计文档

**Files:**
- Modify: `doc/前端架构设计文档.md`

- [ ] **Step 1: 修改项目结构**

将 monorepo 结构描述改为实际的两个独立项目：

```markdown
### 1.2 项目结构

项目包含两个独立的前端应用：

- `userfront/` — 用户端移动端商城（Vant UI）
- `adminfront/` — 管理后台（Element Plus）

两个项目独立构建部署，不使用 monorepo。
```

- [ ] **Step 2: 修改路由表**

将路由定义改为实际实现的路由（参考 `userfront/src/router/index.js` 和 `adminfront/src/router/index.js`）。

- [ ] **Step 3: 删除共享包描述**

删除 `packages/` 相关的共享组件、工具函数、API 模块等描述。

- [ ] **Step 4: 提交**

```bash
git add doc/前端架构设计文档.md
git commit -m "docs: 对齐前端架构设计文档与实际实现"
```

---

### Task 4: 修改订单服务接口文档

**Files:**
- Modify: `doc/订单服务接口文档.md`

- [ ] **Step 1: 删除支付模块接口**

删除 `POST /api/payments`、`POST /api/payments/notify/alipay`、`POST /api/payments/notify/wechat` 三个端点的描述。

替换为：

```markdown
### 5. 支付模块

当前为模拟支付实现：

| 序号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 1 | POST | /api/orders/{id}/pay | 模拟支付（直接标记已支付） | 登录用户 |
```

- [ ] **Step 2: 提交**

```bash
git add doc/订单服务接口文档.md
git commit -m "docs: 对齐订单服务接口文档，删除未实现的支付接口"
```

---

### Task 5: 修改搜索服务接口文档

**Files:**
- Modify: `doc/搜索服务接口文档.md`

- [ ] **Step 1: 删除 ES 索引同步接口**

删除内部接口部分的：
- `POST /internal/search/index/product`
- `DELETE /internal/search/index/product/{id}`
- `POST /internal/search/index/product/batch`

在文档开头添加：

```markdown
> **注意：** 当前搜索使用 MySQL LIKE 查询实现，未集成 Elasticsearch。
```

- [ ] **Step 2: 提交**

```bash
git add doc/搜索服务接口文档.md
git commit -m "docs: 对齐搜索服务接口文档，删除未实现的ES接口"
```

---

### Task 6: 修改通知服务接口文档

**Files:**
- Modify: `doc/通知服务接口文档.md`

- [ ] **Step 1: 标注 Mock 实现**

在短信和邮件接口部分添加：

```markdown
> **注意：** 短信和邮件发送当前为 Mock 实现，仅记录日志到 sms_log 表，不实际发送。
```

- [ ] **Step 2: 提交**

```bash
git add doc/通知服务接口文档.md
git commit -m "docs: 标注通知服务短信/邮件为Mock实现"
```

---

### Task 7: 修改 AI 服务接口文档

**Files:**
- Modify: `doc/AI服务接口文档.md`

- [ ] **Step 1: 标注未实现**

在文档开头添加：

```markdown
> **状态：未实现。** ai-mall-ai 模块当前为空壳，以下接口均为设计规划，尚未开发。
```

- [ ] **Step 2: 提交**

```bash
git add doc/AI服务接口文档.md
git commit -m "docs: 标注AI服务接口文档为未实现状态"
```

---

## 第二阶段：补全后端功能

### Task 8: 公告管理 — 后端实体和 Mapper

**Files:**
- Create: `backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/entity/Notice.java`
- Create: `backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/mapper/NoticeMapper.java`

- [ ] **Step 1: 创建 Notice 实体**

```java
package com.sxpi.pan.aimalluser.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sxpi.pan.aimallcommon.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notice")
public class Notice extends BaseEntity {
    private String title;
    private String content;
    private Integer status;
}
```

- [ ] **Step 2: 创建 NoticeMapper**

```java
package com.sxpi.pan.aimalluser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sxpi.pan.aimalluser.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
```

- [ ] **Step 3: 提交**

```bash
git add backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/entity/Notice.java
git add backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/mapper/NoticeMapper.java
git commit -m "feat: 添加公告实体和Mapper"
```

---

### Task 9: 公告管理 — DTO 和 Service

**Files:**
- Create: `backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/dto/NoticeDTO.java`
- Create: `backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/service/NoticeService.java`
- Create: `backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/service/impl/NoticeServiceImpl.java`

- [ ] **Step 1: 创建 NoticeDTO**

```java
package com.sxpi.pan.aimalluser.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticeDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    private String content;
}
```

- [ ] **Step 2: 创建 NoticeService 接口**

```java
package com.sxpi.pan.aimalluser.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimalluser.dto.NoticeDTO;
import com.sxpi.pan.aimalluser.entity.Notice;

public interface NoticeService {
    Page<Notice> getNoticeList(Integer page, Integer size);
    Page<Notice> getActiveNoticeList(Integer page, Integer size);
    void addNotice(NoticeDTO dto);
    void updateNotice(Long id, NoticeDTO dto);
    void deleteNotice(Long id);
    void updateStatus(Long id, Integer status);
}
```

- [ ] **Step 3: 创建 NoticeServiceImpl**

```java
package com.sxpi.pan.aimalluser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimalluser.dto.NoticeDTO;
import com.sxpi.pan.aimalluser.entity.Notice;
import com.sxpi.pan.aimalluser.mapper.NoticeMapper;
import com.sxpi.pan.aimalluser.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public Page<Notice> getNoticeList(Integer page, Integer size) {
        return noticeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime));
    }

    @Override
    public Page<Notice> getActiveNoticeList(Integer page, Integer size) {
        return noticeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getStatus, 1)
                        .orderByDesc(Notice::getCreateTime));
    }

    @Override
    public void addNotice(NoticeDTO dto) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(dto, notice);
        notice.setStatus(1);
        noticeMapper.insert(notice);
    }

    @Override
    public void updateNotice(Long id, NoticeDTO dto) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(40420, "公告不存在");
        }
        BeanUtils.copyProperties(dto, notice);
        noticeMapper.updateById(notice);
    }

    @Override
    public void deleteNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(40420, "公告不存在");
        }
        noticeMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(40420, "公告不存在");
        }
        notice.setStatus(status);
        noticeMapper.updateById(notice);
    }
}
```

- [ ] **Step 4: 提交**

```bash
git add backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/dto/NoticeDTO.java
git add backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/service/NoticeService.java
git add backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/service/impl/NoticeServiceImpl.java
git commit -m "feat: 添加公告DTO和Service"
```

---

### Task 10: 公告管理 — Controller 端点

**Files:**
- Modify: `backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/controller/AdminController.java`
- Create: `backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/controller/NoticeController.java`

- [ ] **Step 1: 在 AdminController 中注入 NoticeService**

在 AdminController 类中添加：

```java
private final NoticeService noticeService;
```

添加 import：

```java
import com.sxpi.pan.aimalluser.dto.NoticeDTO;
import com.sxpi.pan.aimalluser.entity.Notice;
import com.sxpi.pan.aimalluser.service.NoticeService;
import jakarta.validation.Valid;
```

- [ ] **Step 2: 在 AdminController 中添加公告端点**

在 AdminController 末尾（最后一个 `}` 之前）添加：

```java
// ==================== 公告管理 ====================

@GetMapping("/notices")
public Result<Page<Notice>> getNoticeList(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size) {
    return Result.success(noticeService.getNoticeList(page, size));
}

@PostMapping("/notices")
public Result<Void> addNotice(@Valid @RequestBody NoticeDTO dto) {
    noticeService.addNotice(dto);
    return Result.success();
}

@PutMapping("/notices/{id}")
public Result<Void> updateNotice(@PathVariable Long id, @Valid @RequestBody NoticeDTO dto) {
    noticeService.updateNotice(id, dto);
    return Result.success();
}

@DeleteMapping("/notices/{id}")
public Result<Void> deleteNotice(@PathVariable Long id) {
    noticeService.deleteNotice(id);
    return Result.success();
}

@PutMapping("/notices/{id}/status")
public Result<Void> updateNoticeStatus(@PathVariable Long id, @RequestParam Integer status) {
    noticeService.updateStatus(id, status);
    return Result.success();
}
```

- [ ] **Step 3: 创建公开的 NoticeController**

```java
package com.sxpi.pan.aimalluser.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.result.Result;
import com.sxpi.pan.aimalluser.entity.Notice;
import com.sxpi.pan.aimalluser.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public Result<Page<Notice>> getActiveNotices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(noticeService.getActiveNoticeList(page, size));
    }
}
```

- [ ] **Step 4: 在网关添加白名单**

修改 `backend/aiShopBackend/ai-mall-gateway/src/main/java/.../filter/AuthFilter.java`，在白名单中添加：

```
/api/notices
```

- [ ] **Step 5: 在网关添加路由**

修改 `backend/aiShopBackend/ai-mall-gateway/src/main/resources/application.yml`，在 user-svc 的路径谓词中添加：

```
- /api/notices/**
```

- [ ] **Step 6: 提交**

```bash
git add backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/controller/AdminController.java
git add backend/aiShopBackend/ai-mall-user/src/main/java/com/sxpi/pan/aimalluser/controller/NoticeController.java
git add backend/aiShopBackend/ai-mall-gateway/
git commit -m "feat: 添加公告管理Controller和网关路由"
```

---

### Task 11: 短信日志 — 后端实体和记录逻辑

**Files:**
- Create: `backend/aiShopBackend/ai-mall-notify/src/main/java/com/sxpi/pan/aimallnotify/entity/SmsLog.java`
- Create: `backend/aiShopBackend/ai-mall-notify/src/main/java/com/sxpi/pan/aimallnotify/mapper/SmsLogMapper.java`
- Modify: `backend/aiShopBackend/ai-mall-notify/src/main/java/com/sxpi/pan/aimallnotify/service/impl/NotificationServiceImpl.java`

- [ ] **Step 1: 创建 SmsLog 实体**

```java
package com.sxpi.pan.aimallnotify.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sms_log")
public class SmsLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String phone;
    private String templateCode;
    private String params;
    private Integer status;
    private String errorMsg;
    private LocalDateTime createTime;
}
```

- [ ] **Step 2: 创建 SmsLogMapper**

```java
package com.sxpi.pan.aimallnotify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sxpi.pan.aimallnotify.entity.SmsLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsLogMapper extends BaseMapper<SmsLog> {
}
```

- [ ] **Step 3: 修改 NotificationServiceImpl 的 sendSms 方法**

在 `sendSms` 方法中，发送短信前后记录 sms_log：

```java
@Autowired
private SmsLogMapper smsLogMapper;

@Override
public void sendSms(String phone, String templateCode, Map<String, String> params) {
    SmsLog log = new SmsLog();
    log.setPhone(phone);
    log.setTemplateCode(templateCode);
    log.setParams(params != null ? params.toString() : null);
    try {
        // Mock 发送
        log.info("[MOCK] 短信发送: phone={}, template={}, params={}", phone, templateCode, params);
        log.setStatus(1);
    } catch (Exception e) {
        log.setStatus(2);
        log.setErrorMsg(e.getMessage());
    }
    smsLogMapper.insert(log);
}
```

注意：需要将方法参数中的 `log` 变量名改为 `smsLog` 以避免与 Lombok 的 `log` 冲突。

- [ ] **Step 4: 在 NotificationController 添加日志查询端点**

```java
@GetMapping("/sms-logs")
public Result<Page<SmsLog>> getSmsLogs(
        @RequestParam(required = false) String phone,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size) {
    LambdaQueryWrapper<SmsLog> wrapper = new LambdaQueryWrapper<>();
    if (phone != null && !phone.isEmpty()) {
        wrapper.eq(SmsLog::getPhone, phone);
    }
    wrapper.orderByDesc(SmsLog::getCreateTime);
    return Result.success(smsLogMapper.selectPage(new Page<>(page, size), wrapper));
}
```

需要注入 `SmsLogMapper`。

- [ ] **Step 5: 提交**

```bash
git add backend/aiShopBackend/ai-mall-notify/
git commit -m "feat: 添加短信日志实体和记录逻辑"
```

---

### Task 12: 模拟支付 — 后端端点

**Files:**
- Modify: `backend/aiShopBackend/ai-mall-order/src/main/java/com/sxpi/pan/aimallorder/controller/OrderController.java`
- Modify: `backend/aiShopBackend/ai-mall-order/src/main/java/com/sxpi/pan/aimallorder/service/OrderService.java`
- Modify: `backend/aiShopBackend/ai-mall-order/src/main/java/com/sxpi/pan/aimallorder/service/impl/OrderServiceImpl.java`

- [ ] **Step 1: 在 OrderService 接口添加方法**

```java
void payOrder(Long id, Long userId);
```

- [ ] **Step 2: 在 OrderServiceImpl 添加实现**

```java
@Override
public void payOrder(Long id, Long userId) {
    Order order = orderMapper.selectById(id);
    if (order == null) {
        throw new BusinessException(40416, "订单不存在");
    }
    if (!order.getUserId().equals(userId)) {
        throw new BusinessException(40301, "无权操作此订单");
    }
    if (order.getStatus() != 0) {
        throw new BusinessException(40040, "订单状态不允许支付");
    }
    order.setStatus(1);
    order.setPayMethod("mock");
    order.setPayTime(LocalDateTime.now());
    orderMapper.updateById(order);
}
```

需要 import `java.time.LocalDateTime`。

- [ ] **Step 3: 在 OrderController 添加端点**

```java
@PostMapping("/{id}/pay")
public Result<Void> payOrder(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
    orderService.payOrder(id, userId);
    return Result.success();
}
```

- [ ] **Step 4: 提交**

```bash
git add backend/aiShopBackend/ai-mall-order/
git commit -m "feat: 添加模拟支付端点"
```

---

## 第三阶段：补全前端功能

### Task 13: 公告管理 — 管理后台页面

**Files:**
- Create: `adminfront/src/pages/notice/index.vue`
- Create: `adminfront/src/api/notice.js`
- Modify: `adminfront/src/layout/DefaultLayout.vue` (侧边栏菜单)
- Modify: `adminfront/src/router/index.js` (路由)

- [ ] **Step 1: 创建 notice API 文件**

```javascript
import request from './request'

export function getNoticeList(params) { return request.get('/admin/notices', { params }) }
export function addNotice(data) { return request.post('/admin/notices', data) }
export function updateNotice(id, data) { return request.put(`/admin/notices/${id}`, data) }
export function deleteNotice(id) { return request.delete(`/admin/notices/${id}`) }
export function updateNoticeStatus(id, status) { return request.put(`/admin/notices/${id}/status`, null, { params: { status } }) }
```

- [ ] **Step 2: 创建公告管理页面**

创建 `adminfront/src/pages/notice/index.vue`，包含：
- el-table 展示公告列表（标题、状态、时间）
- 新增/编辑对话框（标题输入、内容 textarea）
- 上下架按钮
- 删除按钮
- 分页

- [ ] **Step 3: 添加路由**

在 `adminfront/src/router/index.js` 中添加：

```javascript
{
  path: '/notice',
  component: () => import('../pages/notice/index.vue'),
  meta: { title: '公告管理' }
}
```

- [ ] **Step 4: 添加侧边栏菜单**

在 `DefaultLayout.vue` 的菜单列表中添加"公告管理"项。

- [ ] **Step 5: 提交**

```bash
git add adminfront/src/pages/notice/
git add adminfront/src/api/notice.js
git add adminfront/src/router/index.js
git add adminfront/src/layout/DefaultLayout.vue
git commit -m "feat: 添加公告管理前端页面"
```

---

### Task 14: 短信日志 — 管理后台页面

**Files:**
- Modify: `adminfront/src/pages/notification/index.vue` (添加短信日志标签页)
- Modify: `adminfront/src/api/notification.js` (添加短信日志 API)

- [ ] **Step 1: 在 notification API 中添加短信日志接口**

```javascript
export function getSmsLogs(params) { return request.get('/notifications/sms-logs', { params }) }
```

- [ ] **Step 2: 在通知管理页面添加"短信日志"标签页**

使用 el-tabs 组件，在现有通知管理基础上添加"短信日志"标签页，展示：
- 手机号、模板编码、状态、时间
- 支持按手机号筛选
- 分页

- [ ] **Step 3: 提交**

```bash
git add adminfront/src/pages/notification/index.vue
git add adminfront/src/api/notification.js
git commit -m "feat: 添加短信日志查询页面"
```

---

### Task 15: 模拟支付 — 用户端按钮绑定

**Files:**
- Modify: `userfront/src/pages/order/list.vue`
- Modify: `userfront/src/pages/order/detail.vue`
- Modify: `userfront/src/api/order.js`

- [ ] **Step 1: 在 order API 中添加支付接口**

```javascript
export const payOrder = (id) => request.post(`/orders/${id}/pay`)
```

- [ ] **Step 2: 修改订单列表页的"去付款"按钮**

找到"去付款"按钮，添加点击事件：

```vue
<van-button type="danger" size="small" @click.stop="handlePay(row.id)">去付款</van-button>
```

添加方法：

```javascript
const handlePay = async (id) => {
  try {
    await payOrder(id)
    showToast('支付成功')
    loadOrders()
  } catch (e) {
    showToast(e.message || '支付失败')
  }
}
```

- [ ] **Step 3: 修改订单详情页的"去付款"按钮**

同样绑定 `handlePay` 事件，支付成功后刷新订单详情。

- [ ] **Step 4: 提交**

```bash
git add userfront/src/pages/order/list.vue
git add userfront/src/pages/order/detail.vue
git add userfront/src/api/order.js
git commit -m "feat: 绑定订单支付按钮事件"
```

---

### Task 16: 首页公告展示

**Files:**
- Modify: `userfront/src/pages/home/index.vue`
- Create: `userfront/src/api/notice.js`

- [ ] **Step 1: 创建 notice API 文件**

```javascript
import request from './request'

export const getNotices = (params) => request.get('/notices', { params })
```

- [ ] **Step 2: 在首页添加公告展示区域**

在 banner 轮播图下方、分类导航上方，添加公告滚动展示：

```vue
<van-notice-bar v-if="notices.length" left-icon="volume-o" :text="notices.map(n => n.title).join(' | ')" />
```

在 script 中加载公告数据：

```javascript
import { getNotices } from '../../api/notice'

const notices = ref([])

onMounted(async () => {
  // ...existing code...
  try {
    const noticeRes = await getNotices({ page: 1, size: 5 })
    notices.value = noticeRes?.records || []
  } catch (e) {
    console.error('公告加载失败:', e.message)
  }
})
```

- [ ] **Step 3: 提交**

```bash
git add userfront/src/pages/home/index.vue
git add userfront/src/api/notice.js
git commit -m "feat: 首页添加公告滚动展示"
```

---

## 第四阶段：同步 SQL 文件

### Task 17: 同步 ai_mall.sql

**Files:**
- Modify: `backend/aiShopBackend/sql/ai_mall.sql`

- [ ] **Step 1: 核对所有表结构**

逐表对比 `ai_mall.sql` 和修改后的 `数据库设计文档.md`，确保字段定义一致。

- [ ] **Step 2: 更新种子数据**

确保初始数据完整：
- 1 个管理员用户
- 4 个初始分类
- 如需要，添加初始公告

- [ ] **Step 3: 提交**

```bash
git add backend/aiShopBackend/sql/ai_mall.sql
git commit -m "docs: 同步SQL文件与设计文档"
```

---

## 完成检查

- [ ] 所有设计文档已修改并与实际实现对齐
- [ ] 公告管理功能完整（后端 CRUD + 管理后台页面 + 用户端展示）
- [ ] 短信日志功能完整（发送时记录 + 管理后台查询）
- [ ] 模拟支付功能完整（后端端点 + 前端按钮绑定）
- [ ] SQL 文件与设计文档一致
- [ ] 网关白名单和路由已更新
- [ ] 所有代码已提交
