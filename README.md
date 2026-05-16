# 快递代收管理系统 (Express Delivery Management System)

## 项目概述

快递代收管理系统是一个为小区、学校或办公楼等场所提供快递代收服务的后端管理系统。系统支持快递入库登记、取件管理、退件处理、逾期提醒、异常处理及统计报表等功能，并支持快递员、管理员、业主三种角色的多用户操作。

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.5 | 应用框架 |
| Spring Data JPA | - | 数据持久化 |
| MySQL | 8.0+ | 数据库 |
| SpringDoc OpenAPI | 2.5.0 | API文档(Swagger) |
| Spring Security | - | 认证与授权 |
| JWT (jjwt) | 0.12.5 | Token认证 |
| Spring AOP | - | 权限切面控制 |
| Spring Mail | - | 邮件通知服务 |
| Lombok | - | 代码简化 |
| Maven | 3.9.6 | 构建工具 |

---

## 项目结构

```
express-system/
├── pom.xml                                    # Maven项目配置文件
├── mvnw.cmd                                   # Maven Wrapper启动脚本
├── .mvn/wrapper/maven-wrapper.properties      # Maven Wrapper配置
├── README.md                                  # 项目说明文档
│
└── src/main/
    ├── java/com/express/
    │   ├── ExpressApplication.java            # 应用启动入口
    │   │
    │   ├── config/                            # 配置类
    │   │   ├── OpenApiConfig.java             # Swagger/OpenAPI文档配置
    │   │   ├── SecurityConfig.java            # Spring Security安全配置
    │   │   └── WebConfig.java                 # CORS跨域配置
    │   │
    │   ├── security/                          # 权限安全模块
    │   │   ├── JwtUtil.java                   # JWT Token工具类
    │   │   ├── JwtAuthenticationFilter.java   # JWT认证过滤器
    │   │   ├── JwtUserDetails.java            # 当前登录用户信息
    │   │   ├── RequireRole.java               # 角色权限注解
    │   │   └── RoleAspect.java                # 权限校验AOP切面
    │   │
    │   ├── entity/                            # 数据实体类
    │   │   ├── User.java                      # 用户实体
    │   │   ├── Package.java                   # 快递信息实体
    │   │   ├── PickupRecord.java              # 取件记录实体
    │   │   ├── ReturnRecord.java              # 退件记录实体
    │   │   ├── OverdueReminder.java           # 逾期提醒实体
    │   │   ├── ExceptionRecord.java           # 异常处理记录实体
    │   │   └── SystemConfig.java              # 系统配置实体
    │   │
    │   ├── repository/                        # 数据访问层(Repository)
    │   │   ├── UserRepository.java
    │   │   ├── PackageRepository.java
    │   │   ├── PickupRecordRepository.java
    │   │   ├── ReturnRecordRepository.java
    │   │   ├── OverdueReminderRepository.java
    │   │   ├── ExceptionRecordRepository.java
    │   │   └── SystemConfigRepository.java
    │   │
    │   ├── service/                           # 业务逻辑层(Service)
    │   │   ├── UserService.java               # 用户管理业务
    │   │   ├── PackageService.java            # 快递管理业务
    │   │   ├── PickupService.java             # 取件管理业务
    │   │   ├── ReturnService.java             # 退件管理业务
    │   │   ├── ExceptionService.java          # 异常处理业务
    │   │   ├── StatisticsService.java         # 统计报表业务
    │   │   ├── OverdueReminderService.java    # 逾期提醒业务(含定时任务)
    │   │   └── MailService.java               # 邮件通知服务
    │   │
    │   ├── controller/                        # REST API控制器层
    │   │   ├── UserController.java            # 用户管理接口
    │   │   ├── PackageController.java         # 快递管理接口
    │   │   ├── PickupController.java          # 取件管理接口
    │   │   ├── ReturnController.java          # 退件管理接口
    │   │   ├── ExceptionController.java       # 异常处理接口
    │   │   ├── StatisticsController.java      # 统计报表接口
    │   │   └── OverdueReminderController.java # 逾期提醒接口
    │   │
    │   ├── dto/                               # 数据传输对象(DTO)
    │   │   ├── PackageDTO.java                # 快递信息DTO
    │   │   ├── PickupDTO.java                 # 取件DTO
    │   │   ├── ReturnDTO.java                 # 退件DTO
    │   │   ├── ExceptionDTO.java              # 异常记录DTO
    │   │   ├── StatisticsDTO.java             # 统计报表DTO
    │   │   ├── LoginDTO.java                  # 登录DTO
    │   │   └── ApiResponse.java               # 通用API响应封装
    │   │
    │   └── exception/                         # 异常处理
    │       ├── BusinessException.java         # 业务异常类
    │       └── GlobalExceptionHandler.java    # 全局异常处理器
    │
    └── resources/
        ├── application.yml                    # 应用配置文件
        └── schema.sql                         # 数据库初始化脚本
```

