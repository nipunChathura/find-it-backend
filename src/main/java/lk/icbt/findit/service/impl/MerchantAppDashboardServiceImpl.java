package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.*;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.ItemRepository;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.PaymentRepository;
import lk.icbt.findit.repository.SubMerchantRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.response.MerchantAppDashboardResponse;
import lk.icbt.findit.response.OutletListItemResponse;
import lk.icbt.findit.response.PaymentListItemResponse;
import lk.icbt.findit.response.SubMerchantResponse;
import lk.icbt.findit.service.MerchantAppDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantAppDashboardServiceImpl implements MerchantAppDashboardService {

    private final UserRepository userRepository;
    private final OutletRepository outletRepository;
    private final ItemRepository itemRepository;
    private final PaymentRepository paymentRepository;
    private final SubMerchantRepository subMerchantRepository;

    @Override
    public MerchantAppDashboardResponse getDashboard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.USER_NOT_FOUND_CODE, "User not found"));
        if (user.getRole() != Role.MERCHANT && user.getRole() != Role.SUBMERCHANT) {
            throw new InvalidRequestException(
                    ResponseCodes.VALIDATION_ERROR_CODE, "Only merchant or sub-merchant can access dashboard");
        }

        // MERCHANT: outlets by merchant_id column; SUBMERCHANT: outlets by sub_merchant_id column
        List<Outlet> outlets;
        if (user.getRole() == Role.MERCHANT && user.getMerchantId() != null) {
            outlets = outletRepository.findByMerchant_MerchantId(user.getMerchantId());
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

        // Item count: exclude DELETED status
        long totalItems = outletIds.isEmpty() ? 0L : itemRepository.countByOutlet_OutletIdInAndStatusNot(outletIds, Constants.ITEM_DELETED_STATUS);

        List<Payment> pendingPaymentList = outletIds.isEmpty()
                ? Collections.emptyList()
                : paymentRepository.findByOutlet_OutletIdInAndStatus(outletIds, Constants.PAYMENT_PENDING_STATUS);
        long pendingPaymentCount = pendingPaymentList.size();
        List<PaymentListItemResponse> pendingPayments = pendingPaymentList.stream()
                .map(this::toPaymentListItem)
                .collect(Collectors.toList());

        List<PaymentListItemResponse> allPayments = outletIds.isEmpty()
                ? Collections.emptyList()
                : paymentRepository.findByOutlet_OutletIdInOrderByPaymentIdDesc(outletIds).stream()
                .map(this::toPaymentListItem)
                .collect(Collectors.toList());

        Map<Long, Long> outletIdToItemCount = outletIds.isEmpty() ? Collections.emptyMap() : itemRepository
                .countByOutletIdInGroupByOutletId(outletIds, Constants.ITEM_DELETED_STATUS).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).longValue()));

        List<OutletListItemResponse> outletList = outlets.stream()
                .map(o -> toOutletListItem(o, outletIdToItemCount))
                .collect(Collectors.toList());
        // Dashboard outlet section: show only first 2 outlets
        int maxOutletsOnDashboard = 2;
        if (outletList.size() > maxOutletsOnDashboard) {
            outletList = new ArrayList<>(outletList.subList(0, maxOutletsOnDashboard));
        }

        List<SubMerchantResponse> subMerchantList = Collections.emptyList();
        if (user.getRole() == Role.MERCHANT && user.getMerchantId() != null) {
            subMerchantList = subMerchantRepository.findByMerchant_MerchantId(user.getMerchantId()).stream()
                    .map(this::toSubMerchantResponse)
                    .collect(Collectors.toList());
        }

        MerchantAppDashboardResponse response = new MerchantAppDashboardResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setResponseMessage("Success");
        response.setTotalOutletCount(totalOutletCount);
        response.setActiveOutletCount(activeOutletCount);
        response.setTotalItems(totalItems);
        response.setPendingPaymentCount(pendingPaymentCount);
        response.setPendingPayments(pendingPayments);
        response.setOutlets(outletList);
        response.setPayments(allPayments);
        response.setSubMerchants(subMerchantList);
        return response;
    }

    private OutletListItemResponse toOutletListItem(Outlet o, Map<Long, Long> outletIdToItemCount) {
        OutletListItemResponse r = new OutletListItemResponse();
        r.setOutletId(o.getOutletId());
        r.setItemCount(outletIdToItemCount.getOrDefault(o.getOutletId(), 0L));
        r.setMerchantId(o.getMerchant() != null ? o.getMerchant().getMerchantId() : null);
        r.setSubMerchantId(o.getSubMerchant() != null ? o.getSubMerchant().getSubMerchantId() : null);
        r.setMerchantName(o.getMerchant() != null ? o.getMerchant().getMerchantName() : null);
        r.setSubMerchantName(o.getSubMerchant() != null ? o.getSubMerchant().getMerchantName() : null);
        r.setOutletName(o.getOutletName());
        r.setBusinessRegistrationNumber(o.getBusinessRegistrationNumber());
        r.setTaxIdentificationNumber(o.getTaxIdentificationNumber());
        r.setPostalCode(o.getPostalCode());
        r.setProvinceId(o.getProvince() != null ? o.getProvince().getProvinceId() : null);
        r.setDistrictId(o.getDistrict() != null ? o.getDistrict().getDistrictId() : null);
        r.setCityId(o.getCity() != null ? o.getCity().getCityId() : null);
        r.setProvinceName(o.getProvince() != null ? o.getProvince().getName() : null);
        r.setDistrictName(o.getDistrict() != null ? o.getDistrict().getName() : null);
        r.setCityName(o.getCity() != null ? o.getCity().getName() : null);
        r.setContactNumber(o.getContactNumber());
        r.setEmailAddress(o.getEmailAddress());
        r.setAddressLine1(o.getAddressLine1());
        r.setAddressLine2(o.getAddressLine2());
        r.setOutletType(o.getOutletType());
        r.setBusinessCategory(o.getBusinessCategory());
        r.setLatitude(o.getLatitude());
        r.setLongitude(o.getLongitude());
        r.setBankName(o.getBankName());
        r.setBankBranch(o.getBankBranch());
        r.setAccountNumber(o.getAccountNumber());
        r.setAccountHolderName(o.getAccountHolderName());
        r.setRemarks(o.getRemarks());
        r.setStatus(o.getStatus());
        r.setSubscriptionValidUntil(o.getSubscriptionValidUntil());
        r.setRating(o.getRating());
        return r;
    }

    private SubMerchantResponse toSubMerchantResponse(SubMerchant s) {
        SubMerchantResponse r = new SubMerchantResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setResponseMessage("Success");
        r.setSubMerchantId(s.getSubMerchantId());
        r.setMerchantId(s.getMerchant() != null ? s.getMerchant().getMerchantId() : null);
        r.setParentMerchantName(s.getMerchant() != null ? s.getMerchant().getMerchantName() : null);
        r.setMerchantName(s.getMerchantName());
        r.setMerchantEmail(s.getMerchantEmail());
        r.setMerchantNic(s.getMerchantNic());
        r.setMerchantProfileImage(s.getMerchantProfileImage());
        r.setMerchantAddress(s.getMerchantAddress());
        r.setMerchantPhoneNumber(s.getMerchantPhoneNumber());
        r.setMerchantType(s.getMerchantType());
        r.setSubMerchantStatus(s.getStatus());
        return r;
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
