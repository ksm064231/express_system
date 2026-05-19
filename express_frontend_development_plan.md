# 快递代收管理系统 - 前端开发执行方案

> 基于 Spring Boot 后端 REST API 接口的课程作业简化版前端项目

---

## 项目概览

| 项目信息 | 说明                                        |
| -------- | ------------------------------------------- |
| 项目名称 | 快递代收管理系统前端                        |
| 后端地址 | `http://localhost:8080/api`                 |
| 认证方式 | JWT Token (Bearer)                          |
| 响应格式 | `{ code, message, data }`                   |
| 支持角色 | 管理员(ADMIN)、快递员(COURIER)、业主(OWNER) |

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
  "HTTP客户端": "Axios 1.6+"
}
```

---

## 📋 目录结构

```
express-frontend/
├── src/
│   ├── api/              # API请求层
│   │   ├── request.js    # Axios实例 + 极简拦截器
│   │   ├── user.js       # 用户登录API
│   │   ├── package.js    # 快递管理API
│   │   └── pickup.js     # 取件管理API
│   ├── layouts/          # 布局组件
│   │   └── DefaultLayout.vue  # 主布局
│   ├── router/           # 路由配置
│   │   └── index.js      # 静态导入 + 极简路由守卫
│   ├── stores/           # Pinia状态管理
│   │   └── user.js       # 用户状态管理（简化版）
│   ├── views/            # 页面组件
│   │   ├── login/        # 登录页（含测试账号快速填充）
│   │   ├── dashboard/    # 数据统计
│   │   ├── packages/     # 快递管理（列表、入库）
│   │   └── pickup/       # 取件操作
│   ├── App.vue
│   ├── main.js
│   └── style.css         # 全局样式（已修复冲突）
├── .env.development      # 环境变量
├── vite.config.js        # Vite配置
└── package.json
```

---

## 🔧 核心配置文件说明

---

### 1. Vite 配置

**文件：** `vite.config.js`

```javascript
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    port: 3000,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
```

---

### 2. 环境变量

**文件：** `.env.development`

```env
VITE_API_BASE_URL = http://localhost:8080/api
VITE_APP_TITLE = 快递代收管理系统
```

---

### 3. Axios 请求封装（简化版）

**文件：** `src/api/request.js`

> 课程作业简化原则：去掉复杂的错误处理，优先保证功能可用

```javascript
import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 10000,
});

// 极简请求拦截器：只添加 Token
request.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token && !token.startsWith("mock")) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 极简响应拦截器：直接返回数据
request.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error("请求错误:", error);
    ElMessage.error(error.response?.data?.message || "请求失败");
    return Promise.reject(error);
  },
);

export default request;
```

---

## 🚀 登录认证模块（简化版）

> 设计原则：课程作业优先保证功能稳定，去掉复杂的权限验证和角色控制

---

### 1. 用户状态管理

**文件：** `src/stores/user.js`

```javascript
import { defineStore } from "pinia";
import { login } from "@/api/user";

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    username: localStorage.getItem("username") || "",
    role: localStorage.getItem("role") || "ADMIN",
  }),

  actions: {
    async login(loginData) {
      // 真实后端登录
      const res = await login(loginData);
      this.token = res.data.token;
      this.username = res.data.username;
      this.role = res.data.role;
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("username", res.data.username);
      localStorage.setItem("role", res.data.role);
      return res;

      // ========== 开发模式 Mock（后端有问题时使用） ==========
      // 注释掉上面的代码，取消下面的注释即可使用 Mock 登录
      /*
      this.token = "mock-token-12345";
      this.username = loginData.username;
      this.role = "ADMIN";
      localStorage.setItem("token", "mock-token-12345");
      localStorage.setItem("username", loginData.username);
      localStorage.setItem("role", "ADMIN");
      return { code: 200, message: "登录成功" };
      */
    },

    logout() {
      this.token = "";
      this.username = "";
      this.role = "";
      localStorage.clear();
    },
  },
});
```

---

### 2. 路由配置（简化版）

**文件：** `src/router/index.js`

> 关键优化：使用 **静态导入** 替代动态导入，避免组件加载失败导致页面空白

```javascript
import { createRouter, createWebHistory } from "vue-router";

