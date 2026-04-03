package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Customer;
import lk.icbt.findit.entity.CustomerFavorite;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.CustomerFavoriteRepository;
import lk.icbt.findit.repository.CustomerRepository;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.request.CustomerFavoriteRequest;
import lk.icbt.findit.response.CustomerFavoriteResponse;
import lk.icbt.findit.response.OutletDetailItem;
import lk.icbt.findit.service.CustomerFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerFavoriteServiceImpl implements CustomerFavoriteService {

    private final CustomerFavoriteRepository favoriteRepository;
    private final OutletRepository outletRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerFavoriteResponse create(Long customerId, CustomerFavoriteRequest request) {
        if (request.getRating() == null) {
            throw new InvalidRequestException(ResponseCodes.VALIDATION_ERROR_CODE, "Rating is required");
        }
        if (favoriteRepository.existsByCustomer_CustomerIdAndOutlet_OutletId(customerId, request.getOutletId())) {
            throw new InvalidRequestException(ResponseCodes.CUSTOMER_FAVORITE_ALREADY_EXISTS_CODE,
                    "This outlet is already in your favorites");
        }
        Outlet outlet = outletRepository.findById(request.getOutletId())
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.CUSTOMER_NOT_FOUND_CODE, "Customer not found"));

        CustomerFavorite entity = new CustomerFavorite();
        entity.setCustomer(customer);
        entity.setOutlet(outlet);
        entity.setNickname(request.getNickname() != null ? request.getNickname().trim() : null);
        entity.setRating(request.getRating());

        CustomerFavorite saved = favoriteRepository.save(entity);
        favoriteRepository.flush();
        recalculateOutletRating(outlet.getOutletId());
        return toResponse(saved, "Favorite saved successfully.");
    }

    @Override
    public CustomerFavoriteResponse getById(Long id, Long customerId) {
        CustomerFavorite entity = favoriteRepository.findByIdAndCustomer_CustomerId(id, customerId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.CUSTOMER_FAVORITE_NOT_FOUND_CODE, "Favorite not found"));
        return toResponse(entity, null);
    }

    @Override
    public List<CustomerFavoriteResponse> listByCustomerId(Long customerId) {
        return favoriteRepository.findByCustomer_CustomerIdOrderByIdAsc(customerId).stream()
                .map(e -> toResponse(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerFavoriteResponse update(Long id, Long customerId, CustomerFavoriteRequest request) {
        CustomerFavorite entity = favoriteRepository.findByIdAndCustomer_CustomerId(id, customerId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.CUSTOMER_FAVORITE_NOT_FOUND_CODE, "Favorite not found"));
        Long previousOutletId = entity.getOutlet().getOutletId();
        entity.setNickname(request.getNickname() != null ? request.getNickname().trim() : null);
        if (request.getRating() != null) {
            entity.setRating(request.getRating());
        }
        if (request.getOutletId() != null && !request.getOutletId().equals(entity.getOutlet().getOutletId())) {
            if (favoriteRepository.existsByCustomer_CustomerIdAndOutlet_OutletId(customerId, request.getOutletId())) {
                throw new InvalidRequestException(ResponseCodes.CUSTOMER_FAVORITE_ALREADY_EXISTS_CODE,
                        "This outlet is already in your favorites");
            }
            Outlet outlet = outletRepository.findById(request.getOutletId())
                    .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));
            entity.setOutlet(outlet);
        }
        CustomerFavorite saved = favoriteRepository.save(entity);
        favoriteRepository.flush();
        recalculateOutletRating(previousOutletId);
        if (!Objects.equals(previousOutletId, saved.getOutlet().getOutletId())) {
            recalculateOutletRating(saved.getOutlet().getOutletId());
        }
        return toResponse(saved, "Favorite updated successfully.");
    }

    @Override
    @Transactional
    public void delete(Long id, Long customerId) {
        CustomerFavorite entity = favoriteRepository.findByIdAndCustomer_CustomerId(id, customerId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.CUSTOMER_FAVORITE_NOT_FOUND_CODE, "Favorite not found"));
        Long outletId = entity.getOutlet().getOutletId();
        favoriteRepository.deleteByIdAndCustomer_CustomerId(id, customerId);
        favoriteRepository.flush();
        recalculateOutletRating(outletId);
    }

    private void recalculateOutletRating(Long outletId) {
        Double avg = favoriteRepository.averageRatingByOutletId(outletId);
        Outlet outlet = outletRepository.findById(outletId).orElse(null);
        if (outlet == null) {
            return;
        }
        if (avg == null) {
            outlet.setRating(null);
        } else {
            outlet.setRating(Math.round(avg * 100.0) / 100.0);
        }
        outletRepository.save(outlet);
    }

    private CustomerFavoriteResponse toResponse(CustomerFavorite entity, String message) {
        CustomerFavoriteResponse r = new CustomerFavoriteResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setResponseMessage(message);
        r.setId(entity.getId());
        if (entity.getCustomer() != null) {
            r.setCustomerId(entity.getCustomer().getCustomerId());
        }
        if (entity.getOutlet() != null) {
            r.setOutletId(entity.getOutlet().getOutletId());
            r.setOutlet(toOutletDetail(entity.getOutlet()));
        }
        r.setNickname(entity.getNickname());
        r.setRating(entity.getRating());
        return r;
    }

    private OutletDetailItem toOutletDetail(Outlet o) {
        OutletDetailItem d = new OutletDetailItem();
        d.setOutletId(o.getOutletId());
        d.setOutletName(o.getOutletName());
        d.setStatus(o.getStatus());
        d.setSubscriptionStatus(o.getSubscriptionStatus());
        d.setAddressLine1(o.getAddressLine1());
        d.setAddressLine2(o.getAddressLine2());
        d.setContactNumber(o.getContactNumber());
        d.setEmailAddress(o.getEmailAddress());
        d.setLatitude(o.getLatitude());
        d.setLongitude(o.getLongitude());
        d.setRating(o.getRating());
        return d;
    }
}
