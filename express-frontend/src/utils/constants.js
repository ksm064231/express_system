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
