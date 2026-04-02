package lk.icbt.findit.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.config.ApiRequestLoggingFilter;
import lk.icbt.findit.config.TestJacksonConfig;
import lk.icbt.findit.controller.CustomerAppController;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.request.NearestOutletSearchRequest;
import lk.icbt.findit.security.CustomUserDetailsService;
import lk.icbt.findit.security.JwtService;
import lk.icbt.findit.response.NearestOutletResultItem;
import lk.icbt.findit.response.NearestOutletSearchResponse;
import lk.icbt.findit.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Tag("performance")
@WebMvcTest(CustomerAppController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class CustomerAppNearestOutletPerformanceTest {

    private static final int WARMUP_ITERATIONS = 3;
    private static final int MEASURED_ITERATIONS = 10;
    private static final long SLA_AVG_MS = 500;
    private static final long SLA_P95_MS = 800;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private NearestOutletSearchService nearestOutletSearchService;

    @MockitoBean
    private CustomerSearchHistoryService customerSearchHistoryService;

    @MockitoBean
    private CustomerFavoriteService customerFavoriteService;

    @MockitoBean
    private FeedbackService feedbackService;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private ApiRequestLoggingFilter apiRequestLoggingFilter;

    private User customerUser;
    private NearestOutletSearchRequest validRequest;
    private NearestOutletSearchResponse mockResponse;

    @BeforeEach
    void setUp() {
        customerUser = new User();
        customerUser.setUserId(1L);
        customerUser.setUsername("customer1");
        customerUser.setRole(Role.CUSTOMER);
        customerUser.setCustomerId(1L);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(customerUser));

        validRequest = new NearestOutletSearchRequest();
        validRequest.setLatitude(6.9271);
        validRequest.setLongitude(79.8612);
        validRequest.setDistanceKm(10.0);
        validRequest.setItemName("Coffee");

        mockResponse = new NearestOutletSearchResponse();
        mockResponse.setStatus("SUCCESS");
        NearestOutletResultItem outlet = new NearestOutletResultItem();
        outlet.setOutletId(1L);
        outlet.setOutletName("Nearest Cafe");
        outlet.setDistanceKm(2.5);
        outlet.setCurrentStatus("OPEN");
        mockResponse.setOutlets(List.of(outlet));

        when(nearestOutletSearchService.searchNearestOutlets(any(NearestOutletSearchRequest.class), any(Long.class)))
                .thenReturn(mockResponse);
    }

    @Test
    @WithMockUser(username = "customer1", roles = "CUSTOMER")
    @DisplayName("Performance: POST /api/customer-app/outlets/nearest - average and p95 under SLA")
    void searchNearestOutlets_responseTimeUnderSLA() throws Exception {
        long[] times = runTimedRequests(MEASURED_ITERATIONS, WARMUP_ITERATIONS, () ->
                mockMvc.perform(post("/api/customer-app/outlets/nearest")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                        .andExpect(status().isOk())
                        .andReturn());

        double avgMs = averageMs(times);
        long p95Ms = p95Ms(times);
        long totalMs = sumMs(times);
        PerformanceSummaryReport.getShared().record("POST /api/customer-app/outlets/nearest (Get)", avgMs, p95Ms, 200, totalMs, MEASURED_ITERATIONS, 1, WARMUP_ITERATIONS);

        assertThat(avgMs)
                .as("Average response time (ms) for POST /api/customer-app/outlets/nearest")
                .isLessThan(SLA_AVG_MS);
        assertThat(p95Ms)
                .as("P95 response time (ms) for POST /api/customer-app/outlets/nearest")
                .isLessThan(SLA_P95_MS);
    }

    @RepeatedTest(5)
    @WithMockUser(username = "customer1", roles = "CUSTOMER")
    @DisplayName("Performance: Single request POST /api/customer-app/outlets/nearest completes within max time")
    void searchNearestOutlets_singleRequestWithinMaxTime() throws Exception {
        long startNanos = System.nanoTime();
        mockMvc.perform(post("/api/customer-app/outlets/nearest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

        assertThat(elapsedMs)
                .as("Single request POST /api/customer-app/outlets/nearest (ms)")
                .isLessThan(SLA_P95_MS);
    }

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
