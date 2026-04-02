package lk.icbt.findit.service;

import lk.icbt.findit.request.PaymentRequest;
import lk.icbt.findit.response.PaymentListItemResponse;
import lk.icbt.findit.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse create(PaymentRequest request);

    PaymentResponse getById(Long paymentId);

    List<PaymentListItemResponse> list(Long outletId, String status);

    
    List<PaymentListItemResponse> listForOutletIds(List<Long> outletIds, String status);

    PaymentResponse update(Long paymentId, PaymentRequest request);

    void delete(Long paymentId);

    
    PaymentResponse approvePayment(Long paymentId);
}
