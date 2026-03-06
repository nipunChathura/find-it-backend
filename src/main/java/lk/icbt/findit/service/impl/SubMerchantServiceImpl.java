package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.SubMerchantAddDTO;
import lk.icbt.findit.dto.SubMerchantApprovalDTO;
import lk.icbt.findit.entity.Merchant;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.SubMerchant;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.MerchantRepository;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.SubMerchantRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.response.SubMerchantWithOutletsResponse;
import lk.icbt.findit.service.NotificationService;
import lk.icbt.findit.service.OutletService;
import lk.icbt.findit.service.ServiceLoggingHelper;
import lk.icbt.findit.service.SubMerchantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubMerchantServiceImpl implements SubMerchantService {

    private static final Logger log = LoggerFactory.getLogger(SubMerchantServiceImpl.class);
    private static final String SERVICE_NAME = "SubMerchantService";

    private final SubMerchantRepository subMerchantRepository;
    private final MerchantRepository merchantRepository;
    private final OutletRepository outletRepository;
    private final OutletService outletService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public SubMerchantAddDTO addSubMerchant(SubMerchantAddDTO dto) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "addSubMerchant", "merchantId", dto.getMerchantId(), "email", dto.getMerchantEmail());
        if (dto.getMerchantId() == null) {
            ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_ID_REQUIRED_CODE, "Merchant ID is required");
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_ID_REQUIRED_CODE,
                    "Merchant ID is required"
            );
        }
        ServiceLoggingHelper.logGettingData(log, "Merchant by id", "merchantId", dto.getMerchantId());
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> {
                    ServiceLoggingHelper.logValidationError(log, ResponseCodes.MERCHANT_NOT_FOUND_CODE, "Merchant not found");
                    return new InvalidRequestException(
                            ResponseCodes.MERCHANT_NOT_FOUND_CODE,
                            "Merchant not found"
                    );
                });
        if (subMerchantRepository.existsByMerchantEmail(dto.getMerchantEmail().trim())) {
            ServiceLoggingHelper.logValidationError(log, ResponseCodes.SUB_MERCHANT_EMAIL_ALREADY_EXISTS_CODE, "Sub-merchant with this email already exists");
            throw new InvalidRequestException(
                    ResponseCodes.SUB_MERCHANT_EMAIL_ALREADY_EXISTS_CODE,
                    "Sub-merchant with this email already exists"
            );
        }

        SubMerchant subMerchant = new SubMerchant();
        subMerchant.setMerchant(merchant);
        subMerchant.setMerchantName(dto.getMerchantName().trim());
        subMerchant.setMerchantEmail(dto.getMerchantEmail().trim().toLowerCase());
        subMerchant.setMerchantNic(dto.getMerchantNic() != null ? dto.getMerchantNic().trim() : null);
        subMerchant.setMerchantProfileImage(dto.getMerchantProfileImage());
        subMerchant.setMerchantAddress(dto.getMerchantAddress().trim());
        subMerchant.setMerchantPhoneNumber(dto.getMerchantPhoneNumber().trim());
        subMerchant.setMerchantType(dto.getMerchantType());
        subMerchant.setStatus(dto.isActiveOnCreate() ? Constants.MERCHANT_ACTIVE_STATUS : Constants.MERCHANT_PENDING_STATUS);

        Date now = new Date();
        subMerchant.setCreatedDatetime(now);
        subMerchant.setModifiedDatetime(now);
        subMerchant.setVersion(1);

        SubMerchant saved = subMerchantRepository.save(subMerchant);
        if (Constants.MERCHANT_PENDING_STATUS.equals(saved.getStatus())) {
            notificationService.notifyAdminsOfPendingItem("Sub-merchant", saved.getMerchantName(), "Sub-merchant pending approval.");
        }
        String message = dto.isActiveOnCreate()
                ? "Sub-merchant added successfully. Status: ACTIVE."
                : "Sub-merchant added successfully. Pending approval.";
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "addSubMerchant", "subMerchantId", saved.getSubMerchantId());
        return mapToDto(saved, message);
    }

    @Override
    @Transactional
    public SubMerchantAddDTO addSubMerchantWithAuth(SubMerchantAddDTO dto, String authenticatedUsername) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "addSubMerchantWithAuth", "merchantId", dto.getMerchantId(), "username", authenticatedUsername);
        if (dto.getMerchantId() == null) {
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_ID_REQUIRED_CODE,
                    "Merchant ID is required"
            );
        }
        if (authenticatedUsername != null && !authenticatedUsername.isBlank()) {
            User user = userRepository.findByUsername(authenticatedUsername)
                    .orElseThrow(() -> new InvalidRequestException(
                            ResponseCodes.USER_NOT_FOUND_CODE,
                            "User not found"
                    ));
            if (user.getRole() == Role.MERCHANT && user.getMerchantId() != null && user.getSubMerchantId() == null) {
                if (!dto.getMerchantId().equals(user.getMerchantId())) {
                    throw new InvalidRequestException(
                            ResponseCodes.MERCHANT_NOT_LINKED_CODE,
                            "Merchant ID must match your account"
                    );
                }
                dto.setActiveOnCreate(true);
                return addSubMerchant(dto);
            }
        }
        dto.setActiveOnCreate(false);
        return addSubMerchant(dto);
    }

    @Override
    @Transactional
    public SubMerchantApprovalDTO approveSubMerchant(Long subMerchantId, Long merchantId) {
        SubMerchant subMerchant = subMerchantRepository.findBySubMerchantIdAndMerchant_MerchantId(subMerchantId, merchantId)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.SUB_MERCHANT_NOT_FOUND_CODE,
                        "Sub-merchant not found or you do not have permission to approve it"
                ));
        if (!Constants.MERCHANT_PENDING_STATUS.equals(subMerchant.getStatus())) {
            throw new InvalidRequestException(
                    ResponseCodes.SUB_MERCHANT_NOT_PENDING_CODE,
                    "Sub-merchant is not in pending status"
            );
        }
        subMerchant.setStatus(Constants.MERCHANT_ACTIVE_STATUS);
        subMerchant.setModifiedDatetime(new Date());
        SubMerchant saved = subMerchantRepository.save(subMerchant);

        List<Long> userIdsToNotify = new ArrayList<>();
        Long parentMerchantId = saved.getMerchant() != null ? saved.getMerchant().getMerchantId() : null;
        if (parentMerchantId != null) {
            userIdsToNotify.addAll(userRepository.findByMerchantIdAndRole(parentMerchantId, Role.MERCHANT).stream().map(User::getUserId).toList());
        }
        userIdsToNotify.addAll(userRepository.findBySubMerchantIdAndRole(saved.getSubMerchantId(), Role.SUBMERCHANT).stream().map(User::getUserId).toList());
        notificationService.notifyUserIds(userIdsToNotify, "SUB_MERCHANT_APPROVAL",
                "Sub-merchant approved",
                "Sub-merchant \"" + saved.getMerchantName() + "\" has been approved successfully.");

        return mapToApprovalDto(saved, "Sub-merchant approved successfully.");
    }

    @Override
    @Transactional
    public SubMerchantApprovalDTO rejectSubMerchant(Long subMerchantId, Long merchantId, String reason) {
        return updateSubMerchantStatus(subMerchantId, merchantId, Constants.MERCHANT_INACTIVE_STATUS, reason);
    }

    @Override
    @Transactional
    public SubMerchantApprovalDTO approveSubMerchantForMerchant(String username, Long subMerchantId) {
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
        return approveSubMerchant(subMerchantId, user.getMerchantId());
    }

    @Override
    @Transactional
    public SubMerchantApprovalDTO rejectSubMerchantForMerchant(String username, Long subMerchantId, String reason) {
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
        return rejectSubMerchant(subMerchantId, user.getMerchantId(), reason);
    }

    @Override
    @Transactional
    public SubMerchantApprovalDTO updateSubMerchantStatus(Long subMerchantId, Long merchantId, String newStatus, String inactiveReason) {
        SubMerchant subMerchant = subMerchantRepository.findBySubMerchantIdAndMerchant_MerchantId(subMerchantId, merchantId)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.SUB_MERCHANT_NOT_FOUND_CODE,
                        "Sub-merchant not found or you do not have permission to update it"
                ));
        if (newStatus == null || (!Constants.MERCHANT_ACTIVE_STATUS.equals(newStatus)
                && !Constants.MERCHANT_INACTIVE_STATUS.equals(newStatus)
                && !Constants.MERCHANT_PENDING_STATUS.equals(newStatus))) {
            throw new InvalidRequestException(
                    ResponseCodes.INVALID_MERCHANT_STATUS_CODE,
                    "Status must be ACTIVE, INACTIVE, or PENDING"
            );
        }
        subMerchant.setStatus(newStatus);
        subMerchant.setInactiveReason(Constants.MERCHANT_INACTIVE_STATUS.equals(newStatus) ? inactiveReason : null);
        subMerchant.setModifiedDatetime(new Date());
        SubMerchant saved = subMerchantRepository.save(subMerchant);

        if (Constants.MERCHANT_INACTIVE_STATUS.equals(newStatus)) {
            List<Long> userIdsToNotify = new ArrayList<>();
            Long parentMerchantId = saved.getMerchant() != null ? saved.getMerchant().getMerchantId() : null;
            if (parentMerchantId != null) {
                userIdsToNotify.addAll(userRepository.findByMerchantIdAndRole(parentMerchantId, Role.MERCHANT).stream().map(User::getUserId).toList());
            }
            userIdsToNotify.addAll(userRepository.findBySubMerchantIdAndRole(saved.getSubMerchantId(), Role.SUBMERCHANT).stream().map(User::getUserId).toList());
            String reason = (inactiveReason != null && !inactiveReason.isBlank()) ? " Reason: " + inactiveReason : ".";
            notificationService.notifyUserIds(userIdsToNotify, "SUB_MERCHANT_REJECTED",
                    "Sub-merchant not approved",
                    "Sub-merchant \"" + saved.getMerchantName() + "\" has been set to inactive." + reason);
        }

        return mapToApprovalDto(saved, "Sub-merchant status updated successfully.");
    }

    @Override
    @Transactional
    public SubMerchantApprovalDTO updateSubMerchantStatusForMerchant(String username, Long subMerchantId, String newStatus, String inactiveReason) {
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
        return updateSubMerchantStatus(subMerchantId, user.getMerchantId(), newStatus, inactiveReason);
    }

    @Override
    public SubMerchantWithOutletsResponse getSubMerchantWithOutlets(Long subMerchantId) {
        SubMerchant subMerchant = subMerchantRepository.findById(subMerchantId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.SUB_MERCHANT_NOT_FOUND_CODE, "Sub-merchant not found"));
        List<Outlet> outlets = outletRepository.findBySubMerchant_SubMerchantId(subMerchantId);
        SubMerchantWithOutletsResponse response = new SubMerchantWithOutletsResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setSubMerchantId(subMerchant.getSubMerchantId());
        response.setMerchantId(subMerchant.getMerchant() != null ? subMerchant.getMerchant().getMerchantId() : null);
        response.setParentMerchantName(subMerchant.getMerchant() != null ? subMerchant.getMerchant().getMerchantName() : null);
        response.setMerchantName(subMerchant.getMerchantName());
        response.setMerchantEmail(subMerchant.getMerchantEmail());
        response.setMerchantNic(subMerchant.getMerchantNic());
        response.setMerchantProfileImage(subMerchant.getMerchantProfileImage());
        response.setMerchantAddress(subMerchant.getMerchantAddress());
        response.setMerchantPhoneNumber(subMerchant.getMerchantPhoneNumber());
        response.setMerchantType(subMerchant.getMerchantType());
        response.setSubMerchantStatus(subMerchant.getStatus());
        response.setOutlets(outlets.stream().map(outletService::toListItemResponse).collect(Collectors.toList()));
        return response;
    }

    private SubMerchantAddDTO mapToDto(SubMerchant subMerchant, String message) {
        SubMerchantAddDTO result = new SubMerchantAddDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage(message);
        result.setSubMerchantId(subMerchant.getSubMerchantId());
        result.setMerchantId(subMerchant.getMerchant().getMerchantId());
        result.setMerchantName(subMerchant.getMerchantName());
        result.setMerchantEmail(subMerchant.getMerchantEmail());
        result.setMerchantNic(subMerchant.getMerchantNic());
        result.setMerchantProfileImage(subMerchant.getMerchantProfileImage());
        result.setMerchantAddress(subMerchant.getMerchantAddress());
        result.setMerchantPhoneNumber(subMerchant.getMerchantPhoneNumber());
        result.setMerchantType(subMerchant.getMerchantType());
        result.setSubMerchantStatus(subMerchant.getStatus());
        return result;
    }

    private SubMerchantApprovalDTO mapToApprovalDto(SubMerchant subMerchant, String message) {
        SubMerchantApprovalDTO result = new SubMerchantApprovalDTO();
        result.setStatus(ResponseStatus.SUCCESS.getStatus());
        result.setResponseCode(ResponseCodes.SUCCESS_CODE);
        result.setResponseMessage(message);
        result.setSubMerchantId(subMerchant.getSubMerchantId());
        result.setMerchantId(subMerchant.getMerchant().getMerchantId());
        result.setMerchantName(subMerchant.getMerchantName());
        result.setMerchantEmail(subMerchant.getMerchantEmail());
        result.setMerchantNic(subMerchant.getMerchantNic());
        result.setMerchantProfileImage(subMerchant.getMerchantProfileImage());
        result.setMerchantAddress(subMerchant.getMerchantAddress());
        result.setMerchantPhoneNumber(subMerchant.getMerchantPhoneNumber());
        result.setMerchantType(subMerchant.getMerchantType());
        result.setSubMerchantStatus(subMerchant.getStatus());
        return result;
    }
}
