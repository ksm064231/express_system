import { defineStore } from "pinia";
import { login } from "@/api/user";

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    username: localStorage.getItem("username") || "",
    role: localStorage.getItem("role") || "ADMIN",
  }),

  actions: {
    async login(loginData) {
      // ========== 真实后端登录 ==========
      const res = await login(loginData);
      this.token = res.data.token;
      this.username = res.data.username;
      this.role = res.data.role;
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("username", res.data.username);
      localStorage.setItem("role", res.data.role);
      return res;

      // ========== 开发模式 Mock（后端有问题时取消注释） ==========
      /*
      this.token = "mock-token-12345";
      this.username = loginData.username;
      this.role = "ADMIN";
      localStorage.setItem("token", "mock-token-12345");
      localStorage.setItem("username", loginData.username);
      localStorage.setItem("role", "ADMIN");
      return { code: 200, message: "登录成功" };
      */
    },

    logout() {
      this.token = "";
      this.username = "";
      this.role = "";
      localStorage.clear();
    },
  },
});
