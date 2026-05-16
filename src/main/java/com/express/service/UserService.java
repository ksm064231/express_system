package com.express.service;

import com.express.dto.LoginDTO;
import com.express.dto.ApiResponse;
import com.express.entity.User;
import com.express.exception.BusinessException;
import com.express.repository.UserRepository;
import com.express.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     */
    public ApiResponse<Map<String, Object>> login(LoginDTO loginDTO) {
        // 通过用户名查找用户
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == User.UserStatus.DISABLED) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("tokenType", "Bearer");
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        result.put("roomNumber", user.getRoomNumber());

        return ApiResponse.success("登录成功", result);
    }

    /**
     * 获取所有用户
     */
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.success(userRepository.findAll());
    }

    /**
     * 根据ID获取用户
     */
    public ApiResponse<User> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return ApiResponse.success(user);
    }

    /**
     * 创建用户（密码使用 BCrypt 加密）
     */
    public ApiResponse<User> createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ApiResponse.success("创建成功", userRepository.save(user));
    }

    /**
     * 更新用户
     */
    public ApiResponse<User> updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        user.setRealName(userDetails.getRealName());
        user.setPhone(userDetails.getPhone());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setRoomNumber(userDetails.getRoomNumber());
        user.setStatus(userDetails.getStatus());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return ApiResponse.success("更新成功", userRepository.save(user));
    }

    /**
     * 删除用户
     */
    public ApiResponse<Void> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
