package lk.icbt.findit.service;

import lk.icbt.findit.request.NearestOutletSearchRequest;
import lk.icbt.findit.response.NearestOutletSearchResponse;

public interface NearestOutletSearchService {

    /**
     * Search nearest outlets that have the given item (by name), within max distance from customer location.
     * Filters: item available and active; outlet active and currently OPEN; optional category and outlet type.
     * When customerId is not null, each outlet in the response includes is_favorite, customer_favorite_id and nickname from customer_favorite.
     */
    NearestOutletSearchResponse searchNearestOutlets(NearestOutletSearchRequest request, Long customerId);
}
