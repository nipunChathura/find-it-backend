package lk.icbt.findit.service;

import lk.icbt.findit.dto.OutletAddDTO;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.response.GetAllOutletsResponse;
import lk.icbt.findit.response.OutletListItemResponse;
import lk.icbt.findit.response.OutletListResponse;

import java.util.List;

public interface OutletService {

    /**
     * List outlets with optional name and status filter. Includes current open/closed status from schedule.
     * GET /api/outlets?name=&status=
     */
    List<OutletListResponse> listOutlets(String name, String status);

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
     * Expire outlets whose subscription valid period has passed (status → EXPIRED_SUBSCRIPTION). Called by scheduler.
     */
    void expireOutletsWithEndedSubscription();

    /**
     * Map an Outlet entity to OutletListItemResponse (for use in merchant/sub-merchant with-outlets responses).
     */
    OutletListItemResponse toListItemResponse(Outlet outlet);
}