---

## 权限控制

系统基于 **Spring Security + JWT + AOP** 实现了完整的权限控制体系。

### 认证流程

1. **登录**：用户通过 `POST /api/users/login` 提交用户名和密码
2. **验证**：服务端使用 BCrypt 验证密码，验证通过后生成 JWT Token
3. **返回**：登录接口返回 Token 及用户信息（含角色、邮箱等）
4. **请求**：后续请求在 Header 中携带 `Authorization: Bearer <token>`
5. **拦截**：JwtAuthenticationFilter 拦截请求，解析 Token 并设置认证上下文
6. **鉴权**：RoleAspect 切面根据 `@RequireRole` 注解校验用户角色

### 角色定义

| 角色 | 枚举值 | 说明 |
|------|--------|------|
| 管理员 | `ADMIN` | 系统管理员，拥有全部功能权限 |
| 快递员 | `COURIER` | 快递员，可进行入库、取件、退件等操作 |
| 业主 | `OWNER` | 收件人/业主，可查询快递信息 |

### 权限规则

| 接口 | 方法 | 允许角色 |
|------|------|----------|
| `/api/users/login` | POST | 所有（无需认证） |
| `/api/users` | GET | ADMIN |
| `/api/users/{id}` | GET | ADMIN |
| `/api/users` | POST | ADMIN |
| `/api/users/{id}` | PUT | ADMIN |
| `/api/users/{id}` | DELETE | ADMIN |
| `/api/packages` | POST | COURIER, ADMIN |
| `/api/packages` | GET | COURIER, ADMIN |
| `/api/packages/{id}` | GET | COURIER, ADMIN, OWNER |
| `/api/packages/tracking/{trackingNumber}` | GET | COURIER, ADMIN, OWNER |
| `/api/packages/phone/{phone}` | GET | COURIER, ADMIN |
| `/api/packages/name/{name}` | GET | COURIER, ADMIN |
| `/api/packages/room/{roomNumber}` | GET | COURIER, ADMIN |
| `/api/packages/status/{status}` | GET | COURIER, ADMIN |
| `/api/packages/search` | GET | COURIER, ADMIN |
| `/api/packages/overdue` | GET | COURIER, ADMIN |
| `/api/packages/{id}` | PUT | COURIER, ADMIN |
| `/api/packages/{id}` | DELETE | ADMIN |
| `/api/pickups` | POST | COURIER, ADMIN |
| `/api/pickups/by-code` | POST | COURIER, ADMIN |
| `/api/pickups` | GET | COURIER, ADMIN |
| `/api/pickups/package/{packageId}` | GET | COURIER, ADMIN |
| `/api/pickups/check/{packageId}` | GET | COURIER, ADMIN, OWNER |
| `/api/returns` | POST | COURIER, ADMIN |
| `/api/returns` | GET | COURIER, ADMIN |
| `/api/returns/package/{packageId}` | GET | COURIER, ADMIN |
| `/api/exceptions` | POST | COURIER, ADMIN |
| `/api/exceptions` | GET | COURIER, ADMIN |
| `/api/exceptions/package/{packageId}` | GET | COURIER, ADMIN |
| `/api/exceptions/status/{status}` | GET | COURIER, ADMIN |
| `/api/exceptions/{id}/handle` | PUT | COURIER, ADMIN |
| `/api/exceptions/{id}/close` | PUT | ADMIN |
| `/api/statistics/daily` | GET | COURIER, ADMIN |
| `/api/reminders` | GET | COURIER, ADMIN |
| `/api/reminders/unresolved` | GET | COURIER, ADMIN |
| `/api/reminders/{id}/resolve` | PUT | COURIER, ADMIN |

