package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.request.MerchantOnboardingRequest;
import lk.icbt.findit.request.MerchantRequest;
import lk.icbt.findit.request.ProfileImageChangeRequest;
import lk.icbt.findit.request.SubMerchantAddByMerchantRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.dto.SubMerchantAddDTO;
import lk.icbt.findit.response.DiscountListItemResponse;
import lk.icbt.findit.response.MerchantAppDashboardResponse;
import lk.icbt.findit.response.MerchantLoginResponse;
import lk.icbt.findit.response.MerchantResponse;
import lk.icbt.findit.response.OutletDetailResponse;
import lk.icbt.findit.response.OutletSchedulesGroupedResponse;
import lk.icbt.findit.response.PaymentListItemResponse;
import lk.icbt.findit.response.Response;
import lk.icbt.findit.response.SubMerchantResponse;
import lk.icbt.findit.response.UserResponse;
import lk.icbt.findit.service.MerchantAppDashboardService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/merchant-app")
@RequiredArgsConstructor
public class MerchantAppController {

    private final UserService userService;
    private final MerchantService merchantService;
    private final MerchantAppDashboardService merchantAppDashboardService;
    private final PaymentService paymentService;
    private final SubMerchantService subMerchantService;
    private final OutletService outletService;
    private final UserRepository userRepository;
    private final OutletRepository outletRepository;

    
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
    public ResponseEntity<MerchantResponse> onboarding(@Valid @RequestBody MerchantOnboardingRequest request) {
        MerchantOnboardingDTO dto = new MerchantOnboardingDTO();
        BeanUtils.copyProperties(request, dto);
        dto.setParentMerchantId(request.getParentMerchantId());
        MerchantOnboardingDTO result = merchantService.onboard(dto);
        MerchantResponse response = new MerchantResponse();
        BeanUtils.copyProperties(result, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
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

    
    @PutMapping(value = "/profile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<MerchantResponse> updateProfile(@Valid @RequestBody MerchantRequest request) {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InvalidRequestException("404", "User not found"));
        if (user.getRole() != Role.MERCHANT) {
            return ResponseEntity.status(403).build();
        }
        lk.icbt.findit.dto.MerchantUpdateDTO dto = new lk.icbt.findit.dto.MerchantUpdateDTO();
        BeanUtils.copyProperties(request, dto);
        lk.icbt.findit.dto.MerchantUpdateDTO result = merchantService.updateProfileForMerchant(username, dto);
        MerchantResponse response = new MerchantResponse();
        BeanUtils.copyProperties(result, response);
        return ResponseEntity.ok(response);
    }

    
    @PutMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Response> changePassword(@Valid @RequestBody UserRequest request) {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InvalidRequestException("404", "User not found"));
        lk.icbt.findit.dto.PasswordChangeDTO result = user.getRole() == Role.MERCHANT
                ? userService.changePasswordForMerchant(username, request.getCurrentPassword(), request.getNewPassword())
                : userService.changePasswordForSubMerchant(username, request.getCurrentPassword(), request.getNewPassword());
        Response response = new Response();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.ok(response);
    }

    
    @PutMapping(value = "/profile/image", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> changeProfileImage(@Valid @RequestBody ProfileImageChangeRequest request) {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InvalidRequestException("404", "User not found"));
        if (user.getRole() != Role.MERCHANT && user.getRole() != Role.SUBMERCHANT) {
            return ResponseEntity.status(403).build();
        }
        UserResponse result = userService.changeProfileImage(user.getUserId(), request.getFileName());
        return ResponseEntity.ok(result);
    }

    
    @GetMapping(value = "/payments", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<PaymentListItemResponse>> getPaymentList(
            @RequestParam(required = false) String status) {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InvalidRequestException("404", "User not found"));
        if (user.getRole() != Role.MERCHANT && user.getRole() != Role.SUBMERCHANT) {
            return ResponseEntity.status(403).build();
        }
        List<Long> outletIds = getOutletIdsForUser(user);
        List<PaymentListItemResponse> list = outletIds.isEmpty() ? Collections.emptyList() : paymentService.listForOutletIds(outletIds, status);
        return ResponseEntity.ok(list);
    }

    
    @GetMapping(value = "/outlets/{outletId}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletDetailResponse> getOutletDetails(@PathVariable Long outletId) {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        OutletDetailResponse response = outletService.getOutletDetailsForMerchantApp(username, outletId);
        return ResponseEntity.ok(response);
    }

    
    @GetMapping(value = "/outlets/{outletId}/payment-details", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<PaymentListItemResponse>> getOutletPaymentDetails(@PathVariable Long outletId) {
        String username = getAuthenticatedUsername();
        System.out.println("username = " + username);
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(outletService.getPaymentDetailsForMerchantApp(username, outletId));
    }

    
    @GetMapping(value = "/outlets/{outletId}/schedule-details", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OutletSchedulesGroupedResponse> getOutletScheduleDetails(@PathVariable Long outletId) {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(outletService.getScheduleDetailsForMerchantApp(username, outletId));
    }

    
    @GetMapping(value = "/outlets/{outletId}/discount-details", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DiscountListItemResponse>> getOutletDiscountDetails(@PathVariable Long outletId) {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(outletService.getDiscountDetailsForMerchantApp(username, outletId));
    }

    
    @GetMapping(value = "/sub-merchants", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<SubMerchantResponse>> getSubMerchantList(
            @RequestParam(required = false) String name) {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        List<SubMerchantResponse> list = subMerchantService.listByMerchantUsername(username, name);
        return ResponseEntity.ok(list);
    }

    
    @PostMapping(value = "/sub-merchants", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SubMerchantResponse> addSubMerchant(@Valid @RequestBody SubMerchantAddByMerchantRequest request) {
        String username = getAuthenticatedUsername();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.USER_NOT_FOUND_CODE, "User not found"));
        if (user.getRole() != Role.MERCHANT || user.getMerchantId() == null) {
            throw new InvalidRequestException(ResponseCodes.VALIDATION_ERROR_CODE, "Only main merchant can add sub-merchants");
        }
        Long merchantId = user.getMerchantId();
        if (request.getParentMerchantId() != null && !request.getParentMerchantId().equals(merchantId)) {
            throw new InvalidRequestException(ResponseCodes.VALIDATION_ERROR_CODE, "parentMerchantId must match your merchant");
        }
        if (request.getParentMerchantId() != null) {
            merchantId = request.getParentMerchantId();
        }
        SubMerchantAddDTO dto = new SubMerchantAddDTO();
        dto.setMerchantId(merchantId);
        dto.setMerchantName(request.getMerchantName());
        dto.setMerchantEmail(request.getMerchantEmail());
        dto.setMerchantNic(request.getMerchantNic());
        dto.setMerchantProfileImage(request.getMerchantProfileImage());
        dto.setMerchantAddress(request.getMerchantAddress());
        dto.setMerchantPhoneNumber(request.getMerchantPhoneNumber());
        dto.setMerchantType(request.getMerchantType());
        dto.setPassword(request.getPassword());
        dto.setUsername(request.getUsername());
        SubMerchantAddDTO result = subMerchantService.addSubMerchantWithAuth(dto, username);
        SubMerchantResponse response = new SubMerchantResponse();
        BeanUtils.copyProperties(result, response);
        response.setSubMerchantStatus(result.getSubMerchantStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private List<Long> getOutletIdsForUser(User user) {
        List<Outlet> outlets;
        if (user.getRole() == Role.MERCHANT && user.getMerchantId() != null) {
            outlets = outletRepository.findByMerchant_MerchantId(user.getMerchantId());
        } else if (user.getRole() == Role.SUBMERCHANT && user.getSubMerchantId() != null) {
            outlets = outletRepository.findBySubMerchant_SubMerchantId(user.getSubMerchantId());
        } else {
            outlets = Collections.emptyList();
        }
        return outlets.stream().map(Outlet::getOutletId).collect(Collectors.toList());
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
