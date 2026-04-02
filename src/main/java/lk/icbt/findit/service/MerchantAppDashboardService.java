package lk.icbt.findit.service;

import lk.icbt.findit.response.MerchantAppDashboardResponse;


public interface MerchantAppDashboardService {

    
    MerchantAppDashboardResponse getDashboard(String username);
}