### 使用方式

**登录获取 Token：**
```bash
POST /api/users/login
{
  "username": "admin",
  "password": "admin123"
}
```
响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "realName": "系统管理员",
    "role": "ADMIN",
    "phone": "13800138000",
    "email": "admin@example.com",
    "roomNumber": null
  }
}
```

**请求携带 Token：**
```bash
GET /api/packages
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 错误响应

| 状态码 | 说明 | 响应示例 |
|--------|------|----------|
| 401 | 未登录或Token过期 | `{"code": 401, "message": "未登录或登录已过期，请重新登录", "data": null}` |
| 403 | 权限不足 | `{"code": 403, "message": "权限不足，需要角色: [ADMIN]", "data": null}` |

---

## 数据库设计

系统共包含7张数据表，覆盖所有业务功能：

### 1. 用户表 (users)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名(唯一) |
| password | VARCHAR(255) | 密码(BCrypt加密) |
| real_name | VARCHAR(50) | 真实姓名 |
| phone | VARCHAR(20) | 联系电话 |
| email | VARCHAR(100) | 邮箱(用于接收取件码通知) |
| role | ENUM | 角色: COURIER(快递员)/ADMIN(管理员)/OWNER(业主) |
| room_number | VARCHAR(50) | 房号(业主) |
| status | ENUM | 状态: ACTIVE/DISABLED |

### 2. 快递信息表 (packages)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| tracking_number | VARCHAR(100) | 运单号(唯一) |
| courier_company | VARCHAR(50) | 快递公司 |
| recipient_name | VARCHAR(50) | 收件人姓名 |
| recipient_phone | VARCHAR(20) | 收件人电话 |
| room_number | VARCHAR(50) | 房号 |
| pickup_code | VARCHAR(20) | 取件码(6位随机数字) |
| item_name | VARCHAR(200) | 物品名称 |
| item_type | VARCHAR(50) | 物品类型 |
| status | ENUM | 状态: STORED/PICKED_UP/RETURNED/LOST/MISMATCH |
| shelf_number | VARCHAR(50) | 存放货架号 |
| arrival_time | DATETIME | 到达时间 |
| stored_by | BIGINT | 入库登记人(快递员ID) |

### 3. 取件记录表 (pickup_records)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| package_id | BIGINT | 快递ID |
| pickup_time | DATETIME | 取件时间 |
| pickup_person_name | VARCHAR(50) | 取件人姓名 |
| pickup_person_phone | VARCHAR(20) | 取件人电话 |
| signature | VARCHAR(255) | 取件人签名(base64) |
| verification_method | ENUM | 验证方式: CODE/PHONE/ID_CARD/SIGNATURE |
| verified_by | BIGINT | 核验人(管理员ID) |

### 4. 退件记录表 (return_records)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| package_id | BIGINT | 快递ID |
| return_reason | VARCHAR(500) | 退件原因 |
| return_time | DATETIME | 退回时间 |
| processed_by | BIGINT | 处理人(管理员ID) |
| courier_name | VARCHAR(50) | 退回快递员 |

### 5. 逾期提醒记录表 (overdue_reminders)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| package_id | BIGINT | 快递ID |
| overdue_days | INT | 逾期天数 |
| reminder_time | DATETIME | 提醒时间 |
| reminder_method | ENUM | 提醒方式: SMS/PHONE/APP/MANUAL |
| is_resolved | BOOLEAN | 是否已处理 |

