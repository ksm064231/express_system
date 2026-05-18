package com.express.service;

import com.express.entity.Package;
import com.express.entity.User;
import com.express.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public MailService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    /**
     * 快递入库时发送取件码通知邮件
     * 优先查找收件人手机号对应的业主用户，如果该用户配置了邮箱则发送
     */
    public void sendPickupCodeNotification(Package pkg) {
        // 尝试通过收件人手机号查找业主用户
        Optional<User> userOpt = userRepository.findByPhone(pkg.getRecipientPhone());

        if (userOpt.isEmpty()) {
            log.info("未找到收件人 {} 的用户信息，跳过邮件通知", pkg.getRecipientPhone());
            return;
        }

        User user = userOpt.get();
        String email = user.getEmail();

        if (email == null || email.isBlank()) {
            log.info("用户 {} 未配置邮箱，跳过邮件通知", user.getUsername());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("【快递代收】您有新的快递已入库");

            String text = String.format(
                    "尊敬的 %s，您好！\n\n" +
                    "您有一件快递已到达快递代收点，请凭取件码及时取件。\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "  取件码：%s\n" +
                    "  快递公司：%s\n" +
                    "  运单号：%s\n" +
                    "  存放位置：%s\n" +
                    "  到达时间：%s\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "温馨提示：快递到店后请尽快取件，超过 %d 天未取件将视为逾期。\n\n" +
                    "本邮件由快递代收管理系统自动发送，请勿回复。",
                    user.getRealName(),
                    pkg.getPickupCode(),
                    pkg.getCourierCompany(),
                    pkg.getTrackingNumber(),
                    pkg.getShelfNumber() != null ? pkg.getShelfNumber() : "待分配",
                    pkg.getArrivalTime().toLocalDate().toString(),
                    3
            );

            message.setText(text);
            mailSender.send(message);
            log.info("取件码邮件已发送至 {} ({})", email, user.getRealName());
        } catch (Exception e) {
            log.error("发送取件码邮件失败: {}", e.getMessage());
        }
    }
}
