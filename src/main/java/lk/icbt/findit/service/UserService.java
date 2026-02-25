package lk.icbt.findit.service;

import lk.icbt.findit.dto.ForgetPasswordDTO;
import lk.icbt.findit.dto.ForgotPasswordApprovalDTO;
import lk.icbt.findit.dto.LoginDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.dto.UserApprovalDTO;
import lk.icbt.findit.dto.UserRegistrationDTO;

public interface UserService {

    LoginDTO login(LoginDTO dto);

    UserRegistrationDTO register(UserRegistrationDTO dto);

    UserApprovalDTO approveUser(UserApprovalDTO dto);

    PasswordChangeDTO changePassword(PasswordChangeDTO dto);

    ForgetPasswordDTO forgetPassword(ForgetPasswordDTO dto);

    ForgotPasswordApprovalDTO approveForgotPassword(ForgotPasswordApprovalDTO dto);
}
