package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.dto.CustomerLoginDTO;
import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.ForgotPasswordApprovalDTO;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.MerchantLoginDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.dto.UserAddDTO;
import lk.icbt.findit.dto.UserApprovalDTO;
import lk.icbt.findit.dto.UserUpdateDTO;
import lk.icbt.findit.dto.UserRegistrationDTO;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.response.UserResponse;
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
import java.util.List;
import java.util.stream.Collectors;

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
        if (Constants.USER_APPROVED_STATUS.equals(user.getStatus())) {
            user.setStatus(Constants.USER_ACTIVE_STATUS);
        }
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
        result.setProfileImageUrl(user.getProfileImageUrl());
        return result;
    }

    @Override
    @Transactional
    public MerchantLoginDTO loginMerchant(LoginDTO dto) {
        LoginDTO loginResult = login(dto);
        if (loginResult.getRole() != Role.MERCHANT && loginResult.getRole() != Role.SUBMERCHANT) {
            throw new InvalidRequestException(
                    ResponseCodes.NOT_MERCHANT_OR_SUB_MERCHANT_CODE,
                    "Only merchant and sub-merchant users can use this login"
            );
        }
        User user = userRepository.findByUsername(loginResult.getUsername())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        MerchantLoginDTO result = new MerchantLoginDTO();
        result.setStatus(loginResult.getStatus());
        result.setResponseCode(loginResult.getResponseCode());
        result.setResponseMessage(loginResult.getResponseMessage());
        result.setToken(loginResult.getToken());
        result.setUserId(loginResult.getUserId());
        result.setUsername(loginResult.getUsername());
        result.setUserStatus(loginResult.getUserStatus());
        result.setRole(loginResult.getRole());
        result.setMerchantId(user.getMerchantId());
        result.setSubMerchantId(user.getSubMerchantId());
        return result;
    }

    @Override
    @Transactional
    public CustomerLoginDTO loginCustomer(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new InvalidRequestException(
                    ResponseCodes.INVALID_LOGIN_CREDENTIALS_CODE,
                    "Invalid email or password"
            );
        }
        User user = userRepository.findByEmailIgnoreCaseAndRole(email.trim().toLowerCase(), Role.CUSTOMER)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.INVALID_LOGIN_CREDENTIALS_CODE,
                        "Invalid email or password"
                ));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidRequestException(
                    ResponseCodes.INVALID_LOGIN_CREDENTIALS_CODE,
                    "Invalid email or password"
            );
        }
        if (Constants.USER_DELETED_STATUS.equals(user.getStatus())) {
            throw new InvalidRequestException(
                    ResponseCodes.INVALID_LOGIN_CREDENTIALS_CODE,
                    "Invalid email or password"
            );
        }
        if (Constants.USER_APPROVED_STATUS.equals(user.getStatus())) {
            user.setStatus(Constants.USER_ACTIVE_STATUS);
        }
        user.setLastLogin(new Date());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        CustomerLoginDTO result = new CustomerLoginDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage("Login successful");
        result.setToken(token);
        result.setUserId(user.getUserId());
        result.setEmail(user.getEmail());
        result.setUsername(user.getUsername());
        result.setUserStatus(user.getStatus());
        result.setRole(user.getRole());
        result.setCustomerId(user.getCustomerId());
        return result;
    }

    @Override
    @Transactional
    public UserAddDTO addUser(UserAddDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername().trim())) {
            throw new InvalidRequestException(
                    ResponseCodes.USERNAME_ALREADY_EXISTS_CODE,
                    "Username already exists"
            );
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && userRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new InvalidRequestException(
                    ResponseCodes.EMAIL_ALREADY_EXISTS_CODE,
                    "Email already exists"
            );
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail() != null && !dto.getEmail().isBlank()
                ? dto.getEmail().trim().toLowerCase() : null);
        user.setIsSystemUser(Constants.DB_FALSE);
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus() != null && !dto.getStatus().isBlank()
                ? dto.getStatus() : Constants.USER_PENDING_STATUS);
        user.setMerchantId(dto.getMerchantId());
        user.setSubMerchantId(dto.getSubMerchantId());

        Date now = new Date();
        user.setCreatedDatetime(now);
        user.setModifiedDatetime(now);
        user.setVersion(1);

        User saved = userRepository.save(user);

        UserAddDTO result = new UserAddDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage("User created successfully.");
        result.setUserId(saved.getUserId());
        result.setUsername(saved.getUsername());
        result.setEmail(saved.getEmail());
        result.setUserStatus(saved.getStatus());
        result.setIsSystemUser(saved.getIsSystemUser());
        result.setRole(saved.getRole());
        result.setMerchantId(saved.getMerchantId());
        result.setSubMerchantId(saved.getSubMerchantId());
        return result;
    }

    @Override
    @Transactional
    public UserUpdateDTO updateUser(Long userId, UserUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            String newUsername = dto.getUsername().trim();
            if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
                throw new InvalidRequestException(
                        ResponseCodes.USERNAME_ALREADY_EXISTS_CODE,
                        "Username already exists"
                );
            }
            user.setUsername(newUsername);
        }
        if (dto.getEmail() != null) {
            String newEmail = dto.getEmail().isBlank() ? null : dto.getEmail().trim().toLowerCase();
            if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new InvalidRequestException(
                        ResponseCodes.EMAIL_ALREADY_EXISTS_CODE,
                        "Email already exists"
                );
            }
            user.setEmail(newEmail);
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getStatus() != null && (Constants.USER_ACTIVE_STATUS.equals(dto.getStatus())
                || Constants.USER_INACTIVE_STATUS.equals(dto.getStatus())
                || Constants.USER_PENDING_STATUS.equals(dto.getStatus())
                || Constants.USER_APPROVED_STATUS.equals(dto.getStatus())
                || Constants.USER_DELETED_STATUS.equals(dto.getStatus()))) {
            user.setStatus(dto.getStatus());
        }
        if (dto.getMerchantId() != null) {
            user.setMerchantId(dto.getMerchantId());
        }
        if (dto.getSubMerchantId() != null) {
            user.setSubMerchantId(dto.getSubMerchantId());
        }

        user.setModifiedDatetime(new Date());
        User saved = userRepository.save(user);

        UserUpdateDTO result = new UserUpdateDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage("User updated successfully.");
        result.setUserId(saved.getUserId());
        result.setUsername(saved.getUsername());
        result.setEmail(saved.getEmail());
        result.setUserStatus(saved.getStatus());
        result.setIsSystemUser(saved.getIsSystemUser());
        result.setRole(saved.getRole());
        result.setMerchantId(saved.getMerchantId());
        result.setSubMerchantId(saved.getSubMerchantId());
        return result;
    }

    @Override
    @Transactional
    public UserUpdateDTO updateUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        String newStatus = status != null ? status.trim() : null;
        if (newStatus == null || !(Constants.USER_ACTIVE_STATUS.equals(newStatus)
                || Constants.USER_INACTIVE_STATUS.equals(newStatus)
                || Constants.USER_PENDING_STATUS.equals(newStatus)
                || Constants.USER_APPROVED_STATUS.equals(newStatus)
                || Constants.USER_DELETED_STATUS.equals(newStatus))) {
            throw new InvalidRequestException(
                    ResponseCodes.INVALID_USER_STATUS_CODE,
                    "Status must be ACTIVE, INACTIVE, PENDING, APPROVED, or DELETED"
            );
        }
        user.setStatus(newStatus);
        user.setModifiedDatetime(new Date());
        User saved = userRepository.save(user);

        UserUpdateDTO result = new UserUpdateDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage("User status updated successfully.");
        result.setUserId(saved.getUserId());
        result.setUsername(saved.getUsername());
        result.setEmail(saved.getEmail());
        result.setUserStatus(saved.getStatus());
        result.setIsSystemUser(saved.getIsSystemUser());
        result.setRole(saved.getRole());
        result.setMerchantId(saved.getMerchantId());
        result.setSubMerchantId(saved.getSubMerchantId());
        return result;
    }

    @Override
    public List<UserResponse> getAllUsers(String status, String search) {
        String statusFilter = (status != null && !status.isBlank()) ? status.trim() : null;
        String searchTerm = (search != null && !search.isBlank()) ? search.trim().toLowerCase() : null;

        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != Role.MERCHANT && user.getRole() != Role.SUBMERCHANT)
                .filter(user -> statusFilter == null
                        || (user.getStatus() != null && user.getStatus().equalsIgnoreCase(statusFilter)))
                .filter(user -> searchTerm == null
                        || (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchTerm))
                        || (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchTerm)))
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapUserToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setUserStatus(user.getStatus());
        response.setIsSystemUser(user.getIsSystemUser());
        response.setRole(user.getRole());
        response.setMerchantId(user.getMerchantId());
        response.setSubMerchantId(user.getSubMerchantId());
        response.setCreatedDatetime(user.getCreatedDatetime());
        response.setProfileImageUrl(user.getProfileImageUrl());
        return response;
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
            user.setRole(Role.ADMIN);
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
        dto.setProfileImageUrl(user.getProfileImageUrl());
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
    public PasswordChangeDTO changePasswordForMerchant(String username, String currentPassword, String newPassword) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        if (user.getMerchantId() == null || user.getSubMerchantId() != null) {
            throw new InvalidRequestException(
                    ResponseCodes.NOT_A_MERCHANT_USER_CODE,
                    "Not a merchant user. Use merchant password APIs only for main merchant accounts."
            );
        }
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setUsername(username);
        dto.setCurrentPassword(currentPassword);
        dto.setNewPassword(newPassword);
        return changePassword(dto);
    }

    @Override
    @Transactional
    public PasswordChangeDTO changePasswordForSubMerchant(String username, String currentPassword, String newPassword) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        if (user.getSubMerchantId() == null) {
            throw new InvalidRequestException(
                    ResponseCodes.NOT_A_SUB_MERCHANT_USER_CODE,
                    "Not a sub-merchant user. Use sub-merchant password APIs only for sub-merchant accounts."
            );
        }
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setUsername(username);
        dto.setCurrentPassword(currentPassword);
        dto.setNewPassword(newPassword);
        return changePassword(dto);
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
    public ForgetPasswordDTO forgotPasswordForMerchant(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "Username is required");
        }
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        if (user.getRole() != Role.MERCHANT || user.getMerchantId() == null || user.getSubMerchantId() != null) {
            throw new InvalidRequestException(
                    ResponseCodes.NOT_A_MERCHANT_USER_CODE,
                    "Only main merchant users can use this endpoint."
            );
        }
        ForgetPasswordDTO dto = new ForgetPasswordDTO();
        dto.setUsername(user.getUsername());
        return forgetPassword(dto);
    }

    @Override
    @Transactional
    public ForgetPasswordDTO forgotPasswordForSubMerchant(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "Username is required");
        }
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        if (user.getSubMerchantId() == null) {
            throw new InvalidRequestException(
                    ResponseCodes.NOT_A_SUB_MERCHANT_USER_CODE,
                    "Only sub-merchant users can use this endpoint."
            );
        }
        ForgetPasswordDTO dto = new ForgetPasswordDTO();
        dto.setUsername(user.getUsername());
        return forgetPassword(dto);
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
