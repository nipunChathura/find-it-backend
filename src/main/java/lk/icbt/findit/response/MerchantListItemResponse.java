package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;

/**
 * Single item in get-all-merchants list. type is MERCHANT (main) or SUB_MERCHANT.
 * For SUB_MERCHANT, parentMerchantName and merchantId (parent id) are set.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantListItemResponse {
    private String type; // "MERCHANT" or "SUB_MERCHANT"
    private Long merchantId;       // main merchant id (for MERCHANT) or parent merchant id (for SUB_MERCHANT)
    private Long subMerchantId;    // only for SUB_MERCHANT
    private String parentMerchantName; // only for SUB_MERCHANT
    private String username;       // only for MERCHANT (main)
    private String merchantName;
    private String merchantEmail;
    private String merchantNic;
    private String merchantProfileImage;
    private String merchantAddress;
    private String merchantPhoneNumber;
    private MerchantType merchantType;
    private String status;
    private String inactiveReason;
}
