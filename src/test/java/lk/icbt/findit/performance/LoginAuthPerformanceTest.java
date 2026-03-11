package lk.icbt.findit.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.config.ApiRequestLoggingFilter;
import lk.icbt.findit.config.TestJacksonConfig;
import lk.icbt.findit.controller.AuthController;
import lk.icbt.findit.controller.UserController;
import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.dto.UserRegistrationDTO;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.service.UserService;
import lk.icbt.findit.security.CustomUserDetailsService;
import lk.icbt.findit.security.JwtService;
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

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performance tests for Login and Auth APIs. Records to shared Performance Summary.
 */
@Tag("performance")
@WebMvcTest(controllers = {UserController.class, AuthController.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class LoginAuthPerformanceTest {

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
    private JwtService jwtService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private ApiRequestLoggingFilter apiRequestLoggingFilter;

    @Test
    @DisplayName("Performance: POST /api/users/login")
    void login_responseTime() throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername("u");
        req.setPassword("p");
        LoginDTO dto = new LoginDTO();
        dto.setStatus("SUCCESS");
        dto.setToken("t");
        when(userService.login(any(LoginDTO.class))).thenReturn(dto);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk()).andReturn());
        record("POST /api/users/login (Login)", times);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @DisplayName("Performance: POST /api/users/registration")
    void registration_responseTime() throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername("u");
        req.setPassword("p");
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setStatus("SUCCESS");
        when(userService.register(any(UserRegistrationDTO.class))).thenReturn(dto);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(post("/api/users/registration").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isCreated()).andReturn());
        record("POST /api/users/registration (Save)", times);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @WithMockUser(username = "u")
    @DisplayName("Performance: PUT /api/auth/password/change")
    void passwordChange_responseTime() throws Exception {
        UserRequest req = new UserRequest();
        req.setCurrentPassword("old");
        req.setNewPassword("new");
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setStatus("SUCCESS");
        when(userService.changePassword(any(PasswordChangeDTO.class))).thenReturn(dto);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(put("/api/auth/password/change").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk()).andReturn());
        record("PUT /api/auth/password/change (Update)", times);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @DisplayName("Performance: PUT /api/auth/password/forgot")
    void passwordForgot_responseTime() throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername("u");
        ForgetPasswordDTO dto = new ForgetPasswordDTO();
        dto.setStatus("SUCCESS");
        when(userService.forgetPassword(any(ForgetPasswordDTO.class))).thenReturn(dto);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(put("/api/auth/password/forgot").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk()).andReturn());
        record("PUT /api/auth/password/forgot (Update)", times);
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

    private void record(String endpoint, long[] times) {
        double avg = averageMs(times);
        long total = 0;
        for (long x : times) total += x;
        int status = endpoint.contains("registration") ? 201 : 200;
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
