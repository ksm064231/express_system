# 快递代收管理系统 - 前端开发执行方案

> 基于 Spring Boot 后端 36 个 REST API 接口的全栈前端开发计划

---

## 项目概览

| 项目信息 | 说明                                        |
| -------- | ------------------------------------------- |
| 项目名称 | 快递代收管理系统前端                        |
| 后端地址 | `http://localhost:8080/api`                 |
| 认证方式 | JWT Token (Bearer)                          |
| 响应格式 | `{ code, message, data }`                   |
| 支持角色 | 管理员(ADMIN)、快递员(COURIER)、业主(OWNER) |
| API总数  | 36个接口，7大功能模块                       |

---

## 技术栈选型

### 核心技术

```json
{
  "框架": "Vue 3.4+ (Composition API)",
  "构建工具": "Vite 5.0+",
  "路由": "Vue Router 4.2+",
  "状态管理": "Pinia 2.1+",
  "UI组件库": "Element Plus 2.5+",
  "HTTP客户端": "Axios 1.6+",
  "日期处理": "Day.js 1.11+",
  "图表": "ECharts 5.4+"
}
```

---

## 📋 开发阶段总览

| 阶段  | 名称         | 核心目标               | 预计工时 | 优先级 |
| ----- | ------------ | ---------------------- | -------- | ------ |
| 阶段0 | 项目准备     | 项目搭建、基础配置     | 0.5天    | P0     |
| 阶段1 | 核心功能开发 | 登录、快递CRUD、取件   | 2天      | P0     |
| 阶段2 | 业务功能完善 | 统计、退件、异常、逾期 | 1.5天    | P1     |
| 阶段3 | 管理功能优化 | 用户管理、体验优化     | 1天      | P1     |
| 阶段4 | 测试与部署   | 测试、构建、部署       | 0.5天    | P2     |

**总计：5.5 个工作日**

---

## 🚀 阶段0：项目准备阶段

### 阶段目标

完成项目初始化、基础配置、目录结构搭建

### 具体任务

#### 任务0.1：项目初始化

```bash
# 使用 Vite 创建 Vue 3 项目
npm create vite@latest express-frontend -- --template vue
cd express-frontend

# 安装依赖
npm install axios vue-router pinia element-plus @element-plus/icons-vue dayjs echarts
```

#### 任务0.2：目录结构搭建

```
src/
├── api/              # API请求层
│   ├── request.js    # Axios实例 + 拦截器
│   ├── user.js       # 用户管理API
│   ├── package.js    # 快递管理API
│   ├── pickup.js     # 取件管理API
│   ├── return.js     # 退件管理API
│   ├── exception.js  # 异常管理API
│   ├── reminder.js   # 逾期提醒API
│   └── statistics.js # 统计报表API
├── components/       # 公共组件
├── router/           # 路由配置
├── stores/           # Pinia状态管理
├── utils/            # 工具函数
├── views/            # 页面组件
├── App.vue
└── main.js
```

#### 任务0.3：基础配置

**文件：`vite.config.js`** - 配置端口、代理、路径别名
**文件：`.env.development`** - 环境变量配置

```env
VITE_API_BASE_URL = http://localhost:8080/api
VITE_APP_TITLE = 快递代收管理系统
```

#### 任务0.4：Axios请求封装

**文件：`src/api/request.js`**

- 创建 Axios 实例
- 请求拦截器：自动携带 Token
- 响应拦截器：统一错误处理、401跳转登录、403权限提示

### 本阶段产出

✅ 可运行的 Vue 3 项目骨架
✅ 完善的目录结构
✅ Axios 请求封装完成

---

## 🔥 阶段1：核心功能开发（P0）

### 阶段目标

完成系统的核心业务流程：登录 → 快递管理 → 取件操作

### 具体任务

---

#### 任务1.1：用户认证模块

**开发目标**：实现登录、登出、路由守卫

**涉及API**：

```javascript
POST / api / users / login; // 用户登录
```

**开发内容**：

1. **登录页面** (`src/views/login/Login.vue`)
   - 用户名/密码表单
   - 表单验证（非空校验）
   - 登录按钮 Loading 状态
   - 错误信息提示

2. **用户状态管理** (`src/stores/user.js`)
   - Pinia store 存储用户信息
   - Token 本地存储 (localStorage)
   - 登录/登出 action

3. **路由配置** (`src/router/index.js`)
   - 基础路由表（登录页、404页）
   - 路由守卫实现：
     - 白名单过滤（仅登录页无需认证）
     - Token 校验
     - 角色权限校验（后续扩展）

