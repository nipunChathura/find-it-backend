package lk.icbt.findit.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lk.icbt.findit.entity.BusinessCategory;
import lk.icbt.findit.entity.OutletType;
import lombok.Data;

/**
 * Request for adding an outlet. Merchant is required; sub-merchant is optional.
 */
@Data
public class OutletAddRequest {

    @NotNull(message = "Merchant ID is required")
    private Long merchantId;

    /** Optional. When set, outlet is linked to this sub-merchant (must belong to the merchant). */
    private Long subMerchantId;

    @NotNull(message = "Outlet name is required")
    @Size(max = 255)
    private String outletName;

    @Size(max = 100)
    private String businessRegistrationNumber;

    @Size(max = 100)
    private String taxIdentificationNumber;

    @Size(max = 20)
    private String postalCode;

    private Long provinceId;
    private Long districtId;
    private Long cityId;

    @Size(max = 20)
    private String contactNumber;

    @Size(max = 255)
    private String emailAddress;

    @Size(max = 500)
    private String addressLine1;

    @Size(max = 500)
    private String addressLine2;

    private OutletType outletType;
    private BusinessCategory businessCategory;

    private Double latitude;
    private Double longitude;

    @Size(max = 255)
    private String bankName;

    @Size(max = 255)
    private String bankBranch;

    @Size(max = 50)
    private String accountNumber;

    @Size(max = 255)
    private String accountHolderName;

    @Size(max = 500)
    private String remarks;

    /** Optional outlet rating (e.g. 0.0 to 5.0). */
    private Double rating;
}
