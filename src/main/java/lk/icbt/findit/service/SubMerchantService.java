package lk.icbt.findit.service;

import lk.icbt.findit.dto.SubMerchantAddDTO;
import lk.icbt.findit.dto.SubMerchantApprovalDTO;
import lk.icbt.findit.response.SubMerchantWithOutletsResponse;

public interface SubMerchantService {

    SubMerchantAddDTO addSubMerchant(SubMerchantAddDTO dto);

    SubMerchantAddDTO addSubMerchantWithAuth(SubMerchantAddDTO dto, String authenticatedUsername);

    SubMerchantApprovalDTO approveSubMerchant(Long subMerchantId, Long merchantId);

    SubMerchantApprovalDTO approveSubMerchantForMerchant(String username, Long subMerchantId);

    /**
     * Reject a pending sub-merchant (set status to INACTIVE). Sends notification to merchant and sub-merchant users.
     */
    SubMerchantApprovalDTO rejectSubMerchant(Long subMerchantId, Long merchantId, String reason);

    /** Reject sub-merchant by authenticated merchant (parent). */
    SubMerchantApprovalDTO rejectSubMerchantForMerchant(String username, Long subMerchantId, String reason);

    SubMerchantApprovalDTO updateSubMerchantStatus(Long subMerchantId, Long merchantId, String newStatus, String inactiveReason);

    SubMerchantApprovalDTO updateSubMerchantStatusForMerchant(String username, Long subMerchantId, String newStatus, String inactiveReason);

    /**
     * Get sub-merchant by ID with all assigned outlets.
     */
    SubMerchantWithOutletsResponse getSubMerchantWithOutlets(Long subMerchantId);
}
