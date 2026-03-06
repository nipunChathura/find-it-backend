package lk.icbt.findit.repository;

import lk.icbt.findit.entity.OutletSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OutletScheduleRepository extends JpaRepository<OutletSchedule, Long> {

    List<OutletSchedule> findByOutlet_OutletIdAndIsActiveOrderByPriorityDesc(Long outletId, String isActive);

    /** TEMPORARY: active schedules whose date range contains the given date */
    @Query("SELECT s FROM OutletSchedule s WHERE s.outlet.outletId = :outletId AND s.isActive = 'Y' AND (s.status IS NULL OR s.status = 'ACTIVE') " +
           "AND s.scheduleType = 'TEMPORARY' AND s.startDate <= :date AND s.endDate >= :date")
    List<OutletSchedule> findTemporaryByOutletAndDate(
            @Param("outletId") Long outletId,
            @Param("date") LocalDate date);

    /** EMERGENCY or DAILY: exact special_date match */
    @Query("SELECT s FROM OutletSchedule s WHERE s.outlet.outletId = :outletId AND s.isActive = 'Y' AND (s.status IS NULL OR s.status = 'ACTIVE') " +
           "AND s.scheduleType IN ('EMERGENCY', 'DAILY') AND s.specialDate = :date")
    List<OutletSchedule> findSpecialDateByOutletAndDate(
            @Param("outletId") Long outletId,
            @Param("date") LocalDate date);

    /** NORMAL: day of week (e.g. MONDAY) */
    @Query("SELECT s FROM OutletSchedule s WHERE s.outlet.outletId = :outletId AND s.isActive = 'Y' AND (s.status IS NULL OR s.status = 'ACTIVE') " +
           "AND s.scheduleType = 'NORMAL' AND s.dayOfWeek = :dayOfWeek")
    List<OutletSchedule> findNormalByOutletAndDayOfWeek(
            @Param("outletId") Long outletId,
            @Param("dayOfWeek") String dayOfWeek);

    List<OutletSchedule> findByOutlet_OutletIdOrderByPriorityDesc(Long outletId);

    /** Active schedules only (excludes status = DELETED; treats NULL as active for backward compatibility). */
    @Query("SELECT s FROM OutletSchedule s WHERE s.outlet.outletId = :outletId AND (s.status IS NULL OR s.status <> 'DELETED') ORDER BY s.priority DESC")
    List<OutletSchedule> findActiveByOutletOrderByPriorityDesc(@Param("outletId") Long outletId);

    boolean existsByIdAndOutlet_OutletId(Long scheduleId, Long outletId);
}
