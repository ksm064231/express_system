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
@Table(name = "return_records")
public class ReturnRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "return_reason", nullable = false, length = 500)
    private String returnReason;

    @Column(name = "return_time", nullable = false)
    private LocalDateTime returnTime;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(name = "courier_name", length = 50)
    private String courierName;

    @Column(name = "return_notes", columnDefinition = "TEXT")
    private String returnNotes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (returnTime == null) {
            returnTime = LocalDateTime.now();
        }
    }
}
