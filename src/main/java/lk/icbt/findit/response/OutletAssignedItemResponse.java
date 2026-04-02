package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.BusinessCategory;
import lk.icbt.findit.entity.OutletType;
import lombok.Data;

import java.util.Date;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletAssignedItemResponse {

    private Long outletId;
    private Long merchantId;
    private Long subMerchantId;
    private String merchantName;
    private String subMerchantName;
    private String outletName;
    private String businessRegistrationNumber;
    private String taxIdentificationNumber;
    private String postalCode;
    private Long provinceId;
    private Long districtId;
    private Long cityId;
    private String provinceName;
    private String districtName;
    private String cityName;
    private String contactNumber;
    private String emailAddress;
    private String addressLine1;
    private String addressLine2;
    private OutletType outletType;
    private BusinessCategory businessCategory;
    private Double latitude;
    private Double longitude;
    private String bankName;
    private String bankBranch;
    private String accountNumber;
    private String accountHolderName;
    private String remarks;
    private String status;
    private Date subscriptionValidUntil;
    private Double rating;

    
    private String currentStatus;

    
    private Long itemCount;

    
    private SubMerchantInfo subMerchantInfo;
}
