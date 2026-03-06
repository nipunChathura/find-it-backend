package lk.icbt.findit.service;

import lk.icbt.findit.entity.MembershipType;
import lk.icbt.findit.request.CustomerOnboardingRequest;
import lk.icbt.findit.request.CustomerRequest;
import lk.icbt.findit.response.CustomerListItemResponse;
import lk.icbt.findit.response.CustomerOnboardingResponse;
import lk.icbt.findit.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerOnboardingResponse onboard(CustomerOnboardingRequest request);

    CustomerResponse create(CustomerRequest request);

    CustomerResponse getById(Long customerId);

    List<CustomerListItemResponse> list(String search, String status, MembershipType membershipType);

    CustomerResponse update(Long customerId, CustomerRequest request);

    void delete(Long customerId);
}
