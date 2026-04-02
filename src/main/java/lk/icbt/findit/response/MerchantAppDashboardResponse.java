package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantAppDashboardResponse extends Response {

    private Long totalOutletCount;
    private Long activeOutletCount;
    private Long totalItems;
    private Long pendingPaymentCount;
    
    private List<PaymentListItemResponse> pendingPayments;
    
    private List<OutletListItemResponse> outlets;
    
    private List<PaymentListItemResponse> payments;
    
    private List<SubMerchantResponse> subMerchants;
}
