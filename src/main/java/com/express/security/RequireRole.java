package com.express.security;

import com.express.entity.User.UserRole;

import java.lang.annotation.*;

/**
 * 自定义注解：要求用户具有指定角色才能访问接口
 * 使用方式：@RequireRole(UserRole.ADMIN)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    UserRole[] value() default {};
}
