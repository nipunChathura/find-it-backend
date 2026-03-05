package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.OutletAddDTO;
import lk.icbt.findit.entity.*;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.*;
import lk.icbt.findit.response.GetAllOutletsResponse;
import lk.icbt.findit.response.OutletListItemResponse;
import lk.icbt.findit.response.OutletListResponse;
import lk.icbt.findit.response.OutletStatusResponse;
import lk.icbt.findit.service.NotificationService;
import lk.icbt.findit.service.OutletScheduleService;
import lk.icbt.findit.service.OutletService;
import lk.icbt.findit.service.ServiceLoggingHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutletServiceImpl implements OutletService {

    private static final Logger log = LoggerFactory.getLogger(OutletServiceImpl.class);
    private static final String SERVICE_NAME = "OutletService";

    private final OutletRepository outletRepository;
    private final MerchantRepository merchantRepository;
    private final SubMerchantRepository subMerchantRepository;
    private final UserRepository userRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final OutletScheduleService outletScheduleService;
    private final NotificationService notificationService;

    @Override
    public List<OutletListResponse> listOutlets(String name, String status) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "listOutlets", "name", name, "status", status);
        String searchParam = (name != null && !name.isBlank()) ? name.trim() : "";
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : "";
        ServiceLoggingHelper.logGettingData(log, "Outlets with filters", "search", searchParam, "status", statusParam);
        List<Outlet> list = outletRepository.findAllWithFilters(searchParam, statusParam, null);
        LocalDateTime now = LocalDateTime.now();
        List<OutletListResponse> result = list.stream().map(o -> {
            String currentStatus = OutletStatusResponse.STATUS_CLOSED;
            try {
                currentStatus = outletScheduleService.getOutletStatus(o.getOutletId(), now).getStatus();
            } catch (Exception ignored) { }
            return OutletListResponse.builder()
                    .id(o.getOutletId())
                    .name(o.getOutletName())
                    .status(o.getStatus())
                    .currentStatus(currentStatus)
                    .rating(o.getRating())
                    .build();
        }).collect(Collectors.toList());
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "listOutlets", "count", result.size());
        return result;
    }

    @Override
    @Transactional
    public OutletAddDTO addOutlet(OutletAddDTO dto, String authenticatedUsername) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "addOutlet", "merchantId", dto.getMerchantId(), "outletName", dto.getOutletName());
        ServiceLoggingHelper.logGettingData(log, "Merchant by id", "merchantId", dto.getMerchantId());
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> {
                    ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_NOT_FOUND_CODE, "Merchant not found");
                    return new InvalidRequestException(
                            ResponseCodes.MERCHANT_NOT_FOUND_CODE,
                            "Merchant not found"
                    );
                });

        SubMerchant subMerchant = null;
        if (dto.getSubMerchantId() != null) {
            ServiceLoggingHelper.logGettingData(log, "SubMerchant by id and merchantId", "subMerchantId", dto.getSubMerchantId());
            subMerchant = subMerchantRepository.findBySubMerchantIdAndMerchant_MerchantId(dto.getSubMerchantId(), merchant.getMerchantId())
                    .orElseThrow(() -> {
                        ServiceLoggingHelper.logValidationError(log, ResponseCodes.SUB_MERCHANT_NOT_FOUND_CODE, "Sub-merchant not found or does not belong to this merchant");
                        return new InvalidRequestException(
                                ResponseCodes.SUB_MERCHANT_NOT_FOUND_CODE,
                                "Sub-merchant not found or does not belong to this merchant"
                        );
                    });
        }

        String outletStatus = Constants.OUTLET_PENDING_STATUS;
        if (authenticatedUsername != null && !authenticatedUsername.isBlank()) {
            User user = userRepository.findByUsername(authenticatedUsername).orElse(null);
            if (user != null && user.getRole() == Role.MERCHANT && user.getMerchantId() != null && user.getSubMerchantId() == null
                    && user.getMerchantId().equals(merchant.getMerchantId())) {
                outletStatus = Constants.OUTLET_ACTIVE_STATUS;
            }
        }

        Outlet outlet = mapDtoToOutlet(dto, merchant, subMerchant, outletStatus);
        Date now = new Date();
        outlet.setCreatedDatetime(now);
        outlet.setModifiedDatetime(now);
        outlet.setVersion(1);
        outlet.setSubscriptionValidUntil(addMonths(now, Constants.OUTLET_FREE_TRIAL_MONTHS));

        Outlet saved = outletRepository.save(outlet);
        if (Constants.OUTLET_PENDING_STATUS.equals(outletStatus)) {
            notificationService.notifyAdminsOfPendingItem("Outlet", saved.getOutletName(), "Outlet pending approval.");
        }
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "addOutlet", "outletId", saved.getOutletId());
        return mapToDto(saved, "Outlet added successfully. Status: " + outletStatus + ". Free trial until " + outlet.getSubscriptionValidUntil() + ".");
    }

    @Override
    @Transactional
    public OutletAddDTO approveOutlet(Long outletId, String authenticatedUsername) {
        if (authenticatedUsername == null || authenticatedUsername.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Not authenticated");
        }
        User user = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        if (user.getRole() != Role.MERCHANT || user.getMerchantId() == null || user.getSubMerchantId() != null) {
            throw new InvalidRequestException(
                    ResponseCodes.NOT_A_MERCHANT_USER_CODE,
                    "Only main merchant users can approve outlets"
            );
        }

        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.OUTLET_NOT_FOUND_CODE,
                        "Outlet not found"
                ));
        if (outlet.getMerchant() == null || !outlet.getMerchant().getMerchantId().equals(user.getMerchantId())) {
            throw new InvalidRequestException(
                    ResponseCodes.OUTLET_NOT_OWNED_BY_MERCHANT_CODE,
                    "Outlet does not belong to your merchant"
            );
        }
        if (Constants.OUTLET_ACTIVE_STATUS.equals(outlet.getStatus())) {
            throw new InvalidRequestException(
                    ResponseCodes.OUTLET_NOT_PENDING_CODE,
                    "Outlet is already approved"
            );
        }

        outlet.setStatus(Constants.OUTLET_ACTIVE_STATUS);
        outlet.setModifiedDatetime(new Date());
        Outlet saved = outletRepository.save(outlet);
        return mapToDto(saved, "Outlet approved successfully.");
    }

    @Override
    @Transactional
    public OutletAddDTO submitPayment(Long outletId, String authenticatedUsername) {
        if (authenticatedUsername == null || authenticatedUsername.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Not authenticated");
        }
        User user = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.USER_NOT_FOUND_CODE, "User not found"));

        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));

        boolean isOwner = false;
        if (outlet.getMerchant() != null && user.getMerchantId() != null && outlet.getMerchant().getMerchantId().equals(user.getMerchantId())) {
            if (outlet.getSubMerchant() == null && user.getSubMerchantId() == null) {
                isOwner = true;
            }
            if (outlet.getSubMerchant() != null && user.getSubMerchantId() != null && outlet.getSubMerchant().getSubMerchantId().equals(user.getSubMerchantId())) {
                isOwner = true;
            }
        }
        if (!isOwner) {
            throw new InvalidRequestException(ResponseCodes.OUTLET_NOT_OWNED_BY_MERCHANT_CODE, "Outlet does not belong to you");
        }
        if (!Constants.OUTLET_ACTIVE_STATUS.equals(outlet.getStatus()) && !Constants.OUTLET_EXPIRED_SUBSCRIPTION_STATUS.equals(outlet.getStatus())) {
            throw new InvalidRequestException(ResponseCodes.OUTLET_NOT_ELIGIBLE_FOR_PAYMENT_CODE,
                    "Only ACTIVE or EXPIRED_SUBSCRIPTION outlets can submit payment");
        }

        outlet.setStatus(Constants.OUTLET_PENDING_SUBSCRIPTION_STATUS);
        outlet.setModifiedDatetime(new Date());
        Outlet saved = outletRepository.save(outlet);
        return mapToDto(saved, "Payment submitted. Awaiting admin verification.");
    }

    @Override
    @Transactional
    public OutletAddDTO verifyPayment(Long outletId) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));
        if (!Constants.OUTLET_PENDING_SUBSCRIPTION_STATUS.equals(outlet.getStatus())) {
            throw new InvalidRequestException(ResponseCodes.OUTLET_PAYMENT_ALREADY_VERIFIED_CODE,
                    "Outlet is not in PENDING_SUBSCRIPTION status");
        }

        outlet.setStatus(Constants.OUTLET_ACTIVE_STATUS);
        Date now = new Date();
        outlet.setSubscriptionValidUntil(addMonths(now, 12));
        outlet.setModifiedDatetime(now);
        Outlet saved = outletRepository.save(outlet);
        return mapToDto(saved, "Payment verified. Outlet is active. Subscription extended by 12 months.");
    }

    @Override
    @Transactional
    public OutletAddDTO updateOutlet(Long outletId, OutletAddDTO dto, String authenticatedUsername) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));

        if (authenticatedUsername != null && !authenticatedUsername.isBlank()) {
            User user = userRepository.findByUsername(authenticatedUsername).orElse(null);
            if (user != null && user.getRole() != Role.SYSADMIN && user.getRole() != Role.ADMIN) {
                boolean isOwner = outlet.getMerchant() != null && user.getMerchantId() != null
                        && outlet.getMerchant().getMerchantId().equals(user.getMerchantId())
                        && (outlet.getSubMerchant() == null && user.getSubMerchantId() == null
                        || outlet.getSubMerchant() != null && user.getSubMerchantId() != null
                        && outlet.getSubMerchant().getSubMerchantId().equals(user.getSubMerchantId()));
                if (!isOwner) {
                    throw new InvalidRequestException(ResponseCodes.OUTLET_NOT_OWNED_BY_MERCHANT_CODE, "Outlet does not belong to you");
                }
            }
        }

        applyUpdateToOutlet(outlet, dto);
        outlet.setModifiedDatetime(new Date());
        Outlet saved = outletRepository.save(outlet);
        return mapToDto(saved, "Outlet updated successfully.");
    }

    @Override
    @Transactional
    public OutletAddDTO updateOutletStatus(Long outletId, String status) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));
        String s = status != null ? status.trim() : "";
        if (!Constants.OUTLET_ACTIVE_STATUS.equals(s) && !Constants.OUTLET_PENDING_STATUS.equals(s)
                && !Constants.OUTLET_PENDING_SUBSCRIPTION_STATUS.equals(s) && !Constants.OUTLET_EXPIRED_SUBSCRIPTION_STATUS.equals(s)) {
            throw new InvalidRequestException(ResponseCodes.OUTLET_NOT_PENDING_CODE, "Invalid outlet status");
        }
        outlet.setStatus(s);
        outlet.setModifiedDatetime(new Date());
        Outlet saved = outletRepository.save(outlet);
        return mapToDto(saved, "Outlet status updated successfully.");
    }

    @Override
    public GetAllOutletsResponse getAllOutlets(String search, String status, String outletTypeStr) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : "";
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : "";
        OutletType outletTypeParam = null;
        if (outletTypeStr != null && !outletTypeStr.isBlank()) {
            try {
                outletTypeParam = OutletType.valueOf(outletTypeStr.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) { }
        }
        List<Outlet> list = outletRepository.findAllWithFilters(searchParam, statusParam, outletTypeParam);
        List<OutletListItemResponse> items = list.stream().map(this::mapToListItem).collect(Collectors.toList());
        GetAllOutletsResponse response = new GetAllOutletsResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setResponseMessage("Success");
        response.setOutlets(items);
        return response;
    }

    private OutletListItemResponse mapToListItem(Outlet o) {
        OutletListItemResponse r = new OutletListItemResponse();
        r.setOutletId(o.getOutletId());
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

    @Override
    public OutletListItemResponse toListItemResponse(Outlet outlet) {
        return mapToListItem(outlet);
    }

    private void applyUpdateToOutlet(Outlet outlet, OutletAddDTO dto) {
        if (dto.getOutletName() != null) outlet.setOutletName(dto.getOutletName().trim());
        outlet.setBusinessRegistrationNumber(trim(dto.getBusinessRegistrationNumber()));
        outlet.setTaxIdentificationNumber(trim(dto.getTaxIdentificationNumber()));
        outlet.setPostalCode(trim(dto.getPostalCode()));
        outlet.setContactNumber(trim(dto.getContactNumber()));
        outlet.setEmailAddress(trim(dto.getEmailAddress()));
        outlet.setAddressLine1(trim(dto.getAddressLine1()));
        outlet.setAddressLine2(trim(dto.getAddressLine2()));
        if (dto.getOutletType() != null) outlet.setOutletType(dto.getOutletType());
        if (dto.getBusinessCategory() != null) outlet.setBusinessCategory(dto.getBusinessCategory());
        outlet.setLatitude(dto.getLatitude());
        outlet.setLongitude(dto.getLongitude());
        outlet.setBankName(trim(dto.getBankName()));
        outlet.setBankBranch(trim(dto.getBankBranch()));
        outlet.setAccountNumber(trim(dto.getAccountNumber()));
        outlet.setAccountHolderName(trim(dto.getAccountHolderName()));
        outlet.setRemarks(trim(dto.getRemarks()));
        outlet.setRating(dto.getRating());
        if (dto.getProvinceId() != null) {
            provinceRepository.findById(dto.getProvinceId()).ifPresent(outlet::setProvince);
        } else {
            outlet.setProvince(null);
        }
        if (dto.getDistrictId() != null) {
            districtRepository.findById(dto.getDistrictId()).ifPresent(outlet::setDistrict);
        } else {
            outlet.setDistrict(null);
        }
        if (dto.getCityId() != null) {
            cityRepository.findById(dto.getCityId()).ifPresent(outlet::setCity);
        } else {
            outlet.setCity(null);
        }
    }

    private static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    private Outlet mapDtoToOutlet(OutletAddDTO dto, Merchant merchant, SubMerchant subMerchant, String status) {
        Outlet outlet = new Outlet();
        outlet.setMerchant(merchant);
        outlet.setSubMerchant(subMerchant);
        outlet.setOutletName(dto.getOutletName() != null ? dto.getOutletName().trim() : null);
        outlet.setBusinessRegistrationNumber(trim(dto.getBusinessRegistrationNumber()));
        outlet.setTaxIdentificationNumber(trim(dto.getTaxIdentificationNumber()));
        outlet.setPostalCode(trim(dto.getPostalCode()));
        outlet.setContactNumber(trim(dto.getContactNumber()));
        outlet.setEmailAddress(trim(dto.getEmailAddress()));
        outlet.setAddressLine1(trim(dto.getAddressLine1()));
        outlet.setAddressLine2(trim(dto.getAddressLine2()));
        outlet.setOutletType(dto.getOutletType());
        outlet.setBusinessCategory(dto.getBusinessCategory());
        outlet.setLatitude(dto.getLatitude());
        outlet.setLongitude(dto.getLongitude());
        outlet.setBankName(trim(dto.getBankName()));
        outlet.setBankBranch(trim(dto.getBankBranch()));
        outlet.setAccountNumber(trim(dto.getAccountNumber()));
        outlet.setAccountHolderName(trim(dto.getAccountHolderName()));
        outlet.setRemarks(trim(dto.getRemarks()));
        outlet.setStatus(status);
        outlet.setRating(dto.getRating());

        if (dto.getProvinceId() != null) {
            provinceRepository.findById(dto.getProvinceId()).ifPresent(outlet::setProvince);
        }
        if (dto.getDistrictId() != null) {
            districtRepository.findById(dto.getDistrictId()).ifPresent(outlet::setDistrict);
        }
        if (dto.getCityId() != null) {
            cityRepository.findById(dto.getCityId()).ifPresent(outlet::setCity);
        }
        return outlet;
    }

    private static String trim(String s) {
        return s != null && !s.isBlank() ? s.trim() : null;
    }

    private OutletAddDTO mapToDto(Outlet outlet, String message) {
        OutletAddDTO dto = new OutletAddDTO();
        dto.setStatus(ResponseStatus.SUCCESS.getStatus());
        dto.setResponseCode(ResponseCodes.SUCCESS_CODE);
        dto.setResponseMessage(message);
        dto.setOutletId(outlet.getOutletId());
        dto.setMerchantId(outlet.getMerchant() != null ? outlet.getMerchant().getMerchantId() : null);
        dto.setSubMerchantId(outlet.getSubMerchant() != null ? outlet.getSubMerchant().getSubMerchantId() : null);
        dto.setOutletName(outlet.getOutletName());
        dto.setBusinessRegistrationNumber(outlet.getBusinessRegistrationNumber());
        dto.setTaxIdentificationNumber(outlet.getTaxIdentificationNumber());
        dto.setPostalCode(outlet.getPostalCode());
        dto.setProvinceId(outlet.getProvince() != null ? outlet.getProvince().getProvinceId() : null);
        dto.setDistrictId(outlet.getDistrict() != null ? outlet.getDistrict().getDistrictId() : null);
        dto.setCityId(outlet.getCity() != null ? outlet.getCity().getCityId() : null);
        dto.setContactNumber(outlet.getContactNumber());
        dto.setEmailAddress(outlet.getEmailAddress());
        dto.setAddressLine1(outlet.getAddressLine1());
        dto.setAddressLine2(outlet.getAddressLine2());
        dto.setOutletType(outlet.getOutletType());
        dto.setBusinessCategory(outlet.getBusinessCategory());
        dto.setLatitude(outlet.getLatitude());
        dto.setLongitude(outlet.getLongitude());
        dto.setBankName(outlet.getBankName());
        dto.setBankBranch(outlet.getBankBranch());
        dto.setAccountNumber(outlet.getAccountNumber());
        dto.setAccountHolderName(outlet.getAccountHolderName());
        dto.setRemarks(outlet.getRemarks());
        dto.setOutletStatus(outlet.getStatus());
        dto.setSubscriptionValidUntil(outlet.getSubscriptionValidUntil());
        dto.setRating(outlet.getRating());
        return dto;
    }
}
