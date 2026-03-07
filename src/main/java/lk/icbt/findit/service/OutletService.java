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

    /**
     * List outlets with optional name and status filter. Includes current open/closed status from schedule.
     * GET /api/outlets?name=&status=
     */
    List<OutletListResponse> listOutlets(String name, String status);

    /**
     * List outlets by merchant or sub-merchant with full details. Pass merchantId to get all outlets assigned to that merchant
     * (direct outlets + all sub-merchant outlets). Pass subMerchantId to get only outlets assigned to that sub-merchant.
     * Each item includes full outlet details, currentStatus (OPEN/CLOSED), and subMerchantInfo when outlet has a sub-merchant.
     * SYSADMIN/ADMIN may pass any id; MERCHANT may only pass their own merchantId; SUBMERCHANT only their subMerchantId.
     */
    List<OutletAssignedItemResponse> listOutletsByMerchantOrSubMerchant(Long merchantId, Long subMerchantId, String username);

    /**
     * Add an outlet. Merchant user (main merchant) saves with ACTIVE status; others save with PENDING.
     * Access: MERCHANT, SUBMERCHANT, ADMIN, SYSADMIN.
     */
    OutletAddDTO addOutlet(OutletAddDTO dto, String authenticatedUsername);

    /**
     * Update outlet details (not merchant/sub-merchant or status). Owner or admin.
     */
    OutletAddDTO updateOutlet(Long outletId, OutletAddDTO dto, String authenticatedUsername);

    /**
     * Update outlet status. Access: ADMIN, SYSADMIN.
     */
    OutletAddDTO updateOutletStatus(Long outletId, String status);

    /**
     * Get all outlets with optional search (merchant name, sub-merchant name, outlet name) and filters (status, outletType).
     * Access: ADMIN, SYSADMIN.
     */
    GetAllOutletsResponse getAllOutlets(String search, String status, String outletType);

    /**
     * Merchant user approves an outlet (sets status to ACTIVE). Only main merchant can approve outlets under their merchant.
     * Access: MERCHANT only.
     */
    OutletAddDTO approveOutlet(Long outletId, String authenticatedUsername);

    /**
     * Outlet owner submits payment (status → PENDING_SUBSCRIPTION). Access: MERCHANT, SUBMERCHANT (owner only).
     */
    OutletAddDTO submitPayment(Long outletId, String authenticatedUsername);

    /**
     * Admin verifies payment and activates outlet (status → ACTIVE, extends subscription). Access: ADMIN, SYSADMIN.
     */
    OutletAddDTO verifyPayment(Long outletId);

    /**
     * Map an Outlet entity to OutletListItemResponse (for use in merchant/sub-merchant with-outlets responses).
     */
    OutletListItemResponse toListItemResponse(Outlet outlet);

    /**
     * Get full outlet details by outlet ID: outlet info, items, discounts, and payments for that outlet.
     */
    OutletDetailResponse getOutletDetails(Long outletId);

    /**
     * Get outlet details for merchant-app. Only returns details if the outlet belongs to the authenticated
     * merchant (direct or via sub-merchant) or sub-merchant. Otherwise throws.
     */
    OutletDetailResponse getOutletDetailsForMerchantApp(String username, Long outletId);

    /** Payment details list for the given outlet. Merchant-app only; outlet must belong to the user. */
    List<PaymentListItemResponse> getPaymentDetailsForMerchantApp(String username, Long outletId);

    /** Schedule details (grouped by type) for the given outlet. Merchant-app only; outlet must belong to the user. */
    OutletSchedulesGroupedResponse getScheduleDetailsForMerchantApp(String username, Long outletId);

    /** Discount details list for the given outlet. Merchant-app only; outlet must belong to the user. */
    List<DiscountListItemResponse> getDiscountDetailsForMerchantApp(String username, Long outletId);
}
