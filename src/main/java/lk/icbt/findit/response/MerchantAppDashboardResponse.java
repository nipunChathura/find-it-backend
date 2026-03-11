package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Merchant app dashboard data: outlet counts, total items, pending payments, and lists of outlets, payments, sub-merchants.
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
    /** Outlets belonging to the merchant (or sub-merchant). */
    private List<OutletListItemResponse> outlets;
    /** All payments for the merchant/sub-merchant outlets. */
    private List<PaymentListItemResponse> payments;
    /** Sub-merchants under the main merchant (MERCHANT role only; empty for SUBMERCHANT). */
    private List<SubMerchantResponse> subMerchants;
}
