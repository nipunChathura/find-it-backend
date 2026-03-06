package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.OutletAddDTO;
import lk.icbt.findit.request.OutletAddRequest;
import lk.icbt.findit.request.OutletScheduleRequest;
import lk.icbt.findit.request.OutletUpdateRequest;
import lk.icbt.findit.response.MessageResponse;
import lk.icbt.findit.response.DiscountListItemResponse;
import lk.icbt.findit.response.OutletListResponse;
import lk.icbt.findit.response.OutletResponse;
import lk.icbt.findit.response.OutletScheduleItemResponse;
import lk.icbt.findit.response.OutletSchedulesGroupedResponse;
import lk.icbt.findit.response.OutletStatusResponse;
import lk.icbt.findit.response.FeedbackResponse;
import lk.icbt.findit.response.OutletFeedbackCountResponse;
import lk.icbt.findit.service.DiscountService;
import lk.icbt.findit.service.FeedbackService;
import lk.icbt.findit.service.OutletScheduleService;
import lk.icbt.findit.service.OutletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Outlet APIs. Base URL: /api/outlets.
 * List outlets (name/status filter, currentStatus OPEN/CLOSED), get/update outlet, opening hours (schedules, status).
 */
@RestController
@RequestMapping("/api/outlets")
@RequiredArgsConstructor
public class OutletController {

    private final OutletService outletService;
    private final OutletScheduleService outletScheduleService;
    private final DiscountService discountService;
    private final FeedbackService feedbackService;

    /** 1. List outlets. Query: name (optional), status (optional). Response includes currentStatus (OPEN/CLOSED). */
    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<OutletListResponse>> listOutlets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(outletService.listOutlets(name, status));
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletResponse> addOutlet(@Valid @RequestBody OutletAddRequest request) {
        String username = getAuthenticatedUsername();
        OutletAddDTO dto = new OutletAddDTO();
        BeanUtils.copyProperties(request, dto);
        OutletAddDTO result = outletService.addOutlet(dto, username);
        OutletResponse response = new OutletResponse();
        BeanUtils.copyProperties(result, response);
        response.setOutletStatus(result.getOutletStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PutMapping(value = "/{outletId}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletResponse> approveOutlet(@PathVariable Long outletId) {
        String username = getAuthenticatedUsername();
        OutletAddDTO result = outletService.approveOutlet(outletId, username);
        OutletResponse response = new OutletResponse();
        BeanUtils.copyProperties(result, response);
        response.setOutletStatus(result.getOutletStatus());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('MERCHANT', 'SUBMERCHANT')")
    @PutMapping(value = "/{outletId}/submit-payment", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletResponse> submitPayment(@PathVariable Long outletId) {
        String username = getAuthenticatedUsername();
        OutletAddDTO result = outletService.submitPayment(outletId, username);
        OutletResponse response = new OutletResponse();
        BeanUtils.copyProperties(result, response);
        response.setOutletStatus(result.getOutletStatus());
        response.setSubscriptionValidUntil(result.getSubscriptionValidUntil());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PutMapping(value = "/{outletId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletResponse> updateOutlet(@PathVariable Long outletId, @Valid @RequestBody OutletUpdateRequest request) {
        String username = getAuthenticatedUsername();
        OutletAddDTO dto = new OutletAddDTO();
        BeanUtils.copyProperties(request, dto);
        OutletAddDTO result = outletService.updateOutlet(outletId, dto, username);
        OutletResponse response = new OutletResponse();
        BeanUtils.copyProperties(result, response);
        response.setOutletStatus(result.getOutletStatus());
        response.setSubscriptionValidUntil(result.getSubscriptionValidUntil());
        return ResponseEntity.ok(response);
    }

    /** Get current available discounts for the given outlet (ACTIVE and today within startDate–endDate). */
    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'CUSTOMER')")
    @GetMapping(value = "/{outletId}/discounts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DiscountListItemResponse>> getOutletDiscounts(@PathVariable Long outletId) {
        List<DiscountListItemResponse> list = discountService.listCurrentByOutletId(outletId);
        return ResponseEntity.ok(list);
    }

    /** Get list of feedbacks for the given outlet (newest first). */
    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'CUSTOMER')")
    @GetMapping(value = "/{outletId}/feedbacks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<FeedbackResponse>> getOutletFeedbacks(@PathVariable Long outletId) {
        return ResponseEntity.ok(feedbackService.listByOutletId(outletId));
    }

    /** Get feedback count for the given outlet. */
    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'CUSTOMER')")
    @GetMapping(value = "/{outletId}/feedbacks/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletFeedbackCountResponse> getOutletFeedbackCount(@PathVariable Long outletId) {
        return ResponseEntity.ok(feedbackService.getOutletFeedbackCount(outletId));
    }

    // ---------- Outlet opening hours / schedule ----------

    /** 3. Check outlet open status. Query: datetime (optional, ISO-8601 e.g. 2026-03-05T11:00). */
    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "/{outletId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletStatusResponse> getOutletStatus(
            @PathVariable Long outletId,
            @RequestParam(value = "datetime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime) {
        LocalDateTime check = datetime != null ? datetime : LocalDateTime.now();
        OutletStatusResponse response = outletScheduleService.getOutletStatus(outletId, check);
        return ResponseEntity.ok(response);
    }

    /** 2. Get outlet schedules grouped by type. Optional: date (YYYY-MM-DD), dayOfWeek (e.g. MONDAY). */
    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "/{outletId}/schedules", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletSchedulesGroupedResponse> getOutletSchedules(
            @PathVariable Long outletId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String dayOfWeek) {
        return ResponseEntity.ok(outletScheduleService.getSchedulesGroupedByType(outletId, date, dayOfWeek));
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PostMapping(value = "/{outletId}/schedules", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletScheduleItemResponse> createSchedule(
            @PathVariable Long outletId,
            @Valid @RequestBody OutletScheduleRequest request) {
        OutletScheduleItemResponse created = outletScheduleService.createSchedule(outletId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PutMapping(value = "/{outletId}/schedules/{scheduleId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletScheduleItemResponse> updateSchedule(
            @PathVariable Long outletId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody OutletScheduleRequest request) {
        return ResponseEntity.ok(outletScheduleService.updateSchedule(outletId, scheduleId, request));
    }

    /** 6. Delete schedule. Returns success message. */
    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @DeleteMapping(value = "/{outletId}/schedules/{scheduleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MessageResponse> deleteSchedule(
            @PathVariable Long outletId,
            @PathVariable Long scheduleId) {
        outletScheduleService.deleteSchedule(outletId, scheduleId);
        return ResponseEntity.ok(MessageResponse.of("Schedule deleted successfully"));
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
