package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Opening hours / schedule for an outlet.
 * Supports NORMAL (weekly), EMERGENCY/DAILY (single date), TEMPORARY (date range).
 */
@Entity
@Table(name = "outlet_schedule", indexes = {
    @Index(name = "idx_outlet_schedule_outlet_id", columnList = "outlet_id"),
    @Index(name = "idx_outlet_schedule_special_date", columnList = "special_date"),
    @Index(name = "idx_outlet_schedule_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_outlet_schedule_active", columnList = "outlet_id, is_active")
})
@Getter
@Setter
public class OutletSchedule extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outlet_id", nullable = false)
    private Outlet outlet;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 20)
    private ScheduleType scheduleType;

    /** For NORMAL: MONDAY, TUESDAY, ... SUNDAY */
    @Column(name = "day_of_week", length = 15)
    private String dayOfWeek;

    /** For EMERGENCY / DAILY: exact date */
    @Column(name = "special_date")
    private LocalDate specialDate;

    /** For TEMPORARY: range start */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** For TEMPORARY: range end */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** Open time HH:mm */
    @Column(name = "open_time", length = 5)
    private String openTime;

    /** Close time HH:mm */
    @Column(name = "close_time", length = 5)
    private String closeTime;

    @Column(name = "is_closed", length = 1)
    private String isClosed = "N";

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "priority")
    private Integer priority = 1;

    @Column(name = "is_active", length = 1)
    private String isActive = "Y";

    /** ACTIVE or DELETED; soft delete uses DELETED instead of removing the row */
    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    /** Parses open_time to LocalTime; returns null if not set or invalid. */
    public LocalTime getOpenTimeAsLocalTime() {
        return parseTime(openTime);
    }

    /** Parses close_time to LocalTime; returns null if not set or invalid. */
    public LocalTime getCloseTimeAsLocalTime() {
        return parseTime(closeTime);
    }

    private static LocalTime parseTime(String time) {
        if (time == null || time.isBlank()) return null;
        try {
            String[] parts = time.trim().split(":");
            if (parts.length >= 2) {
                int h = Integer.parseInt(parts[0].trim());
                int m = Integer.parseInt(parts[1].trim());
                return LocalTime.of(h, m);
            }
        } catch (Exception ignored) { }
        return null;
    }

    public boolean isClosed() {
        return "Y".equalsIgnoreCase(isClosed);
    }

    public boolean isActive() {
        return "Y".equalsIgnoreCase(isActive);
    }
}
