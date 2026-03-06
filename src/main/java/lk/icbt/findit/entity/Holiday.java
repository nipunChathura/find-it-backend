package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Optional: Master list of holidays. When a date matches, outlet can be treated as closed
 * (used in opening logic when no TEMPORARY/EMERGENCY/DAILY override exists).
 */
@Entity
@Table(name = "holiday_master", indexes = @Index(name = "idx_holiday_date", columnList = "holiday_date"))
@Getter
@Setter
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", length = 500)
    private String description;
}
