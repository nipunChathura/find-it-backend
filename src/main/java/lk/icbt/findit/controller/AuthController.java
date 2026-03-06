package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.Response;
import lk.icbt.findit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Unified auth APIs for password operations. Replaces role-specific password endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PutMapping(value = "/password/change", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Response> changePassword(@Valid @RequestBody UserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null || username.isBlank()) {
            Response r = new Response();
            r.setStatus("FAILURE");
            r.setResponseCode("401");
            r.setResponseMessage("Not authenticated");
            return ResponseEntity.status(401).body(r);
        }
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setUsername(username);
        dto.setCurrentPassword(request.getCurrentPassword());
        dto.setNewPassword(request.getNewPassword());
        PasswordChangeDTO result = userService.changePassword(dto);
        Response response = new Response();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/password/forgot", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Response> forgotPassword(@Valid @RequestBody UserRequest request) {
        ForgetPasswordDTO dto = new ForgetPasswordDTO();
        dto.setUsername(request.getUsername());
        ForgetPasswordDTO result = userService.forgetPassword(dto);
        Response response = new Response();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.ok(response);
    }
}
