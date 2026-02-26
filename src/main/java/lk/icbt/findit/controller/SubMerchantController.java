package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.dto.SubMerchantAddDTO;
import lk.icbt.findit.request.SubMerchantAddRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.SubMerchantResponse;
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
 * Sub-merchant APIs. Add sub-merchant: SYSADMIN, ADMIN (any merchant), or MERCHANT (own merchant only).
 */
@RestController
@RequestMapping("/api/sub-merchants")
@RequiredArgsConstructor
public class SubMerchantController {

    private final SubMerchantService subMerchantService;
    private final UserService userService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT')")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SubMerchantResponse> addSubMerchant(@Valid @RequestBody SubMerchantAddRequest request) {
        SubMerchantAddDTO dto = new SubMerchantAddDTO();
        BeanUtils.copyProperties(request, dto);
        String username = getAuthenticatedUsername();
        SubMerchantAddDTO result = subMerchantService.addSubMerchantWithAuth(dto, username);
        SubMerchantResponse response = new SubMerchantResponse();
        BeanUtils.copyProperties(result, response);
        response.setSubMerchantStatus(result.getSubMerchantStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PutMapping(value = "/password/change", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SubMerchantResponse> changePassword(@Valid @RequestBody UserRequest request) {
        String username = getAuthenticatedUsername();
        PasswordChangeDTO result = userService.changePasswordForSubMerchant(
                username, request.getCurrentPassword(), request.getNewPassword());
        SubMerchantResponse response = new SubMerchantResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/password/forgot", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SubMerchantResponse> forgotPassword(@Valid @RequestBody UserRequest request) {
        ForgetPasswordDTO result = userService.forgotPasswordForSubMerchant(request.getUsername());
        SubMerchantResponse response = new SubMerchantResponse();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.ok(response);
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
