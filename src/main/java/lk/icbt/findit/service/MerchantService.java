package lk.icbt.findit.service;

import lk.icbt.findit.dto.MerchantApprovalDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.dto.MerchantStatusChangeDTO;
import lk.icbt.findit.dto.MerchantUpdateDTO;
import lk.icbt.findit.response.GetAllMerchantsResponse;
import lk.icbt.findit.response.MerchantWithOutletsResponse;

public interface MerchantService {

    /**
     * Get all main merchants and sub-merchants from both tables, with optional search (matches name, email, or username) and filters (status, merchantType).
     * Pass null or blank for any param to omit that filter.
     */
    GetAllMerchantsResponse getAllMerchantsAndSubMerchants(String search, String status, String merchantType);

    MerchantOnboardingDTO onboard(MerchantOnboardingDTO dto);

    MerchantApprovalDTO approveMerchant(MerchantApprovalDTO dto);

    /**
     * Reject a pending merchant (set status to INACTIVE). Sends notification to merchant users.
     */
    MerchantStatusChangeDTO rejectMerchant(Long merchantId, String reason);

    MerchantUpdateDTO updateMerchant(MerchantUpdateDTO dto);

    MerchantUpdateDTO updateProfileForMerchant(String username, MerchantUpdateDTO dto);

    MerchantStatusChangeDTO changeMerchantStatus(MerchantStatusChangeDTO dto);

    /**
     * Get merchant by ID with all assigned outlets (direct outlets only; subMerchant is null).
     */
    MerchantWithOutletsResponse getMerchantWithOutlets(Long merchantId);
}
