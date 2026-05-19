<script setup>
import { useRouter } from "vue-router";
const router = useRouter();

const goBack = () => {
  router.back();
};

const goHome = () => {
  router.push("/dashboard");
};
</script>

<template>
  <div class="not-found">
    <div class="error-code">404</div>
    <div class="error-message">页面不存在</div>
    <div class="error-desc">抱歉，您访问的页面不存在或已被移除</div>
    <div class="error-actions">
      <el-button @click="goBack">返回上一页</el-button>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f7fa;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #409eff;
  line-height: 1;
  margin-bottom: 20px;
}

.error-message {
  font-size: 32px;
  color: #333;
  margin-bottom: 16px;
}

.error-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 40px;
}

.error-actions {
  display: flex;
  gap: 16px;
}
</style>
