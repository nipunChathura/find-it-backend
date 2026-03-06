package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Merchant app dashboard data: outlet counts, total items, and pending payments.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantAppDashboardResponse extends Response {

    private Long totalOutletCount;
    private Long activeOutletCount;
    private Long totalItems;
    private Long pendingPaymentCount;
    /** List of pending payments for the merchant/sub-merchant outlets. */
    private List<PaymentListItemResponse> pendingPayments;
}