4. **登出功能**
   - 清除本地存储
   - 跳转登录页

**验收标准**：

- ✅ 输入正确账号密码可登录
- ✅ 登录成功后跳转首页
- ✅ 未登录状态自动跳转登录页
- ✅ 点击登出可正常退出

**测试账号**：

| 用户名   | 密码       | 角色   |
| -------- | ---------- | ------ |
| admin    | admin123   | 管理员 |
| courier1 | courier123 | 快递员 |
| owner1   | owner123   | 业主   |

---

#### 任务1.2：主布局框架

**开发目标**：实现侧边栏菜单、顶部导航栏、动态菜单权限控制

**开发内容**：

1. **主布局组件** (`src/components/Layout.vue`)
   - 左侧侧边栏（可折叠）
   - 顶部导航栏（用户信息、登出按钮）
   - 内容区域（router-view）

2. **动态菜单实现**
   - 菜单配置（按角色过滤）
   - 菜单项：仪表盘、快递列表、快递入库、取件管理、退件管理、异常管理、逾期提醒、用户管理
   - 根据登录用户的 `role` 字段显示对应菜单

3. **面包屑导航**（可选）

**验收标准**：

- ✅ 布局结构完整
- ✅ 不同角色显示不同菜单
- ✅ 菜单高亮与路由同步

---

#### 任务1.3：快递列表页

**开发目标**：实现快递信息的查询、搜索、分页展示

**涉及API**：

```javascript
GET /api/packages                    // 获取所有快递
GET /api/packages/tracking/{number}  // 按运单号查询
GET /api/packages/phone/{phone}      // 按收件人电话查询
GET /api/packages/name/{name}        // 按收件人姓名查询
GET /api/packages/room/{room}        // 按房号查询
GET /api/packages/status/{status}    // 按状态查询
GET /api/packages/search?keyword=xxx // 模糊搜索
DELETE /api/packages/{id}            // 删除快递（仅管理员）
```

**开发内容**：

1. **搜索表单区域**
   - 运单号输入框
   - 收件人姓名/电话输入框
   - 房号输入框
   - 状态下拉选择（已入库/已取件/已退回/丢失/错件）
   - 搜索按钮、重置按钮

2. **数据表格展示**
   - 列：运单号、快递公司、收件人、电话、房号、取件码、状态、到达时间、操作
   - 状态标签颜色区分：
     - 已入库(STORED)：绿色
     - 已取件(PICKED_UP)：灰色
     - 已退回(RETURNED)：橙色
     - 丢失(LOST)：红色
     - 错件(MISMATCH)：黄色

3. **操作列功能**
   - 查看详情按钮
   - 编辑按钮（快递员/管理员）
   - 删除按钮（仅管理员，需二次确认）

4. **分页功能**
   - 前端分页或后端分页（根据实际API）

**验收标准**：

- ✅ 页面加载显示快递列表
- ✅ 各搜索条件可正常过滤
- ✅ 删除功能正常（仅管理员可见）
- ✅ 状态标签颜色正确

---

#### 任务1.4：快递入库页

**开发目标**：实现新快递的入库登记功能

**涉及API**：

```javascript
POST / api / packages; // 快递入库
PUT / api / packages / { id }; // 更新快递信息
```

**开发内容**：

1. **入库表单**
   - 运单号 \*（必填，唯一校验）
   - 快递公司 \*（下拉选择：顺丰、京东、中通、圆通、韵达、其他）
   - 收件人姓名 \*
   - 收件人电话 \*
   - 房号
   - 货架号 \*
   - 物品名称
   - 物品类型（下拉：文件、电子产品、服装、食品、其他）

2. **表单验证**
   - 必填项校验
   - 手机号格式校验
   - 运单号唯一性预检查（可选）

3. **提交成功处理**
   - 显示成功提示
   - 显示系统生成的取件码（6位数字）
   - 提供复制取件码功能
   - 重置表单或跳转到列表页

**验收标准**：

- ✅ 表单验证正常工作
- ✅ 提交成功后返回取件码
- ✅ 重复运单号提示错误

---

#### 任务1.5：取件操作页

**开发目标**：实现快递取件功能，支持两种取件方式

**涉及API**：

```javascript
POST / api / pickups; // 取件操作
POST / api / pickups / by - code; // 凭取件码取件
GET / api / pickups / check / { packageId }; // 检查是否已取件
GET / api / pickups; // 取件记录列表
```

**开发内容**：

1. **取件方式选择**
   - Tab切换：凭取件码取件 / 管理员核验取件

