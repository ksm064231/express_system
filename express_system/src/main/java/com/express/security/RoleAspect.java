package com.express.security;

import com.express.entity.User.UserRole;
import com.express.exception.BusinessException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 切面：校验 @RequireRole 注解，实现角色权限控制
 */
@Aspect
@Component
public class RoleAspect {

    @Before("@within(requireRole) || @annotation(requireRole)")
    public void checkRole(RequireRole requireRole) {
        if (requireRole == null) {
            return;
        }

        UserRole[] requiredRoles = requireRole.value();
        if (requiredRoles.length == 0) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(401, "未登录，请先登录");
        }

        // 获取当前用户拥有的角色
        Set<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // 检查是否拥有所需角色之一
        boolean hasRole = Arrays.stream(requiredRoles)
                .anyMatch(role -> userRoles.contains("ROLE_" + role.name()));

        if (!hasRole) {
            String required = Arrays.toString(requiredRoles);
            throw new BusinessException(403, "权限不足，需要角色: " + required);
        }
    }
}
