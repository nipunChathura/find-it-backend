package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.MerchantApprovalDTO;
import lk.icbt.findit.dto.MerchantStatusChangeDTO;
import lk.icbt.findit.dto.MerchantUpdateDTO;
import lk.icbt.findit.dto.UserApprovalDTO;
import lk.icbt.findit.request.MerchantRequest;
import lk.icbt.findit.request.MerchantStatusChangeRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.MerchantResponse;
import lk.icbt.findit.response.UserResponse;
import lk.icbt.findit.service.MerchantService;
import lk.icbt.findit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin APIs. All endpoints require authenticated user (JWT Bearer token).
 * User approval: SYSADMIN only. Merchant approval: SYSADMIN or ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MerchantService merchantService;
    private final UserService userService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PutMapping(value = "/merchants/approval/{merchantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> approveMerchant(@PathVariable Long merchantId) {
        MerchantApprovalDTO dto = new MerchantApprovalDTO();
        dto.setMerchantId(merchantId);

        MerchantApprovalDTO result = merchantService.approveMerchant(dto);

        MerchantResponse response = new MerchantResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        response.setMerchantId(result.getMerchantId());
        response.setMerchantName(result.getMerchantName());
        response.setMerchantEmail(result.getMerchantEmail());
        response.setMerchantNic(result.getMerchantNic());
        response.setMerchantProfileImage(result.getMerchantProfileImage());
        response.setMerchantAddress(result.getMerchantAddress());
        response.setMerchantPhoneNumber(result.getMerchantPhoneNumber());
        response.setMerchantType(result.getMerchantType());
        response.setMerchantStatus(result.getMerchantStatus());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PutMapping(value = "/merchants/{merchantId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> updateMerchant(
            @PathVariable Long merchantId,
            @Valid @RequestBody MerchantRequest request) {
        MerchantUpdateDTO dto = new MerchantUpdateDTO();
        BeanUtils.copyProperties(request, dto);
        dto.setMerchantId(merchantId);

        MerchantUpdateDTO result = merchantService.updateMerchant(dto);

        MerchantResponse response = new MerchantResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        response.setMerchantId(result.getMerchantId());
        response.setMerchantName(result.getMerchantName());
        response.setMerchantEmail(result.getMerchantEmail());
        response.setMerchantNic(result.getMerchantNic());
        response.setMerchantProfileImage(result.getMerchantProfileImage());
        response.setMerchantAddress(result.getMerchantAddress());
        response.setMerchantPhoneNumber(result.getMerchantPhoneNumber());
        response.setMerchantType(result.getMerchantType());
        response.setMerchantStatus(result.getMerchantStatus());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PutMapping(value = "/merchants/{merchantId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> changeMerchantStatus(
            @PathVariable Long merchantId,
            @Valid @RequestBody MerchantStatusChangeRequest request) {
        MerchantStatusChangeDTO dto = new MerchantStatusChangeDTO();
        dto.setMerchantId(merchantId);
        dto.setNewStatus(request.getStatus());
        dto.setInactiveReason(request.getInactiveReason());

        MerchantStatusChangeDTO result = merchantService.changeMerchantStatus(dto);

        MerchantResponse response = new MerchantResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        response.setMerchantId(result.getMerchantId());
        response.setMerchantName(result.getMerchantName());
        response.setMerchantEmail(result.getMerchantEmail());
        response.setMerchantStatus(result.getMerchantStatus());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SYSADMIN')")
    @PutMapping(value = "/users/approval/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> approveUser(@PathVariable Long userId) {
        UserRequest request = new UserRequest();
        request.setUserId(userId);

        UserApprovalDTO dto = new UserApprovalDTO();
        BeanUtils.copyProperties(request, dto);

        UserApprovalDTO result = userService.approveUser(dto);

        UserResponse response = new UserResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        response.setUserId(result.getUserId());
        response.setUsername(result.getUsername());
        response.setUserStatus(result.getUserStatus());
        return ResponseEntity.ok(response);
    }
}
