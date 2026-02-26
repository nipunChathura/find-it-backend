package lk.icbt.findit.service;

import lk.icbt.findit.dto.MerchantApprovalDTO;
import lk.icbt.findit.dto.MerchantOnboardingDTO;
import lk.icbt.findit.dto.MerchantStatusChangeDTO;
import lk.icbt.findit.dto.MerchantUpdateDTO;

public interface MerchantService {

    MerchantOnboardingDTO onboard(MerchantOnboardingDTO dto);

    MerchantApprovalDTO approveMerchant(MerchantApprovalDTO dto);

    MerchantUpdateDTO updateMerchant(MerchantUpdateDTO dto);

    MerchantUpdateDTO updateProfileForMerchant(String username, MerchantUpdateDTO dto);

    MerchantStatusChangeDTO changeMerchantStatus(MerchantStatusChangeDTO dto);
}
