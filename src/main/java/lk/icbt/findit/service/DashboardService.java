package lk.icbt.findit.service;

import lk.icbt.findit.response.DashboardActivityResponse;
import lk.icbt.findit.response.DashboardMonthlyIncomeResponse;
import lk.icbt.findit.response.DashboardSummaryResponse;
import lk.icbt.findit.response.MerchantSummaryResponse;

public interface DashboardService {

    
    DashboardSummaryResponse getSummary();

    
    MerchantSummaryResponse getMerchantSummary();

    
    DashboardActivityResponse getActivity(int months);

    
    DashboardMonthlyIncomeResponse getMonthlyIncome(int months);
}
