package lk.icbt.findit.common;

public class ResponseCodes {
    public static final String SUCCESS_CODE = "00";
    public static final String FAILED_CODE = "01";

    /** Request body validation failed (e.g. @Valid). */
    public static final String VALIDATION_ERROR_CODE = "5000";
    public static final String MISSING_PARAMETER_CODE = "5100";

    public static final String INVALID_REGISTRATION_TYPE_CODE = "5201";
    public static final String USERNAME_VALIDATION_ERROR_CODE = "5202";
    public static final String PASSWORD_VALIDATION_ERROR_CODE = "5203";
    public static final String EMAIL_VALIDATION_ERROR_CODE = "5204";
    public static final String PHONE_NUMBER_VALIDATION_ERROR_CODE = "5205";

    public static final String USERNAME_ALREADY_EXISTS_CODE = "5301";
    public static final String EMAIL_ALREADY_EXISTS_CODE = "5309";
    public static final String USER_NOT_FOUND_CODE = "5302";
    public static final String USER_ALREADY_APPROVED_CODE = "5303";
    public static final String INVALID_USER_STATUS_CODE = "5310";
    public static final String FORBIDDEN_NOT_SYSADMIN_CODE = "5304";
    public static final String INVALID_CURRENT_PASSWORD_CODE = "5305";
    public static final String FORGOT_PASSWORD_NOT_PENDING_CODE = "5306";
    public static final String SYSTEM_USER_FORGOT_PASSWORD_NOT_ALLOWED_CODE = "5307";
    public static final String INVALID_LOGIN_CREDENTIALS_CODE = "5308";
    public static final String MERCHANT_EMAIL_ALREADY_EXISTS_CODE = "5401";
    public static final String MERCHANT_NOT_FOUND_CODE = "5402";
    public static final String MERCHANT_ALREADY_APPROVED_CODE = "5403";
    public static final String INVALID_MERCHANT_STATUS_CODE = "5404";
    public static final String MERCHANT_NOT_LINKED_CODE = "5405";
    public static final String SUB_MERCHANT_EMAIL_ALREADY_EXISTS_CODE = "5501";
    public static final String MERCHANT_ID_REQUIRED_CODE = "5502";
    public static final String SUB_MERCHANT_NOT_FOUND_CODE = "5503";
    public static final String SUB_MERCHANT_NOT_PENDING_CODE = "5504";
    public static final String SUB_MERCHANT_NOT_OWNED_CODE = "5505";
    public static final String NOT_A_MERCHANT_USER_CODE = "5506";
    public static final String NOT_A_SUB_MERCHANT_USER_CODE = "5507";
    public static final String NOT_MERCHANT_OR_SUB_MERCHANT_CODE = "5508";
    /** Merchant or sub-merchant status is not active; login not allowed. */
    public static final String MERCHANT_STATUS_NOT_ACTIVE_CODE = "5509";
    public static final String CATEGORY_NOT_FOUND_CODE = "5601";
    public static final String OUTLET_NOT_FOUND_CODE = "5701";
    public static final String OUTLET_NOT_PENDING_CODE = "5702";
    public static final String OUTLET_NOT_OWNED_BY_MERCHANT_CODE = "5703";
    public static final String OUTLET_NOT_ELIGIBLE_FOR_PAYMENT_CODE = "5704";
    public static final String OUTLET_PAYMENT_ALREADY_VERIFIED_CODE = "5705";
    public static final String ITEM_NOT_FOUND_CODE = "5801";
    public static final String DISCOUNT_NOT_FOUND_CODE = "5901";
    public static final String PAYMENT_NOT_FOUND_CODE = "6001";
    public static final String CUSTOMER_NOT_FOUND_CODE = "6101";
    public static final String CUSTOMER_EMAIL_ALREADY_EXISTS_CODE = "6102";
    public static final String SEARCH_HISTORY_NOT_FOUND_CODE = "6201";
    public static final String NOTIFICATION_NOT_FOUND_CODE = "6301";
    public static final String FIREBASE_MESSAGING_ERROR_CODE = "6302";
    public static final String IMAGE_UPLOAD_INVALID_TYPE_CODE = "6401";
    public static final String IMAGE_UPLOAD_INVALID_FILE_CODE = "6402";
    public static final String FEEDBACK_NOT_FOUND_CODE = "6501";
    public static final String CUSTOMER_FAVORITE_NOT_FOUND_CODE = "6601";
    public static final String CUSTOMER_FAVORITE_ALREADY_EXISTS_CODE = "6602";
}
