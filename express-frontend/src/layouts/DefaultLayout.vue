<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import {
  Menu,
  Box,
  TakeawayBox,
  Warning,
  DataLine,
  User,
  SwitchButton,
} from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const isCollapse = ref(false);

const menuItems = [
  {
    path: "/dashboard",
    title: "数据统计",
    icon: DataLine,
  },
  {
    path: "/packages",
    title: "快递管理",
    icon: Box,
  },
  {
    path: "/pickups",
    title: "取件操作",
    icon: TakeawayBox,
  },
  {
    path: "/exceptions",
    title: "异常处理",
    icon: Warning,
  },
];

const activeMenu = computed(() => route.path);

const handleSelect = (key) => {
  router.push(key);
};

const handleLogout = () => {
  userStore.logout();
  router.push("/login");
};
</script>

<template>
  <div class="layout-container">
    <el-container style="height: 100vh">
      <el-aside :width="isCollapse ? '64px' : '200px'">
        <div class="logo">
          <h2 v-if="!isCollapse">快递驿站</h2>
          <h2 v-else>驿站</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          class="sidebar-menu"
          @select="handleSelect"
        >
          <el-menu-item
            v-for="item in menuItems"
            :key="item.path"
            :index="item.path"
          >
            <el-icon>
              <component :is="item.icon" />
            </el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header>
          <div class="header-left">
            <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
              <Menu />
            </el-icon>
          </div>
          <div class="header-right">
            <el-dropdown>
              <div class="user-info">
                <el-icon><User /></el-icon>
                <span>{{ userStore.username || "管理员" }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.el-aside {
  background-color: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background-color: #263445;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.sidebar-menu {
  border-right: none;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.collapse-btn:hover {
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.user-info:hover {
  color: #409eff;
}

.el-main {
  background-color: #f5f7fa;
  overflow-y: auto;
}
</style>
