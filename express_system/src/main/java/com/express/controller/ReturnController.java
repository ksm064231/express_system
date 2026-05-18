package com.express.controller;

import com.express.dto.ReturnDTO;
import com.express.dto.ApiResponse;
import com.express.entity.User.UserRole;
import com.express.security.RequireRole;
import com.express.service.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
@Tag(name = "退件管理", description = "退件处理、退件记录查询接口")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "退件处理（快递员/管理员）")
    public ApiResponse<ReturnDTO> processReturn(@Valid @RequestBody ReturnDTO returnDTO) {
        return returnService.processReturn(returnDTO);
    }

    @GetMapping
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取所有退件记录（快递员/管理员）")
    public ApiResponse<List<ReturnDTO>> getAllReturnRecords() {
        return returnService.getAllReturnRecords();
    }

    @GetMapping("/package/{packageId}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取快递的退件记录（快递员/管理员）")
    public ApiResponse<List<ReturnDTO>> getByPackageId(@PathVariable Long packageId) {
        return returnService.getReturnRecordsByPackageId(packageId);
    }
}