### 6. 异常处理记录表 (exception_records)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| package_id | BIGINT | 关联快递ID |
| exception_type | ENUM | 异常类型: DUPLICATE_PICKUP/WRONG_PICKUP/LOST/DAMAGED/MISMATCH_INFO/OTHER |
| description | VARCHAR(1000) | 异常描述 |
| handler_id | BIGINT | 处理人(管理员ID) |
| handling_result | VARCHAR(1000) | 处理结果 |
| status | ENUM | 处理状态: PENDING/RESOLVED/CLOSED |
| compensation_amount | DECIMAL(10,2) | 赔偿金额 |

### 7. 系统配置表 (system_config)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| config_key | VARCHAR(100) | 配置键(唯一) |
| config_value | VARCHAR(500) | 配置值 |
| description | VARCHAR(255) | 配置说明 |

---

## API接口文档

系统共包含 **36 个 REST API 接口**，覆盖 7 大功能模块。所有接口以 `/api` 为统一前缀。

### 用户管理 (`/api/users`)

| 方法 | 路径 | 说明 | 权限 | 请求体/参数 |
|------|------|------|------|------------|
| POST | `/api/users/login` | 用户登录 | 公开 | `LoginDTO`（用户名+密码） |
| GET | `/api/users` | 获取所有用户 | ADMIN | - |
| GET | `/api/users/{id}` | 根据ID获取用户 | ADMIN | `id`（路径参数） |
| POST | `/api/users` | 创建用户 | ADMIN | `User`（JSON） |
| PUT | `/api/users/{id}` | 更新用户信息 | ADMIN | `id`（路径参数）+ `User`（JSON） |
| DELETE | `/api/users/{id}` | 删除用户 | ADMIN | `id`（路径参数） |

### 快递管理 (`/api/packages`)

| 方法 | 路径 | 说明 | 权限 | 请求体/参数 |
|------|------|------|------|------------|
| POST | `/api/packages` | 快递入库登记 | COURIER / ADMIN | `PackageDTO`（JSON） |
| GET | `/api/packages` | 获取所有快递列表 | COURIER / ADMIN | - |
| GET | `/api/packages/{id}` | 根据ID查询快递 | COURIER / ADMIN / OWNER | `id`（路径参数） |
| GET | `/api/packages/tracking/{trackingNumber}` | 根据运单号查询 | COURIER / ADMIN / OWNER | `trackingNumber`（路径参数） |
| GET | `/api/packages/phone/{phone}` | 根据收件人电话查询 | COURIER / ADMIN | `phone`（路径参数） |
| GET | `/api/packages/name/{name}` | 根据收件人姓名查询 | COURIER / ADMIN | `name`（路径参数） |
| GET | `/api/packages/room/{roomNumber}` | 根据房号查询 | COURIER / ADMIN | `roomNumber`（路径参数） |
| GET | `/api/packages/status/{status}` | 根据状态查询 | COURIER / ADMIN | `status`（路径参数） |
| GET | `/api/packages/search` | 搜索快递（模糊搜索） | COURIER / ADMIN | `keyword`（请求参数） |
| GET | `/api/packages/overdue` | 查询逾期未取快递 | COURIER / ADMIN | `days`（请求参数，默认3天） |
| PUT | `/api/packages/{id}` | 更新快递信息 | COURIER / ADMIN | `id`（路径参数）+ `PackageDTO`（JSON） |
| DELETE | `/api/packages/{id}` | 删除快递 | ADMIN | `id`（路径参数） |

### 取件管理 (`/api/pickups`)

| 方法 | 路径 | 说明 | 权限 | 请求体/参数 |
|------|------|------|------|------------|
| POST | `/api/pickups` | 取件操作 | COURIER / ADMIN | `PickupDTO`（JSON） |
| POST | `/api/pickups/by-code` | 根据取件码取件 | COURIER / ADMIN | `trackingNumber` + `pickupCode`（请求参数）+ `PickupDTO`（JSON） |
| GET | `/api/pickups` | 获取所有取件记录 | COURIER / ADMIN | - |
| GET | `/api/pickups/package/{packageId}` | 获取快递的取件记录 | COURIER / ADMIN | `packageId`（路径参数） |
| GET | `/api/pickups/check/{packageId}` | 检查快递是否已被取件 | COURIER / ADMIN / OWNER | `packageId`（路径参数） |

