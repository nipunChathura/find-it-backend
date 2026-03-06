package lk.icbt.findit.service;

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
import lk.icbt.findit.response.UserResponse;

import java.util.List;

public interface UserService {

    LoginDTO login(LoginDTO dto);

    UserAddDTO addUser(UserAddDTO dto);

    UserUpdateDTO updateUser(Long userId, UserUpdateDTO dto);

    UserUpdateDTO updateUserStatus(Long userId, String status);

    List<UserResponse> getAllUsers(String status, String search);

    MerchantLoginDTO loginMerchant(LoginDTO dto);

    CustomerLoginDTO loginCustomer(String email, String password);

    UserRegistrationDTO register(UserRegistrationDTO dto);

    UserApprovalDTO approveUser(UserApprovalDTO dto);

    /**
     * Reject a pending user (set status to INACTIVE).
     */
    UserUpdateDTO rejectUser(Long userId, String reason);

    PasswordChangeDTO changePassword(PasswordChangeDTO dto);

    PasswordChangeDTO changePasswordForMerchant(String username, String currentPassword, String newPassword);

    PasswordChangeDTO changePasswordForSubMerchant(String username, String currentPassword, String newPassword);

    ForgetPasswordDTO forgetPassword(ForgetPasswordDTO dto);

    ForgetPasswordDTO forgotPasswordForMerchant(String username);

    ForgetPasswordDTO forgotPasswordForSubMerchant(String username);

    ForgotPasswordApprovalDTO approveForgotPassword(ForgotPasswordApprovalDTO dto);

    /**
     * Updates the user's profile image. fileName should be the name returned by the image upload API (e.g. for type profile).
     * Stores profileImageUrl as "profile/{fileName}".
     */
    UserResponse changeProfileImage(Long userId, String fileName);
}
