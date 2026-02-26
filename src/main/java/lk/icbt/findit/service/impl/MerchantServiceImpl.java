package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.dto.MerchantApprovalDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.dto.MerchantStatusChangeDTO;
import lk.icbt.findit.dto.MerchantUpdateDTO;
import lk.icbt.findit.entity.Merchant;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.MerchantRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MerchantOnboardingDTO onboard(MerchantOnboardingDTO dto) {
        if (merchantRepository.existsByMerchantEmail(dto.getMerchantEmail().trim())) {
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_EMAIL_ALREADY_EXISTS_CODE,
                    "Merchant with this email already exists"
            );
        }

        Merchant merchant = new Merchant();
        merchant.setMerchantName(dto.getMerchantName().trim());
        merchant.setMerchantEmail(dto.getMerchantEmail().trim().toLowerCase());
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
        return mapToDto(saved, "Merchant onboarding submitted successfully. Pending approval.");
    }

    @Override
    @Transactional
    public MerchantApprovalDTO approveMerchant(MerchantApprovalDTO dto) {
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.MERCHANT_NOT_FOUND_CODE,
                        "Merchant not found"
                ));
        if (!Constants.MERCHANT_PENDING_STATUS.equals(merchant.getStatus())) {
            throw new InvalidRequestException(
                    ResponseCodes.MERCHANT_ALREADY_APPROVED_CODE,
                    "Merchant is not in pending status or already approved"
            );
        }
        merchant.setStatus(Constants.MERCHANT_ACTIVE_STATUS);
        merchant.setModifiedDatetime(new Date());
        Merchant saved = merchantRepository.save(merchant);
        return mapToApprovalDto(saved, "Merchant approved successfully.");
    }

    @Override
    @Transactional
    public MerchantUpdateDTO updateMerchant(MerchantUpdateDTO dto) {
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.MERCHANT_NOT_FOUND_CODE,
                        "Merchant not found"
                ));
        String newEmail = dto.getMerchantEmail() != null ? dto.getMerchantEmail().trim().toLowerCase() : null;
        if (newEmail != null && merchantRepository.existsByMerchantEmailAndMerchantIdNot(newEmail, merchant.getMerchantId())) {
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
        return mapToStatusChangeDto(saved, "Merchant status updated successfully.");
    }

    private MerchantOnboardingDTO mapToDto(Merchant merchant, String message) {
        MerchantOnboardingDTO result = new MerchantOnboardingDTO();
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
