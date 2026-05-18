package com.express.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "异常记录DTO")
public class ExceptionDTO {

    private Long id;

    @Schema(description = "关联快递ID")
    private Long packageId;

    @NotNull(message = "异常类型不能为空")
    @Schema(description = "异常类型: DUPLICATE_PICKUP/WRONG_PICKUP/LOST/DAMAGED/MISMATCH_INFO/OTHER", required = true)
    private String exceptionType;

    @NotBlank(message = "异常描述不能为空")
    @Schema(description = "异常描述", required = true)
    private String description;

    @Schema(description = "处理人ID")
    private Long handlerId;

    @Schema(description = "处理人姓名")
    private String handlerName;

    @Schema(description = "处理结果")
    private String handlingResult;

    @Schema(description = "处理时间")
    private LocalDateTime handlingTime;

    @Schema(description = "处理状态: PENDING/RESOLVED/CLOSED")
    private String status;

    @Schema(description = "赔偿金额")
    private BigDecimal compensationAmount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
