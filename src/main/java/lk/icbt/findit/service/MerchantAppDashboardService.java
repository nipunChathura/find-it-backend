package lk.icbt.findit.service;

import lk.icbt.findit.response.MerchantAppDashboardResponse;

/**
 * Dashboard data for the merchant app: outlet counts, total items, pending payments.
 */
public interface MerchantAppDashboardService {

    /**
     * Get dashboard for the authenticated merchant or sub-merchant (by username).
     * Returns total outlet count, active outlet count, total items, and pending payments.
     */
    MerchantAppDashboardResponse getDashboard(String username);
}
