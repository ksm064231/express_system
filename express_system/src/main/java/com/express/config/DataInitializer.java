package com.express.config;

import com.express.entity.User;
import com.express.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 创建或更新管理员
        createOrUpdateUser("admin", "admin123", "系统管理员", "13800138000", "admin@example.com", "ADMIN", "001");
        
        // 创建或更新快递员
        createOrUpdateUser("courier1", "courier123", "张三", "13900139001", "courier1@example.com", "COURIER", "002");
        
        // 创建或更新业主
        createOrUpdateUser("owner1", "owner123", "李四", "13700137001", "owner1@example.com", "OWNER", "101");
    }

    private void createOrUpdateUser(String username, String rawPassword, String realName, 
                                   String phone, String email, String role, String roomNumber) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 检查密码是否已经是BCrypt格式
            if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
                user.setPassword(passwordEncoder.encode(rawPassword));
                userRepository.save(user);
                System.out.println("已更新用户 " + username + " 的密码为BCrypt加密格式");
            }
        } else {
            // 创建新用户
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRealName(realName);
            user.setPhone(phone);
            user.setEmail(email);
            user.setRole(User.UserRole.valueOf(role));
            user.setRoomNumber(roomNumber);
            user.setStatus(User.UserStatus.ACTIVE);
            userRepository.save(user);
            System.out.println("已创建用户 " + username);
        }
    }
}
