import { createRouter, createWebHistory } from "vue-router";

// 静态导入 - 确保组件正常加载
import Login from "@/views/login/Login.vue";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import Dashboard from "@/views/dashboard/Dashboard.vue";
import PackageList from "@/views/packages/PackageList.vue";
import PackageStore from "@/views/packages/PackageStore.vue";
import PickupOperation from "@/views/pickup/PickupOperation.vue";
import ExceptionList from "@/views/exceptions/ExceptionList.vue";
import ExceptionCreate from "@/views/exceptions/ExceptionCreate.vue";
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
      {
        path: "packages/edit/:id",
        name: "PackageEdit",
        component: PackageStore,
      },
      {
        path: "exceptions",
        name: "ExceptionList",
        component: ExceptionList,
      },
      {
        path: "exceptions/create",
        name: "ExceptionCreate",
        component: ExceptionCreate,
      },
      {
        path: "exceptions/edit/:id",
        name: "ExceptionEdit",
        component: ExceptionCreate,
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
// 课程作业简化版：只检查Token，不做复杂权限验证
router.beforeEach((to) => {
  const token = localStorage.getItem("token");

  // 访问登录页：有token跳首页，没token继续
  if (to.path === "/login") {
    if (token) {
      return "/dashboard";
    }
    return;
  }

  // 访问其他页面：有token继续，没token跳登录
  if (!token) {
    return "/login";
  }
});

export default router;
