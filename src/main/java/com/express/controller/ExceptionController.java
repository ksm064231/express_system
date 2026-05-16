package com.express.controller;

import com.express.dto.ExceptionDTO;
import com.express.dto.ApiResponse;
import com.express.entity.User.UserRole;
import com.express.security.RequireRole;
import com.express.service.ExceptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exceptions")
@Tag(name = "异常管理", description = "错件/丢件/重复取件等异常处理接口")
public class ExceptionController {

    private final ExceptionService exceptionService;

    public ExceptionController(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    @PostMapping
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "创建异常记录（快递员/管理员）")
    public ApiResponse<ExceptionDTO> createException(@Valid @RequestBody ExceptionDTO exceptionDTO) {
        return exceptionService.createException(exceptionDTO);
    }

    @GetMapping
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取所有异常记录（快递员/管理员）")
    public ApiResponse<List<ExceptionDTO>> getAllExceptions() {
        return exceptionService.getAllExceptions();
    }

    @GetMapping("/package/{packageId}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取快递的异常记录（快递员/管理员）")
    public ApiResponse<List<ExceptionDTO>> getByPackageId(@PathVariable Long packageId) {
        return exceptionService.getExceptionsByPackageId(packageId);
    }

    @GetMapping("/status/{status}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "根据状态获取异常记录（快递员/管理员）")
    public ApiResponse<List<ExceptionDTO>> getByStatus(@PathVariable String status) {
        return exceptionService.getExceptionsByStatus(status);
    }

    @PutMapping("/{id}/handle")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "处理异常（快递员/管理员）")
    public ApiResponse<ExceptionDTO> handleException(@PathVariable Long id,
                                                      @RequestBody ExceptionDTO exceptionDTO) {
        return exceptionService.handleException(id, exceptionDTO);
    }

    @PutMapping("/{id}/close")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "关闭异常记录（仅管理员）")
    public ApiResponse<ExceptionDTO> closeException(@PathVariable Long id) {
        return exceptionService.closeException(id);
    }
}
