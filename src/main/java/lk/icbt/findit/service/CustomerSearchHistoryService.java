package lk.icbt.findit.service;

import lk.icbt.findit.request.CustomerSearchHistoryRequest;
import lk.icbt.findit.response.CustomerSearchHistoryResponse;

import java.util.List;

public interface CustomerSearchHistoryService {

    CustomerSearchHistoryResponse create(Long customerId, CustomerSearchHistoryRequest request);

    CustomerSearchHistoryResponse getById(Long id, Long customerId);

    List<CustomerSearchHistoryResponse> listByCustomerId(Long customerId);

    CustomerSearchHistoryResponse update(Long id, Long customerId, CustomerSearchHistoryRequest request);

    void delete(Long id, Long customerId);
}