// 静态导入 - 确保组件正常加载（课程作业推荐）
import Login from "@/views/login/Login.vue";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import Dashboard from "@/views/dashboard/Dashboard.vue";
import PackageList from "@/views/packages/PackageList.vue";
import PackageStore from "@/views/packages/PackageStore.vue";
import PickupOperation from "@/views/pickup/PickupOperation.vue";
import NotFound from "@/views/NotFound.vue";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/",
    redirect: "/dashboard",
    component: DefaultLayout,
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: Dashboard,
      },
      {
        path: "packages",
        name: "PackageList",
        component: PackageList,
      },
      {
        path: "packages/store",
        name: "PackageStore",
        component: PackageStore,
      },
      {
        path: "pickups",
        name: "PickupOperation",
        component: PickupOperation,
      },
    ],
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: NotFound,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// ========== 极简路由守卫 ==========
// 课程作业简化版：只检查 Token 存在性，不做复杂角色权限验证
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");

  // 访问登录页：有 token 跳首页，没 token 继续
  if (to.path === "/login") {
    token ? next("/dashboard") : next();
    return;
  }

  // 访问其他页面：有 token 继续，没 token 跳登录
  token ? next() : next("/login");
});

export default router;
```

---

### 3. 登录页面（课程作业专用）

**文件：** `src/views/login/Login.vue`

> 特色功能：内置测试账号，点击快速填充

```vue
<script setup>
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore();

// ========== 课程作业测试账号 ==========
const testAccounts = [
  { username: "admin", password: "123456", desc: "管理员" },
  { username: "courier", password: "123456", desc: "快递员" },
  { username: "owner", password: "123456", desc: "业主" },
];

// 默认选中第一个测试账号
const loginForm = {
  username: testAccounts[0].username,
  password: testAccounts[0].password,
};

// 点击快速选择测试账号
const selectAccount = (account) => {
  loginForm.username = account.username;
  loginForm.password = account.password;
};

const handleLogin = async () => {
  try {
    await userStore.login(loginForm);
    ElMessage.success("登录成功");
    router.push("/dashboard");
  } catch (error) {
    console.error("登录失败:", error);
    ElMessage.error("登录失败，请检查用户名密码");
  }
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <h2>快递代收管理系统</h2>

      <!-- 测试账号快速选择区域 -->
      <div class="test-accounts">
        <p>测试账号（点击快速填充）：</p>
        <div class="account-list">
          <div
            v-for="account in testAccounts"
            :key="account.username"
            class="account-item"
            @click="selectAccount(account)"
          >
            <span class="account-desc">{{ account.desc }}：</span>
            <span class="account-info"
              >{{ account.username }} / {{ account.password }}</span
            >
          </div>
        </div>
      </div>

      <el-form :model="loginForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 450px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.test-accounts {
  margin-bottom: 30px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 6px;
}

.test-accounts p {
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.account-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.account-item {
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.account-item:hover {
  background: #ecf5ff;
  color: #409eff;
}

.account-desc {
  font-weight: 500;
}

.account-info {
  color: #666;
  font-family: monospace;
}

.login-btn {
  width: 100%;
}
</style>
```

---

## 📁 API 文件参考

---

### 用户 API

**文件：** `src/api/user.js`

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

---

### 快递管理 API

**文件：** `src/api/package.js`

```javascript
import request from "./request";

// 获取快递列表
export const getPackageList = () => request.get("/packages");

// 按运单号查询
export const getPackageByTracking = (trackingNumber) =>
  request.get(`/packages/tracking/${trackingNumber}`);

// 搜索快递
export const searchPackages = (keyword) =>
  request.get("/packages/search", { params: { keyword } });

// 按状态查询
export const getPackageByStatus = (status) =>
  request.get(`/packages/status/${status}`);

// 快递入库
export const storePackage = (data) => request.post("/packages", data);

// 更新快递
export const updatePackage = (id, data) => request.put(`/packages/${id}`, data);

// 删除快递
export const deletePackage = (id) => request.delete(`/packages/${id}`);
```

---

### 取件管理 API

**文件：** `src/api/pickup.js`

```javascript
import request from "./request";

// 取件操作
export const pickupPackage = (data) => request.post("/pickups", data);

// 凭取件码取件
export const pickupByCode = (data) => request.post("/pickups/by-code", data);

// 获取取件记录
export const getPickupRecords = () => request.get("/pickups");
```

---

## 🎨 全局样式

**文件：** `src/style.css`

> 已修复 Vite 默认模板样式冲突问题

```css
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  width: 100%;
  height: 100%;
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue",
    Arial, sans-serif;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Element Plus 覆盖样式 */
.el-card {
  margin-bottom: 20px;
}

.el-table {
  margin-top: 10px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
```

---

## 🚀 项目启动方式

```bash
# 进入前端目录
cd express-frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

浏览器访问：`http://localhost:3000`

---

## 📋 测试账号

| 用户名  | 密码   | 角色   |
| ------- | ------ | ------ |
| admin   | 123456 | 管理员 |
| courier | 123456 | 快递员 |
| owner   | 123456 | 业主   |
