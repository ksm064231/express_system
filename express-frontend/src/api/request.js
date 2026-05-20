import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 10000,
});

// ========== 极简请求拦截器 ==========
// 课程作业简化版：只加token，不做复杂处理
request.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  // Mock token 不发送到后端
  if (token && !token.startsWith("mock")) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ========== 响应拦截器 ==========
// 适配后端 ApiResponse 格式：{ code, message, data }
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    // 如果 code 不是 200，说明后端返回错误
    if (res.code !== 200) {
      ElMessage.error(res.message || "请求失败");
      return Promise.reject(new Error(res.message || "请求失败"));
    }
    // 返回实际的数据部分
    return res.data;
  },
  (error) => {
    const errorMessage =
      error.response?.data?.message ||
      error.response?.data?.error ||
      "请求失败，请稍后重试";
    ElMessage.error(errorMessage);
    return Promise.reject(error);
  },
);

export default request;
