package lk.icbt.findit.service;

import lk.icbt.findit.request.DiscountRequest;
import lk.icbt.findit.response.DiscountListItemResponse;
import lk.icbt.findit.response.DiscountResponse;

import java.util.List;

public interface DiscountService {

    DiscountResponse create(DiscountRequest request);

    DiscountResponse getById(Long discountId);

    List<DiscountListItemResponse> list(String status, Long itemId, Long outletId);

    /** Current available discounts for the outlet (ACTIVE and today within startDate–endDate). */
    List<DiscountListItemResponse> listCurrentByOutletId(Long outletId);

    DiscountResponse update(Long discountId, DiscountRequest request);

    void delete(Long discountId);
}
