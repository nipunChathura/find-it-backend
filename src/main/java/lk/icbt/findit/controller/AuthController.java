package lk.icbt.findit.controller;


import lk.icbt.findit.dto.AuthDto;
import lk.icbt.findit.request.AuthRequest;
import lk.icbt.findit.response.AuthResponse;
import lk.icbt.findit.response.Response;
import lk.icbt.findit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Response register(@RequestBody AuthRequest authRequest) {

        AuthDto authDto = new AuthDto();
        BeanUtils.copyProperties(authRequest, authDto);

        AuthDto result = authService.register(authDto);
        AuthResponse response = new AuthResponse();
        response.setUserId(result.getUserId());
        response.setMerchantId(authDto.getMerchantId());
        response.setCustomerId(result.getCustomerId());
        response.setToken(authDto.getToken());
        response.setRole(authDto.getRole());
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());

        return response;
    }

}
