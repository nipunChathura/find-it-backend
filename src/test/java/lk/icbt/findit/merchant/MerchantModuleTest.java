package lk.icbt.findit.merchant;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.controller.MerchantController;
import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.MerchantLoginDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.dto.MerchantUpdateDTO;
import lk.icbt.findit.dto.SubMerchantApprovalDTO;
import lk.icbt.findit.entity.MerchantType;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.request.MerchantOnboardingRequest;
import lk.icbt.findit.request.MerchantRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.MerchantWithOutletsResponse;
import lk.icbt.findit.response.SubMerchantWithOutletsResponse;
import lk.icbt.findit.response.SubMerchantResponse;
import lk.icbt.findit.service.MerchantService;
import lk.icbt.findit.service.SubMerchantService;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Tag("unit")
@WebMvcTest(MerchantController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class MerchantModuleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MerchantService merchantService;

    @MockitoBean
    private SubMerchantService subMerchantService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private ApiRequestLoggingFilter apiRequestLoggingFilter;

    
    @Test
    @DisplayName("UT19 - Merchant login with valid credentials")
    void merchantLoginWithValidCredentials_returnsSuccess() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("merchant1");
        request.setPassword("pass123");

        MerchantLoginDTO loginResult = new MerchantLoginDTO();
        loginResult.setStatus("SUCCESS");
        loginResult.setToken("jwt-merchant-token");
        loginResult.setUserId(1L);
        loginResult.setUsername("merchant1");
        loginResult.setRole(Role.MERCHANT);
        loginResult.setMerchantId(1L);

        when(userService.loginMerchant(any(lk.icbt.findit.dto.LoginDTO.class))).thenReturn(loginResult);

        mockMvc.perform(post("/api/merchants/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.token").value("jwt-merchant-token"))
                .andExpect(jsonPath("$.username").value("merchant1"));
    }

    
    @Test
    @DisplayName("UT20 - Merchant onboarding")
    void merchantOnboarding_returnsCreated() throws Exception {
        MerchantOnboardingRequest request = new MerchantOnboardingRequest();
        request.setMerchantName("New Merchant");
        request.setMerchantEmail("merchant@test.com");
        request.setMerchantAddress("123 Main St");
        request.setMerchantPhoneNumber("0771234567");
        request.setMerchantType(MerchantType.FREE);
        request.setUsername("newmerchant");
        request.setPassword("pass123");

        MerchantOnboardingDTO result = new MerchantOnboardingDTO();
        result.setStatus("SUCCESS");
        result.setMerchantId(1L);
        result.setUsername("newmerchant");
        result.setMerchantName("New Merchant");

        when(merchantService.onboard(any(MerchantOnboardingDTO.class))).thenReturn(result);

        mockMvc.perform(post("/api/merchants/onboarding")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.merchantId").value(1))
                .andExpect(jsonPath("$.merchantName").value("New Merchant"));
    }

    
    @Test
    @WithMockUser(username = "merchant1", roles = "MERCHANT")
    @DisplayName("UT21 - Get merchant with outlets")
    void getMerchantWithOutlets_returnsOk() throws Exception {
        User user = new User();
        user.setUsername("merchant1");
        user.setRole(Role.MERCHANT);
        user.setMerchantId(1L);

        when(userRepository.findByUsername("merchant1")).thenReturn(Optional.of(user));

        MerchantWithOutletsResponse response = new MerchantWithOutletsResponse();
        response.setMerchantId(1L);
        response.setMerchantName("My Merchant");
        when(merchantService.getMerchantWithOutlets(1L)).thenReturn(response);

        mockMvc.perform(get("/api/merchants/with-outlets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchantId").value(1))
                .andExpect(jsonPath("$.merchantName").value("My Merchant"));
    }

    
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT22 - Update merchant profile")
    void updateMerchantProfile_returnsOk() throws Exception {
        MerchantRequest request = new MerchantRequest();
        request.setMerchantName("Updated Name");
        request.setMerchantEmail("user@test.com");
        request.setMerchantAddress("456 Updated St");
        request.setMerchantPhoneNumber("0771234567");
        request.setMerchantType(MerchantType.FREE);

        MerchantUpdateDTO dtoResp = new MerchantUpdateDTO();
        dtoResp.setStatus("SUCCESS");
        dtoResp.setMerchantId(1L);
        dtoResp.setMerchantName("Updated Name");

        when(merchantService.updateProfileForMerchant(anyString(), any(MerchantUpdateDTO.class)))
                .thenReturn(dtoResp);

        mockMvc.perform(put("/api/merchants/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.merchantName").value("Updated Name"));
    }

    
    @Test
    @DisplayName("UT23 - Merchant forgot password")
    void merchantForgotPassword_returnsOk() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("merchant1");

        ForgetPasswordDTO result = new ForgetPasswordDTO();
        result.setStatus("SUCCESS");
        result.setResponseCode("200");
        result.setResponseMessage("Reset link sent");

        when(userService.forgotPasswordForMerchant("merchant1")).thenReturn(result);

        mockMvc.perform(put("/api/merchants/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT24 - Approve sub-merchant")
    void approveSubMerchant_returnsOk() throws Exception {
        SubMerchantApprovalDTO result = new SubMerchantApprovalDTO();
        result.setStatus("SUCCESS");
        result.setSubMerchantId(2L);
        result.setMerchantId(1L);
        result.setSubMerchantStatus("ACTIVE");

        when(subMerchantService.approveSubMerchantForMerchant(anyString(), eq(2L))).thenReturn(result);

        mockMvc.perform(put("/api/merchants/sub-merchants/2/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.subMerchantStatus").value("ACTIVE"));
    }
}
