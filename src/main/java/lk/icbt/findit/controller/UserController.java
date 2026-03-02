package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.ForgotPasswordApprovalDTO;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.dto.UserRegistrationDTO;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.UserResponse;
import lk.icbt.findit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserRequest request) {
        LoginDTO dto = new LoginDTO();
        BeanUtils.copyProperties(request, dto);

        LoginDTO result = userService.login(dto);

        UserResponse response = new UserResponse();
        mapResultToResponse(result, response);
        response.setToken(result.getToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        BeanUtils.copyProperties(request, dto);

        UserRegistrationDTO result = userService.register(dto);

        UserResponse response = new UserResponse();
        mapResultToResponse(result, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/password/change", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> changePassword(@Valid @RequestBody UserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;

        PasswordChangeDTO dto = new PasswordChangeDTO();
        BeanUtils.copyProperties(request, dto);
        dto.setUsername(username);

        PasswordChangeDTO result = userService.changePassword(dto);

        UserResponse response = new UserResponse();
        mapResultToResponse(result, response);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/password/forgot", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> forgetPassword(@Valid @RequestBody UserRequest request) {
        ForgetPasswordDTO dto = new ForgetPasswordDTO();
        BeanUtils.copyProperties(request, dto);

        ForgetPasswordDTO result = userService.forgetPassword(dto);

        UserResponse response = new UserResponse();
        mapResultToResponse(result, response);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/password/forgot/approval/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> approveForgotPassword(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest request) {
        ForgotPasswordApprovalDTO dto = new ForgotPasswordApprovalDTO();
        BeanUtils.copyProperties(request, dto);
        dto.setUserId(userId);

        ForgotPasswordApprovalDTO result = userService.approveForgotPassword(dto);

        UserResponse response = new UserResponse();
        mapResultToResponse(result, response);
        return ResponseEntity.ok(response);
    }

    private void mapResultToResponse(lk.icbt.findit.response.Response result, UserResponse response) {
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        if (result instanceof LoginDTO login) {
            response.setUserId(login.getUserId());
            response.setUsername(login.getUsername());
            response.setUserStatus(login.getUserStatus());
            response.setIsSystemUser(login.getIsSystemUser());
            response.setRole(login.getRole());
            response.setProfileImageUrl(login.getProfileImageUrl());
        } else if (result instanceof UserRegistrationDTO reg) {
            response.setUserId(reg.getUserId());
            response.setUsername(reg.getUsername());
            response.setUserStatus(reg.getUserStatus());
            response.setIsSystemUser(reg.getIsSystemUser());
            response.setRole(reg.getRole());
            response.setProfileImageUrl(reg.getProfileImageUrl());
        } else if (result instanceof PasswordChangeDTO || result instanceof ForgetPasswordDTO || result instanceof ForgotPasswordApprovalDTO) {
            // Only status, responseCode, responseMessage set above
        }
    }
}
