package com.express.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pickup_records")
public class PickupRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "pickup_time", nullable = false)
    private LocalDateTime pickupTime;

    @Column(name = "pickup_person_name", nullable = false, length = 50)
    private String pickupPersonName;

    @Column(name = "pickup_person_phone", length = 20)
    private String pickupPersonPhone;

    @Column(length = 255)
    private String signature;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_method", length = 20)
    @Builder.Default
    private VerificationMethod verificationMethod = VerificationMethod.CODE;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (pickupTime == null) {
            pickupTime = LocalDateTime.now();
        }
    }

    public enum VerificationMethod {
        CODE,       // 取件码验证
        PHONE,      // 手机号验证
        ID_CARD,    // 身份证验证
        SIGNATURE   // 签名验证
    }
}
