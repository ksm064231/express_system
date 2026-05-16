package com.express.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "统计报表DTO")
public class StatisticsDTO {

    @Schema(description = "统计日期")
    private LocalDate date;

    @Schema(description = "每日入库量")
    private Long dailyStoredCount;

    @Schema(description = "每日取件量")
    private Long dailyPickupCount;

    @Schema(description = "当前滞留件数")
    private Long currentStoredCount;

    @Schema(description = "逾期未取件数")
    private Long overdueCount;

    @Schema(description = "今日退件数")
    private Long dailyReturnCount;

    @Schema(description = "待处理异常数")
    private Long pendingExceptionCount;

    @Schema(description = "按快递公司统计入库量")
    private Map<String, Long> storedByCompany;

    @Schema(description = "按快递公司统计取件量")
    private Map<String, Long> pickupByCompany;
}
