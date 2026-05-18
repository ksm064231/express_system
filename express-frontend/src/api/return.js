import request from "./request";

// 退件处理
export const returnPackage = (data) => request.post("/returns", data);

// 获取退件记录
export const getReturnRecords = () => request.get("/returns");

// 按快递查询退件记录
export const getReturnRecordsByPackage = (packageId) =>
  request.get(`/returns/package/${packageId}`);
