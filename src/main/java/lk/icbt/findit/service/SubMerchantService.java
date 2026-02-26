package lk.icbt.findit.service;

import lk.icbt.findit.dto.SubMerchantAddDTO;
import lk.icbt.findit.dto.SubMerchantApprovalDTO;

public interface SubMerchantService {

    SubMerchantAddDTO addSubMerchant(SubMerchantAddDTO dto);

    SubMerchantAddDTO addSubMerchantWithAuth(SubMerchantAddDTO dto, String authenticatedUsername);

    SubMerchantApprovalDTO approveSubMerchant(Long subMerchantId, Long merchantId);

    SubMerchantApprovalDTO approveSubMerchantForMerchant(String username, Long subMerchantId);

    SubMerchantApprovalDTO updateSubMerchantStatus(Long subMerchantId, Long merchantId, String newStatus, String inactiveReason);

    SubMerchantApprovalDTO updateSubMerchantStatusForMerchant(String username, Long subMerchantId, String newStatus, String inactiveReason);
}
