package lk.icbt.findit.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.config.ApiRequestLoggingFilter;
import lk.icbt.findit.config.TestJacksonConfig;
import lk.icbt.findit.controller.MerchantAppController;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.MerchantLoginDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.entity.MerchantType;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.request.MerchantOnboardingRequest;
import lk.icbt.findit.request.MerchantRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.MerchantAppDashboardResponse;
import lk.icbt.findit.response.MerchantResponse;
import lk.icbt.findit.service.MerchantAppDashboardService;
import lk.icbt.findit.service.MerchantService;
import lk.icbt.findit.service.OutletService;
import lk.icbt.findit.service.PaymentService;
import lk.icbt.findit.service.SubMerchantService;
import lk.icbt.findit.service.UserService;
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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("performance")
@WebMvcTest(MerchantAppController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class MerchantAppPerformanceTest {

    private static final int WARMUP = 2;
    private static final int MEASURED = 5;
    private static final long SLA_MS = 500;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private MerchantService merchantService;
    @MockitoBean
    private MerchantAppDashboardService merchantAppDashboardService;
    @MockitoBean
    private PaymentService paymentService;
    @MockitoBean
    private SubMerchantService subMerchantService;
    @MockitoBean
    private OutletService outletService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private OutletRepository outletRepository;
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
        user.setUsername("merchant1");
        user.setRole(Role.MERCHANT);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Performance: POST /api/merchant-app/login (Login)")
    void login_responseTime() throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername("m");
        req.setPassword("p");
        MerchantLoginDTO dto = new MerchantLoginDTO();
        dto.setStatus("SUCCESS");
        dto.setToken("t");
        when(userService.loginMerchant(any(LoginDTO.class))).thenReturn(dto);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(post("/api/merchant-app/login").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk()).andReturn());
        record("POST /api/merchant-app/login (Login)", times, 200);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @DisplayName("Performance: POST /api/merchant-app/onboarding (Save)")
    void onboarding_responseTime() throws Exception {
        MerchantOnboardingRequest req = new MerchantOnboardingRequest();
        req.setMerchantName("Merchant One");
        req.setMerchantEmail("a@b.com");
        req.setMerchantAddress("Addr");
        req.setMerchantPhoneNumber("0712345678");
        req.setMerchantType(MerchantType.FREE);
        req.setUsername("user1");
        req.setPassword("pass123");
        MerchantResponse res = new MerchantResponse();
        res.setStatus("SUCCESS");
        when(merchantService.onboard(any(MerchantOnboardingDTO.class))).thenReturn(new MerchantOnboardingDTO());

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(post("/api/merchant-app/onboarding").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isCreated()).andReturn());
        record("POST /api/merchant-app/onboarding (Save)", times, 201);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @WithMockUser(username = "merchant1", roles = "MERCHANT")
    @DisplayName("Performance: GET /api/merchant-app/dashboard (Get)")
    void getDashboard_responseTime() throws Exception {
        MerchantAppDashboardResponse res = new MerchantAppDashboardResponse();
        when(merchantAppDashboardService.getDashboard(anyString())).thenReturn(res);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(get("/api/merchant-app/dashboard")).andExpect(status().isOk()).andReturn());
        record("GET /api/merchant-app/dashboard (Get)", times, 200);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @WithMockUser(username = "merchant1", roles = "MERCHANT")
    @DisplayName("Performance: PUT /api/merchant-app/profile (Update)")
    void updateProfile_responseTime() throws Exception {
        MerchantRequest req = new MerchantRequest();
        req.setMerchantName("Merchant One");
        req.setMerchantEmail("m@b.com");
        req.setMerchantAddress("Address");
        req.setMerchantPhoneNumber("0712345678");
        req.setMerchantType(MerchantType.FREE);
        MerchantResponse res = new MerchantResponse();
        res.setStatus("SUCCESS");
        when(merchantService.updateProfileForMerchant(anyString(), any())).thenReturn(new lk.icbt.findit.dto.MerchantUpdateDTO());

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(put("/api/merchant-app/profile").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk()).andReturn());
        record("PUT /api/merchant-app/profile (Update)", times, 200);
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
