package lk.icbt.findit.service;

import lk.icbt.findit.entity.OutletSchedule;
import lk.icbt.findit.request.OutletScheduleRequest;
import lk.icbt.findit.response.OutletStatusResponse;
import lk.icbt.findit.response.OutletScheduleItemResponse;
import lk.icbt.findit.response.OutletSchedulesGroupedResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OutletScheduleService {

    /**
     * Check if the outlet is open at the given date/time.
     * Priority: TEMPORARY (date range) → EMERGENCY/DAILY (exact date) → NORMAL (day of week). No match = CLOSED.
     */
    OutletStatusResponse getOutletStatus(Long outletId, LocalDateTime checkTime);

    /**
     * Get today's schedule for the outlet (all applicable schedule rows for the date).
     */
    List<OutletScheduleItemResponse> getTodaysSchedule(Long outletId, LocalDate date);

    /**
     * List all schedules for an outlet.
     */
    List<OutletScheduleItemResponse> getSchedulesForOutlet(Long outletId);

    /**
     * List all schedules for an outlet grouped by type (NORMAL, EMERGENCY, TEMPORARY, DAILY).
     * @param date optional – filter by date: NORMAL by day-of-week, EMERGENCY/DAILY by special_date, TEMPORARY by range
     * @param dayOfWeek optional – filter NORMAL schedules by day name (e.g. MONDAY)
     */
    OutletSchedulesGroupedResponse getSchedulesGroupedByType(Long outletId, LocalDate date, String dayOfWeek);

    OutletScheduleItemResponse createSchedule(Long outletId, OutletScheduleRequest request);

    OutletScheduleItemResponse updateSchedule(Long outletId, Long scheduleId, OutletScheduleRequest request);

    void deleteSchedule(Long outletId, Long scheduleId);
}
