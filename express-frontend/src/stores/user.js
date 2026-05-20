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
      const res = await login(loginData);
      this.token = res.data.token;
      this.username = res.data.username;
      this.role = res.data.role;
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("username", res.data.username);
      localStorage.setItem("role", res.data.role);
      return res;
    },

    logout() {
      this.token = "";
      this.username = "";
      this.role = "";
      localStorage.clear();
    },
  },
});
