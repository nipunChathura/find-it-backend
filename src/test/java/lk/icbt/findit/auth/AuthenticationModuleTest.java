package lk.icbt.findit.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.controller.AuthController;
import lk.icbt.findit.controller.UserController;
import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.dto.UserRegistrationDTO;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.Response;
import lk.icbt.findit.service.UserService;
import lk.icbt.findit.config.ApiRequestLoggingFilter;
import lk.icbt.findit.security.CustomUserDetailsService;
import lk.icbt.findit.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import lk.icbt.findit.config.TestJacksonConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * JUnit tests for Authentication module APIs.
 * Covers: User login, User registration, Auth password change/forgot, Merchant login/forgot password.
 */
@Tag("unit")
@WebMvcTest(controllers = {UserController.class, AuthController.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class AuthenticationModuleTest {

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

    // ---------- UT01: User login with valid credentials ----------
    @Test
    @DisplayName("UT01 - User login with valid credentials")
    void userLoginWithValidCredentials_returnsSuccess() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        LoginDTO loginResult = new LoginDTO();
        loginResult.setStatus("SUCCESS");
        loginResult.setToken("jwt-token-123");
        loginResult.setUserId(1L);
        loginResult.setUsername("testuser");
        loginResult.setRole(Role.USER);

        when(userService.login(any(LoginDTO.class))).thenReturn(loginResult);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    // ---------- UT02: User registration ----------
    @Test
    @DisplayName("UT02 - User registration with valid data")
    void userRegistrationWithValidData_returnsCreated() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword("pass123");

        UserRegistrationDTO regResult = new UserRegistrationDTO();
        regResult.setStatus("SUCCESS");
        regResult.setUserId(2L);
        regResult.setUsername("newuser");
        regResult.setRole(Role.USER);

        when(userService.register(any(UserRegistrationDTO.class))).thenReturn(regResult);

        mockMvc.perform(post("/api/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    // ---------- UT03: Auth password change (authenticated) ----------
    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("UT03 - Change password when authenticated")
    void changePasswordWhenAuthenticated_returnsOk() throws Exception {
        UserRequest request = new UserRequest();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");

        PasswordChangeDTO result = new PasswordChangeDTO();
        result.setStatus("SUCCESS");
        result.setResponseCode("200");
        result.setResponseMessage("Password changed successfully");

        when(userService.changePassword(any(PasswordChangeDTO.class))).thenReturn(result);

        mockMvc.perform(put("/api/auth/password/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    // ---------- UT04: Auth forgot password ----------
    @Test
    @DisplayName("UT04 - Forgot password request")
    void forgotPassword_returnsOk() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("testuser");

        ForgetPasswordDTO result = new ForgetPasswordDTO();
        result.setStatus("SUCCESS");
        result.setResponseCode("200");
        result.setResponseMessage("Reset link sent");

        when(userService.forgetPassword(any(ForgetPasswordDTO.class))).thenReturn(result);

        mockMvc.perform(put("/api/auth/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
