import request from "./request";

// 获取快递列表
export const getPackageList = () => request.get("/packages");

// 按运单号查询
export const getPackageByTracking = (trackingNumber) =>
  request.get(`/packages/tracking/${trackingNumber}`);

// 按手机号查询
export const getPackageByPhone = (phone) =>
  request.get(`/packages/phone/${phone}`);

// 搜索快递
export const searchPackages = (keyword) =>
  request.get("/packages/search", { params: { keyword } });

// 按状态查询
export const getPackageByStatus = (status) =>
  request.get(`/packages/status/${status}`);

// 查询逾期快递
export const getOverduePackages = (days) =>
  request.get("/packages/overdue", { params: { days } });

// 快递入库
export const storePackage = (data) => request.post("/packages", data);

// 更新快递
export const updatePackage = (id, data) => request.put(`/packages/${id}`, data);

// 删除快递
export const deletePackage = (id) => request.delete(`/packages/${id}`);
