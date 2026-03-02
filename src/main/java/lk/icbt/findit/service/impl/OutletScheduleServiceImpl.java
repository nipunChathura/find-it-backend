package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.OutletSchedule;
import lk.icbt.findit.entity.ScheduleType;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.HolidayRepository;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.OutletScheduleRepository;
import lk.icbt.findit.request.OutletScheduleRequest;
import lk.icbt.findit.response.OutletScheduleItemResponse;
import lk.icbt.findit.response.OutletScheduleRowResponse;
import lk.icbt.findit.response.OutletSchedulesGroupedResponse;
import lk.icbt.findit.response.OutletStatusResponse;
import lk.icbt.findit.service.OutletScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class OutletScheduleServiceImpl implements OutletScheduleService {

    private final OutletRepository outletRepository;
    private final OutletScheduleRepository scheduleRepository;
    private final HolidayRepository holidayRepository;

    @Override
    public OutletStatusResponse getOutletStatus(Long outletId, LocalDateTime checkTime) {
        ensureOutletExists(outletId);
        LocalDate date = checkTime.toLocalDate();
        LocalTime time = checkTime.toLocalTime();

        OutletSchedule match = findApplicableSchedule(outletId, date);
        if (match == null) {
            String reason = getHolidayReason(date);
            if (reason == null) reason = "No schedule defined";
            return OutletStatusResponse.builder()
                    .outletId(outletId)
                    .status(OutletStatusResponse.STATUS_CLOSED)
                    .isClosed("Y")
                    .reason(reason)
                    .todaySchedule(getTodaysSchedule(outletId, date))
                    .build();
        }

        if (match.isClosed()) {
            return OutletStatusResponse.builder()
                    .outletId(outletId)
                    .status(OutletStatusResponse.STATUS_CLOSED)
                    .isClosed("Y")
                    .openTime(match.getOpenTime())
                    .closeTime(match.getCloseTime())
                    .reason(match.getReason())
                    .todaySchedule(getTodaysSchedule(outletId, date))
                    .build();
        }

        LocalTime open = match.getOpenTimeAsLocalTime();
        LocalTime close = match.getCloseTimeAsLocalTime();
        if (open == null || close == null) {
            return OutletStatusResponse.builder()
                    .outletId(outletId)
                    .status(OutletStatusResponse.STATUS_CLOSED)
                    .isClosed("Y")
                    .reason("Opening hours not set")
                    .openTime(match.getOpenTime())
                    .closeTime(match.getCloseTime())
                    .todaySchedule(getTodaysSchedule(outletId, date))
                    .build();
        }

        boolean openNow = !time.isBefore(open) && time.isBefore(close);
        return OutletStatusResponse.builder()
                .outletId(outletId)
                .status(openNow ? OutletStatusResponse.STATUS_OPEN : OutletStatusResponse.STATUS_CLOSED)
                .isClosed(openNow ? "N" : "Y")
                .openTime(match.getOpenTime())
                .closeTime(match.getCloseTime())
                .reason(openNow ? null : "Outside opening hours")
                .todaySchedule(getTodaysSchedule(outletId, date))
                .build();
    }

    @Override
    public List<OutletScheduleItemResponse> getTodaysSchedule(Long outletId, LocalDate date) {
        ensureOutletExists(outletId);
        String dayOfWeek = date.getDayOfWeek().name();

        List<OutletSchedule> list = new ArrayList<>();
        List<OutletSchedule> temp = scheduleRepository.findTemporaryByOutletAndDate(outletId, date);
        list.addAll(temp);
        List<OutletSchedule> special = scheduleRepository.findSpecialDateByOutletAndDate(outletId, date);
        list.addAll(special);
        List<OutletSchedule> normal = scheduleRepository.findNormalByOutletAndDayOfWeek(outletId, dayOfWeek);
        list.addAll(normal);

        return list.stream()
                .sorted(Comparator.comparing(OutletSchedule::getPriority, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OutletScheduleItemResponse> getSchedulesForOutlet(Long outletId) {
        ensureOutletExists(outletId);
        return scheduleRepository.findActiveByOutletOrderByPriorityDesc(outletId).stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OutletSchedulesGroupedResponse getSchedulesGroupedByType(Long outletId, LocalDate date, String dayOfWeek) {
        ensureOutletExists(outletId);
        List<OutletSchedule> all = scheduleRepository.findActiveByOutletOrderByPriorityDesc(outletId);
        String dayForNormal = dayOfWeek != null && !dayOfWeek.isBlank()
                ? dayOfWeek.trim().toUpperCase()
                : (date != null ? date.getDayOfWeek().name() : null);
        List<OutletScheduleRowResponse> normal = all.stream()
                .filter(s -> s.getScheduleType() == ScheduleType.NORMAL)
                .filter(s -> dayForNormal == null || dayForNormal.equals(s.getDayOfWeek()))
                .map(this::toRowResponse)
                .collect(Collectors.toList());
        List<OutletScheduleRowResponse> emergency = all.stream()
                .filter(s -> s.getScheduleType() == ScheduleType.EMERGENCY)
                .filter(s -> date == null || (s.getSpecialDate() != null && s.getSpecialDate().equals(date)))
                .map(this::toRowResponse)
                .collect(Collectors.toList());
        List<OutletScheduleRowResponse> temporary = all.stream()
                .filter(s -> s.getScheduleType() == ScheduleType.TEMPORARY)
                .filter(s -> date == null || (s.getStartDate() != null && s.getEndDate() != null && !date.isBefore(s.getStartDate()) && !date.isAfter(s.getEndDate())))
                .map(this::toRowResponse)
                .collect(Collectors.toList());
        List<OutletScheduleRowResponse> daily = all.stream()
                .filter(s -> s.getScheduleType() == ScheduleType.DAILY)
                .filter(s -> date == null || (s.getSpecialDate() != null && s.getSpecialDate().equals(date)))
                .map(this::toRowResponse)
                .collect(Collectors.toList());
        return OutletSchedulesGroupedResponse.builder()
                .normal(normal.isEmpty() ? emptyList() : normal)
                .emergency(emergency.isEmpty() ? emptyList() : emergency)
                .temporary(temporary.isEmpty() ? emptyList() : temporary)
                .daily(daily.isEmpty() ? emptyList() : daily)
                .build();
    }

    private OutletScheduleRowResponse toRowResponse(OutletSchedule s) {
        return OutletScheduleRowResponse.builder()
                .id(s.getId())
                .dayOfWeek(s.getDayOfWeek())
                .specialDate(s.getSpecialDate() != null ? s.getSpecialDate().toString() : null)
                .startDate(s.getStartDate() != null ? s.getStartDate().toString() : null)
                .endDate(s.getEndDate() != null ? s.getEndDate().toString() : null)
                .openTime(s.getOpenTime())
                .closeTime(s.getCloseTime())
                .isClosed("Y".equalsIgnoreCase(s.getIsClosed()) ? "Y" : "N")
                .reason(s.getReason())
                .priority(s.getPriority())
                .build();
    }

    @Override
    @Transactional
    public OutletScheduleItemResponse createSchedule(Long outletId, OutletScheduleRequest request) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.FAILED_CODE, "Outlet not found"));
        OutletSchedule schedule = mapRequestToEntity(request, outlet);
        schedule.setStatus(Constants.SCHEDULE_ACTIVE_STATUS);
        schedule.setCreatedDatetime(java.util.Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        schedule.setModifiedDatetime(schedule.getCreatedDatetime());
        schedule.setVersion(1);
        OutletSchedule saved = scheduleRepository.save(schedule);
        return toItemResponse(saved);
    }

    @Override
    @Transactional
    public OutletScheduleItemResponse updateSchedule(Long outletId, Long scheduleId, OutletScheduleRequest request) {
        OutletSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.FAILED_CODE, "Schedule not found"));
        if (!schedule.getOutlet().getOutletId().equals(outletId)) {
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Schedule does not belong to this outlet");
        }
        updateEntityFromRequest(request, schedule);
        schedule.setModifiedDatetime(java.util.Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        OutletSchedule saved = scheduleRepository.save(schedule);
        return toItemResponse(saved);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long outletId, Long scheduleId) {
        OutletSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.FAILED_CODE, "Schedule not found"));
        if (!schedule.getOutlet().getOutletId().equals(outletId)) {
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Schedule does not belong to this outlet");
        }
        schedule.setStatus(Constants.SCHEDULE_DELETED_STATUS);
        schedule.setModifiedDatetime(java.util.Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        scheduleRepository.save(schedule);
    }

    /** Priority: TEMPORARY → EMERGENCY/DAILY → NORMAL. Holiday overrides NORMAL (outlet closed). */
    private OutletSchedule findApplicableSchedule(Long outletId, LocalDate date) {
        List<OutletSchedule> temp = scheduleRepository.findTemporaryByOutletAndDate(outletId, date);
        if (!temp.isEmpty()) {
            return temp.stream().max(Comparator.comparing(OutletSchedule::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))).orElse(temp.get(0));
        }
        List<OutletSchedule> special = scheduleRepository.findSpecialDateByOutletAndDate(outletId, date);
        if (!special.isEmpty()) {
            return special.stream().max(Comparator.comparing(OutletSchedule::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))).orElse(special.get(0));
        }
        // Optional: if date is in holiday_master, treat as closed (return null so caller shows CLOSED with holiday reason)
        if (holidayRepository.findByHolidayDate(date).isPresent()) {
            return null;  // Caller will use getHolidayReason(date) for reason in status response
        }
        String dayOfWeek = date.getDayOfWeek().name();
        List<OutletSchedule> normal = scheduleRepository.findNormalByOutletAndDayOfWeek(outletId, dayOfWeek);
        if (!normal.isEmpty()) {
            return normal.stream().max(Comparator.comparing(OutletSchedule::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))).orElse(normal.get(0));
        }
        return null;
    }

    private String getHolidayReason(LocalDate date) {
        return holidayRepository.findByHolidayDate(date)
                .map(h -> h.getName() != null ? h.getName() : "Holiday")
                .orElse(null);
    }

    private void ensureOutletExists(Long outletId) {
        if (!outletRepository.existsById(outletId)) {
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Outlet not found");
        }
    }

    private OutletScheduleItemResponse toItemResponse(OutletSchedule s) {
        return OutletScheduleItemResponse.builder()
                .id(s.getId())
                .scheduleType(s.getScheduleType())
                .dayOfWeek(s.getDayOfWeek())
                .specialDate(s.getSpecialDate() != null ? s.getSpecialDate().toString() : null)
                .startDate(s.getStartDate() != null ? s.getStartDate().toString() : null)
                .endDate(s.getEndDate() != null ? s.getEndDate().toString() : null)
                .openTime(s.getOpenTime())
                .closeTime(s.getCloseTime())
                .closed(s.isClosed())
                .reason(s.getReason())
                .priority(s.getPriority())
                .active(s.isActive())
                .status(s.getStatus())
                .build();
    }

    private OutletSchedule mapRequestToEntity(OutletScheduleRequest req, Outlet outlet) {
        OutletSchedule s = new OutletSchedule();
        s.setOutlet(outlet);
        s.setScheduleType(req.getScheduleType());
        s.setDayOfWeek(req.getDayOfWeek());
        s.setSpecialDate(req.getSpecialDate());
        s.setStartDate(req.getStartDate());
        s.setEndDate(req.getEndDate());
        s.setOpenTime(req.getOpenTime());
        s.setCloseTime(req.getCloseTime());
        s.setIsClosed(req.getClosed() != null && req.getClosed() ? "Y" : "N");
        s.setReason(req.getReason());
        s.setPriority(req.getPriority() != null ? req.getPriority() : 1);
        s.setIsActive(req.getActive() != null && req.getActive() ? "Y" : "Y");
        return s;
    }

    private void updateEntityFromRequest(OutletScheduleRequest req, OutletSchedule s) {
        if (req.getScheduleType() != null) s.setScheduleType(req.getScheduleType());
        s.setDayOfWeek(req.getDayOfWeek());
        s.setSpecialDate(req.getSpecialDate());
        s.setStartDate(req.getStartDate());
        s.setEndDate(req.getEndDate());
        s.setOpenTime(req.getOpenTime());
        s.setCloseTime(req.getCloseTime());
        if (req.getClosed() != null) s.setIsClosed(req.getClosed() ? "Y" : "N");
        s.setReason(req.getReason());
        if (req.getPriority() != null) s.setPriority(req.getPriority());
        if (req.getActive() != null) s.setIsActive(req.getActive() ? "Y" : "N");
    }
}
