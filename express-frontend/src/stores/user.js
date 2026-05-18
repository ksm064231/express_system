import { defineStore } from "pinia";
import { login } from "@/api/user";

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    userInfo: JSON.parse(localStorage.getItem("user") || "null"),
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userRole: (state) => state.userInfo?.role || "",
  },

  actions: {
    async login(loginData) {
      const res = await login(loginData);
      this.token = res.data.token;
      this.userInfo = res.data.user;
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("user", JSON.stringify(res.data.user));
      return res;
    },

    logout() {
      this.token = "";
      this.userInfo = null;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
    },
  },
});