2. **方式一：凭取件码取件**
   - 运单号输入框
   - 取件码输入框
   - 取件人姓名
   - 取件按钮

3. **方式二：管理员核验取件**
   - 搜索选择快递（运单号/收件人）
   - 验证方式选择（手机号/身份证/签名）
   - 取件人信息录入（姓名、电话）
   - 签名功能（Canvas手写签名，转base64）

4. **取件记录子页面**
   - 取件记录表格
   - 按快递查询取件记录

**验收标准**：

- ✅ 取件码正确可完成取件
- ✅ 取件后快递状态更新为"已取件"
- ✅ 已取件快递无法重复取件
- ✅ 取件记录可查询

---

### 阶段1产出

✅ 完整的登录认证体系
✅ 快递入库、查询、删除功能
✅ 取件操作与记录查询功能
✅ 系统核心业务流程闭环

---

## 📊 阶段2：业务功能完善（P1）

### 阶段目标

完成统计报表、退件管理、异常管理、逾期提醒等辅助业务功能

### 具体任务

---

#### 任务2.1：仪表盘/统计页

**开发目标**：实现数据可视化展示，直观了解当日运营情况

**涉及API**：

```javascript
GET /api/statistics/daily?date=YYYY-MM-DD  // 获取每日统计
```

**开发内容**：

1. **数据概览卡片**（4个）
   - 今日入库量
   - 今日取件量
   - 当前滞留件数
   - 逾期未取件数

2. **图表展示**
   - 按快递公司入库量统计（柱状图）
   - 按快递公司取件量统计（柱状图）

3. **快捷入口**
   - 快递入库按钮
   - 取件操作按钮
   - 待处理异常提醒

4. **日期选择器**
   - 可选择查看历史日期的统计数据

**验收标准**：

- ✅ 数据卡片显示正确数值
- ✅ 图表渲染正常
- ✅ 切换日期数据同步更新

---

#### 任务2.2：退件管理页

**开发目标**：实现退件处理和退件记录查询

**涉及API**：

```javascript
POST / api / returns; // 退件处理
GET / api / returns; // 退件记录列表
GET / api / returns / package / { id }; // 按快递查询退件记录
```

**开发内容**：

1. **退件处理表单**
   - 选择待退件快递（仅显示已入库状态）
   - 退件原因输入（多行文本）
   - 退回快递员姓名
   - 处理备注

2. **退件记录表格**
   - 列：快递信息、退件原因、退回时间、处理人、退回快递员、操作
   - 查看详情按钮

**验收标准**：

- ✅ 退件提交成功
- ✅ 快递状态更新为"已退回"
- ✅ 退件记录可查询

---

#### 任务2.3：异常管理页

**开发目标**：实现异常记录的创建、查询、处理

**涉及API**：

```javascript
POST / api / exceptions; // 创建异常记录
GET / api / exceptions; // 获取所有异常记录
GET / api / exceptions / package / { id }; // 按快递查询
GET / api / exceptions / status / { status }; // 按状态查询
PUT / api / exceptions / { id } / handle; // 处理异常
PUT / api / exceptions / { id } / close; // 关闭异常（仅管理员）
```

**开发内容**：

1. **异常类型枚举**
   - DUPLICATE_PICKUP: 重复取件
   - WRONG_PICKUP: 取错件
   - LOST: 丢件
   - DAMAGED: 损坏
   - MISMATCH_INFO: 信息不符
   - OTHER: 其他

2. **异常记录表格**
   - 列：快递信息、异常类型、描述、状态、创建时间、处理人、操作
   - 状态筛选标签页：全部 / 待处理 / 已处理 / 已关闭

3. **新建异常弹窗**
   - 选择关联快递
   - 选择异常类型
   - 输入异常描述

4. **处理异常弹窗**
   - 输入处理结果
   - 输入赔偿金额（可选）

**验收标准**：

- ✅ 可创建异常记录
- ✅ 可按状态筛选查询
- ✅ 处理/关闭功能正常
- ✅ 快递状态同步更新（丢件→LOST，错件→MISMATCH）

---

#### 任务2.4：逾期提醒页

**开发目标**：实现逾期快递的提醒管理

**涉及API**：

```javascript
GET / api / reminders; // 获取所有逾期提醒
GET / api / reminders / unresolved; // 获取未处理的逾期提醒
PUT / api / reminders / { id } / resolve; // 标记为已处理
```

**开发内容**：

