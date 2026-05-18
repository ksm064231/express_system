<script setup>
import { useRouter } from "vue-router";

const router = useRouter();
</script>

<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <p>页面不存在</p>
      <el-button type="primary" @click="router.push('/')"> 返回首页 </el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

.content {
  text-align: center;
}

h1 {
  font-size: 120px;
  color: #409eff;
  margin: 0;
  line-height: 1;
}

p {
  font-size: 24px;
  color: #666;
  margin: 20px 0 30px;
}
</style>
