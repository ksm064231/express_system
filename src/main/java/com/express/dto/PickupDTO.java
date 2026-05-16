package com.express.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "取件DTO")
public class PickupDTO {

    private Long id;

    @NotNull(message = "快递ID不能为空")
    @Schema(description = "快递ID", required = true)
    private Long packageId;

    @Schema(description = "取件时间")
    private LocalDateTime pickupTime;

    @NotBlank(message = "取件人姓名不能为空")
    @Schema(description = "取件人姓名", required = true)
    private String pickupPersonName;

    @Schema(description = "取件人电话")
    private String pickupPersonPhone;

    @Schema(description = "取件人签名(base64)")
    private String signature;

    @Schema(description = "验证方式: CODE/PHONE/ID_CARD/SIGNATURE")
    private String verificationMethod;

    @Schema(description = "核验人ID")
    private Long verifiedBy;

    @Schema(description = "核验人姓名")
    private String verifiedByName;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
