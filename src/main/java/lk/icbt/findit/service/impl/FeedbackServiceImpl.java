package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Customer;
import lk.icbt.findit.entity.Feedback;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.CustomerRepository;
import lk.icbt.findit.repository.FeedbackRepository;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.request.FeedbackRequest;
import lk.icbt.findit.response.FeedbackResponse;
import lk.icbt.findit.response.OutletFeedbackCountResponse;
import lk.icbt.findit.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final OutletRepository outletRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public FeedbackResponse create(Long customerId, FeedbackRequest request) {
        Outlet outlet = outletRepository.findById(request.getOutletId())
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.CUSTOMER_NOT_FOUND_CODE, "Customer not found"));

        Feedback entity = new Feedback();
        entity.setOutlet(outlet);
        entity.setCustomer(customer);
        entity.setFeedbackText(request.getFeedbackText() != null ? request.getFeedbackText().trim() : null);
        entity.setRating(request.getRating());
        Date now = new Date();
        entity.setCreatedDatetime(now);

        Feedback saved = feedbackRepository.save(entity);
        return toResponse(saved, "Feedback saved successfully.");
    }

    @Override
    public List<FeedbackResponse> listByCustomerId(Long customerId) {
        return feedbackRepository.findByCustomer_CustomerIdOrderByCreatedDatetimeDesc(customerId).stream()
                .map(e -> toResponse(e, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> listByOutletId(Long outletId) {
        if (!outletRepository.existsById(outletId)) {
            throw new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found");
        }
        return feedbackRepository.findByOutlet_OutletIdOrderByCreatedDatetimeDesc(outletId).stream()
                .map(e -> toResponse(e, null))
                .collect(Collectors.toList());
    }

    @Override
    public long countByOutletId(Long outletId) {
        if (!outletRepository.existsById(outletId)) {
            throw new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found");
        }
        return feedbackRepository.countByOutlet_OutletId(outletId);
    }

    @Override
    public OutletFeedbackCountResponse getOutletFeedbackCount(Long outletId) {
        if (!outletRepository.existsById(outletId)) {
            throw new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found");
        }
        long count = feedbackRepository.countByOutlet_OutletId(outletId);
        OutletFeedbackCountResponse response = new OutletFeedbackCountResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setResponseMessage("Success");
        response.setOutletId(outletId);
        response.setFeedbackCount(count);
        return response;
    }

    private FeedbackResponse toResponse(Feedback entity, String message) {
        FeedbackResponse r = new FeedbackResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setResponseMessage(message);
        r.setFeedbackId(entity.getFeedbackId());
        r.setFeedbackText(entity.getFeedbackText());
        r.setRating(entity.getRating());
        if (entity.getCustomer() != null) {
            r.setCustomerId(entity.getCustomer().getCustomerId());
        }
        if (entity.getOutlet() != null) {
            r.setOutletId(entity.getOutlet().getOutletId());
            r.setOutletName(entity.getOutlet().getOutletName());
        }
        r.setCreatedDatetime(entity.getCreatedDatetime());
        return r;
    }
}
