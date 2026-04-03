package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantListItemResponse {
    private String type; 
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
    private String status;
    private String inactiveReason;
}
