package com.express.service;

import com.express.dto.StatisticsDTO;
import com.express.dto.ApiResponse;
import com.express.entity.ExceptionRecord;
import com.express.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final PackageRepository packageRepository;
    private final PickupRecordRepository pickupRecordRepository;
    private final ReturnRecordRepository returnRecordRepository;
    private final ExceptionRecordRepository exceptionRecordRepository;
    private final OverdueReminderRepository overdueReminderRepository;
    private final SystemConfigRepository systemConfigRepository;

    public StatisticsService(PackageRepository packageRepository,
                             PickupRecordRepository pickupRecordRepository,
                             ReturnRecordRepository returnRecordRepository,
                             ExceptionRecordRepository exceptionRecordRepository,
                             OverdueReminderRepository overdueReminderRepository,
                             SystemConfigRepository systemConfigRepository) {
        this.packageRepository = packageRepository;
        this.pickupRecordRepository = pickupRecordRepository;
        this.returnRecordRepository = returnRecordRepository;
        this.exceptionRecordRepository = exceptionRecordRepository;
        this.overdueReminderRepository = overdueReminderRepository;
        this.systemConfigRepository = systemConfigRepository;
    }

    /**
     * 获取每日统计报表
     */
    public ApiResponse<StatisticsDTO> getDailyStatistics(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // 获取逾期天数配置
        int overdueDays = 3;
        try {
            String val = systemConfigRepository.findByConfigKey("overdue_days")
                    .map(c -> c.getConfigValue()).orElse("3");
            overdueDays = Integer.parseInt(val);
        } catch (Exception ignored) {}

        LocalDateTime overdueThreshold = LocalDateTime.now().minusDays(overdueDays);

        // 统计各项数据
        Long dailyStoredCount = packageRepository.countByArrivalTimeBetween(startOfDay, endOfDay);
        Long dailyPickupCount = pickupRecordRepository.countByPickupTimeBetween(startOfDay, endOfDay);
        Long currentStoredCount = packageRepository.countStoredPackages();
        Long overdueCount = (long) packageRepository.findOverduePackages(overdueThreshold).size();
        Long dailyReturnCount = returnRecordRepository.countByReturnTimeBetween(startOfDay, endOfDay);
        Long pendingExceptionCount = exceptionRecordRepository.countByStatus(ExceptionRecord.ExceptionStatus.PENDING);

        // 按快递公司统计
        Map<String, Long> storedByCompany = new HashMap<>();
        List<Object[]> storedCompanyData = packageRepository.countByCompanyAndArrivalTimeBetween(startOfDay, endOfDay);
        for (Object[] row : storedCompanyData) {
            storedByCompany.put((String) row[0], (Long) row[1]);
        }

        Map<String, Long> pickupByCompany = new HashMap<>();
        List<Object[]> pickupCompanyData = packageRepository.countPickupByCompanyAndTimeBetween(startOfDay, endOfDay);
        for (Object[] row : pickupCompanyData) {
            pickupByCompany.put((String) row[0], (Long) row[1]);
        }

        StatisticsDTO statistics = StatisticsDTO.builder()
                .date(date)
                .dailyStoredCount(dailyStoredCount)
                .dailyPickupCount(dailyPickupCount)
                .currentStoredCount(currentStoredCount)
                .overdueCount(overdueCount)
                .dailyReturnCount(dailyReturnCount)
                .pendingExceptionCount(pendingExceptionCount)
                .storedByCompany(storedByCompany)
                .pickupByCompany(pickupByCompany)
                .build();

        return ApiResponse.success(statistics);
    }
}
