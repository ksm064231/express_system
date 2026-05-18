import request from "./request";

// 登录
export const login = (data) => request.post("/users/login", data);

// 获取用户列表
export const getUserList = () => request.get("/users");

// 获取单个用户
export const getUser = (id) => request.get(`/users/${id}`);

// 创建用户
export const createUser = (data) => request.post("/users", data);

// 更新用户
export const updateUser = (id, data) => request.put(`/users/${id}`, data);

// 删除用户
export const deleteUser = (id) => request.delete(`/users/${id}`);
