package lk.icbt.findit.service;

import lk.icbt.findit.dto.SubMerchantAddDTO;
import lk.icbt.findit.dto.SubMerchantApprovalDTO;
import lk.icbt.findit.response.SubMerchantResponse;
import lk.icbt.findit.response.SubMerchantWithOutletsResponse;

import java.util.List;

public interface SubMerchantService {

    SubMerchantAddDTO addSubMerchant(SubMerchantAddDTO dto);

    SubMerchantAddDTO addSubMerchantWithAuth(SubMerchantAddDTO dto, String authenticatedUsername);

    SubMerchantApprovalDTO approveSubMerchant(Long subMerchantId, Long merchantId);

    SubMerchantApprovalDTO approveSubMerchantForMerchant(String username, Long subMerchantId);

    
    SubMerchantApprovalDTO rejectSubMerchant(Long subMerchantId, Long merchantId, String reason);

    
    SubMerchantApprovalDTO rejectSubMerchantForMerchant(String username, Long subMerchantId, String reason);

    SubMerchantApprovalDTO updateSubMerchantStatus(Long subMerchantId, Long merchantId, String newStatus, String inactiveReason);

    SubMerchantApprovalDTO updateSubMerchantStatusForMerchant(String username, Long subMerchantId, String newStatus, String inactiveReason);

    
    SubMerchantWithOutletsResponse getSubMerchantWithOutlets(Long subMerchantId);

    
    List<SubMerchantResponse> listByMerchantUsername(String username, String nameSearch);

    
    List<SubMerchantResponse> listByMerchantId(Long merchantId, String username);
}
