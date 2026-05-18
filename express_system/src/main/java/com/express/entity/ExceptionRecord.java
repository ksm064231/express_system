package com.express.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "exception_records")
public class ExceptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id")
    private Long packageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "exception_type", nullable = false, length = 30)
    private ExceptionType exceptionType;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "handler_id")
    private Long handlerId;

    @Column(name = "handling_result", length = 1000)
    private String handlingResult;

    @Column(name = "handling_time")
    private LocalDateTime handlingTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ExceptionStatus status = ExceptionStatus.PENDING;

    @Column(name = "compensation_amount", precision = 10, scale = 2)
    private BigDecimal compensationAmount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ExceptionType {
        DUPLICATE_PICKUP,  // 重复取件
        WRONG_PICKUP,      // 取错件
        LOST,              // 丢件
        DAMAGED,           // 损坏
        MISMATCH_INFO,     // 信息不符
        OTHER              // 其他
    }

    public enum ExceptionStatus {
        PENDING,  RESOLVED, CLOSED
    }
}
