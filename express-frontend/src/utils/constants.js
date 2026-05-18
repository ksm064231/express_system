// 快递状态枚举
export const PACKAGE_STATUS = {
  STORED: { value: "STORED", label: "已入库", type: "success" },
  PICKED_UP: { value: "PICKED_UP", label: "已取件", type: "info" },
  RETURNED: { value: "RETURNED", label: "已退回", type: "warning" },
  LOST: { value: "LOST", label: "丢失", type: "danger" },
  MISMATCH: { value: "MISMATCH", label: "错件", type: "warning" },
};

// 用户角色枚举
export const USER_ROLES = {
  ADMIN: { value: "ADMIN", label: "管理员", type: "primary" },
  COURIER: { value: "COURIER", label: "快递员", type: "success" },
  OWNER: { value: "OWNER", label: "业主", type: "info" },
};

// 异常类型枚举
export const EXCEPTION_TYPES = {
  DUPLICATE_PICKUP: { value: "DUPLICATE_PICKUP", label: "重复取件" },
  WRONG_PICKUP: { value: "WRONG_PICKUP", label: "取错件" },
  LOST: { value: "LOST", label: "丢件" },
  DAMAGED: { value: "DAMAGED", label: "损坏" },
  MISMATCH_INFO: { value: "MISMATCH_INFO", label: "信息不符" },
  OTHER: { value: "OTHER", label: "其他" },
};

// 快递公司列表
export const COURIER_COMPANIES = [
  "顺丰速运",
  "京东物流",
  "中通快递",
  "圆通速递",
  "韵达快递",
  "其他",
];

// 本地存储Key
export const STORAGE_KEYS = {
  TOKEN: "token",
  USER: "user",
};