### 退件管理 (`/api/returns`)

| 方法 | 路径 | 说明 | 权限 | 请求体/参数 |
|------|------|------|------|------------|
| POST | `/api/returns` | 退件处理 | COURIER / ADMIN | `ReturnDTO`（JSON） |
| GET | `/api/returns` | 获取所有退件记录 | COURIER / ADMIN | - |
| GET | `/api/returns/package/{packageId}` | 获取快递的退件记录 | COURIER / ADMIN | `packageId`（路径参数） |

### 异常管理 (`/api/exceptions`)

| 方法 | 路径 | 说明 | 权限 | 请求体/参数 |
|------|------|------|------|------------|
| POST | `/api/exceptions` | 创建异常记录（错件/丢件等） | COURIER / ADMIN | `ExceptionDTO`（JSON） |
| GET | `/api/exceptions` | 获取所有异常记录 | COURIER / ADMIN | - |
| GET | `/api/exceptions/package/{packageId}` | 获取快递的异常记录 | COURIER / ADMIN | `packageId`（路径参数） |
| GET | `/api/exceptions/status/{status}` | 根据状态获取异常记录 | COURIER / ADMIN | `status`（路径参数） |
| PUT | `/api/exceptions/{id}/handle` | 处理异常 | COURIER / ADMIN | `id`（路径参数）+ `ExceptionDTO`（JSON） |
| PUT | `/api/exceptions/{id}/close` | 关闭异常记录 | ADMIN | `id`（路径参数） |

### 统计报表 (`/api/statistics`)

| 方法 | 路径 | 说明 | 权限 | 请求体/参数 |
|------|------|------|------|------------|
| GET | `/api/statistics/daily` | 获取每日统计报表 | COURIER / ADMIN | `date`（请求参数，可选，默认当天） |

### 逾期提醒 (`/api/reminders`)

| 方法 | 路径 | 说明 | 权限 | 请求体/参数 |
|------|------|------|------|------------|
| GET | `/api/reminders` | 获取所有逾期提醒 | COURIER / ADMIN | - |
| GET | `/api/reminders/unresolved` | 获取未处理的逾期提醒 | COURIER / ADMIN | - |
| PUT | `/api/reminders/{id}/resolve` | 标记提醒为已处理 | COURIER / ADMIN | `id`（路径参数） |

### 接口统计汇总

| 模块 | 接口数量 | 基础路径 |
|------|---------|---------|
| 用户管理 | 6 | `/api/users` |
| 快递管理 | 12 | `/api/packages` |
| 取件管理 | 5 | `/api/pickups` |
| 退件管理 | 3 | `/api/returns` |
| 异常管理 | 6 | `/api/exceptions` |
| 统计报表 | 1 | `/api/statistics` |
| 逾期提醒 | 3 | `/api/reminders` |
| **总计** | **36** | - |
---

## 功能模块详解

### 1. 快递入库登记
- 快递员登记快递信息：运单号、快递公司、收件人信息、房号、物品名称、物品类型
- 系统自动生成6位随机取件码
- 指定存放货架号，记录到达时间
- 运单号唯一性校验，防止重复入库
- **入库后自动发送取件码通知邮件**（如果收件人已配置邮箱）

### 2. 取件管理
- 支持多种身份验证方式：取件码验证、手机号验证、身份证验证、签名验证
- 记录取件人信息、取件时间、签名(base64)
- 自动检测重复取件：已取件的快递无法再次取件
- 支持管理员核验取件

### 3. 逾期提醒
- 定时任务：每天上午9:00自动检查逾期未取快递
- 逾期天数可通过系统配置表动态配置(默认3天)
- 自动生成逾期提醒记录，记录逾期天数和通知信息
- 支持手动标记提醒为已处理

