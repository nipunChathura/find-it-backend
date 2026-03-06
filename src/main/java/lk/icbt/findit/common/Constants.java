package lk.icbt.findit.common;

public class Constants {

    public final static String SYSTEM_NAME = "find-it";
    public final static String TIME_ZONE = "Asia/Colombo";

    public final static String CUSTOMER_REGISTRATION_TYPE = "CUSTOMER_REGISTRATION";
    public final static String MERCHANT_REGISTRATION_TYPE = "MERCHANT_REGISTRATION";

    // user status
    public final static String USER_ACTIVE_STATUS = "ACTIVE";
    public final static String USER_INACTIVE_STATUS = "INACTIVE";
    public final static String USER_PENDING_STATUS = "PENDING";
    public final static String USER_APPROVED_STATUS = "APPROVED";
    public final static String USER_DELETED_STATUS = "DELETED";
    public final static String USER_FORGOT_PASSWORD_PENDING_STATUS = "FORGOT_PASSWORD_PENDING";

    // customer status
    public final static String CUSTOMER_ACTIVE_STATUS = "ACTIVE";
    public final static String CUSTOMER_INACTIVE_STATUS = "INACTIVE";

    // merchant status
    public final static String MERCHANT_ACTIVE_STATUS = "ACTIVE";
    public final static String MERCHANT_INACTIVE_STATUS = "INACTIVE";
    public final static String MERCHANT_PENDING_STATUS = "PENDING";
    public final static String MERCHANT_DELETED_STATUS = "DELETED";

    // category status
    public final static String CATEGORY_ACTIVE_STATUS = "ACTIVE";
    public final static String CATEGORY_INACTIVE_STATUS = "INACTIVE";
    public final static String CATEGORY_DELETED_STATUS = "DELETED";

    // outlet status
    public final static String OUTLET_ACTIVE_STATUS = "ACTIVE";
    public final static String OUTLET_PENDING_STATUS = "PENDING";
    public final static String OUTLET_PENDING_SUBSCRIPTION_STATUS = "PENDING_SUBSCRIPTION";
    public final static String OUTLET_EXPIRED_SUBSCRIPTION_STATUS = "EXPIRED_SUBSCRIPTION";

    // outlet schedule status
    public static final String SCHEDULE_ACTIVE_STATUS = "ACTIVE";
    public static final String SCHEDULE_DELETED_STATUS = "DELETED";

    // discount status
    public static final String DISCOUNT_ACTIVE_STATUS = "ACTIVE";
    public static final String DISCOUNT_INACTIVE_STATUS = "INACTIVE";
    public static final String DISCOUNT_DELETED_STATUS = "DELETED";

    // item status
    public static final String ITEM_ACTIVE_STATUS = "ACTIVE";
    public static final String ITEM_INACTIVE_STATUS = "INACTIVE";
    public static final String ITEM_DELETED_STATUS = "DELETED";

    // payment status
    public static final String PAYMENT_PENDING_STATUS = "PENDING";
    public static final String PAYMENT_APPROVED_STATUS = "APPROVED";
    public static final String PAYMENT_REJECTED_STATUS = "REJECTED";

    /** Free trial months from outlet registration. */
    public static final int OUTLET_FREE_TRIAL_MONTHS = 3;

    // DB boolean value
    public static final String DB_TRUE = "Y";
    public static final String DB_FALSE = "N";
}
