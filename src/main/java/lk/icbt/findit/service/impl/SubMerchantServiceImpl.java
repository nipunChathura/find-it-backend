package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.SubMerchantAddDTO;
import lk.icbt.findit.dto.SubMerchantApprovalDTO;
import lk.icbt.findit.entity.Merchant;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.SubMerchant;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.MerchantRepository;
import lk.icbt.findit.repository.SubMerchantRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.service.SubMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SubMerchantServiceImpl implements SubMerchantService {

    private final SubMerchantRepository subMerchantRepository;
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubMerchantAddDTO addSubMerchant(SubMerchantAddDTO dto) {
        if (dto.getMerchantId() == null) {
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_ID_REQUIRED_CODE,
                    "Merchant ID is required"
            );
        }
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.MERCHANT_NOT_FOUND_CODE,
                        "Merchant not found"
                ));
        if (subMerchantRepository.existsByMerchantEmail(dto.getMerchantEmail().trim())) {
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
        String message = dto.isActiveOnCreate()
                ? "Sub-merchant added successfully. Status: ACTIVE."
                : "Sub-merchant added successfully. Pending approval.";
        return mapToDto(saved, message);
    }

    @Override
    @Transactional
    public SubMerchantAddDTO addSubMerchantWithAuth(SubMerchantAddDTO dto, String authenticatedUsername) {
        if (authenticatedUsername != null && !authenticatedUsername.isBlank()) {
            User user = userRepository.findByUsername(authenticatedUsername)
                    .orElseThrow(() -> new InvalidRequestException(
                            ResponseCodes.USER_NOT_FOUND_CODE,
                            "User not found"
                    ));
            if (user.getRole() == Role.MERCHANT && user.getMerchantId() != null && user.getSubMerchantId() == null) {
                dto.setMerchantId(user.getMerchantId());
                dto.setActiveOnCreate(true);
                return addSubMerchant(dto);
            }
        }
        if (dto.getMerchantId() == null) {
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_ID_REQUIRED_CODE,
                    "Merchant ID is required for admin"
            );
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
        return mapToApprovalDto(saved, "Sub-merchant approved successfully.");
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
