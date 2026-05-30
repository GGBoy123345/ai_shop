# AI 商城毕业设计

基于 Vue3 + Spring Cloud 的智能电商平台

## 项目结构

```
├── adminfront/          # 管理后台前端 (Vue3 + Vite)
├── userfront/          # 用户前台前端 (Vue3 + Vite)
├── backend/            # Spring Cloud 微服务后端
│   └── aiShopBackend/
│       ├── ai-mall-gateway/    # 网关服务 (8087)
│       ├── ai-mall-user/       # 用户服务 (8081)
│       ├── ai-mall-product/    # 商品服务 (8082)
│       ├── ai-mall-order/      # 订单服务 (8083)
│       ├── ai-mall-file/       # 文件服务 (8084)
│       ├── ai-mall-search/     # 搜索服务 (8085)
│       ├── ai-mall-notify/     # 通知服务 (8086)
│       └── ai-mall-ai/         # AI 服务
├── doc/                # 项目文档
├── docker-compose.yml  # Docker 部署配置
└── .env.example        # 环境变量模板
```

## 快速开始

### 1. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，填入你的配置
# vim .env 或使用其他编辑器
```

**必填配置项：**

```bash
# 数据库
DB_HOST=localhost
DB_PORT=3306
DB_NAME=ai_mall
DB_USERNAME=root
DB_PASSWORD=your_password_here

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Nacos
NACOS_ADDR=localhost:8848

# JWT 密钥（生产环境请使用强密钥）
JWT_SECRET=your_jwt_secret_key_here

# MinIO 文件存储
MINIO_ENDPOINT=http://localhost:9000
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=your_minio_password
MINIO_BUCKET=ai-mall
```

### 2. 启动基础设施

```bash
# 启动 MySQL、Redis、Nacos、MinIO
docker-compose up -d
```

### 3. 启动后端服务

```bash
cd backend/aiShopBackend

# 使用 Maven 启动各个服务
mvn spring-boot:run -pl ai-mall-gateway
mvn spring-boot:run -pl ai-mall-user
mvn spring-boot:run -pl ai-mall-product
# ... 其他服务
```

### 4. 启动前端

```bash
# 管理后台
cd adminfront
npm install
npm run dev

# 用户前台
cd userfront
npm install
npm run dev
```

## 访问地址

- **管理后台**: http://localhost:5173
- **用户前台**: http://localhost:5174
- **API 网关**: http://localhost:8087
- **Nacos 控制台**: http://localhost:8848/nacos
- **MinIO 控制台**: http://localhost:9001

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Cloud 2022.x
- Spring Cloud Alibaba (Nacos)
- MyBatis-Plus
- MySQL 8.0
- Redis
- MinIO
- JWT 认证

### 前端
- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router
- Pinia

## 部署说明

### 生产环境配置

1. **修改 `.env` 文件**，使用强密码和安全的 JWT 密钥
2. **配置 HTTPS**（推荐使用 Nginx 反向代理）
3. **配置数据库备份**
4. **配置日志收集**

### Docker 部署

```bash
# 构建并启动所有服务
docker-compose -f docker-compose.prod.yml up -d
```

## 常见问题

### Q: 如何修改数据库密码？

A: 编辑 `.env` 文件中的 `DB_PASSWORD` 字段，然后重启后端服务。

### Q: 如何添加新的环境变量？

A: 
1. 在 `.env.example` 中添加变量说明
2. 在 `.env` 中添加实际值
3. 在对应的 `application.yml` 中使用 `${VARIABLE_NAME}` 引用

### Q: 为什么使用环境变量？

A: 
- **安全性**：敏感信息不会提交到 Git 仓库
- **灵活性**：不同环境可以使用不同配置
- **可维护性**：配置集中管理，易于修改

## 开发规范

1. **不要在代码中硬编码敏感信息**
2. **使用环境变量管理配置**
3. **提交前检查 `.gitignore` 是否生效**
4. **定期更新依赖版本**

## 许可证

本项目为毕业设计作品，仅供学习交流使用。