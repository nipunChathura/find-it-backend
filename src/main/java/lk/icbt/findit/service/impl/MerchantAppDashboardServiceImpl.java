package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.Payment;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.ItemRepository;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.PaymentRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.response.MerchantAppDashboardResponse;
import lk.icbt.findit.response.PaymentListItemResponse;
import lk.icbt.findit.service.MerchantAppDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantAppDashboardServiceImpl implements MerchantAppDashboardService {

    private final UserRepository userRepository;
    private final OutletRepository outletRepository;
    private final ItemRepository itemRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public MerchantAppDashboardResponse getDashboard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.USER_NOT_FOUND_CODE, "User not found"));
        if (user.getRole() != Role.MERCHANT && user.getRole() != Role.SUBMERCHANT) {
            throw new InvalidRequestException(
                    ResponseCodes.VALIDATION_ERROR_CODE, "Only merchant or sub-merchant can access dashboard");
        }

        List<Outlet> outlets;
        if (user.getRole() == Role.MERCHANT && user.getMerchantId() != null) {
            outlets = outletRepository.findAllOutletsByMerchantId(user.getMerchantId());
        } else if (user.getRole() == Role.SUBMERCHANT && user.getSubMerchantId() != null) {
            outlets = outletRepository.findBySubMerchant_SubMerchantId(user.getSubMerchantId());
        } else {
            outlets = Collections.emptyList();
        }

        List<Long> outletIds = outlets.stream().map(Outlet::getOutletId).toList();

        long totalOutletCount = outlets.size();
        long activeOutletCount = outlets.stream()
                .filter(o -> Constants.OUTLET_ACTIVE_STATUS.equals(o.getStatus()))
                .count();

        long totalItems = outletIds.isEmpty() ? 0L : itemRepository.countByOutlet_OutletIdIn(outletIds);

        List<Payment> pendingPaymentList = outletIds.isEmpty()
                ? Collections.emptyList()
                : paymentRepository.findByOutlet_OutletIdInAndStatus(outletIds, Constants.PAYMENT_PENDING_STATUS);
        long pendingPaymentCount = pendingPaymentList.size();
        List<PaymentListItemResponse> pendingPayments = pendingPaymentList.stream()
                .map(this::toPaymentListItem)
                .collect(Collectors.toList());

        MerchantAppDashboardResponse response = new MerchantAppDashboardResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setResponseMessage("Success");
        response.setTotalOutletCount(totalOutletCount);
        response.setActiveOutletCount(activeOutletCount);
        response.setTotalItems(totalItems);
        response.setPendingPaymentCount(pendingPaymentCount);
        response.setPendingPayments(pendingPayments);
        return response;
    }

    private PaymentListItemResponse toPaymentListItem(Payment p) {
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

    private static String formatDate(Date date) {
        if (date == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
