package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantOnboardingDTO extends Response {

    private String merchantName;
    private String merchantEmail;
    private String merchantNic;
    private String merchantProfileImage;
    private String merchantAddress;
    private String merchantPhoneNumber;
    private MerchantType merchantType;

    /** When set, onboarding is for a sub-merchant under this parent. */
    private Long parentMerchantId;

    private String username;
    private String password;

    private Long merchantId;
    private Long subMerchantId;
    private String parentMerchantName;
    private String merchantStatus;
}
