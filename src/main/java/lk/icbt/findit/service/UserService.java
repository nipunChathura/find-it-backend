package lk.icbt.findit.service;

import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.ForgotPasswordApprovalDTO;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.MerchantLoginDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.dto.UserApprovalDTO;
import lk.icbt.findit.dto.UserRegistrationDTO;

public interface UserService {

    LoginDTO login(LoginDTO dto);

    MerchantLoginDTO loginMerchant(LoginDTO dto);

    UserRegistrationDTO register(UserRegistrationDTO dto);

    UserApprovalDTO approveUser(UserApprovalDTO dto);

    PasswordChangeDTO changePassword(PasswordChangeDTO dto);

    PasswordChangeDTO changePasswordForMerchant(String username, String currentPassword, String newPassword);

    PasswordChangeDTO changePasswordForSubMerchant(String username, String currentPassword, String newPassword);

    ForgetPasswordDTO forgetPassword(ForgetPasswordDTO dto);

    ForgetPasswordDTO forgotPasswordForMerchant(String username);

    ForgetPasswordDTO forgotPasswordForSubMerchant(String username);

    ForgotPasswordApprovalDTO approveForgotPassword(ForgotPasswordApprovalDTO dto);
}
