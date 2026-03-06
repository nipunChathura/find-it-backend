package lk.icbt.findit.service;

import lk.icbt.findit.request.FeedbackRequest;
import lk.icbt.findit.response.FeedbackResponse;
import lk.icbt.findit.response.OutletFeedbackCountResponse;

import java.util.List;

public interface FeedbackService {

    FeedbackResponse create(Long customerId, FeedbackRequest request);

    List<FeedbackResponse> listByCustomerId(Long customerId);

    List<FeedbackResponse> listByOutletId(Long outletId);

    long countByOutletId(Long outletId);

    OutletFeedbackCountResponse getOutletFeedbackCount(Long outletId);
}
