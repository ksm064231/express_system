import request from "./request";

// 获取每日统计
export const getDailyStatistics = (date) =>
  request.get("/statistics/daily", { params: { date } });
