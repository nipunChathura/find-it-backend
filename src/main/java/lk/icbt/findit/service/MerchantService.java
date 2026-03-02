package lk.icbt.findit.service;

import lk.icbt.findit.dto.MerchantApprovalDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.dto.MerchantStatusChangeDTO;
import lk.icbt.findit.dto.MerchantUpdateDTO;
import lk.icbt.findit.response.GetAllMerchantsResponse;
import lk.icbt.findit.response.MerchantWithOutletsResponse;

public interface MerchantService {

    GetAllMerchantsResponse getAllMerchantsAndSubMerchants();

    /**
     * Get all main merchants and sub-merchants with optional search (name, email, username) and filters (status, merchantType).
     * Pass null or blank for any param to omit that filter.
     */
    GetAllMerchantsResponse getAllMerchantsAndSubMerchants(String name, String email, String username, String status, String merchantType);

    MerchantOnboardingDTO onboard(MerchantOnboardingDTO dto);

    MerchantApprovalDTO approveMerchant(MerchantApprovalDTO dto);

    MerchantUpdateDTO updateMerchant(MerchantUpdateDTO dto);

    MerchantUpdateDTO updateProfileForMerchant(String username, MerchantUpdateDTO dto);

    MerchantStatusChangeDTO changeMerchantStatus(MerchantStatusChangeDTO dto);

    /**
     * Get merchant by ID with all assigned outlets (direct outlets only; subMerchant is null).
     */
    MerchantWithOutletsResponse getMerchantWithOutlets(Long merchantId);
}
