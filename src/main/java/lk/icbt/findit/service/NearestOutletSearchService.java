package lk.icbt.findit.service;

import lk.icbt.findit.request.NearestOutletSearchRequest;
import lk.icbt.findit.response.NearestOutletSearchResponse;

public interface NearestOutletSearchService {

    /**
     * Search nearest outlets that have the given item (by name), within max distance from customer location.
     * Filters: item available and active; outlet active and currently OPEN; optional category and outlet type.
     * Returns outlets sorted by distance, each with matching items list.
     */
    NearestOutletSearchResponse searchNearestOutlets(NearestOutletSearchRequest request);
}
