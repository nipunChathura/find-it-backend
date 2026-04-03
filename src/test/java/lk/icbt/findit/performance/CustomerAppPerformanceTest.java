package lk.icbt.findit.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.config.ApiRequestLoggingFilter;
import lk.icbt.findit.config.TestJacksonConfig;
import lk.icbt.findit.controller.CustomerAppController;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.request.CustomerFavoriteRequest;
import lk.icbt.findit.request.CustomerLoginRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.CustomerFavoriteResponse;
import lk.icbt.findit.response.CustomerLoginResponse;
import lk.icbt.findit.response.ItemListItemResponse;
import lk.icbt.findit.service.*;
import lk.icbt.findit.security.CustomUserDetailsService;
import lk.icbt.findit.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("performance")
@WebMvcTest(CustomerAppController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class CustomerAppPerformanceTest {

    private static final int WARMUP = 2;
    private static final int MEASURED = 5;
    private static final long SLA_MS = 500;

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

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("customer1");
        user.setRole(Role.CUSTOMER);
        user.setCustomerId(1L);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Performance: POST /api/customer-app/login (Login)")
    void login_responseTime() throws Exception {
        CustomerLoginRequest req = new CustomerLoginRequest();
        req.setEmail("c@c.com");
        req.setPassword("p");
        lk.icbt.findit.dto.CustomerLoginDTO dto = new lk.icbt.findit.dto.CustomerLoginDTO();
        dto.setStatus("SUCCESS");
        dto.setToken("t");
        when(userService.loginCustomer(anyString(), anyString())).thenReturn(dto);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(post("/api/customer-app/login").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk()).andReturn());
        record("POST /api/customer-app/login (Login)", times, 200);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @WithMockUser(username = "customer1", roles = "CUSTOMER")
    @DisplayName("Performance: GET /api/customer-app/items/search (Get)")
    void getItemsSearch_responseTime() throws Exception {
        when(itemService.search(any(), any(), any(), any(), any())).thenReturn(List.of(new ItemListItemResponse()));

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(get("/api/customer-app/items/search")).andExpect(status().isOk()).andReturn());
        record("GET /api/customer-app/items/search (Get)", times, 200);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @WithMockUser(username = "customer1", roles = "CUSTOMER")
    @DisplayName("Performance: POST /api/customer-app/favorites (Save)")
    void postFavorites_responseTime() throws Exception {
        CustomerFavoriteRequest req = new CustomerFavoriteRequest();
        req.setOutletId(1L);
        req.setRating(4.0);
        CustomerFavoriteResponse res = new CustomerFavoriteResponse();
        when(customerFavoriteService.create(eq(1L), any())).thenReturn(res);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(post("/api/customer-app/favorites").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isCreated()).andReturn());
        record("POST /api/customer-app/favorites (Save)", times, 201);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @WithMockUser(username = "customer1", roles = "CUSTOMER")
    @DisplayName("Performance: PUT /api/customer-app/password (Update)")
    void putPassword_responseTime() throws Exception {
        UserRequest req = new UserRequest();
        req.setCurrentPassword("old");
        req.setNewPassword("new");
        lk.icbt.findit.dto.PasswordChangeDTO dto = new lk.icbt.findit.dto.PasswordChangeDTO();
        dto.setStatus("SUCCESS");
        when(userService.changePassword(any())).thenReturn(dto);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(put("/api/customer-app/password").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk()).andReturn());
        record("PUT /api/customer-app/password (Update)", times, 200);
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
