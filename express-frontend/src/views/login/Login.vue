<script setup>
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore();

// ========== 测试账号（课程作业专用） ==========
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

// 快速选择测试账号
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
    // 错误已在 userStore 和拦截器中处理
  }
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <h2>快递代收管理系统</h2>

      <!-- 测试账号快速选择 -->
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
