import request from "./request";

// 取件操作
export const pickupPackage = (data) => request.post("/pickups", data);

// 凭取件码取件
export const pickupByCode = (trackingNumber, pickupCode, data) =>
  request.post("/pickups/by-code", data, {
    params: { trackingNumber, pickupCode },
  });

// 获取取件记录
export const getPickupRecords = () => request.get("/pickups");

// 按快递查询取件记录
export const getPickupRecordsByPackage = (packageId) =>
  request.get(`/pickups/package/${packageId}`);

// 检查是否已取件
export const checkIfPickedUp = (packageId) =>
  request.get(`/pickups/check/${packageId}`);
