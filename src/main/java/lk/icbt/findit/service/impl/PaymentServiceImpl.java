package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.Payment;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.PaymentRepository;
import lk.icbt.findit.request.PaymentRequest;
import lk.icbt.findit.response.PaymentListItemResponse;
import lk.icbt.findit.response.PaymentResponse;
import lk.icbt.findit.service.OutletService;
import lk.icbt.findit.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutletRepository outletRepository;
    private final OutletService outletService;

    @Override
    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        Outlet outlet = outletRepository.findById(request.getOutletId())
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));

        Payment payment = new Payment();
        payment.setOutlet(outlet);
        payment.setPaymentType(trim(request.getPaymentType()));
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(toDate(request.getPaymentDate()));
        payment.setPaidMonth(request.getPaidMonth());
        payment.setReceiptImage(request.getReceiptImage());
        String paymentStatus = request.getStatus() != null && !request.getStatus().isBlank()
                ? request.getStatus().trim()
                : Constants.PAYMENT_PENDING_STATUS;
        payment.setStatus(paymentStatus);

        Date now = new Date();
        payment.setCreatedDatetime(now);
        payment.setModifiedDatetime(now);
        payment.setVersion(1);

        Payment saved = paymentRepository.save(payment);

        
        applyOutletSubscriptionAndStatus(outlet, saved, paymentStatus, now);

        return toResponse(paymentRepository.findById(saved.getPaymentId()).orElse(saved), "Payment created successfully.");
    }

    
    private void applyOutletSubscriptionAndStatus(Outlet outlet, Payment payment, String paymentStatus, Date now) {
        boolean isApproved = Constants.PAYMENT_APPROVED_STATUS.equalsIgnoreCase(paymentStatus)
                || "ACTIVE".equalsIgnoreCase(paymentStatus);

        if (isApproved) {
            Date currentSubEnd = outlet.getSubscriptionValidUntil();
            Date baseDate = (currentSubEnd != null && currentSubEnd.after(now)) ? currentSubEnd : now;
            outlet.setSubscriptionValidUntil(addMonths(baseDate, 1));
        }
        

        Date paidDate = payment.getPaymentDate() != null ? payment.getPaymentDate() : now;
        Date todayStart = startOfDay(now);
        Date subEnd = outlet.getSubscriptionValidUntil();

        if (subEnd != null) {
            if (!subEnd.before(todayStart)) {
                outlet.setStatus(Constants.OUTLET_ACTIVE_STATUS);
            } else if (subEnd.before(paidDate)) {
                outlet.setStatus(Constants.OUTLET_PENDING_SUBSCRIPTION_STATUS);
            } else {
                outlet.setStatus(Constants.OUTLET_EXPIRED_SUBSCRIPTION_STATUS);
            }
        }
        outlet.setModifiedDatetime(now);
        outletRepository.save(outlet);
    }

    private static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    private static Date startOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public PaymentResponse getById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.PAYMENT_NOT_FOUND_CODE, "Payment not found"));
        return toResponse(payment, null);
    }

    @Override
    public List<PaymentListItemResponse> list(Long outletId, String status) {
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : null;
        List<Payment> list = paymentRepository.findAllWithFilters(outletId, statusParam);
        if (list.isEmpty()) return Collections.emptyList();
        return list.stream().map(this::toListItem).collect(Collectors.toList());
    }

    @Override
    public List<PaymentListItemResponse> listForOutletIds(List<Long> outletIds, String status) {
        if (outletIds == null || outletIds.isEmpty()) return Collections.emptyList();
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : null;
        List<Payment> list = statusParam != null
                ? paymentRepository.findByOutlet_OutletIdInAndStatus(outletIds, statusParam)
                : paymentRepository.findByOutlet_OutletIdInOrderByPaymentIdDesc(outletIds);
        if (list.isEmpty()) return Collections.emptyList();
        return list.stream().map(this::toListItem).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponse update(Long paymentId, PaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.PAYMENT_NOT_FOUND_CODE, "Payment not found"));

        if (request.getOutletId() != null) {
            Outlet outlet = outletRepository.findById(request.getOutletId())
                    .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));
            payment.setOutlet(outlet);
        }
        if (request.getPaymentType() != null) payment.setPaymentType(request.getPaymentType().trim());
        if (request.getAmount() != null) payment.setAmount(request.getAmount());
        if (request.getPaymentDate() != null) payment.setPaymentDate(toDate(request.getPaymentDate()));
        if (request.getPaidMonth() != null) payment.setPaidMonth(request.getPaidMonth());
        if (request.getReceiptImage() != null) payment.setReceiptImage(request.getReceiptImage());
        if (request.getStatus() != null && !request.getStatus().isBlank()) payment.setStatus(request.getStatus().trim());

        payment.setModifiedDatetime(new Date());
        Payment saved = paymentRepository.save(payment);
        return toResponse(saved, "Payment updated successfully.");
    }

    @Override
    @Transactional
    public void delete(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.PAYMENT_NOT_FOUND_CODE, "Payment not found"));
        paymentRepository.delete(payment);
    }

    @Override
    @Transactional
    public PaymentResponse approvePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.PAYMENT_NOT_FOUND_CODE, "Payment not found"));
        if (Constants.PAYMENT_APPROVED_STATUS.equals(payment.getStatus())) {
            return toResponse(payment, "Payment already approved.");
        }
        payment.setStatus(Constants.PAYMENT_APPROVED_STATUS);
        payment.setModifiedDatetime(new Date());
        paymentRepository.save(payment);

        Outlet outlet = payment.getOutlet();
        if (outlet != null && Constants.OUTLET_PENDING_SUBSCRIPTION_STATUS.equals(outlet.getStatus())) {
            outletService.verifyPayment(outlet.getOutletId());
        }
        return toResponse(paymentRepository.findById(paymentId).orElse(payment), "Payment approved successfully.");
    }

    private static String trim(String s) {
        return s != null ? s.trim() : null;
    }

    private static Date toDate(java.time.LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }

    private static String formatDate(Date date) {
        if (date == null) return null;
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private PaymentResponse toResponse(Payment p, String message) {
        PaymentResponse r = new PaymentResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setResponseMessage(message);
        r.setPaymentId(p.getPaymentId());
        r.setOutletId(p.getOutlet() != null ? p.getOutlet().getOutletId() : null);
        r.setOutletName(p.getOutlet() != null ? p.getOutlet().getOutletName() : null);
        r.setPaymentType(p.getPaymentType());
        r.setAmount(p.getAmount());
        r.setPaymentDate(formatDate(p.getPaymentDate()));
        r.setPaidMonth(p.getPaidMonth());
        r.setReceiptImage(p.getReceiptImage());
        r.setPaymentStatus(p.getStatus());
        return r;
    }

    private PaymentListItemResponse toListItem(Payment p) {
        PaymentListItemResponse r = new PaymentListItemResponse();
        r.setPaymentId(p.getPaymentId());
        r.setOutletId(p.getOutlet() != null ? p.getOutlet().getOutletId() : null);
        r.setOutletName(p.getOutlet() != null ? p.getOutlet().getOutletName() : null);
        r.setPaymentType(p.getPaymentType());
        r.setAmount(p.getAmount());
        r.setPaymentDate(formatDate(p.getPaymentDate()));
        r.setPaidMonth(p.getPaidMonth());
        r.setReceiptImage(p.getReceiptImage());
        r.setPaymentStatus(p.getStatus());
        return r;
    }
}
