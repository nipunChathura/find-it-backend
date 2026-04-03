package lk.icbt.findit.controller;

import lk.icbt.findit.response.DashboardSummaryResponse;
import lk.icbt.findit.response.MerchantSummaryResponse;
import lk.icbt.findit.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import lk.icbt.findit.response.DashboardActivityResponse;
import lk.icbt.findit.response.DashboardMonthlyIncomeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        DashboardSummaryResponse result = dashboardService.getSummary();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @GetMapping(value = "/merchant-summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantSummaryResponse> getMerchantSummary() {
        MerchantSummaryResponse result = dashboardService.getMerchantSummary();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @GetMapping(value = "/activity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<DashboardActivityResponse> getActivity(
            @RequestParam(value = "months", required = false, defaultValue = "6") int months) {
        DashboardActivityResponse result = dashboardService.getActivity(months);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @GetMapping(value = "/monthly-income", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<DashboardMonthlyIncomeResponse> getMonthlyIncome(
            @RequestParam(value = "months", required = false, defaultValue = "12") int months) {
        DashboardMonthlyIncomeResponse result = dashboardService.getMonthlyIncome(months);
        return ResponseEntity.ok(result);
    }
}
