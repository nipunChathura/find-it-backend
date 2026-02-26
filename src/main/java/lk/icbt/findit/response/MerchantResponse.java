package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response for merchant API endpoints.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantResponse extends Response {
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
