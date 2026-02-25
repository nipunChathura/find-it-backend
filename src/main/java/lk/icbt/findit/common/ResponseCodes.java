package lk.icbt.findit.common;

public class ResponseCodes {
    public static final String SUCCESS_CODE = "00";
    public static final String FAILED_CODE = "01";

    public static final String MISSING_PARAMETER_CODE = "5100";

    public static final String INVALID_REGISTRATION_TYPE_CODE = "5201";
    public static final String USERNAME_VALIDATION_ERROR_CODE = "5202";
    public static final String PASSWORD_VALIDATION_ERROR_CODE = "5203";
    public static final String EMAIL_VALIDATION_ERROR_CODE = "5204";
    public static final String PHONE_NUMBER_VALIDATION_ERROR_CODE = "5205";

    public static final String USERNAME_ALREADY_EXISTS_CODE = "5301";
    public static final String USER_NOT_FOUND_CODE = "5302";
    public static final String USER_ALREADY_APPROVED_CODE = "5303";
    public static final String FORBIDDEN_NOT_SYSADMIN_CODE = "5304";
    public static final String INVALID_CURRENT_PASSWORD_CODE = "5305";
    public static final String FORGOT_PASSWORD_NOT_PENDING_CODE = "5306";
    public static final String SYSTEM_USER_FORGOT_PASSWORD_NOT_ALLOWED_CODE = "5307";
    public static final String INVALID_LOGIN_CREDENTIALS_CODE = "5308";
}
