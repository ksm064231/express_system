import request from "./request";

// 获取所有逾期提醒
export const getReminderList = () => request.get("/reminders");

// 获取未处理的逾期提醒
export const getUnresolvedReminders = () =>
  request.get("/reminders/unresolved");

// 标记为已处理
export const resolveReminder = (id) => request.put(`/reminders/${id}/resolve`);
