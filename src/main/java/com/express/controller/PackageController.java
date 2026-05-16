package com.express.controller;

import com.express.dto.PackageDTO;
import com.express.dto.ApiResponse;
import com.express.entity.User.UserRole;
import com.express.security.RequireRole;
import com.express.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@Tag(name = "快递管理", description = "快递入库、查询、管理接口")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @PostMapping
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "快递入库登记（快递员/管理员）")
    public ApiResponse<PackageDTO> storePackage(@Valid @RequestBody PackageDTO packageDTO) {
        return packageService.storePackage(packageDTO);
    }

    @GetMapping
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取所有快递列表（快递员/管理员）")
    public ApiResponse<List<PackageDTO>> getAllPackages() {
        return packageService.getAllPackages();
    }

    @GetMapping("/{id}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN, UserRole.OWNER})
    @Operation(summary = "根据ID查询快递")
    public ApiResponse<PackageDTO> getById(@PathVariable Long id) {
        return packageService.getById(id);
    }

    @GetMapping("/tracking/{trackingNumber}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN, UserRole.OWNER})
    @Operation(summary = "根据运单号查询快递")
    public ApiResponse<PackageDTO> getByTrackingNumber(@PathVariable String trackingNumber) {
        return packageService.getByTrackingNumber(trackingNumber);
    }

    @GetMapping("/phone/{phone}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "根据收件人电话查询（快递员/管理员）")
    public ApiResponse<List<PackageDTO>> getByPhone(@PathVariable String phone) {
        return packageService.getByRecipientPhone(phone);
    }

    @GetMapping("/name/{name}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "根据收件人姓名查询（快递员/管理员）")
    public ApiResponse<List<PackageDTO>> getByName(@PathVariable String name) {
        return packageService.getByRecipientName(name);
    }

    @GetMapping("/room/{roomNumber}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "根据房号查询（快递员/管理员）")
    public ApiResponse<List<PackageDTO>> getByRoomNumber(@PathVariable String roomNumber) {
        return packageService.getByRoomNumber(roomNumber);
    }

    @GetMapping("/status/{status}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "根据状态查询（快递员/管理员）")
    public ApiResponse<List<PackageDTO>> getByStatus(@PathVariable String status) {
        return packageService.getByStatus(status);
    }

    @GetMapping("/search")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "搜索快递（快递员/管理员）")
    public ApiResponse<List<PackageDTO>> search(@RequestParam String keyword) {
        return packageService.searchPackages(keyword);
    }

    @GetMapping("/overdue")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "查询逾期未取快递（快递员/管理员）")
    public ApiResponse<List<PackageDTO>> getOverdue(
            @RequestParam(defaultValue = "3") int days) {
        return packageService.getOverduePackages(days);
    }

    @PutMapping("/{id}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "更新快递信息（快递员/管理员）")
    public ApiResponse<PackageDTO> updatePackage(@PathVariable Long id,
                                                  @RequestBody PackageDTO packageDTO) {
        return packageService.updatePackage(id, packageDTO);
    }

    @DeleteMapping("/{id}")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "删除快递（仅管理员）")
    public ApiResponse<Void> deletePackage(@PathVariable Long id) {
        return packageService.deletePackage(id);
    }
}
