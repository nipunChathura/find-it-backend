package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.CustomerSearchHistory;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.CustomerSearchHistoryRepository;
import lk.icbt.findit.request.CustomerSearchHistoryRequest;
import lk.icbt.findit.response.CustomerSearchHistoryResponse;
import lk.icbt.findit.service.CustomerSearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerSearchHistoryServiceImpl implements CustomerSearchHistoryService {

    private final CustomerSearchHistoryRepository repository;

    @Override
    @Transactional
    public CustomerSearchHistoryResponse create(Long customerId, CustomerSearchHistoryRequest request) {
        CustomerSearchHistory entity = new CustomerSearchHistory();
        entity.setCustomerId(customerId);
        mapRequestToEntity(request, entity);
        CustomerSearchHistory saved = repository.save(entity);
        return toResponse(saved, "Search history saved.");
    }

    @Override
    public CustomerSearchHistoryResponse getById(Long id, Long customerId) {
        CustomerSearchHistory entity = repository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.SEARCH_HISTORY_NOT_FOUND_CODE, "Search history not found"));
        return toResponse(entity, null);
    }

    @Override
    public List<CustomerSearchHistoryResponse> listByCustomerId(Long customerId) {
        return repository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(e -> toResponse(e, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerSearchHistoryResponse update(Long id, Long customerId, CustomerSearchHistoryRequest request) {
        CustomerSearchHistory entity = repository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.SEARCH_HISTORY_NOT_FOUND_CODE, "Search history not found"));
        mapRequestToEntity(request, entity);
        CustomerSearchHistory saved = repository.save(entity);
        return toResponse(saved, "Search history updated.");
    }

    @Override
    @Transactional
    public void delete(Long id, Long customerId) {
        if (!repository.findByIdAndCustomerId(id, customerId).isPresent()) {
            throw new InvalidRequestException(ResponseCodes.SEARCH_HISTORY_NOT_FOUND_CODE, "Search history not found");
        }
        repository.deleteByIdAndCustomerId(id, customerId);
    }

    private void mapRequestToEntity(CustomerSearchHistoryRequest request, CustomerSearchHistory entity) {
        entity.setSearchText(request.getSearchText() != null ? request.getSearchText().trim() : null);
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        entity.setDistanceKm(request.getDistanceKm());
        entity.setCategoryId(request.getCategoryId());
        entity.setOutletType(request.getOutletType() != null && !request.getOutletType().isBlank()
                ? request.getOutletType().trim() : null);
    }

    private CustomerSearchHistoryResponse toResponse(CustomerSearchHistory entity, String message) {
        CustomerSearchHistoryResponse r = new CustomerSearchHistoryResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        if (message != null) {
            r.setResponseMessage(message);
        }
        r.setId(entity.getId());
        r.setCustomerId(entity.getCustomerId());
        r.setSearchText(entity.getSearchText());
        r.setLatitude(entity.getLatitude());
        r.setLongitude(entity.getLongitude());
        r.setDistanceKm(entity.getDistanceKm());
        r.setCategoryId(entity.getCategoryId());
        r.setOutletType(entity.getOutletType());
        r.setCreatedAt(entity.getCreatedAt());
        return r;
    }
}
