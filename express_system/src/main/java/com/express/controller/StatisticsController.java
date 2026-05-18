package com.express.controller;

import com.express.dto.StatisticsDTO;
import com.express.dto.ApiResponse;
import com.express.entity.User.UserRole;
import com.express.security.RequireRole;
import com.express.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "统计报表", description = "每日入库量、取件量、滞留件统计等报表接口")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/daily")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取每日统计报表（快递员/管理员）")
    public ApiResponse<StatisticsDTO> getDailyStatistics(
            @RequestParam(required = false) String date) {
        LocalDate queryDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return statisticsService.getDailyStatistics(queryDate);
    }
}
