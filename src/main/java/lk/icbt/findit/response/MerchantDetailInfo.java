package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;

/** Lightweight merchant info for embedding in outlet detail response. */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantDetailInfo {
    private Long merchantId;
    private String merchantName;
    private String merchantEmail;
    private String merchantNic;
    private String merchantProfileImage;
    private String merchantAddress;
    private String merchantPhoneNumber;
    private MerchantType merchantType;
    private String merchantStatus;
    private String inactiveReason;
}
