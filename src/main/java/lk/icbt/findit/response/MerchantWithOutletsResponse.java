package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantWithOutletsResponse extends Response {

    private Long merchantId;
    private Long subMerchantId;
    private String parentMerchantName;
    private String username;
    private String merchantName;
    private String merchantEmail;
    private String merchantNic;
    private String merchantProfileImage;
    private String merchantAddress;
    private String merchantPhoneNumber;
    private MerchantType merchantType;
    private String merchantStatus;
    private String inactiveReason;

    /** All outlets assigned to this merchant (direct outlets; subMerchant is null). */
    private List<OutletListItemResponse> outlets;
}
