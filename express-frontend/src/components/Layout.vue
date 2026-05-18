<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();

const isCollapse = ref(false);

const menuItems = computed(() => {
  const role = userStore.userRole;
  const menus = [
    {
      path: "/dashboard",
      title: "数据统计",
      icon: "DataLine",
      roles: ["ADMIN", "COURIER", "OWNER"],
    },
    {
      path: "/packages",
      title: "快递管理",
      icon: "Box",
      roles: ["ADMIN", "COURIER"],
    },
    {
      path: "/packages/store",
      title: "快递入库",
      icon: "Plus",
      roles: ["ADMIN", "COURIER"],
    },
    {
      path: "/pickups",
      title: "取件管理",
      icon: "TakeawayBox",
      roles: ["ADMIN", "COURIER"],
    },
    {
      path: "/returns",
      title: "退件管理",
      icon: "RefreshLeft",
      roles: ["ADMIN", "COURIER"],
    },
    {
      path: "/exceptions",
      title: "异常管理",
      icon: "Warning",
      roles: ["ADMIN", "COURIER"],
    },
    {
      path: "/reminders",
      title: "逾期提醒",
      icon: "Alarm",
      roles: ["ADMIN", "COURIER"],
    },
    {
      path: "/users",
      title: "用户管理",
      icon: "User",
      roles: ["ADMIN"],
    },
  ];

  return menus.filter((menu) => menu.roles.includes(role));
});

const handleLogout = () => {
  userStore.logout();
  router.push("/login");
};

const handleMenuSelect = (index) => {
  router.push(index);
};
</script>

<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo">
        <span v-if="!isCollapse">快递管理系统</span>
        <span v-else>快递</span>
      </div>
      <el-menu
        :default-active="router.currentRoute.value.path"
        :collapse="isCollapse"
        class="menu"
        @select="handleMenuSelect"
      >
        <el-menu-item
          v-for="item in menuItems"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-button
            :icon="isCollapse ? 'Expand' : 'Fold'"
            @click="isCollapse = !isCollapse"
            circle
          />
        </div>
        <div class="header-right">
          <span class="username">{{ userStore.userInfo?.username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">
            退出
          </el-button>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}

.menu {
  border-right: none;
  background-color: #304156;
}

:deep(.el-menu-item) {
  color: #bfcbd9;
}

:deep(.el-menu-item:hover) {
  color: #fff;
  background-color: #263445;
}

:deep(.el-menu-item.is-active) {
  color: #409eff;
  background-color: #263445;
}

.main-container {
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #333;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow: auto;
}
</style>
