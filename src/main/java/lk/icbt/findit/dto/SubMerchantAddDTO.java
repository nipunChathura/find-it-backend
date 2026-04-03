package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MerchantType;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubMerchantAddDTO extends Response {

    private Long merchantId;
    private boolean activeOnCreate;

    private String merchantName;
    private String merchantEmail;
    private String merchantNic;
    private String merchantProfileImage;
    private String merchantAddress;
    private String merchantPhoneNumber;
    private MerchantType merchantType;

    private Long subMerchantId;
    private String subMerchantStatus;

    
    private String password;
    
    private String username;
}
