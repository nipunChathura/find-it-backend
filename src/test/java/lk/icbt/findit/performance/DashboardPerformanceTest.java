package lk.icbt.findit.performance;

import lk.icbt.findit.config.ApiRequestLoggingFilter;
import lk.icbt.findit.controller.DashboardController;
import lk.icbt.findit.response.DashboardSummaryResponse;
import lk.icbt.findit.response.MerchantSummaryResponse;
import lk.icbt.findit.service.DashboardService;
import lk.icbt.findit.security.CustomUserDetailsService;
import lk.icbt.findit.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("performance")
@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DashboardPerformanceTest {

    private static final int WARMUP = 2;
    private static final int MEASURED = 5;
    private static final long SLA_MS = 500;

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DashboardService dashboardService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private ApiRequestLoggingFilter apiRequestLoggingFilter;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Performance: GET /api/dashboard/summary (Get)")
    void getSummary_responseTime() throws Exception {
        when(dashboardService.getSummary()).thenReturn(new DashboardSummaryResponse());

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(get("/api/dashboard/summary")).andExpect(status().isOk()).andReturn());
        record("GET /api/dashboard/summary (Get)", times, 200);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Performance: GET /api/dashboard/merchant-summary (Get)")
    void getMerchantSummary_responseTime() throws Exception {
        when(dashboardService.getMerchantSummary()).thenReturn(new MerchantSummaryResponse());

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(get("/api/dashboard/merchant-summary")).andExpect(status().isOk()).andReturn());
        record("GET /api/dashboard/merchant-summary (Get)", times, 200);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    private long[] runTimed(int n, int warmup, RequestRunner r) throws Exception {
        for (int i = 0; i < warmup; i++) r.run();
        long[] t = new long[n];
        for (int i = 0; i < n; i++) {
            long start = System.nanoTime();
            r.run();
            t[i] = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        }
        return t;
    }

    private void record(String endpoint, long[] times, int status) {
        double avg = averageMs(times);
        long total = 0;
        for (long x : times) total += x;
        PerformanceSummaryReport.getShared().record(endpoint, avg, p95(times), status, total, times.length, 1, WARMUP);
    }

    private static double averageMs(long[] t) {
        long s = 0;
        for (long x : t) s += x;
        return t.length == 0 ? 0 : (double) s / t.length;
    }

    private static long p95(long[] t) {
        long[] c = t.clone();
        java.util.Arrays.sort(c);
        int i = Math.max(0, (int) Math.ceil(0.95 * c.length) - 1);
        return c[i];
    }

    @FunctionalInterface
    private interface RequestRunner { MvcResult run() throws Exception; }
}
