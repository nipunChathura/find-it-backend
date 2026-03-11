package lk.icbt.findit.service;

import lk.icbt.findit.request.PaymentRequest;
import lk.icbt.findit.response.PaymentListItemResponse;
import lk.icbt.findit.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse create(PaymentRequest request);

    PaymentResponse getById(Long paymentId);

    List<PaymentListItemResponse> list(Long outletId, String status);

    /** List payments for the given outlet IDs (for merchant app). Optional status filter. */
    List<PaymentListItemResponse> listForOutletIds(List<Long> outletIds, String status);

    PaymentResponse update(Long paymentId, PaymentRequest request);

    void delete(Long paymentId);

    /**
     * Admin approves a payment (status → APPROVED) and activates outlet / extends subscription if applicable.
     */
    PaymentResponse approvePayment(Long paymentId);
}
