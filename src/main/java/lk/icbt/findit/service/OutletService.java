package lk.icbt.findit.service;

import lk.icbt.findit.dto.OutletAddDTO;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.response.GetAllOutletsResponse;
import lk.icbt.findit.response.DiscountListItemResponse;
import lk.icbt.findit.response.OutletAssignedItemResponse;
import lk.icbt.findit.response.OutletDetailResponse;
import lk.icbt.findit.response.OutletListItemResponse;
import lk.icbt.findit.response.OutletListResponse;
import lk.icbt.findit.response.OutletSchedulesGroupedResponse;
import lk.icbt.findit.response.PaymentListItemResponse;

import java.util.List;

public interface OutletService {

    
    List<OutletListResponse> listOutlets(String name, String status);

    
    List<OutletAssignedItemResponse> listOutletsByMerchantOrSubMerchant(Long merchantId, Long subMerchantId, String username);

    
    OutletAddDTO addOutlet(OutletAddDTO dto, String authenticatedUsername);

    
    OutletAddDTO updateOutlet(Long outletId, OutletAddDTO dto, String authenticatedUsername);

    
    OutletAddDTO updateOutletStatus(Long outletId, String status);

    
    GetAllOutletsResponse getAllOutlets(String search, String status, String outletType);

    
    OutletAddDTO approveOutlet(Long outletId, String authenticatedUsername);

    
    OutletAddDTO submitPayment(Long outletId, String authenticatedUsername);

    
    OutletAddDTO verifyPayment(Long outletId);

    
    OutletListItemResponse toListItemResponse(Outlet outlet);

    
    OutletDetailResponse getOutletDetails(Long outletId);

    
    OutletDetailResponse getOutletDetailsForMerchantApp(String username, Long outletId);

    
    List<PaymentListItemResponse> getPaymentDetailsForMerchantApp(String username, Long outletId);

    
    OutletSchedulesGroupedResponse getScheduleDetailsForMerchantApp(String username, Long outletId);

    
    List<DiscountListItemResponse> getDiscountDetailsForMerchantApp(String username, Long outletId);
}
