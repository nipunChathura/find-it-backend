package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.ForgotPasswordApprovalDTO;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.dto.UserApprovalDTO;
import lk.icbt.findit.dto.UserRegistrationDTO;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.security.JwtService;
import lk.icbt.findit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public LoginDTO login(LoginDTO dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
            if (auth == null || !auth.isAuthenticated()) {
                throw new InvalidRequestException(
                        ResponseCodes.INVALID_LOGIN_CREDENTIALS_CODE,
                        "Invalid username or password"
                );
            }
        } catch (BadCredentialsException e) {
            throw new InvalidRequestException(
                    ResponseCodes.INVALID_LOGIN_CREDENTIALS_CODE,
                    "Invalid username or password"
            );
        }
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        user.setLastLogin(new Date());
        userRepository.save(user);

        LoginDTO result = new LoginDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage("Login successful");
        result.setToken(token);
        result.setUserId(user.getUserId());
        result.setUsername(user.getUsername());
        result.setUserStatus(user.getStatus());
        result.setIsSystemUser(user.getIsSystemUser());
        result.setRole(user.getRole());
        return result;
    }

    @Override
    @Transactional
    public UserRegistrationDTO register(UserRegistrationDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new InvalidRequestException(
                    ResponseCodes.USERNAME_ALREADY_EXISTS_CODE,
                    "Username already exists"
            );
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        long userCount = userRepository.count();
        if (userCount == 0) {
            user.setIsSystemUser(Constants.DB_TRUE);
            user.setRole(Role.SYSADMIN);
            user.setStatus(Constants.USER_APPROVED_STATUS);
        } else {
            user.setIsSystemUser(Constants.DB_FALSE);
            user.setRole(Role.USER);
            user.setStatus(Constants.USER_PENDING_STATUS);
        }

        Date now = new Date();
        user.setCreatedDatetime(now);
        user.setModifiedDatetime(now);
        user.setVersion(1);

        User saved = userRepository.save(user);
        return mapToResultDto(saved, "Registration successful");
    }

    private UserRegistrationDTO mapToResultDto(User user, String message) {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setStatus(ResponseStatus.SUCCESS.getStatus());
        dto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        dto.setResponseMessage(message);
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setUserStatus(user.getStatus());
        dto.setIsSystemUser(user.getIsSystemUser());
        dto.setRole(user.getRole());
        return dto;
    }

    @Override
    @Transactional
    public UserApprovalDTO approveUser(UserApprovalDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));

        if (Constants.USER_APPROVED_STATUS.equals(user.getStatus())) {
            throw new InvalidRequestException(
                    ResponseCodes.USER_ALREADY_APPROVED_CODE,
                    "User is already approved"
            );
        }

        user.setStatus(Constants.USER_APPROVED_STATUS);
        user.setModifiedDatetime(new Date());
        User saved = userRepository.save(user);
        return mapToApprovalDto(saved, "User approved successfully");
    }

    private UserApprovalDTO mapToApprovalDto(User user, String message) {
        UserApprovalDTO dto = new UserApprovalDTO();
        dto.setStatus(ResponseStatus.SUCCESS.getStatus());
        dto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        dto.setResponseMessage(message);
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setUserStatus(user.getStatus());
        return dto;
    }

    @Override
    @Transactional
    public PasswordChangeDTO changePassword(PasswordChangeDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidRequestException(
                    ResponseCodes.INVALID_CURRENT_PASSWORD_CODE,
                    "Current password is incorrect"
            );
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setModifiedDatetime(new Date());
        userRepository.save(user);
        PasswordChangeDTO result = new PasswordChangeDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage("Password changed successfully");
        return result;
    }

    @Override
    @Transactional
    public ForgetPasswordDTO forgetPassword(ForgetPasswordDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        if (Constants.DB_TRUE.equals(user.getIsSystemUser())) {
            throw new InvalidRequestException(
                    ResponseCodes.SYSTEM_USER_FORGOT_PASSWORD_NOT_ALLOWED_CODE,
                    "System user cannot use forgot password flow"
            );
        }
        user.setStatus(Constants.USER_FORGOT_PASSWORD_PENDING_STATUS);
        user.setModifiedDatetime(new Date());
        userRepository.save(user);
        ForgetPasswordDTO result = new ForgetPasswordDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage("Forgot password request submitted. Wait for admin approval.");
        return result;
    }

    @Override
    @Transactional
    public ForgotPasswordApprovalDTO approveForgotPassword(ForgotPasswordApprovalDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        if (Constants.DB_TRUE.equals(user.getIsSystemUser())) {
            throw new InvalidRequestException(
                    ResponseCodes.SYSTEM_USER_FORGOT_PASSWORD_NOT_ALLOWED_CODE,
                    "System user cannot use forgot password flow"
            );
        }
        if (!Constants.USER_FORGOT_PASSWORD_PENDING_STATUS.equals(user.getStatus())) {
            throw new InvalidRequestException(
                    ResponseCodes.FORGOT_PASSWORD_NOT_PENDING_CODE,
                    "User is not in forgot password pending status"
            );
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setStatus(Constants.USER_APPROVED_STATUS);
        user.setModifiedDatetime(new Date());
        userRepository.save(user);
        ForgotPasswordApprovalDTO result = new ForgotPasswordApprovalDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage("Password reset approved. User is now active.");
        return result;
    }
}
