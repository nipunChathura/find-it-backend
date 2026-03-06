package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.MerchantApprovalDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.dto.MerchantStatusChangeDTO;
import lk.icbt.findit.dto.MerchantUpdateDTO;
import lk.icbt.findit.entity.Merchant;
import lk.icbt.findit.entity.MerchantType;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.SubMerchant;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.MerchantRepository;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.SubMerchantRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.response.GetAllMerchantsResponse;
import lk.icbt.findit.response.MerchantListItemResponse;
import lk.icbt.findit.response.MerchantWithOutletsResponse;
import lk.icbt.findit.response.OutletListItemResponse;
import lk.icbt.findit.service.MerchantService;
import lk.icbt.findit.service.NotificationService;
import lk.icbt.findit.service.OutletService;
import lk.icbt.findit.service.ServiceLoggingHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private static final Logger log = LoggerFactory.getLogger(MerchantServiceImpl.class);
    private static final String SERVICE_NAME = "MerchantService";

    private final MerchantRepository merchantRepository;
    private final SubMerchantRepository subMerchantRepository;
    private final OutletRepository outletRepository;
    private final OutletService outletService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Override
    public GetAllMerchantsResponse getAllMerchantsAndSubMerchants(String search, String status, String merchantTypeStr) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "getAllMerchantsAndSubMerchants", "search", search, "status", status);
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : "";
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : "";
        MerchantType merchantTypeParam = null;
        if (merchantTypeStr != null && !merchantTypeStr.isBlank()) {
            try {
                merchantTypeParam = MerchantType.valueOf(merchantTypeStr.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // invalid enum, leave filter off
            }
        }

        final List<Merchant> mainMerchants = new java.util.ArrayList<>(merchantRepository.findAllWithFilters(
                Constants.MERCHANT_DELETED_STATUS, searchParam, statusParam, merchantTypeParam));
        final List<SubMerchant> subMerchants = new java.util.ArrayList<>(subMerchantRepository.findAllWithFilters(
                Constants.MERCHANT_DELETED_STATUS, searchParam, statusParam, merchantTypeParam));
        if (!searchParam.isEmpty()) {
            final java.util.Set<Long> mainIdsFromSearch = mainMerchants.stream().map(Merchant::getMerchantId).collect(Collectors.toSet());
            List<Long> merchantIdsWithUsername = userRepository
                    .findByRoleAndMerchantIdNotNullAndUsernameContainingIgnoreCase(Role.MERCHANT, searchParam)
                    .stream()
                    .map(User::getMerchantId)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .toList();
            for (Long mid : merchantIdsWithUsername) {
                if (!mainIdsFromSearch.contains(mid)) {
                    java.util.Optional<Merchant> opt = merchantRepository.findById(mid);
                    if (opt.isPresent()) {
                        addMerchantIfMatches(mainMerchants, opt.get(), statusParam, merchantTypeParam);
                    }
                }
            }
            java.util.Set<Long> subIdsFromSearch = subMerchants.stream().map(SubMerchant::getSubMerchantId).collect(Collectors.toSet());
            List<Long> subMerchantIdsWithUsername = userRepository
                    .findByRoleAndSubMerchantIdNotNullAndUsernameContainingIgnoreCase(Role.SUBMERCHANT, searchParam)
                    .stream()
                    .map(User::getSubMerchantId)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .toList();
            for (Long sid : subMerchantIdsWithUsername) {
                if (!subIdsFromSearch.contains(sid)) {
                    java.util.Optional<SubMerchant> opt = subMerchantRepository.findById(sid);
                    if (opt.isPresent()) {
                        addSubMerchantIfMatches(subMerchants, opt.get(), statusParam, merchantTypeParam);
                    }
                }
            }
        }
        List<Long> mainMerchantIds = mainMerchants.stream().map(Merchant::getMerchantId).toList();
        java.util.Map<Long, String> merchantIdToUsername = mainMerchantIds.isEmpty() ? java.util.Collections.emptyMap()
                : userRepository.findByMerchantIdInAndRole(mainMerchantIds, Role.MERCHANT).stream()
                        .collect(Collectors.toMap(User::getMerchantId, User::getUsername, (a, b) -> a));

        List<MerchantListItemResponse> list = new java.util.ArrayList<>();
        mainMerchants.forEach(m -> list.add(mapToListItem(m, merchantIdToUsername.get(m.getMerchantId()))));
        subMerchants.forEach(s -> list.add(mapToListItem(s)));

        GetAllMerchantsResponse response = new GetAllMerchantsResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setResponseMessage("Success");
        response.setMerchants(list);
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "getAllMerchantsAndSubMerchants", "count", list.size());
        return response;
    }

    private static void addMerchantIfMatches(List<Merchant> list, Merchant m, String statusParam, MerchantType merchantTypeParam) {
        if (!Constants.MERCHANT_DELETED_STATUS.equals(m.getStatus())
                && (statusParam.isEmpty() || (m.getStatus() != null && m.getStatus().equals(statusParam)))
                && (merchantTypeParam == null || merchantTypeParam.equals(m.getMerchantType()))) {
            list.add(m);
        }
    }

    private static void addSubMerchantIfMatches(List<SubMerchant> list, SubMerchant s, String statusParam, MerchantType merchantTypeParam) {
        if (!Constants.MERCHANT_DELETED_STATUS.equals(s.getStatus())
                && (statusParam.isEmpty() || (s.getStatus() != null && s.getStatus().equals(statusParam)))
                && (merchantTypeParam == null || merchantTypeParam.equals(s.getMerchantType()))) {
            list.add(s);
        }
    }

    private MerchantListItemResponse mapToListItem(Merchant m, String username) {
        MerchantListItemResponse r = new MerchantListItemResponse();
        r.setType("MERCHANT");
        r.setMerchantId(m.getMerchantId());
        r.setUsername(username);
        r.setMerchantName(m.getMerchantName());
        r.setMerchantEmail(m.getMerchantEmail());
        r.setMerchantNic(m.getMerchantNic());
        r.setMerchantProfileImage(m.getMerchantProfileImage());
        r.setMerchantAddress(m.getMerchantAddress());
        r.setMerchantPhoneNumber(m.getMerchantPhoneNumber());
        r.setMerchantType(m.getMerchantType());
        r.setStatus(m.getStatus());
        r.setInactiveReason(m.getInactiveReason());
        return r;
    }

    private MerchantListItemResponse mapToListItem(lk.icbt.findit.entity.SubMerchant s) {
        MerchantListItemResponse r = new MerchantListItemResponse();
        r.setType("SUB_MERCHANT");
        r.setMerchantId(s.getMerchant() != null ? s.getMerchant().getMerchantId() : null);
        r.setSubMerchantId(s.getSubMerchantId());
        r.setParentMerchantName(s.getMerchant() != null ? s.getMerchant().getMerchantName() : null);
        r.setMerchantName(s.getMerchantName());
        r.setMerchantEmail(s.getMerchantEmail());
        r.setMerchantNic(s.getMerchantNic());
        r.setMerchantProfileImage(s.getMerchantProfileImage());
        r.setMerchantAddress(s.getMerchantAddress());
        r.setMerchantPhoneNumber(s.getMerchantPhoneNumber());
        r.setMerchantType(s.getMerchantType());
        r.setStatus(s.getStatus());
        r.setInactiveReason(s.getInactiveReason());
        return r;
    }

    @Override
    @Transactional
    public MerchantOnboardingDTO onboard(MerchantOnboardingDTO dto) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "onboard", "merchantEmail", dto.getMerchantEmail(), "parentMerchantId", dto.getParentMerchantId());
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            if (userRepository.existsByUsername(dto.getUsername().trim())) {
                ServiceLoggingHelper.logValidationError(log, ResponseCodes.USERNAME_ALREADY_EXISTS_CODE, "Username already exists");
                throw new InvalidRequestException(
                        ResponseCodes.USERNAME_ALREADY_EXISTS_CODE,
                        "Username already exists"
                );
            }
        }

        if (dto.getParentMerchantId() != null && dto.getParentMerchantId() > 0) {
            return onboardSubMerchant(dto);
        }
        return onboardMainMerchant(dto);
    }

    private MerchantOnboardingDTO onboardSubMerchant(MerchantOnboardingDTO dto) {
        ServiceLoggingHelper.logGettingData(log, "Merchant by id (parent)", "parentMerchantId", dto.getParentMerchantId());
        Merchant parent = merchantRepository.findById(dto.getParentMerchantId())
                .orElseThrow(() -> {
                    ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_NOT_FOUND_CODE, "Parent merchant not found");
                    return new InvalidRequestException(
                            ResponseCodes.MERCHANT_NOT_FOUND_CODE,
                            "Parent merchant not found"
                    );
                });
        if (merchantRepository.existsByMerchantEmail(dto.getMerchantEmail().trim())) {
            ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_EMAIL_ALREADY_EXISTS_CODE, "Merchant with this email already exists");
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_EMAIL_ALREADY_EXISTS_CODE,
                    "Merchant with this email already exists"
            );
        }
        if (subMerchantRepository.existsByMerchantEmail(dto.getMerchantEmail().trim())) {
            ServiceLoggingHelper.logValidationError(log, ResponseCodes.SUB_MERCHANT_EMAIL_ALREADY_EXISTS_CODE, "Sub-merchant with this email already exists");
            throw new InvalidRequestException(
                    ResponseCodes.SUB_MERCHANT_EMAIL_ALREADY_EXISTS_CODE,
                    "Sub-merchant with this email already exists"
            );
        }

        SubMerchant subMerchant = new SubMerchant();
        subMerchant.setMerchant(parent);
        subMerchant.setMerchantName(dto.getMerchantName().trim());
        subMerchant.setMerchantEmail(dto.getMerchantEmail().trim().toLowerCase());
        subMerchant.setMerchantNic(dto.getMerchantNic() != null ? dto.getMerchantNic().trim() : null);
        subMerchant.setMerchantProfileImage(dto.getMerchantProfileImage());
        subMerchant.setMerchantAddress(dto.getMerchantAddress().trim());
        subMerchant.setMerchantPhoneNumber(dto.getMerchantPhoneNumber().trim());
        subMerchant.setMerchantType(dto.getMerchantType());
        subMerchant.setStatus(Constants.MERCHANT_PENDING_STATUS);

        Date now = new Date();
        subMerchant.setCreatedDatetime(now);
        subMerchant.setModifiedDatetime(now);
        subMerchant.setVersion(1);

        SubMerchant saved = subMerchantRepository.save(subMerchant);


        saveSubMerchantUser(parent.getMerchantId(), saved.getSubMerchantId(), dto.getUsername(), saved.getMerchantEmail(),
                    dto.getPassword(), now);

        notificationService.notifyAdminsOfPendingItem("Sub-merchant", saved.getMerchantName(), "Sub-merchant pending approval.");
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "onboardSubMerchant", "subMerchantId", saved.getSubMerchantId());
        return mapToDto(saved, "Sub-merchant onboarding submitted successfully. Pending approval.");
    }

    /**
     * Creates and saves a user with role SUBMERCHANT for sub-merchant login after approval.
     */
    private void saveSubMerchantUser(Long parentMerchantId, Long subMerchantId, String username, String email, String plainPassword, Date now) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setEmail(email);
        user.setIsSystemUser(Constants.DB_FALSE);
        user.setRole(Role.SUBMERCHANT);
        user.setStatus(Constants.USER_PENDING_STATUS);
        user.setMerchantId(parentMerchantId);
        user.setSubMerchantId(subMerchantId);
        user.setCreatedDatetime(now);
        user.setModifiedDatetime(now);
        user.setVersion(1);
        userRepository.save(user);
    }

    private MerchantOnboardingDTO onboardMainMerchant(MerchantOnboardingDTO dto) {
        if (merchantRepository.existsByMerchantEmail(dto.getMerchantEmail().trim())) {
            ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_EMAIL_ALREADY_EXISTS_CODE, "Merchant with this email already exists");
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_EMAIL_ALREADY_EXISTS_CODE,
                    "Merchant with this email already exists"
            );
        }

        Merchant merchant = new Merchant();
        String emailLower = dto.getMerchantEmail().trim().toLowerCase();
        merchant.setMerchantName(dto.getMerchantName().trim());
        merchant.setMerchantEmail(emailLower);
        merchant.setMerchantNic(dto.getMerchantNic() != null ? dto.getMerchantNic().trim() : null);
        merchant.setMerchantProfileImage(dto.getMerchantProfileImage());
        merchant.setMerchantAddress(dto.getMerchantAddress().trim());
        merchant.setMerchantPhoneNumber(dto.getMerchantPhoneNumber().trim());
        merchant.setMerchantType(dto.getMerchantType());
        merchant.setStatus(Constants.MERCHANT_PENDING_STATUS);

        Date now = new Date();
        merchant.setCreatedDatetime(now);
        merchant.setModifiedDatetime(now);
        merchant.setVersion(1);

        Merchant saved = merchantRepository.save(merchant);

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(saved.getMerchantEmail());
        user.setIsSystemUser(Constants.DB_FALSE);
        user.setRole(Role.MERCHANT);
        user.setStatus(Constants.USER_PENDING_STATUS);
        user.setMerchantId(saved.getMerchantId());
        user.setCreatedDatetime(now);
        user.setModifiedDatetime(now);
        user.setVersion(1);
        userRepository.save(user);

        notificationService.notifyAdminsOfPendingItem("Merchant", saved.getMerchantName(), "Merchant onboarding pending approval.");
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "onboardMainMerchant", "merchantId", saved.getMerchantId());
        return mapToDto(saved, "Merchant onboarding submitted successfully. Pending approval.");
    }

    @Override
    @Transactional
    public MerchantApprovalDTO approveMerchant(MerchantApprovalDTO dto) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "approveMerchant", "merchantId", dto.getMerchantId());
        ServiceLoggingHelper.logGettingData(log, "Merchant by id", "merchantId", dto.getMerchantId());
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> {
                    ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_NOT_FOUND_CODE, "Merchant not found");
                    return new InvalidRequestException(
                            ResponseCodes.MERCHANT_NOT_FOUND_CODE,
                            "Merchant not found"
                    );
                });
        if (!Constants.MERCHANT_PENDING_STATUS.equals(merchant.getStatus())) {
            ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_ALREADY_APPROVED_CODE, "Merchant is not in pending status or already approved");
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_ALREADY_APPROVED_CODE,
                    "Merchant is not in pending status or already approved"
            );
        }
        merchant.setStatus(Constants.MERCHANT_ACTIVE_STATUS);
        merchant.setModifiedDatetime(new Date());
        Merchant saved = merchantRepository.save(merchant);

        // Update linked user(s) to ACTIVE so merchant can log in
        List<User> users = userRepository.findByMerchantIdAndRole(saved.getMerchantId(), Role.MERCHANT);
        for (User user : users) {
            user.setStatus(Constants.USER_ACTIVE_STATUS);
            user.setModifiedDatetime(new Date());
            userRepository.save(user);
        }
        List<Long> merchantUserIds = users.stream().map(User::getUserId).toList();
        notificationService.notifyUserIds(merchantUserIds, "MERCHANT_APPROVAL",
                "Merchant approved",
                "Your merchant account \"" + saved.getMerchantName() + "\" has been approved. You can now log in.");

        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "approveMerchant", "merchantId", saved.getMerchantId());
        return mapToApprovalDto(saved, "Merchant approved successfully.");
    }

    @Override
    @Transactional
    public MerchantStatusChangeDTO rejectMerchant(Long merchantId, String reason) {
        MerchantStatusChangeDTO dto = new MerchantStatusChangeDTO();
        dto.setMerchantId(merchantId);
        dto.setNewStatus(Constants.MERCHANT_INACTIVE_STATUS);
        dto.setInactiveReason(reason);
        return changeMerchantStatus(dto);
    }

    @Override
    @Transactional
    public MerchantUpdateDTO updateMerchant(MerchantUpdateDTO dto) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "updateMerchant", "merchantId", dto.getMerchantId());
        ServiceLoggingHelper.logGettingData(log, "Merchant by id", "merchantId", dto.getMerchantId());
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> {
                    ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_NOT_FOUND_CODE, "Merchant not found");
                    return new InvalidRequestException(
                            ResponseCodes.MERCHANT_NOT_FOUND_CODE,
                            "Merchant not found"
                    );
                });
        String newEmail = dto.getMerchantEmail() != null ? dto.getMerchantEmail().trim().toLowerCase() : null;
        if (newEmail != null && merchantRepository.existsByMerchantEmailAndMerchantIdNot(newEmail, merchant.getMerchantId())) {
            ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_EMAIL_ALREADY_EXISTS_CODE, "Merchant with this email already exists");
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_EMAIL_ALREADY_EXISTS_CODE,
                    "Merchant with this email already exists"
            );
        }
        if (dto.getMerchantName() != null) merchant.setMerchantName(dto.getMerchantName().trim());
        if (newEmail != null) merchant.setMerchantEmail(newEmail);
        if (dto.getMerchantNic() != null) merchant.setMerchantNic(dto.getMerchantNic().trim());
        if (dto.getMerchantProfileImage() != null) merchant.setMerchantProfileImage(dto.getMerchantProfileImage());
        if (dto.getMerchantAddress() != null) merchant.setMerchantAddress(dto.getMerchantAddress().trim());
        if (dto.getMerchantPhoneNumber() != null) merchant.setMerchantPhoneNumber(dto.getMerchantPhoneNumber().trim());
        if (dto.getMerchantType() != null) merchant.setMerchantType(dto.getMerchantType());
        merchant.setModifiedDatetime(new Date());
        Merchant saved = merchantRepository.save(merchant);
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "updateMerchant", "merchantId", saved.getMerchantId());
        return mapToUpdateDto(saved, "Merchant updated successfully.");
    }

    @Override
    @Transactional
    public MerchantUpdateDTO updateProfileForMerchant(String username, MerchantUpdateDTO dto) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE,
                        "User not found"
                ));
        if (user.getMerchantId() == null) {
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_NOT_LINKED_CODE,
                    "User is not linked to a merchant"
                );
        }
        dto.setMerchantId(user.getMerchantId());
        return updateMerchant(dto);
    }

    @Override
    @Transactional
    public MerchantStatusChangeDTO changeMerchantStatus(MerchantStatusChangeDTO dto) {
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.MERCHANT_NOT_FOUND_CODE,
                        "Merchant not found"
                ));
        String newStatus = dto.getNewStatus();
        if (newStatus == null || (!Constants.MERCHANT_ACTIVE_STATUS.equals(newStatus)
                && !Constants.MERCHANT_INACTIVE_STATUS.equals(newStatus)
                && !Constants.MERCHANT_PENDING_STATUS.equals(newStatus))) {
            throw new InvalidRequestException(
                    ResponseCodes.INVALID_MERCHANT_STATUS_CODE,
                    "Status must be ACTIVE, INACTIVE, or PENDING"
            );
        }
        merchant.setStatus(newStatus);
        merchant.setInactiveReason(Constants.MERCHANT_INACTIVE_STATUS.equals(newStatus) ? dto.getInactiveReason() : null);
        merchant.setModifiedDatetime(new Date());
        Merchant saved = merchantRepository.save(merchant);
        List<User> merchantUsers = userRepository.findByMerchantIdAndRole(saved.getMerchantId(), Role.MERCHANT);
        List<Long> merchantUserIds = merchantUsers.stream().map(User::getUserId).toList();
        if (Constants.MERCHANT_INACTIVE_STATUS.equals(newStatus)) {
            String reason = (dto.getInactiveReason() != null && !dto.getInactiveReason().isBlank()) ? " Reason: " + dto.getInactiveReason() : ".";
            notificationService.notifyUserIds(merchantUserIds, "MERCHANT_REJECTED",
                    "Merchant application not approved",
                    "Your merchant account \"" + saved.getMerchantName() + "\" has been set to inactive." + reason);
        }
        return mapToStatusChangeDto(saved, "Merchant status updated successfully.");
    }

    @Override
    public MerchantWithOutletsResponse getMerchantWithOutlets(Long merchantId) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "getMerchantWithOutlets", "merchantId", merchantId);
        ServiceLoggingHelper.logGettingData(log, "Merchant by id", "merchantId", merchantId);
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> {
                    ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_NOT_FOUND_CODE, "Merchant not found");
                    return new InvalidRequestException(ResponseCodes.MERCHANT_NOT_FOUND_CODE, "Merchant not found");
                });
        ServiceLoggingHelper.logGettingData(log, "Outlets by merchantId", "merchantId", merchantId);
        List<Outlet> outlets = outletRepository.findByMerchant_MerchantIdAndSubMerchantIsNull(merchantId);
        MerchantWithOutletsResponse response = new MerchantWithOutletsResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setMerchantId(merchant.getMerchantId());
        response.setSubMerchantId(null);
        response.setParentMerchantName(null);
        response.setUsername(userRepository.findByMerchantIdAndRole(merchantId, Role.MERCHANT).stream()
                .findFirst().map(User::getUsername).orElse(null));
        response.setMerchantName(merchant.getMerchantName());
        response.setMerchantEmail(merchant.getMerchantEmail());
        response.setMerchantNic(merchant.getMerchantNic());
        response.setMerchantProfileImage(merchant.getMerchantProfileImage());
        response.setMerchantAddress(merchant.getMerchantAddress());
        response.setMerchantPhoneNumber(merchant.getMerchantPhoneNumber());
        response.setMerchantType(merchant.getMerchantType());
        response.setMerchantStatus(merchant.getStatus());
        response.setInactiveReason(merchant.getInactiveReason());
        response.setOutlets(outlets.stream().map(outletService::toListItemResponse).collect(Collectors.toList()));
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "getMerchantWithOutlets", "merchantId", merchantId, "outletCount", outlets.size());
        return response;
    }

    private MerchantOnboardingDTO mapToDto(Merchant merchant, String message) {
        MerchantOnboardingDTO result = new MerchantOnboardingDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage(message);
        result.setMerchantId(merchant.getMerchantId());
        result.setSubMerchantId(null);
        result.setParentMerchantName(null);
        result.setUsername(userRepository.findByMerchantIdAndRole(merchant.getMerchantId(), Role.MERCHANT).stream()
                .findFirst().map(User::getUsername).orElse(null));
        result.setMerchantName(merchant.getMerchantName());
        result.setMerchantEmail(merchant.getMerchantEmail());
        result.setMerchantNic(merchant.getMerchantNic());
        result.setMerchantProfileImage(merchant.getMerchantProfileImage());
        result.setMerchantAddress(merchant.getMerchantAddress());
        result.setMerchantPhoneNumber(merchant.getMerchantPhoneNumber());
        result.setMerchantType(merchant.getMerchantType());
        result.setMerchantStatus(merchant.getStatus());
        return result;
    }

    private MerchantOnboardingDTO mapToDto(SubMerchant subMerchant, String message) {
        MerchantOnboardingDTO result = new MerchantOnboardingDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage(message);
        result.setMerchantId(subMerchant.getMerchant() != null ? subMerchant.getMerchant().getMerchantId() : null);
        result.setSubMerchantId(subMerchant.getSubMerchantId());
        result.setParentMerchantName(subMerchant.getMerchant() != null ? subMerchant.getMerchant().getMerchantName() : null);
        result.setMerchantName(subMerchant.getMerchantName());
        result.setMerchantEmail(subMerchant.getMerchantEmail());
        result.setMerchantNic(subMerchant.getMerchantNic());
        result.setMerchantProfileImage(subMerchant.getMerchantProfileImage());
        result.setMerchantAddress(subMerchant.getMerchantAddress());
        result.setMerchantPhoneNumber(subMerchant.getMerchantPhoneNumber());
        result.setMerchantType(subMerchant.getMerchantType());
        result.setMerchantStatus(subMerchant.getStatus());
        return result;
    }

    private MerchantApprovalDTO mapToApprovalDto(Merchant merchant, String message) {
        MerchantApprovalDTO result = new MerchantApprovalDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage(message);
        result.setMerchantId(merchant.getMerchantId());
        result.setMerchantName(merchant.getMerchantName());
        result.setMerchantEmail(merchant.getMerchantEmail());
        result.setMerchantNic(merchant.getMerchantNic());
        result.setMerchantProfileImage(merchant.getMerchantProfileImage());
        result.setMerchantAddress(merchant.getMerchantAddress());
        result.setMerchantPhoneNumber(merchant.getMerchantPhoneNumber());
        result.setMerchantType(merchant.getMerchantType());
        result.setMerchantStatus(merchant.getStatus());
        return result;
    }

    private MerchantUpdateDTO mapToUpdateDto(Merchant merchant, String message) {
        MerchantUpdateDTO result = new MerchantUpdateDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage(message);
        result.setMerchantId(merchant.getMerchantId());
        result.setMerchantName(merchant.getMerchantName());
        result.setMerchantEmail(merchant.getMerchantEmail());
        result.setMerchantNic(merchant.getMerchantNic());
        result.setMerchantProfileImage(merchant.getMerchantProfileImage());
        result.setMerchantAddress(merchant.getMerchantAddress());
        result.setMerchantPhoneNumber(merchant.getMerchantPhoneNumber());
        result.setMerchantType(merchant.getMerchantType());
        result.setMerchantStatus(merchant.getStatus());
        return result;
    }

    private MerchantStatusChangeDTO mapToStatusChangeDto(Merchant merchant, String message) {
        MerchantStatusChangeDTO result = new MerchantStatusChangeDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage(message);
        result.setMerchantId(merchant.getMerchantId());
        result.setMerchantName(merchant.getMerchantName());
        result.setMerchantEmail(merchant.getMerchantEmail());
        result.setMerchantStatus(merchant.getStatus());
        return result;
    }
}