### 4. 退件管理
- 管理员处理退件，记录退件原因
- 记录退回快递员信息
- 退件后自动更新快递状态为"已退回"
- 已取件的快递无法退回

### 5. 异常处理
- 支持多种异常类型：重复取件、取错件、丢件、损坏、信息不符、其他
- 异常记录状态流转：待处理 → 已处理 → 已关闭
- 支持记录赔偿金额
- 自动更新关联快递状态(错件→MISMATCH，丢件→LOST)

### 6. 统计报表
- 每日入库量统计
- 每日取件量统计
- 当前滞留件数统计
- 逾期未取件数统计
- 按快递公司分类统计入库/取件量
- 待处理异常数量统计

### 7. 邮件通知
- 快递入库时自动发送取件码通知邮件
- 通过收件人手机号匹配业主用户
- 仅向已配置邮箱的用户发送
- 邮件内容包含：取件码、快递公司、运单号、存放位置、到达时间

### 8. 多用户角色
| 角色 | 权限说明 |
|------|----------|
| COURIER(快递员) | 快递入库登记、取件、退件、异常处理、查询快递、查看统计 |
| ADMIN(管理员) | 全部功能：用户管理、入库、取件、退件、异常处理、统计、逾期提醒 |
| OWNER(业主) | 查询快递信息、检查取件状态 |

---

## 启动指南

### 前置条件
- JDK 21+
- MySQL 8.0+
- 网络连接(用于Maven下载依赖)

### 步骤一：配置数据库
1. 确保MySQL服务已启动
2. 修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/express_delivery?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&createDatabaseIfNotExist=true
    username: root          # 修改为你的MySQL用户名
    password: root          # 修改为你的MySQL密码
```

### 步骤二：配置邮件服务（可选）
如需启用取件码邮件通知功能，配置真实的邮箱信息：
```yaml
spring:
  mail:
    host: smtp.qq.com              # SMTP服务器地址
    port: 587                      # SMTP端口(QQ邮箱)
    username: your-email@qq.com    # 发件邮箱
    password: your-auth-code       # 邮箱授权码(非登录密码)
```
> 如果不需要邮件通知，可以跳过此步骤，系统会正常运行但不会发送邮件。

### 步骤三：初始化数据库(可选)
运行 `src/main/resources/schema.sql` 脚本初始化数据库和默认数据：
- 默认管理员账号：admin / admin123
- 默认快递员账号：courier1 / courier123
- 默认业主账号：owner1 / owner123

> 注意：如果使用JPA的 `ddl-auto: update` 自动建表，则无需手动运行schema.sql

### 步骤四：启动应用
```bash
# Windows
mvnw.cmd spring-boot:run

# 或直接编译运行
mvnw.cmd clean package -DskipTests
java -jar target/express-delivery-system-1.0.0.jar
```

### 步骤五：访问API文档
启动后访问：http://localhost:8080/swagger-ui.html

---

## 默认测试数据

| 用户名 | 密码 | 角色 | 姓名 | 手机号 |
|--------|------|------|------|--------|
| admin | admin123 | 管理员 | 系统管理员 | 13800000000 |
| courier1 | courier123 | 快递员 | 快递员张三 | 13900000001 |
| owner1 | owner123 | 业主 | 业主李四 | 13700000002 |

---

## 异常处理机制

系统采用统一的异常处理架构：

1. **BusinessException** - 业务异常类，包含错误码和错误消息
2. **GlobalExceptionHandler** - 全局异常处理器，统一捕获并返回标准格式的响应
   - 认证异常(401)：未登录或Token过期
   - 权限异常(403)：角色权限不足
   - 参数校验异常(400)：请求参数不合法
   - 业务异常(400)：业务逻辑错误
   - 系统异常(500)：服务器内部错误
3. **参数校验** - 使用 `@Valid` 注解和 Jakarta Validation 进行参数校验

统一响应格式：
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

---

## 前后端连接指南

### 1. CORS跨域配置（已完成）

后端已配置CORS跨域支持，允许来自任何域名的前端请求：

```java
// SecurityConfig.java - 已配置
configuration.setAllowedOriginPatterns(List.of("*"));
configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
configuration.setAllowedHeaders(List.of("*"));
configuration.setAllowCredentials(true);
configuration.setMaxAge(3600L);
```

### 2. 后端API基础地址

```
http://localhost:8080/api
```

所有API接口都以 `/api` 为前缀，例如：
- 用户登录：`POST http://localhost:8080/api/users/login`
- 快递列表：`GET http://localhost:8080/api/packages`
- 快递入库：`POST http://localhost:8080/api/packages`

