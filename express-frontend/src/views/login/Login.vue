<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore();

const loginForm = ref({
  username: "",
  password: "",
});

const loading = ref(false);

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning("请输入用户名和密码");
    return;
  }

  loading.value = true;
  try {
    await userStore.login(loginForm.value);
    ElMessage.success("登录成功");
    router.push("/");
  } catch (error) {
    console.error("登录失败:", error);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">快递代收管理系统</h2>
      <el-form
        :model="loginForm"
        label-width="80px"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="用户名">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :disabled="loading"
          />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :disabled="loading"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="test-accounts">
        <p>测试账号：</p>
        <p>管理员: admin / admin123</p>
        <p>快递员: courier1 / courier123</p>
        <p>业主: owner1 / owner123</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.login-btn {
  width: 100%;
}

.test-accounts {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  font-size: 12px;
  color: #999;
}

.test-accounts p {
  margin: 5px 0;
}
</style>
