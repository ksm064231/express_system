package com.express.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 当前登录用户信息，存储在 SecurityContext 中
 */
@Data
@AllArgsConstructor
public class JwtUserDetails {
    private Long userId;
    private String username;
    private String role;
}
