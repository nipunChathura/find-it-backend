package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.request.MerchantOnboardingRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.MerchantAppDashboardResponse;
import lk.icbt.findit.response.MerchantLoginResponse;
import lk.icbt.findit.response.MerchantResponse;
import lk.icbt.findit.service.MerchantAppDashboardService;
import lk.icbt.findit.service.MerchantService;
import lk.icbt.findit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Merchant app API. Public endpoints for merchant/sub-merchant login and onboarding
 * (self-registration). Use these from the merchant-facing mobile or web app.
 */
@RestController
@RequestMapping("/api/merchant-app")
@RequiredArgsConstructor
public class MerchantAppController {

    private final UserService userService;
    private final MerchantService merchantService;
    private final MerchantAppDashboardService merchantAppDashboardService;

    /**
     * Merchant or sub-merchant login with username and password. Public.
     * Returns JWT token and user/merchant context (role, merchantId, subMerchantId, etc.).
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantLoginResponse> login(@Valid @RequestBody UserRequest request) {
        LoginDTO dto = new LoginDTO();
        dto.setUsername(request.getUsername());
        dto.setPassword(request.getPassword());
        var result = userService.loginMerchant(dto);
        MerchantLoginResponse response = new MerchantLoginResponse();
        BeanUtils.copyProperties(result, response);
        return ResponseEntity.ok(response);
    }

    /**
     * Merchant or sub-merchant onboarding (self-registration). Public.
     * When parentMerchantId is null or not provided, creates a main merchant and user with role MERCHANT.
     * When parentMerchantId is set, creates a sub-merchant under that parent and user with role SUBMERCHANT
     * (subject to parent merchant approval).
     */
    @PostMapping(value = "/onboarding", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> onboarding(@Valid @RequestBody MerchantOnboardingRequest request) {
        MerchantOnboardingDTO dto = new MerchantOnboardingDTO();
        BeanUtils.copyProperties(request, dto);
        dto.setParentMerchantId(request.getParentMerchantId());
        MerchantOnboardingDTO result = merchantService.onboard(dto);
        MerchantResponse response = new MerchantResponse();
        BeanUtils.copyProperties(result, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get dashboard data for the authenticated merchant or sub-merchant.
     * Returns total outlet count, active outlet count, total items, pending payment count, and list of pending payments.
     */
    @GetMapping(value = "/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantAppDashboardResponse> getDashboard() {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        MerchantAppDashboardResponse response = merchantAppDashboardService.getDashboard(username);
        return ResponseEntity.ok(response);
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
