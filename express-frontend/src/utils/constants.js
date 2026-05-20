// 快递状态常量
export const PACKAGE_STATUS = {
  STORED: {
    value: "STORED",
    label: "待取件",
    type: "warning",
  },
  PICKED_UP: {
    value: "PICKED_UP",
    label: "已取件",
    type: "success",
  },
  RETURNED: {
    value: "RETURNED",
    label: "已退回",
    type: "info",
  },
  EXPIRED: {
    value: "EXPIRED",
    label: "已过期",
    type: "danger",
  },
};

// 角色常量
export const ROLES = {
  ADMIN: "ADMIN",
  COURIER: "COURIER",
  OWNER: "OWNER",
};

// 快递公司列表
export const COURIER_COMPANIES = [
  "顺丰速运",
  "京东物流",
  "中通快递",
  "圆通速递",
  "韵达快递",
  "申通快递",
  "EMS",
  "极兔速递",
  "其他",
];

// 异常类型常量 - 必须与后端 ExceptionType 枚举完全一致
export const EXCEPTION_TYPE = {
  DUPLICATE_PICKUP: {
    value: "DUPLICATE_PICKUP",
    label: "重复取件",
  },
  WRONG_PICKUP: {
    value: "WRONG_PICKUP",
    label: "取错件",
  },
  LOST: {
    value: "LOST",
    label: "包裹丢失",
  },
  DAMAGED: {
    value: "DAMAGED",
    label: "包裹破损",
  },
  MISMATCH_INFO: {
    value: "MISMATCH_INFO",
    label: "信息不符",
  },
  OTHER: {
    value: "OTHER",
    label: "其他问题",
  },
};

// 异常状态常量 - 必须与后端 ExceptionStatus 枚举完全一致
export const EXCEPTION_STATUS = {
  PENDING: {
    value: "PENDING",
    label: "待处理",
    type: "warning",
  },
  RESOLVED: {
    value: "RESOLVED",
    label: "已解决",
    type: "success",
  },
  CLOSED: {
    value: "CLOSED",
    label: "已关闭",
    type: "info",
  },
};
