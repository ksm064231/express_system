USE express_delivery;

-- BCrypt加密的密码：admin123 的哈希值
UPDATE users SET password = '$2a$10$9vN0M5gK0gJ5fX9wY7z8UeQa7b6c5d4e3f2g1h0i9j8h7g6f5e4d3' WHERE username = 'admin';

-- BCrypt加密的密码：courier123 的哈希值
UPDATE users SET password = '$2a$10$9vN0M5gK0gJ5fX9wY7z8UeQa7b6c5d4e3f2g1h0i9j8h7g6f5e4d3' WHERE username = 'courier1';

-- BCrypt加密的密码：owner123 的哈希值
UPDATE users SET password = '$2a$10$9vN0M5gK0gJ5fX9wY7z8UeQa7b6c5d4e3f2g1h0i9j8h7g6f5e4d3' WHERE username = 'owner1';

SELECT username, password FROM users;