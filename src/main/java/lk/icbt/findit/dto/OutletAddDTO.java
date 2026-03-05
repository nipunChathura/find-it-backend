package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.BusinessCategory;
import lk.icbt.findit.entity.OutletType;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletAddDTO extends Response {

    private Long merchantId;
    private Long subMerchantId;
    private String outletName;
    private String businessRegistrationNumber;
    private String taxIdentificationNumber;
    private String postalCode;
    private Long provinceId;
    private Long districtId;
    private Long cityId;
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

    private Long outletId;
    private String outletStatus;
    private java.util.Date subscriptionValidUntil;
    private Double rating;
}
