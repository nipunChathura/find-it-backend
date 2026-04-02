package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.MerchantApprovalDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.dto.MerchantStatusChangeDTO;
import lk.icbt.findit.dto.MerchantUpdateDTO;
import lk.icbt.findit.dto.SubMerchantAddDTO;
import lk.icbt.findit.dto.UserAddDTO;
import lk.icbt.findit.dto.UserApprovalDTO;
import lk.icbt.findit.dto.OutletAddDTO;
import lk.icbt.findit.dto.UserUpdateDTO;
import lk.icbt.findit.request.*;
import lk.icbt.findit.dto.SubMerchantApprovalDTO;
import lk.icbt.findit.response.GetAllMerchantsResponse;
import lk.icbt.findit.response.GetAllOutletsResponse;
import lk.icbt.findit.request.MerchantRequest;
import lk.icbt.findit.request.MerchantStatusChangeRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.MerchantResponse;
import lk.icbt.findit.response.MerchantWithOutletsResponse;
import lk.icbt.findit.response.OutletResponse;
import lk.icbt.findit.response.PaymentResponse;
import lk.icbt.findit.response.SubMerchantResponse;
import lk.icbt.findit.response.SubMerchantWithOutletsResponse;
import lk.icbt.findit.response.UserResponse;
import lk.icbt.findit.service.MerchantService;
import lk.icbt.findit.service.OutletService;
import lk.icbt.findit.service.PaymentService;
import lk.icbt.findit.service.SubMerchantService;
import lk.icbt.findit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MerchantService merchantService;
    private final SubMerchantService subMerchantService;
    private final UserService userService;
    private final OutletService outletService;
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('SYSADMIN')")
    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserAddRequest request) {
        UserAddDTO dto = new UserAddDTO();
        dto.setUsername(request.getUsername());
        dto.setPassword(request.getPassword());
        dto.setEmail(request.getEmail());
        dto.setRole(request.getRole());
        dto.setMerchantId(request.getMerchantId());
        dto.setSubMerchantId(request.getSubMerchantId());
        dto.setStatus(request.getStatus());

        UserAddDTO result = userService.addUser(dto);

        UserResponse response = new UserResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        response.setUserId(result.getUserId());
        response.setUsername(result.getUsername());
        response.setEmail(result.getEmail());
        response.setUserStatus(result.getUserStatus());
        response.setIsSystemUser(result.getIsSystemUser());
        response.setRole(result.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('SYSADMIN')")
    @PutMapping(value = "/users/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername(request.getUsername());
        dto.setEmail(request.getEmail());
        dto.setRole(request.getRole());
        dto.setMerchantId(request.getMerchantId());
        dto.setSubMerchantId(request.getSubMerchantId());
        dto.setStatus(request.getStatus());

        UserUpdateDTO result = userService.updateUser(userId, dto);

        UserResponse response = new UserResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        response.setUserId(result.getUserId());
        response.setUsername(result.getUsername());
        response.setEmail(result.getEmail());
        response.setUserStatus(result.getUserStatus());
        response.setIsSystemUser(result.getIsSystemUser());
        response.setRole(result.getRole());
        response.setMerchantId(result.getMerchantId());
        response.setSubMerchantId(result.getSubMerchantId());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SYSADMIN')")
    @PutMapping(value = "/users/{userId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        UserUpdateDTO result = userService.updateUserStatus(userId, request.getStatus());
        UserResponse response = new UserResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        response.setUserId(result.getUserId());
        response.setUsername(result.getUsername());
        response.setEmail(result.getEmail());
        response.setUserStatus(result.getUserStatus());
        response.setIsSystemUser(result.getIsSystemUser());
        response.setRole(result.getRole());
        response.setMerchantId(result.getMerchantId());
        response.setSubMerchantId(result.getSubMerchantId());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        List<UserResponse> list = userService.getAllUsers(status, search);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @GetMapping(value = "/merchants", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GetAllMerchantsResponse> getAllMerchants(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String merchantType) {
        GetAllMerchantsResponse response = merchantService.getAllMerchantsAndSubMerchants(search, status, merchantType);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PostMapping(value = "/merchants", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantWithOutletsResponse> addMerchant(@Valid @RequestBody MerchantOnboardingRequest request) {
        MerchantOnboardingDTO dto = new MerchantOnboardingDTO();
        BeanUtils.copyProperties(request, dto);
        MerchantOnboardingDTO result = merchantService.onboard(dto);
        MerchantWithOutletsResponse response = merchantService.getMerchantWithOutlets(result.getMerchantId());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PostMapping(value = "/merchants/{merchantId}/sub-merchants", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SubMerchantWithOutletsResponse> addSubMerchant(
            @PathVariable Long merchantId,
            @Valid @RequestBody AdminSubMerchantAddRequest request) {
        SubMerchantAddDTO dto = new SubMerchantAddDTO();
        BeanUtils.copyProperties(request, dto);
        dto.setMerchantId(merchantId);
        dto.setActiveOnCreate(false);
        SubMerchantAddDTO result = subMerchantService.addSubMerchant(dto);
        SubMerchantWithOutletsResponse response = subMerchantService.getSubMerchantWithOutlets(result.getSubMerchantId());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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
    @PutMapping(value = "/merchants/reject/{merchantId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> rejectMerchant(
            @PathVariable Long merchantId,
            @RequestBody(required = false) RejectRequest request) {
        String reason = request != null ? request.getReason() : null;
        MerchantStatusChangeDTO result = merchantService.rejectMerchant(merchantId, reason);
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

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PutMapping(value = "/merchants/{merchantId}/sub-merchants/{subMerchantId}/approval", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SubMerchantResponse> approveSubMerchant(
            @PathVariable Long merchantId,
            @PathVariable Long subMerchantId) {
        SubMerchantApprovalDTO result = subMerchantService.approveSubMerchant(subMerchantId, merchantId);
        return ResponseEntity.ok(mapToSubMerchantResponse(result));
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PutMapping(value = "/merchants/{merchantId}/sub-merchants/{subMerchantId}/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SubMerchantResponse> rejectSubMerchant(
            @PathVariable Long merchantId,
            @PathVariable Long subMerchantId,
            @RequestBody(required = false) RejectRequest request) {
        String reason = request != null ? request.getReason() : null;
        SubMerchantApprovalDTO result = subMerchantService.rejectSubMerchant(subMerchantId, merchantId, reason);
        return ResponseEntity.ok(mapToSubMerchantResponse(result));
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
    @PutMapping(value = "/users/reject/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> rejectUser(
            @PathVariable Long userId,
            @RequestBody(required = false) RejectRequest request) {
        String reason = request != null ? request.getReason() : null;
        UserUpdateDTO result = userService.rejectUser(userId, reason);
        UserResponse response = new UserResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        response.setUserId(result.getUserId());
        response.setUsername(result.getUsername());
        response.setEmail(result.getEmail());
        response.setUserStatus(result.getUserStatus());
        response.setRole(result.getRole());
        response.setMerchantId(result.getMerchantId());
        response.setSubMerchantId(result.getSubMerchantId());
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

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PutMapping(value = "/outlets/{outletId}/verify-payment", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletResponse> verifyOutletPayment(@PathVariable Long outletId) {
        OutletAddDTO result = outletService.verifyPayment(outletId);
        OutletResponse response = new OutletResponse();
        BeanUtils.copyProperties(result, response);
        response.setOutletStatus(result.getOutletStatus());
        response.setSubscriptionValidUntil(result.getSubscriptionValidUntil());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PutMapping(value = "/payments/{paymentId}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PaymentResponse> approvePayment(@PathVariable Long paymentId) {
        PaymentResponse result = paymentService.approvePayment(paymentId);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @GetMapping(value = "/outlets", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GetAllOutletsResponse> getAllOutlets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String outletType) {
        GetAllOutletsResponse response = outletService.getAllOutlets(search, status, outletType);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PutMapping(value = "/outlets/{outletId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletResponse> updateOutletStatus(
            @PathVariable Long outletId,
            @Valid @RequestBody OutletStatusUpdateRequest request) {
        OutletAddDTO result = outletService.updateOutletStatus(outletId, request.getStatus());
        OutletResponse response = new OutletResponse();
        BeanUtils.copyProperties(result, response);
        response.setOutletStatus(result.getOutletStatus());
        response.setSubscriptionValidUntil(result.getSubscriptionValidUntil());
        return ResponseEntity.ok(response);
    }

    private static SubMerchantResponse mapToSubMerchantResponse(SubMerchantApprovalDTO result) {
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
