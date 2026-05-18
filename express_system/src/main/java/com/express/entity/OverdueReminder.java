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
@Table(name = "overdue_reminders")
public class OverdueReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "overdue_days", nullable = false)
    private Integer overdueDays;

    @Column(name = "reminder_time", nullable = false)
    private LocalDateTime reminderTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_method", length = 20)
    @Builder.Default
    private ReminderMethod reminderMethod = ReminderMethod.MANUAL;

    @Column(name = "notified_person", length = 50)
    private String notifiedPerson;

    @Column(name = "notified_phone", length = 20)
    private String notifiedPhone;

    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;

    @Column(name = "resolved_time")
    private LocalDateTime resolvedTime;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (reminderTime == null) {
            reminderTime = LocalDateTime.now();
        }
    }

    public enum ReminderMethod {
        SMS, PHONE, APP, MANUAL
    }
}
