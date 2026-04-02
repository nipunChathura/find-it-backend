package lk.icbt.findit.service;

import lk.icbt.findit.request.NearestOutletSearchRequest;
import lk.icbt.findit.response.NearestOutletSearchResponse;

public interface NearestOutletSearchService {

    
    NearestOutletSearchResponse searchNearestOutlets(NearestOutletSearchRequest request, Long customerId);
}