1. **逾期提醒列表**
   - 列：快递信息、逾期天数、提醒时间、通知方式、被通知人、处理状态、操作
   - 状态标签：未处理(红色)、已处理(绿色)

2. **筛选功能**
   - 全部/未处理 切换Tab

3. **处理功能**
   - 标记已处理按钮

**验收标准**：

- ✅ 逾期列表显示正确
- ✅ 标记处理功能正常

---

### 阶段2产出

✅ 数据可视化仪表盘
✅ 退件管理完整流程
✅ 异常处理闭环流程
✅ 逾期提醒管理功能
✅ 所有业务功能模块完成

---

## 👥 阶段3：管理功能与优化（P1）

### 阶段目标

完成用户管理功能，优化整体用户体验

### 具体任务

---

#### 任务3.1：用户管理页（仅管理员）

**开发目标**：实现用户的增删改查管理

**涉及API**：

```javascript
GET / api / users; // 获取所有用户
GET / api / users / { id }; // 获取单个用户
POST / api / users; // 创建用户
PUT / api / users / { id }; // 更新用户
DELETE / api / users / { id }; // 删除用户
```

**开发内容**：

1. **用户列表表格**
   - 列：用户名、真实姓名、手机号、邮箱、角色、房号、状态、创建时间、操作
   - 角色标签颜色：管理员(紫色)、快递员(蓝色)、业主(灰色)
   - 状态标签：启用(绿色)、禁用(红色)

2. **新增用户弹窗**
   - 用户名 \*
   - 密码 \*
   - 真实姓名 \*
   - 手机号 \*
   - 邮箱
   - 角色选择 \*（管理员/快递员/业主）
   - 房号

3. **编辑用户弹窗**
   - 同新增字段，但密码为可选（不填则不修改）
   - 状态切换：启用/禁用

4. **删除功能**
   - 二次确认弹窗
   - 防止误删当前登录用户

**验收标准**：

- ✅ 用户列表完整展示
- ✅ 新增/编辑/删除功能正常
- ✅ 角色权限控制正确（仅管理员可见）

---

#### 任务3.2：用户体验优化

**开发目标**：提升整体系统的易用性和用户体验

**优化内容**：

1. **表单体验优化**
   - 所有表单添加回车提交
   - 表单重置功能
   - 输入框长度限制和提示

2. **加载状态优化**
   - 表格加载时显示骨架屏或loading
   - 按钮点击后显示loading状态防止重复提交

3. **消息提示统一**
   - 操作成功提示（自动消失）
   - 错误信息提示（需手动关闭或自动消失）
   - 确认弹窗样式统一

4. **移动端适配**（可选）
   - 响应式布局调整
   - 侧边栏在小屏幕自动收起

---

### 阶段3产出

✅ 用户管理功能完整实现
✅ 用户体验优化完成
✅ 所有管理功能闭环

---

## 🧪 阶段4：测试与部署

### 阶段目标

完成功能测试、构建优化、部署准备

### 具体任务

#### 任务4.1：功能测试

**测试范围**：

1. **登录认证测试**
   - 正确/错误账号密码登录
   - Token过期自动跳转
   - 登出功能

2. **角色权限测试**
   - 管理员可访问所有页面
   - 快递员不可访问用户管理
   - 业主仅可查询快递

3. **核心功能测试**
   - 快递入库流程
   - 取件操作流程
   - 退件处理流程
   - 异常处理流程

4. **边界测试**
   - 空表单提交
   - 超长文本输入
   - 特殊字符输入

#### 任务4.2：构建优化

**文件**：`vite.config.js`

- 配置生产环境构建参数
- 代码压缩
- 图片资源压缩
- 清除console.log
- 打包分析（可选）

```bash
# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

#### 任务4.3：部署准备

**生成文件**：`deploy.md` 部署文档

部署方式选择：

1. **Nginx 静态部署**（推荐）
   - 配置反向代理到后端 API
   - 配置 gzip 压缩
   - 配置缓存策略

2. **部署到 CDN**
   - 静态资源上传 CDN
   - 配置域名解析

---

### 阶段4产出

✅ 功能测试通过
✅ 生产构建完成
✅ 部署文档就绪

---

## 📁 API 模块文件拆分参考

### `src/api/user.js`

```javascript
import request from "./request";

// 登录
export const login = (data) => request.post("/users/login", data);

// 获取用户列表
export const getUserList = () => request.get("/users");

// 获取单个用户
export const getUser = (id) => request.get(`/users/${id}`);

// 创建用户
export const createUser = (data) => request.post("/users", data);

