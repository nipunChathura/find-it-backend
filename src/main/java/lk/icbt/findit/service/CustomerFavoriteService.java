package lk.icbt.findit.service;

import lk.icbt.findit.request.CustomerFavoriteRequest;
import lk.icbt.findit.response.CustomerFavoriteResponse;

import java.util.List;

public interface CustomerFavoriteService {

    CustomerFavoriteResponse create(Long customerId, CustomerFavoriteRequest request);

    CustomerFavoriteResponse getById(Long id, Long customerId);

    List<CustomerFavoriteResponse> listByCustomerId(Long customerId);

    CustomerFavoriteResponse update(Long id, Long customerId, CustomerFavoriteRequest request);

    void delete(Long id, Long customerId);
}