### 3. 前端请求示例

#### 3.1 使用 Axios（推荐）

```javascript
// api.js - Axios 实例配置
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器：自动携带 JWT Token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器：统一处理认证/权限错误
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response) {
      const { code, message } = error.response.data;
      if (code === 401) {
        // Token过期或未登录，跳转到登录页
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      } else if (code === 403) {
        // 权限不足
        console.error('权限不足:', message);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
```

#### 3.2 用户登录

```javascript
// login.js
import api from './api';

// 登录请求
async function login(username, password) {
  try {
    const res = await api.post('/users/login', {
      username: username,
      password: password
    });
    // res.data = { token, tokenType, userId, username, realName, role, phone, email, roomNumber }
    console.log('登录成功:', res.data);
    localStorage.setItem('token', res.data.token);
    localStorage.setItem('user', JSON.stringify(res.data));
    return res.data;
  } catch (error) {
    console.error('登录失败:', error);
    throw error;
  }
}
```

#### 3.3 快递入库

```javascript
// storePackage.js
import api from './api';

// 快递入库登记
async function storePackage(packageData) {
  try {
    const res = await api.post('/packages', {
      trackingNumber: 'SF1234567890',     // 运单号
      courierCompany: '顺丰速运',          // 快递公司
      recipientName: '张三',               // 收件人姓名
      recipientPhone: '13800138000',       // 收件人电话
      roomNumber: 'A-1201',               // 房号
      shelfNumber: 'A-03',                // 货架号
      itemName: '手机',                    // 物品名称（可选）
      itemType: '电子产品'                 // 物品类型（可选）
    });
    // res.data = { id, trackingNumber, pickupCode: "482916", ... }
    console.log('入库成功，取件码:', res.data.pickupCode);
    return res.data;
  } catch (error) {
    console.error('入库失败:', error);
    throw error;
  }
}
```

#### 3.4 取件操作

```javascript
// pickup.js
import api from './api';

// 方式一：管理员取件（验证身份）
async function pickupByAdmin(packageId, pickupPersonName, pickupPersonPhone) {
  const res = await api.post('/pickups', {
    packageId: packageId,
    pickupPersonName: pickupPersonName,
    pickupPersonPhone: pickupPersonPhone,
    verificationMethod: 'PHONE'  // 验证方式: CODE/PHONE/ID_CARD/SIGNATURE
  });
  return res.data;
}

// 方式二：业主凭取件码取件
async function pickupByCode(trackingNumber, pickupCode, pickupPersonName) {
  const res = await api.post('/pickups/by-code', {
    packageId: null,
    pickupPersonName: pickupPersonName,
    pickupPersonPhone: '',
    verificationMethod: 'CODE',
    signature: ''
  }, {
    params: {
      trackingNumber: trackingNumber,
      pickupCode: pickupCode
    }
  });
  return res.data;
}
```

#### 3.5 查询快递

```javascript
// query.js
import api from './api';

// 根据收件人电话查询
async function queryByPhone(phone) {
  const res = await api.get(`/packages/phone/${phone}`);
  return res.data; // 返回快递列表
}

// 根据房号查询
async function queryByRoom(roomNumber) {
  const res = await api.get(`/packages/room/${roomNumber}`);
  return res.data;
}

// 模糊搜索
async function searchPackages(keyword) {
  const res = await api.get('/packages/search', {
    params: { keyword: keyword }
  });
  return res.data;
}

// 查询逾期快递
async function getOverduePackages(days = 3) {
  const res = await api.get('/packages/overdue', {
    params: { days: days }
  });
  return res.data;
}
```

