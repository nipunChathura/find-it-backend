package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MainMerchantInfo {
    private Long merchantId;
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
