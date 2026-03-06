package lk.icbt.findit.service;

import lk.icbt.findit.response.DashboardActivityResponse;
import lk.icbt.findit.response.DashboardMonthlyIncomeResponse;
import lk.icbt.findit.response.DashboardSummaryResponse;
import lk.icbt.findit.response.MerchantSummaryResponse;

public interface DashboardService {

    /**
     * Returns KPI counts for dashboard: users, merchants, items, customers, outlets, categories,
     * pending approvals (users + merchants + sub-merchants + outlets with PENDING), active discounts.
     */
    DashboardSummaryResponse getSummary();

    /**
     * Returns merchant counts by status (ACTIVE, INACTIVE, PENDING). Excludes DELETED.
     */
    MerchantSummaryResponse getMerchantSummary();

    /**
     * Returns activity data for the last N months: per-month counts of new users, merchants, outlets, customers, payments.
     */
    DashboardActivityResponse getActivity(int months);

    /**
     * Returns monthly income data for the last N months for dashboard chart (sum of payment amounts per month).
     */
    DashboardMonthlyIncomeResponse getMonthlyIncome(int months);
}
