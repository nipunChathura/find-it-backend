package lk.icbt.findit.service;

import lk.icbt.findit.dto.MerchantApprovalDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.dto.MerchantStatusChangeDTO;
import lk.icbt.findit.dto.MerchantUpdateDTO;
import lk.icbt.findit.response.GetAllMerchantsResponse;
import lk.icbt.findit.response.MerchantWithOutletsResponse;

public interface MerchantService {

    
    GetAllMerchantsResponse getAllMerchantsAndSubMerchants(String search, String status, String merchantType);

    MerchantOnboardingDTO onboard(MerchantOnboardingDTO dto);

    MerchantApprovalDTO approveMerchant(MerchantApprovalDTO dto);

    
    MerchantStatusChangeDTO rejectMerchant(Long merchantId, String reason);

    MerchantUpdateDTO updateMerchant(MerchantUpdateDTO dto);

    MerchantUpdateDTO updateProfileForMerchant(String username, MerchantUpdateDTO dto);

    MerchantStatusChangeDTO changeMerchantStatus(MerchantStatusChangeDTO dto);

    
    MerchantWithOutletsResponse getMerchantWithOutlets(Long merchantId);
}
