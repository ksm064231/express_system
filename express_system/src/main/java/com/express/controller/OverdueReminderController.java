package com.express.controller;

import com.express.dto.ApiResponse;
import com.express.entity.OverdueReminder;
import com.express.entity.User.UserRole;
import com.express.security.RequireRole;
import com.express.service.OverdueReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@Tag(name = "逾期提醒", description = "逾期提醒查询、处理接口")
public class OverdueReminderController {

    private final OverdueReminderService overdueReminderService;

    public OverdueReminderController(OverdueReminderService overdueReminderService) {
        this.overdueReminderService = overdueReminderService;
    }

    @GetMapping
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取所有逾期提醒（快递员/管理员）")
    public ApiResponse<List<OverdueReminder>> getAllReminders() {
        return overdueReminderService.getAllReminders();
    }

    @GetMapping("/unresolved")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取未处理的逾期提醒（快递员/管理员）")
    public ApiResponse<List<OverdueReminder>> getUnresolvedReminders() {
        return overdueReminderService.getUnresolvedReminders();
    }

    @PutMapping("/{id}/resolve")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "标记提醒为已处理（快递员/管理员）")
    public ApiResponse<OverdueReminder> resolveReminder(@PathVariable Long id) {
        return overdueReminderService.resolveReminder(id);
    }
}
