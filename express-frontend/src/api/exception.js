import request from "./request";

// 创建异常记录
export const createException = (data) => request.post("/exceptions", data);

// 获取所有异常记录
export const getExceptionList = () => request.get("/exceptions");

// 按快递查询
export const getExceptionByPackage = (packageId) =>
  request.get(`/exceptions/package/${packageId}`);

// 按状态查询
export const getExceptionByStatus = (status) =>
  request.get(`/exceptions/status/${status}`);

// 处理异常
export const handleException = (id, data) =>
  request.put(`/exceptions/${id}/handle`, data);

// 关闭异常（仅管理员）
export const closeException = (id) => request.put(`/exceptions/${id}/close`);

// 解决异常（兼容resolveException命名）
export const resolveException = (id, data) =>
  request.put(`/exceptions/${id}/handle`, data);

// 删除异常
export const deleteException = (id) => request.delete(`/exceptions/${id}`);
