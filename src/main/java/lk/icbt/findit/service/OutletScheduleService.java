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

    
    OutletStatusResponse getOutletStatus(Long outletId, LocalDateTime checkTime);

    
    List<OutletScheduleItemResponse> getTodaysSchedule(Long outletId, LocalDate date);

    
    OutletSchedulesGroupedResponse getSchedulesGroupedByType(Long outletId, LocalDate date, String dayOfWeek);

    OutletScheduleItemResponse createSchedule(Long outletId, OutletScheduleRequest request);

    OutletScheduleItemResponse updateSchedule(Long outletId, Long scheduleId, OutletScheduleRequest request);

    void deleteSchedule(Long outletId, Long scheduleId);
}
