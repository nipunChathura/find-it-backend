package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Full outlet details: outlet info plus items, discounts, and payments for that outlet.
 * Used by GET /api/outlets/{outletId}/details and GET /api/merchant-app/outlets/{outletId}/details.
 * For merchant-app, merchantDetails, assignedSubMerchant and subMerchantDetails are populated.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletDetailResponse extends Response {

    private OutletListItemResponse outlet;
    private List<ItemListItemResponse> items;
    private List<DiscountListItemResponse> discounts;
    private List<PaymentListItemResponse> payments;

    /** Main merchant that owns this outlet (direct or parent of sub-merchant). Populated in merchant-app outlet details. */
    private MerchantDetailInfo merchantDetails;
    /** True if this outlet is assigned to a sub-merchant. */
    private Boolean assignedSubMerchant;
    /** Sub-merchant details when assignedSubMerchant is true. */
    private SubMerchantDetailInfo subMerchantDetails;
}
