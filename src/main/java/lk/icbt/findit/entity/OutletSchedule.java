package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


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

    
    @Column(name = "day_of_week", length = 15)
    private String dayOfWeek;

    
    @Column(name = "special_date")
    private LocalDate specialDate;

    
    @Column(name = "start_date")
    private LocalDate startDate;

    
    @Column(name = "end_date")
    private LocalDate endDate;

    
    @Column(name = "open_time", length = 5)
    private String openTime;

    
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

    
    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    
    public LocalTime getOpenTimeAsLocalTime() {
        return parseTime(openTime);
    }

    
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
