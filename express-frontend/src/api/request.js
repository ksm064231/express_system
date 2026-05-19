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

// ========== 极简响应拦截器 ==========
// 课程作业简化版：直接返回数据，不做复杂的code判断
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
