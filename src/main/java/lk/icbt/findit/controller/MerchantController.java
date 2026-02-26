package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.*;
import lk.icbt.findit.request.MerchantRequest;
import lk.icbt.findit.request.SubMerchantStatusChangeRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.MerchantLoginResponse;
import lk.icbt.findit.response.MerchantResponse;
import lk.icbt.findit.response.SubMerchantResponse;
import lk.icbt.findit.service.MerchantService;
import lk.icbt.findit.service.SubMerchantService;
import lk.icbt.findit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Merchant APIs. Onboarding is public. Profile update requires merchant JWT only.
 */
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;
    private final SubMerchantService subMerchantService;
    private final UserService userService;

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

    @PostMapping(value = "/onboarding", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> onboard(@Valid @RequestBody MerchantRequest request) {
        MerchantOnboardingDTO dto = new MerchantOnboardingDTO();
        BeanUtils.copyProperties(request, dto);
        MerchantOnboardingDTO result = merchantService.onboard(dto);
        MerchantResponse response = new MerchantResponse();
        BeanUtils.copyProperties(result, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PutMapping(value = "/profile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> updateProfile(@Valid @RequestBody MerchantRequest request) {
        String username = getAuthenticatedUsername();
        MerchantUpdateDTO dto = new MerchantUpdateDTO();
        BeanUtils.copyProperties(request, dto);
        MerchantUpdateDTO result = merchantService.updateProfileForMerchant(username, dto);
        MerchantResponse response = new MerchantResponse();
        BeanUtils.copyProperties(result, response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PutMapping(value = "/sub-merchants/{subMerchantId}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SubMerchantResponse> approveSubMerchant(@PathVariable Long subMerchantId) {
        String username = getAuthenticatedUsername();
        SubMerchantApprovalDTO result = subMerchantService.approveSubMerchantForMerchant(username, subMerchantId);
        return ResponseEntity.ok(mapToSubMerchantResponse(result));
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PutMapping(value = "/sub-merchants/{subMerchantId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SubMerchantResponse> updateSubMerchantStatus(
            @PathVariable Long subMerchantId,
            @Valid @RequestBody SubMerchantStatusChangeRequest request) {
        String username = getAuthenticatedUsername();
        SubMerchantApprovalDTO result = subMerchantService.updateSubMerchantStatusForMerchant(
                username, subMerchantId, request.getStatus(), request.getInactiveReason());
        return ResponseEntity.ok(mapToSubMerchantResponse(result));
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PutMapping(value = "/password/change", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> changePassword(@Valid @RequestBody UserRequest request) {
        String username = getAuthenticatedUsername();
        PasswordChangeDTO result = userService.changePasswordForMerchant(
                username, request.getCurrentPassword(), request.getNewPassword());
        MerchantResponse response = new MerchantResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/password/forgot", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> forgotPassword(@Valid @RequestBody UserRequest request) {
        ForgetPasswordDTO result = userService.forgotPasswordForMerchant(request.getUsername());
        MerchantResponse response = new MerchantResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.ok(response);
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    private static SubMerchantResponse mapToSubMerchantResponse(lk.icbt.findit.dto.SubMerchantApprovalDTO result) {
        SubMerchantResponse response = new SubMerchantResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        response.setSubMerchantId(result.getSubMerchantId());
        response.setMerchantId(result.getMerchantId());
        response.setMerchantName(result.getMerchantName());
        response.setMerchantEmail(result.getMerchantEmail());
        response.setMerchantNic(result.getMerchantNic());
        response.setMerchantProfileImage(result.getMerchantProfileImage());
        response.setMerchantAddress(result.getMerchantAddress());
        response.setMerchantPhoneNumber(result.getMerchantPhoneNumber());
        response.setMerchantType(result.getMerchantType());
        response.setSubMerchantStatus(result.getSubMerchantStatus());
        return response;
    }
}
