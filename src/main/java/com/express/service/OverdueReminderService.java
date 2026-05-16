package com.express.service;

import com.express.dto.ApiResponse;
import com.express.entity.OverdueReminder;
import com.express.entity.Package;
import com.express.repository.OverdueReminderRepository;
import com.express.repository.PackageRepository;
import com.express.repository.SystemConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OverdueReminderService {

    private static final Logger log = LoggerFactory.getLogger(OverdueReminderService.class);

    private final PackageRepository packageRepository;
    private final OverdueReminderRepository overdueReminderRepository;
    private final SystemConfigRepository systemConfigRepository;

    public OverdueReminderService(PackageRepository packageRepository,
                                  OverdueReminderRepository overdueReminderRepository,
                                  SystemConfigRepository systemConfigRepository) {
        this.packageRepository = packageRepository;
        this.overdueReminderRepository = overdueReminderRepository;
        this.systemConfigRepository = systemConfigRepository;
    }

    /**
     * 定时任务：每天检查逾期未取快递并生成提醒
     * 每天上午9点执行
     */
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void checkAndCreateOverdueReminders() {
        log.info("开始检查逾期未取快递...");

        // 获取逾期天数配置
        int overdueDays = getOverdueDaysConfig();
        LocalDateTime threshold = LocalDateTime.now().minusDays(overdueDays);

        // 查找逾期未取快递
        List<Package> overduePackages = packageRepository.findOverduePackages(threshold);

        for (Package pkg : overduePackages) {
            // 检查是否已生成过提醒
            List<OverdueReminder> existingReminders = overdueReminderRepository.findByPackageId(pkg.getId());
            boolean alreadyNotified = existingReminders.stream()
                    .anyMatch(r -> !r.getIsResolved());

            if (!alreadyNotified) {
                OverdueReminder reminder = OverdueReminder.builder()
                        .packageId(pkg.getId())
                        .overdueDays(overdueDays)
                        .reminderTime(LocalDateTime.now())
                        .reminderMethod(OverdueReminder.ReminderMethod.MANUAL)
                        .notifiedPerson(pkg.getRecipientName())
                        .notifiedPhone(pkg.getRecipientPhone())
                        .notes("快递" + pkg.getTrackingNumber() + "已逾期" + overdueDays + "天未取件")
                        .build();

                overdueReminderRepository.save(reminder);
                log.info("生成逾期提醒: 运单号={}, 收件人={}, 逾期天数={}",
                        pkg.getTrackingNumber(), pkg.getRecipientName(), overdueDays);
            }
        }

        log.info("逾期检查完成，共处理 {} 个逾期快递", overduePackages.size());
    }

    /**
     * 获取所有未处理的逾期提醒
     */
    public ApiResponse<List<OverdueReminder>> getUnresolvedReminders() {
        List<OverdueReminder> reminders = overdueReminderRepository.findByIsResolvedFalse();
        return ApiResponse.success(reminders);
    }

    /**
     * 获取所有逾期提醒
     */
    public ApiResponse<List<OverdueReminder>> getAllReminders() {
        List<OverdueReminder> reminders = overdueReminderRepository.findAll();
        return ApiResponse.success(reminders);
    }

    /**
     * 标记提醒为已处理
     */
    @Transactional
    public ApiResponse<OverdueReminder> resolveReminder(Long id) {
        OverdueReminder reminder = overdueReminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("提醒记录不存在"));
        reminder.setIsResolved(true);
        reminder.setResolvedTime(LocalDateTime.now());
        return ApiResponse.success("已处理", overdueReminderRepository.save(reminder));
    }

    /**
     * 获取逾期天数配置
     */
    private int getOverdueDaysConfig() {
        try {
            return systemConfigRepository.findByConfigKey("overdue_days")
                    .map(config -> Integer.parseInt(config.getConfigValue()))
                    .orElse(3);
        } catch (Exception e) {
            return 3;
        }
    }
}
