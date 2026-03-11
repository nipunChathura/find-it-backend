package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;

/**
 * Sub-merchant details in login response. Used in subMerchantInfo.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubMerchantInfo {
    private Long subMerchantId;
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
