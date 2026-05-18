package com.express.controller;

import com.express.dto.PickupDTO;
import com.express.dto.ApiResponse;
import com.express.entity.User.UserRole;
import com.express.security.RequireRole;
import com.express.service.PickupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pickups")
@Tag(name = "取件管理", description = "取件操作、取件记录查询接口")
public class PickupController {

    private final PickupService pickupService;

    public PickupController(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    @PostMapping
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "取件操作（快递员/管理员）")
    public ApiResponse<PickupDTO> pickupPackage(@Valid @RequestBody PickupDTO pickupDTO) {
        return pickupService.pickupPackage(pickupDTO);
    }

    @PostMapping("/by-code")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "根据取件码取件（快递员/管理员）")
    public ApiResponse<PickupDTO> pickupByCode(
            @RequestParam String trackingNumber,
            @RequestParam String pickupCode,
            @Valid @RequestBody PickupDTO pickupDTO) {
        return pickupService.pickupByCode(trackingNumber, pickupCode, pickupDTO);
    }

    @GetMapping
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取所有取件记录（快递员/管理员）")
    public ApiResponse<List<PickupDTO>> getAllPickupRecords() {
        return pickupService.getAllPickupRecords();
    }

    @GetMapping("/package/{packageId}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN})
    @Operation(summary = "获取快递的取件记录（快递员/管理员）")
    public ApiResponse<List<PickupDTO>> getByPackageId(@PathVariable Long packageId) {
        return pickupService.getPickupRecordsByPackageId(packageId);
    }

    @GetMapping("/check/{packageId}")
    @RequireRole({UserRole.COURIER, UserRole.ADMIN, UserRole.OWNER})
    @Operation(summary = "检查快递是否已被取件")
    public ApiResponse<Boolean> checkIfPickedUp(@PathVariable Long packageId) {
        return pickupService.checkIfPickedUp(packageId);
    }
}
