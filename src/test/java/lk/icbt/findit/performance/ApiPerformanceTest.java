package lk.icbt.findit.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.config.ApiRequestLoggingFilter;
import lk.icbt.findit.config.TestJacksonConfig;
import lk.icbt.findit.controller.OutletController;
import lk.icbt.findit.dto.OutletAddDTO;
import lk.icbt.findit.request.OutletAddRequest;
import lk.icbt.findit.request.OutletUpdateRequest;
import lk.icbt.findit.response.*;
import lk.icbt.findit.security.CustomUserDetailsService;
import lk.icbt.findit.security.JwtService;
import lk.icbt.findit.service.DiscountService;
import lk.icbt.findit.service.FeedbackService;
import lk.icbt.findit.service.OutletScheduleService;
import lk.icbt.findit.service.OutletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * JUnit API performance tests.
 * Measures response time for selected endpoints and asserts they meet SLA thresholds.
 * Run with: mvn test -Dtest=ApiPerformanceTest
 * Or run only performance tests: mvn test -Dtest="*Performance*Test"
 */
@Tag("performance")
@WebMvcTest(OutletController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class ApiPerformanceTest {

    private static final int WARMUP_ITERATIONS = 3;
    private static final int MEASURED_ITERATIONS = 10;
    /** SLA: average response time (ms) for mocked controller layer. */
    private static final long SLA_AVG_MS = 500;
    /** SLA: p95 response time (ms). */
    private static final long SLA_P95_MS = 800;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OutletService outletService;

    @MockitoBean
    private OutletScheduleService outletScheduleService;

    @MockitoBean
    private DiscountService discountService;

    @MockitoBean
    private FeedbackService feedbackService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private ApiRequestLoggingFilter apiRequestLoggingFilter;

    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("Performance: GET /api/outlets - average and p95 under SLA")
    void listOutlets_responseTimeUnderSLA() throws Exception {
        OutletListResponse out = new OutletListResponse();
        out.setId(1L);
        out.setName("Main Outlet");
        out.setCurrentStatus("OPEN");
        when(outletService.listOutlets(null, null)).thenReturn(List.of(out));

        long[] times = runTimedRequests(MEASURED_ITERATIONS, WARMUP_ITERATIONS, () ->
                mockMvc.perform(get("/api/outlets")).andExpect(status().isOk()).andReturn());

        double avgMs = averageMs(times);
        long p95Ms = p95Ms(times);
        long totalMs = sumMs(times);
        PerformanceSummaryReport.getShared().record("GET /api/outlets (Get)", avgMs, p95Ms, 200, totalMs, MEASURED_ITERATIONS, 1, WARMUP_ITERATIONS);

        assertThat(avgMs)
                .as("Average response time (ms) for GET /api/outlets")
                .isLessThan(SLA_AVG_MS);
        assertThat(p95Ms)
                .as("P95 response time (ms) for GET /api/outlets")
                .isLessThan(SLA_P95_MS);
    }

    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("Performance: GET /api/outlets/{id}/details - average under SLA")
    void getOutletDetails_responseTimeUnderSLA() throws Exception {
        OutletListItemResponse outletItem = new OutletListItemResponse();
        outletItem.setOutletId(1L);
        outletItem.setOutletName("Detail Outlet");
        OutletDetailResponse detail = new OutletDetailResponse();
        detail.setOutlet(outletItem);
        when(outletService.getOutletDetails(1L)).thenReturn(detail);

        long[] times = runTimedRequests(MEASURED_ITERATIONS, WARMUP_ITERATIONS, () ->
                mockMvc.perform(get("/api/outlets/1/details")).andExpect(status().isOk()).andReturn());

        double avgMs = averageMs(times);
        long p95Ms = p95Ms(times);
        long totalMs = sumMs(times);
        PerformanceSummaryReport.getShared().record("GET /api/outlets/1/details (Get)", avgMs, p95Ms, 200, totalMs, MEASURED_ITERATIONS, 1, WARMUP_ITERATIONS);

        assertThat(avgMs)
                .as("Average response time (ms) for GET /api/outlets/1/details")
                .isLessThan(SLA_AVG_MS);
    }

    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("Performance: POST /api/outlets (Save)")
    void addOutlet_responseTimeUnderSLA() throws Exception {
        OutletAddRequest request = new OutletAddRequest();
        request.setMerchantId(1L);
        request.setOutletName("New Outlet");
        request.setAddressLine1("123 Street");
        OutletAddDTO dtoResult = new OutletAddDTO();
        dtoResult.setOutletId(1L);
        dtoResult.setOutletName("New Outlet");
        when(outletService.addOutlet(any(OutletAddDTO.class), anyString())).thenReturn(dtoResult);

        long[] times = runTimedRequests(MEASURED_ITERATIONS, WARMUP_ITERATIONS, () ->
                mockMvc.perform(post("/api/outlets")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated()).andReturn());
        double avgMs = averageMs(times);
        long p95Ms = p95Ms(times);
        long totalMs = sumMs(times);
        PerformanceSummaryReport.getShared().record("POST /api/outlets (Save)", avgMs, p95Ms, 201, totalMs, MEASURED_ITERATIONS, 1, WARMUP_ITERATIONS);
        assertThat(avgMs).as("POST /api/outlets avg ms").isLessThan(SLA_AVG_MS);
    }

    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("Performance: PUT /api/outlets/1 (Update)")
    void updateOutlet_responseTimeUnderSLA() throws Exception {
        OutletUpdateRequest request = new OutletUpdateRequest();
        request.setOutletName("Updated Outlet");
        OutletAddDTO dtoResult = new OutletAddDTO();
        dtoResult.setOutletId(1L);
        dtoResult.setOutletName("Updated Outlet");
        when(outletService.updateOutlet(eq(1L), any(OutletAddDTO.class), anyString())).thenReturn(dtoResult);

        long[] times = runTimedRequests(MEASURED_ITERATIONS, WARMUP_ITERATIONS, () ->
                mockMvc.perform(put("/api/outlets/1")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk()).andReturn());
        double avgMs = averageMs(times);
        long p95Ms = p95Ms(times);
        long totalMs = sumMs(times);
        PerformanceSummaryReport.getShared().record("PUT /api/outlets/1 (Update)", avgMs, p95Ms, 200, totalMs, MEASURED_ITERATIONS, 1, WARMUP_ITERATIONS);
        assertThat(avgMs).as("PUT /api/outlets/1 avg ms").isLessThan(SLA_AVG_MS);
    }

    @RepeatedTest(5)
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("Performance: Single request GET /api/outlets completes within max time")
    void listOutlets_singleRequestWithinMaxTime() throws Exception {
        OutletListResponse out = new OutletListResponse();
        out.setId(1L);
        out.setName("Main Outlet");
        out.setCurrentStatus("OPEN");
        when(outletService.listOutlets(null, null)).thenReturn(List.of(out));

        long startNanos = System.nanoTime();
        mockMvc.perform(get("/api/outlets")).andExpect(status().isOk());
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

        assertThat(elapsedMs)
                .as("Single request GET /api/outlets (ms)")
                .isLessThan(SLA_P95_MS);
    }

    /** Runs warmup then measured iterations, returns elapsed time in ms per measured request. */
    private long[] runTimedRequests(int measured, int warmup, RequestRunner runner) throws Exception {
        for (int i = 0; i < warmup; i++) {
            runner.run();
        }
        long[] times = new long[measured];
        for (int i = 0; i < measured; i++) {
            long start = System.nanoTime();
            runner.run();
            times[i] = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        }
        return times;
    }

    private static double averageMs(long[] times) {
        return times.length == 0 ? 0 : (double) sumMs(times) / times.length;
    }

    private static long sumMs(long[] times) {
        long sum = 0;
        for (long t : times) sum += t;
        return sum;
    }

    private static long p95Ms(long[] times) {
        long[] sorted = times.clone();
        java.util.Arrays.sort(sorted);
        int idx = (int) Math.ceil(0.95 * sorted.length) - 1;
        idx = Math.max(0, idx);
        return sorted[idx];
    }

    @FunctionalInterface
    private interface RequestRunner {
        MvcResult run() throws Exception;
    }
}
