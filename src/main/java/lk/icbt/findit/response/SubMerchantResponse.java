package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubMerchantResponse extends Response {
    private Long subMerchantId;
    private Long merchantId;
    private String parentMerchantName;
    private String merchantName;
    private String merchantEmail;
    private String merchantNic;
    private String merchantProfileImage;
    private String merchantAddress;
    private String merchantPhoneNumber;
    private MerchantType merchantType;
    private String subMerchantStatus;
    private String profileImage;
    /** Number of outlets assigned to this sub-merchant. */
    private Long outletCount;
    /** Names of outlets assigned to this sub-merchant. */
    private List<String> outletNames;
}
