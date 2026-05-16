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
@Schema(description = "退件DTO")
public class ReturnDTO {

    private Long id;

    @NotNull(message = "快递ID不能为空")
    @Schema(description = "快递ID", required = true)
    private Long packageId;

    @NotBlank(message = "退件原因不能为空")
    @Schema(description = "退件原因", required = true)
    private String returnReason;

    @Schema(description = "退回时间")
    private LocalDateTime returnTime;

    @Schema(description = "处理人ID")
    private Long processedBy;

    @Schema(description = "处理人姓名")
    private String processedByName;

    @Schema(description = "退回快递员")
    private String courierName;

    @Schema(description = "处理备注")
    private String returnNotes;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
