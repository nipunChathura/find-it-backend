package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletDetailResponse extends Response {

    private OutletListItemResponse outlet;
    private List<ItemListItemResponse> items;
    private List<DiscountListItemResponse> discounts;
    private List<PaymentListItemResponse> payments;

    
    private MerchantDetailInfo merchantDetails;
    
    private Boolean assignedSubMerchant;
    
    private SubMerchantDetailInfo subMerchantDetails;
}