#### 3.6 获取统计报表

```javascript
// statistics.js
import api from './api';

// 获取每日统计
async function getDailyStatistics(date) {
  const params = date ? { date: date } : {};
  const res = await api.get('/statistics/daily', { params });
  // res.data = {
  //   date: "2026-05-15",
  //   totalStored: 50,       // 入库量
  //   totalPickedUp: 30,     // 取件量
  //   currentStaying: 20,    // 滞留件
  //   overdueCount: 5,       // 逾期件
  //   companyStats: [        // 按快递公司统计
  //     { company: "顺丰速运", storedCount: 20, pickedUpCount: 15 }
  //   ],
  //   pendingExceptionCount: 2  // 待处理异常
  // };
  return res.data;
}
```

### 4. 统一响应格式

所有API返回统一的JSON格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码(200成功/400参数错误/401未认证/403无权限/404未找到/500服务器错误) |
| message | string | 提示信息 |
| data | object | 返回数据 |

### 5. 前端开发建议

#### 5.1 推荐技术栈
- **Vue 3 + Element Plus**（推荐，适合管理后台）
- **React + Ant Design**
- **原生HTML + jQuery**（简单页面）

#### 5.2 快速启动前端项目

```bash
# 使用 Vue 3 + Vite 创建前端项目
npm create vite@latest express-frontend -- --template vue
cd express-frontend
npm install axios element-plus vue-router
npm run dev
```

#### 5.3 前端项目结构建议

```
express-frontend/
├── src/
│   ├── api/                    # API请求模块
│   │   ├── index.js            # Axios实例配置(含Token拦截器)
│   │   ├── user.js             # 用户相关API
│   │   ├── package.js          # 快递相关API
│   │   ├── pickup.js           # 取件相关API
│   │   └── statistics.js       # 统计相关API
│   ├── views/                  # 页面组件
│   │   ├── login.vue           # 登录页
│   │   ├── dashboard.vue       # 仪表盘/统计
│   │   ├── packageList.vue     # 快递列表
│   │   ├── packageStore.vue    # 入库登记
│   │   ├── pickupManage.vue    # 取件管理
│   │   ├── returnManage.vue    # 退件管理
│   │   ├── exceptionManage.vue # 异常管理
│   │   └── userManage.vue      # 用户管理
│   ├── router/                 # 路由配置(含路由守卫)
│   └── store/                  # 状态管理
└── package.json
```

### 6. 常见问题

#### Q: 前端请求报跨域错误？
A: 后端已配置CORS，检查前端请求地址是否为 `http://localhost:8080/api/...`。如果使用不同端口（如前端在5173端口），CORS配置已允许所有来源，无需额外配置。

#### Q: 如何传递用户身份？
A: 系统已集成JWT Token认证。登录后获取Token，后续请求在Header中携带 `Authorization: Bearer <token>`。前端Axios拦截器会自动处理。

#### Q: 前端如何区分用户角色？
A: 登录接口返回 `role` 字段（COURIER/ADMIN/OWNER），前端根据角色控制页面访问权限和功能按钮显示。

#### Q: 接口返回401？
A: 表示未登录或Token已过期，需要重新登录获取新的Token。

#### Q: 接口返回403？
A: 表示当前用户角色没有该接口的访问权限，请联系管理员。

#### Q: 接口返回404？
A: 确认后端服务已启动，检查请求路径是否完整（如 `/api/packages` 而非 `/packages`）。

---

## 扩展建议

1. ~~安全增强~~：✅ 已集成Spring Security + JWT实现Token认证
2. **短信通知**：对接短信服务商API，实现自动短信提醒
3. **前端界面**：开发Vue/React前端页面，对接REST API
4. **缓存优化**：引入Redis缓存热点数据，提升查询性能
5. **日志系统**：集成ELK或类似日志收集系统
6. **文件上传**：实现取件人签名图片上传功能
7. **数据导出**：支持Excel/PDF格式的报表导出
8. **消息推送**：集成WebSocket实现实时通知推送
