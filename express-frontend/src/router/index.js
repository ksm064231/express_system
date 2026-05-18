import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/Login.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/",
    redirect: "/dashboard",
    component: () => import("@/components/Layout.vue"),
    meta: { requiresAuth: true },
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/dashboard/Dashboard.vue"),
        meta: { title: "数据统计" },
      },
      {
        path: "packages",
        name: "PackageList",
        component: () => import("@/views/packages/PackageList.vue"),
        meta: { title: "快递管理" },
      },
      {
        path: "packages/store",
        name: "PackageStore",
        component: () => import("@/views/packages/PackageStore.vue"),
        meta: { title: "快递入库" },
      },
    ],
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import("@/views/NotFound.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const requiresAuth = to.meta.requiresAuth !== false;

  if (requiresAuth && !userStore.isLoggedIn) {
    next("/login");
  } else if (to.path === "/login" && userStore.isLoggedIn) {
    next("/");
  } else {
    next();
  }
});

export default router;
