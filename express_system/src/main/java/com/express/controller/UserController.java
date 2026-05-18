package com.express.controller;

import com.express.dto.LoginDTO;
import com.express.dto.ApiResponse;
import com.express.entity.User;
import com.express.entity.User.UserRole;
import com.express.security.RequireRole;
import com.express.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户登录、注册、管理接口")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @GetMapping
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "获取所有用户（仅管理员）")
    public ApiResponse<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "根据ID获取用户（仅管理员）")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "创建用户（仅管理员）")
    public ApiResponse<User> createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "更新用户信息（仅管理员）")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "删除用户（仅管理员）")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
