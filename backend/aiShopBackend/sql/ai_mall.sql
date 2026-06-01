-- AI商城数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ai_mall;

-- ==================== 用户相关表 ====================

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY,
    `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色: user/merchant/admin',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删 1已删',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_phone` (`phone`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB COMMENT='用户表';

-- 商家表
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
    `id` BIGINT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `shop_name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `license_no` VARCHAR(50) COMMENT '营业执照编号',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `contact_name` VARCHAR(50) COMMENT '联系人',
    `description` TEXT COMMENT '店铺描述',
    `logo` VARCHAR(255) COMMENT '店铺Logo',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0待审核 1审核通过 2审核拒绝 3禁用',
    `audit_remark` VARCHAR(500) COMMENT '审核备注',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='商家表';

-- 收货地址表
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
    `id` BIGINT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收件人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收件人手机号',
    `province` VARCHAR(20) COMMENT '省份',
    `city` VARCHAR(20) COMMENT '城市',
    `district` VARCHAR(20) COMMENT '区/县',
    `detail_address` VARCHAR(200) COMMENT '详细地址',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认: 0否 1是',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='收货地址表';

-- 收藏表
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` BIGINT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB COMMENT='收藏表';

-- ==================== 商品相关表 ====================

-- 分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID, 0为顶级',
    `icon` VARCHAR(255) COMMENT '分类图标',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB COMMENT='商品分类表';

-- 属性模板表
DROP TABLE IF EXISTS `attribute_template`;
CREATE TABLE `attribute_template` (
    `id` BIGINT PRIMARY KEY,
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '属性名称',
    `input_type` VARCHAR(20) DEFAULT 'text' COMMENT '输入类型: text/select/multi_select/textarea',
    `required` TINYINT DEFAULT 0 COMMENT '是否必填: 0否 1是',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_category_id` (`category_id`)
) ENGINE=InnoDB COMMENT='属性模板表';

-- 属性选项表
DROP TABLE IF EXISTS `attribute_option`;
CREATE TABLE `attribute_option` (
    `id` BIGINT PRIMARY KEY,
    `template_id` BIGINT NOT NULL COMMENT '属性模板ID',
    `value` VARCHAR(100) NOT NULL COMMENT '选项值',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_template_id` (`template_id`)
) ENGINE=InnoDB COMMENT='属性选项表';

-- 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT PRIMARY KEY,
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `title` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `subtitle` VARCHAR(500) COMMENT '副标题',
    `main_image` VARCHAR(500) COMMENT '主图URL',
    `images` TEXT COMMENT '商品图片列表(JSON数组)',
    `video` VARCHAR(255) COMMENT '视频URL',
    `description` TEXT COMMENT '商品详情(富文本)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `market_price` DECIMAL(10,2) COMMENT '市场价',
    `cost_price` DECIMAL(10,2) COMMENT '成本价',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `views` INT DEFAULT 0 COMMENT '浏览量',
    `weight` DECIMAL(10,2) COMMENT '重量(kg)',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0下架 1上架 2待审核',
    `is_hot` TINYINT DEFAULT 0 COMMENT '是否热销: 0否 1是',
    `is_new` TINYINT DEFAULT 0 COMMENT '是否新品: 0否 1是',
    `is_recommend` TINYINT DEFAULT 0 COMMENT '是否推荐: 0否 1是',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `audit_remark` VARCHAR(500) COMMENT '审核备注',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_merchant_id` (`merchant_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`),
    FULLTEXT INDEX `ft_name` (`title`, `subtitle`)
) ENGINE=InnoDB COMMENT='商品表';

-- 商品属性表
DROP TABLE IF EXISTS `product_attribute`;
CREATE TABLE `product_attribute` (
    `id` BIGINT PRIMARY KEY,
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `template_id` BIGINT NOT NULL COMMENT '属性模板ID',
    `value` TEXT COMMENT '属性值',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB COMMENT='商品属性表';

-- SKU表
DROP TABLE IF EXISTS `sku`;
CREATE TABLE `sku` (
    `id` BIGINT PRIMARY KEY,
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'SKU价格',
    `stock` INT DEFAULT 0 COMMENT 'SKU库存',
    `attributes` JSON COMMENT '规格属性, 如{"颜色":"红色","尺码":"M"}',
    `image` VARCHAR(500) COMMENT 'SKU图片',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB COMMENT='SKU表';

-- ==================== 订单相关表 ====================

-- 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id` BIGINT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT COMMENT 'SKU ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `checked` TINYINT DEFAULT 1 COMMENT '是否选中: 0否 1是',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='购物车表';

-- 订单表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id` BIGINT PRIMARY KEY,
    `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
    `pay_amount` DECIMAL(10,2) COMMENT '实付金额',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0待付款 1待发货 2待收货 3已完成 4已取消 5已退款',
    `address_snapshot` JSON COMMENT '地址快照',
    `pay_method` VARCHAR(20) COMMENT '支付方式: alipay/wechat',
    `pay_time` DATETIME COMMENT '支付时间',
    `ship_time` DATETIME COMMENT '发货时间',
    `receive_time` DATETIME COMMENT '收货时间',
    `logistics_company` VARCHAR(50) COMMENT '物流公司',
    `logistics_no` VARCHAR(50) COMMENT '物流单号',
    `remark` VARCHAR(500) COMMENT '备注',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_merchant_id` (`merchant_id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='订单表';

-- 订单项表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT PRIMARY KEY,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT COMMENT 'SKU ID',
    `product_title` VARCHAR(200) COMMENT '商品标题(快照)',
    `product_image` VARCHAR(500) COMMENT '商品图片(快照)',
    `sku_attributes` JSON COMMENT 'SKU属性(快照)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB COMMENT='订单项表';

-- 退款表
DROP TABLE IF EXISTS `refund`;
CREATE TABLE `refund` (
    `id` BIGINT PRIMARY KEY,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `refund_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '退款单号',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `reason` VARCHAR(500) COMMENT '退款原因',
    `description` TEXT COMMENT '详细描述',
    `images` TEXT COMMENT '凭证图片(JSON数组)',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0待审核 1已同意 2已拒绝 3已完成',
    `reject_reason` VARCHAR(500) COMMENT '拒绝原因',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='退款表';

-- 物流表
DROP TABLE IF EXISTS `logistics`;
CREATE TABLE `logistics` (
    `id` BIGINT PRIMARY KEY,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `logistics_company` VARCHAR(50) COMMENT '物流公司',
    `logistics_no` VARCHAR(50) COMMENT '物流单号',
    `status` TINYINT DEFAULT 0 COMMENT '状态',
    `traces` JSON COMMENT '物流轨迹',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB COMMENT='物流表';

-- ==================== 系统相关表 ====================

-- 轮播图表
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
    `id` BIGINT PRIMARY KEY,
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `link_url` VARCHAR(500) COMMENT '跳转链接',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='轮播图表';

-- 公告表
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
    `id` BIGINT PRIMARY KEY,
    `title` VARCHAR(100) NOT NULL COMMENT '公告标题',
    `content` TEXT COMMENT '公告内容',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0下架 1上架',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='公告表';

-- 系统配置表
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
    `id` BIGINT PRIMARY KEY,
    `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    `config_value` TEXT NOT NULL COMMENT '配置值',
    `config_desc` VARCHAR(255) COMMENT '配置说明',
    `config_group` VARCHAR(50) COMMENT '配置分组',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_config_group` (`config_group`)
) ENGINE=InnoDB COMMENT='系统配置表';

-- 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT PRIMARY KEY,
    `module` VARCHAR(50) COMMENT '操作模块',
    `operation` VARCHAR(100) COMMENT '操作描述',
    `method` VARCHAR(10) COMMENT '请求方式',
    `url` VARCHAR(500) COMMENT '请求URL',
    `operator_id` BIGINT COMMENT '操作人ID',
    `operator_name` VARCHAR(50) COMMENT '操作人名称',
    `ip` VARCHAR(50) COMMENT '操作IP',
    `params` TEXT COMMENT '请求参数',
    `result` TINYINT DEFAULT 1 COMMENT '结果: 0失败 1成功',
    `error_msg` TEXT COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_operator_id` (`operator_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB COMMENT='操作日志表';

-- 搜索历史表
DROP TABLE IF EXISTS `search_history`;
CREATE TABLE `search_history` (
    `id` BIGINT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `keyword` VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    `deleted` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='搜索历史表';

-- 短信日志表
DROP TABLE IF EXISTS `sms_log`;
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

-- ==================== 文件相关表 ====================

-- 文件信息表
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info` (
    `id` BIGINT PRIMARY KEY,
    `file_name` VARCHAR(255) NOT NULL COMMENT '存储文件名(UUID)',
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `url` VARCHAR(500) NOT NULL COMMENT '文件访问URL',
    `thumbnail_url` VARCHAR(500) COMMENT '缩略图URL(仅图片)',
    `size` BIGINT COMMENT '文件大小(字节)',
    `type` VARCHAR(100) COMMENT 'MIME类型',
    `extension` VARCHAR(20) COMMENT '文件扩展名',
    `width` INT COMMENT '图片宽度(像素)',
    `height` INT COMMENT '图片高度(像素)',
    `bucket` VARCHAR(50) COMMENT '存储桶名称',
    `object_key` VARCHAR(500) COMMENT '存储对象键',
    `uploader_id` BIGINT COMMENT '上传者用户ID',
    `biz_type` VARCHAR(50) COMMENT '业务类型标识',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_uploader` (`uploader_id`),
    INDEX `idx_biz_type` (`biz_type`)
) ENGINE=InnoDB COMMENT='文件信息表';

-- 通知表
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `type` VARCHAR(50) DEFAULT 'system' COMMENT '通知类型: system/order/promotion',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_user_read` (`user_id`, `is_read`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB COMMENT='通知表';

-- ==================== 初始化数据 ====================

-- 默认管理员 (密码: admin123)
INSERT INTO `user` (`id`, `phone`, `password`, `nickname`, `role`, `status`) VALUES
(1, '13800000000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'admin', 1);

-- 初始分类
INSERT INTO `category` (`id`, `name`, `parent_id`, `sort`) VALUES
(1, '服装', 0, 1),
(2, '化妆品', 0, 2),
(3, '食品', 0, 3),
(4, '电子产品', 0, 4);
