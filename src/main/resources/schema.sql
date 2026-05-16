-- ============================================
-- 快递代收管理系统 - 数据库初始化脚本
-- Express Delivery Management System
-- ============================================

CREATE DATABASE IF NOT EXISTS express_delivery
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE express_delivery;

-- ============================================
-- 用户表 (快递员、管理员、业主)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    role ENUM('COURIER', 'ADMIN', 'OWNER') NOT NULL COMMENT '角色: 快递员/管理员/业主',
    room_number VARCHAR(50) COMMENT '房号(业主)',
    status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 快递信息表
-- ============================================
CREATE TABLE IF NOT EXISTS packages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tracking_number VARCHAR(100) NOT NULL UNIQUE COMMENT '运单号',
    courier_company VARCHAR(50) NOT NULL COMMENT '快递公司',
    recipient_name VARCHAR(50) NOT NULL COMMENT '收件人姓名',
    recipient_phone VARCHAR(20) NOT NULL COMMENT '收件人电话',
    room_number VARCHAR(50) COMMENT '房号',
    pickup_code VARCHAR(20) COMMENT '取件码',
    item_name VARCHAR(200) COMMENT '物品名称',
    item_type VARCHAR(50) COMMENT '物品类型',
    status ENUM('STORED', 'PICKED_UP', 'RETURNED', 'LOST', 'MISMATCH') DEFAULT 'STORED' COMMENT '状态: 已入库/已取件/已退回/丢失/错件',
    shelf_number VARCHAR(50) COMMENT '存放货架号',
    arrival_time DATETIME NOT NULL COMMENT '到达时间',
    stored_by BIGINT COMMENT '入库登记人(快递员ID)',
    notes TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tracking_number (tracking_number),
    INDEX idx_recipient_phone (recipient_phone),
    INDEX idx_status (status),
    INDEX idx_arrival_time (arrival_time),
    FOREIGN KEY (stored_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递信息表';

-- ============================================
-- 取件记录表
-- ============================================
CREATE TABLE IF NOT EXISTS pickup_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    package_id BIGINT NOT NULL COMMENT '快递ID',
    pickup_time DATETIME NOT NULL COMMENT '取件时间',
    pickup_person_name VARCHAR(50) NOT NULL COMMENT '取件人姓名',
    pickup_person_phone VARCHAR(20) COMMENT '取件人电话',
    signature VARCHAR(255) COMMENT '取件人签名(base64)',
    verification_method ENUM('CODE', 'PHONE', 'ID_CARD', 'SIGNATURE') DEFAULT 'CODE' COMMENT '验证方式',
    verified_by BIGINT COMMENT '核验人(管理员ID)',
    notes TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (package_id) REFERENCES packages(id),
    FOREIGN KEY (verified_by) REFERENCES users(id),
    INDEX idx_package_id (package_id),
    INDEX idx_pickup_time (pickup_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='取件记录表';

-- ============================================
-- 退件记录表
-- ============================================
CREATE TABLE IF NOT EXISTS return_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    package_id BIGINT NOT NULL COMMENT '快递ID',
    return_reason VARCHAR(500) NOT NULL COMMENT '退件原因',
    return_time DATETIME NOT NULL COMMENT '退回时间',
    processed_by BIGINT COMMENT '处理人(管理员ID)',
    courier_name VARCHAR(50) COMMENT '退回快递员',
    return_notes TEXT COMMENT '处理备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (package_id) REFERENCES packages(id),
    FOREIGN KEY (processed_by) REFERENCES users(id),
    INDEX idx_package_id (package_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退件记录表';

-- ============================================
-- 逾期提醒记录表
-- ============================================
CREATE TABLE IF NOT EXISTS overdue_reminders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    package_id BIGINT NOT NULL COMMENT '快递ID',
    overdue_days INT NOT NULL COMMENT '逾期天数',
    reminder_time DATETIME NOT NULL COMMENT '提醒时间',
    reminder_method ENUM('SMS', 'PHONE', 'APP', 'MANUAL') DEFAULT 'MANUAL' COMMENT '提醒方式',
    notified_person VARCHAR(50) COMMENT '被通知人',
    notified_phone VARCHAR(20) COMMENT '通知电话',
    is_resolved BOOLEAN DEFAULT FALSE COMMENT '是否已处理',
    resolved_time DATETIME COMMENT '处理时间',
    notes TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (package_id) REFERENCES packages(id),
    INDEX idx_package_id (package_id),
    INDEX idx_is_resolved (is_resolved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='逾期提醒记录表';

-- ============================================
-- 异常处理记录表 (重复取件、取错件等)
-- ============================================
CREATE TABLE IF NOT EXISTS exception_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    package_id BIGINT COMMENT '关联快递ID',
    exception_type ENUM('DUPLICATE_PICKUP', 'WRONG_PICKUP', 'LOST', 'DAMAGED', 'MISMATCH_INFO', 'OTHER') NOT NULL COMMENT '异常类型',
    description VARCHAR(1000) NOT NULL COMMENT '异常描述',
    handler_id BIGINT COMMENT '处理人(管理员ID)',
    handling_result VARCHAR(1000) COMMENT '处理结果',
    handling_time DATETIME COMMENT '处理时间',
    status ENUM('PENDING', 'RESOLVED', 'CLOSED') DEFAULT 'PENDING' COMMENT '处理状态',
    compensation_amount DECIMAL(10,2) COMMENT '赔偿金额',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (package_id) REFERENCES packages(id),
    FOREIGN KEY (handler_id) REFERENCES users(id),
    INDEX idx_package_id (package_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常处理记录表';

-- ============================================
-- 系统配置表
-- ============================================
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(500) NOT NULL COMMENT '配置值',
    description VARCHAR(255) COMMENT '配置说明',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入默认配置
INSERT INTO system_config (config_key, config_value, description) VALUES
('overdue_days', '3', '逾期天数阈值，超过此天数未取件视为逾期'),
('max_shelf_days', '7', '最大存放天数'),
('reminder_interval_hours', '24', '提醒间隔(小时)')
ON DUPLICATE KEY UPDATE config_key=config_key;

-- ============================================
-- 插入默认管理员账号
-- ============================================
INSERT INTO users (username, password, real_name, phone, role, room_number, status) VALUES
('admin', 'admin123', '系统管理员', '13800000000', 'ADMIN', NULL, 'ACTIVE'),
('courier1', 'courier123', '快递员张三', '13900000001', 'COURIER', NULL, 'ACTIVE'),
('owner1', 'owner123', '业主李四', '13700000002', 'OWNER', 'A-101', 'ACTIVE')
ON DUPLICATE KEY UPDATE username=username;