// 更新用户
export const updateUser = (id, data) => request.put(`/users/${id}`, data);

// 删除用户
export const deleteUser = (id) => request.delete(`/users/${id}`);
```

### `src/api/package.js`

```javascript
import request from "./request";

// 获取快递列表
export const getPackageList = () => request.get("/packages");

// 按运单号查询
export const getPackageByTracking = (trackingNumber) =>
  request.get(`/packages/tracking/${trackingNumber}`);

// 按手机号查询
export const getPackageByPhone = (phone) =>
  request.get(`/packages/phone/${phone}`);

// 搜索快递
export const searchPackages = (keyword) =>
  request.get("/packages/search", { params: { keyword } });

// 按状态查询
export const getPackageByStatus = (status) =>
  request.get(`/packages/status/${status}`);

// 查询逾期快递
export const getOverduePackages = (days) =>
  request.get("/packages/overdue", { params: { days } });

// 快递入库
export const storePackage = (data) => request.post("/packages", data);

// 更新快递
export const updatePackage = (id, data) => request.put(`/packages/${id}`, data);

// 删除快递
export const deletePackage = (id) => request.delete(`/packages/${id}`);
```

### `src/api/pickup.js`

```javascript
import request from "./request";

// 取件操作
export const pickupPackage = (data) => request.post("/pickups", data);

// 凭取件码取件
export const pickupByCode = (trackingNumber, pickupCode, data) =>
  request.post("/pickups/by-code", data, {
    params: { trackingNumber, pickupCode },
  });

// 获取取件记录
export const getPickupRecords = () => request.get("/pickups");

// 按快递查询取件记录
export const getPickupRecordsByPackage = (packageId) =>
  request.get(`/pickups/package/${packageId}`);

// 检查是否已取件
export const checkIfPickedUp = (packageId) =>
  request.get(`/pickups/check/${packageId}`);
```

---

## 🔑 常量定义参考

### `src/utils/constants.js`

```javascript
// 快递状态枚举
export const PACKAGE_STATUS = {
  STORED: { value: "STORED", label: "已入库", type: "success" },
  PICKED_UP: { value: "PICKED_UP", label: "已取件", type: "info" },
  RETURNED: { value: "RETURNED", label: "已退回", type: "warning" },
  LOST: { value: "LOST", label: "丢失", type: "danger" },
  MISMATCH: { value: "MISMATCH", label: "错件", type: "warning" },
};

// 用户角色枚举
export const USER_ROLES = {
  ADMIN: { value: "ADMIN", label: "管理员", type: "primary" },
  COURIER: { value: "COURIER", label: "快递员", type: "success" },
  OWNER: { value: "OWNER", label: "业主", type: "info" },
};

// 异常类型枚举
export const EXCEPTION_TYPES = {
  DUPLICATE_PICKUP: { value: "DUPLICATE_PICKUP", label: "重复取件" },
  WRONG_PICKUP: { value: "WRONG_PICKUP", label: "取错件" },
  LOST: { value: "LOST", label: "丢件" },
  DAMAGED: { value: "DAMAGED", label: "损坏" },
  MISMATCH_INFO: { value: "MISMATCH_INFO", label: "信息不符" },
  OTHER: { value: "OTHER", label: "其他" },
};

// 快递公司列表
export const COURIER_COMPANIES = [
  "顺丰速运",
  "京东物流",
  "中通快递",
  "圆通速递",
  "韵达快递",
  "其他",
];

// 本地存储Key
export const STORAGE_KEYS = {
  TOKEN: "token",
  USER: "user",
};
```

---

## ✅ 最终交付清单

### 代码交付

1. ✅ 完整的前端项目源码
2. ✅ README.md 项目说明文档
3. ✅ deploy.md 部署文档

### 功能交付

| 模块     | 功能点                 | 状态 |
| -------- | ---------------------- | ---- |
| 用户认证 | 登录、登出、Token管理  | ✅   |
| 快递管理 | 入库、查询、编辑、删除 | ✅   |
| 取件管理 | 取件操作、取件记录     | ✅   |
| 退件管理 | 退件处理、退件记录     | ✅   |
| 异常管理 | 异常登记、处理、关闭   | ✅   |
| 逾期提醒 | 逾期列表、标记处理     | ✅   |
| 统计报表 | 每日统计、图表展示     | ✅   |
| 用户管理 | 用户CRUD（仅管理员）   | ✅   |
| 权限控制 | 角色菜单过滤、路由守卫 | ✅   |

---

**文档完成** - 此方案可直接作为前端开发的详细执行计划使用。
