package com.express.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "快递信息DTO")
public class PackageDTO {

    private Long id;

    @NotBlank(message = "运单号不能为空")
    @Schema(description = "运单号", required = true)
    private String trackingNumber;

    @NotBlank(message = "快递公司不能为空")
    @Schema(description = "快递公司", required = true)
    private String courierCompany;

    @NotBlank(message = "收件人姓名不能为空")
    @Schema(description = "收件人姓名", required = true)
    private String recipientName;

    @NotBlank(message = "收件人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "收件人电话", required = true)
    private String recipientPhone;

    @Schema(description = "房号")
    private String roomNumber;

    @Schema(description = "取件码")
    private String pickupCode;

    @Schema(description = "状态: STORED/PICKED_UP/RETURNED/LOST/MISMATCH")
    private String status;

    @Schema(description = "物品名称")
    private String itemName;

    @Schema(description = "物品类型")
    private String itemType;

    @Schema(description = "存放货架号")
    private String shelfNumber;

    @Schema(description = "到达时间")
    private LocalDateTime arrivalTime;

    @Schema(description = "入库登记人ID")
    private Long storedBy;

    @Schema(description = "入库登记人姓名")
    private String storedByName;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
