package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Customer;
import lk.icbt.findit.entity.MembershipType;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.CustomerRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.request.CustomerOnboardingRequest;
import lk.icbt.findit.request.CustomerRequest;
import lk.icbt.findit.response.CustomerListItemResponse;
import lk.icbt.findit.response.CustomerOnboardingResponse;
import lk.icbt.findit.response.CustomerResponse;
import lk.icbt.findit.service.CustomerService;
import lk.icbt.findit.service.ServiceLoggingHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private static final String SERVICE_NAME = "CustomerService";

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CustomerOnboardingResponse onboard(CustomerOnboardingRequest request) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "onboard", "username", request.getUsername(), "email", request.getEmail());
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            ServiceLoggingHelper.logValidationError(log, ResponseCodes.USERNAME_ALREADY_EXISTS_CODE, "Username already exists");
            throw new InvalidRequestException(ResponseCodes.USERNAME_ALREADY_EXISTS_CODE, "Username already exists");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            ServiceLoggingHelper.logGettingData(log, "Customer by email", "email", request.getEmail());
            customerRepository.findByEmail(request.getEmail().trim().toLowerCase())
                    .ifPresent(c -> {
                        ServiceLoggingHelper.logValidationError(log, ResponseCodes.CUSTOMER_EMAIL_ALREADY_EXISTS_CODE, "Customer with this email already exists");
                        throw new InvalidRequestException(ResponseCodes.CUSTOMER_EMAIL_ALREADY_EXISTS_CODE, "Customer with this email already exists");
                    });
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(trim(request.getLastName()));
        customer.setEmail(request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null);
        customer.setPhoneNumber(trim(request.getPhoneNumber()));
        customer.setNic(trim(request.getNic()));
        customer.setDob(trim(request.getDob()));
        customer.setGender(trim(request.getGender()));
        customer.setCountryName(trim(request.getCountryName()));
        customer.setProfileImage(trim(request.getProfileImage()));
        customer.setMembershipType(request.getMembershipType());
        customer.setStatus(Constants.CUSTOMER_ACTIVE_STATUS);

        Date now = new Date();
        customer.setCreatedDatetime(now);
        customer.setModifiedDatetime(now);
        customer.setVersion(1);

        Customer savedCustomer = customerRepository.save(customer);

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(customer.getEmail());
        user.setIsSystemUser(Constants.DB_FALSE);
        user.setRole(Role.CUSTOMER);
        user.setStatus(Constants.USER_ACTIVE_STATUS);
        user.setCustomerId(savedCustomer.getCustomerId());
        user.setCreatedDatetime(now);
        user.setModifiedDatetime(now);
        user.setVersion(1);
        userRepository.save(user);

        CustomerOnboardingResponse response = new CustomerOnboardingResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setResponseMessage("Customer onboarding successful. You can log in with your username and password.");
        response.setCustomerId(savedCustomer.getCustomerId());
        response.setUsername(user.getUsername());
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "onboard", "customerId", savedCustomer.getCustomerId());
        return response;
    }

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "create", "email", request.getEmail());
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            ServiceLoggingHelper.logGettingData(log, "Customer by email", "email", request.getEmail());
            customerRepository.findByEmail(request.getEmail().trim())
                    .ifPresent(c -> {
                        ServiceLoggingHelper.logValidationError(log, ResponseCodes.CUSTOMER_EMAIL_ALREADY_EXISTS_CODE, "Customer with this email already exists");
                        throw new InvalidRequestException(ResponseCodes.CUSTOMER_EMAIL_ALREADY_EXISTS_CODE, "Customer with this email already exists");
                    });
        }

        Customer customer = new Customer();
        mapRequestToEntity(request, customer);
        customer.setStatus(request.getStatus() != null && !request.getStatus().isBlank()
                ? request.getStatus().trim()
                : Constants.CUSTOMER_ACTIVE_STATUS);

        Date now = new Date();
        customer.setCreatedDatetime(now);
        customer.setModifiedDatetime(now);
        customer.setVersion(1);

        Customer saved = customerRepository.save(customer);
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "create", "customerId", saved.getCustomerId());
        return toResponse(saved, "Customer created successfully.");
    }

    @Override
    public CustomerResponse getById(Long customerId) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "getById", "customerId", customerId);
        ServiceLoggingHelper.logGettingData(log, "Customer by id", "customerId", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    ServiceLoggingHelper.logValidationError(log, ResponseCodes.CUSTOMER_NOT_FOUND_CODE, "Customer not found");
                    return new InvalidRequestException(ResponseCodes.CUSTOMER_NOT_FOUND_CODE, "Customer not found");
                });
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "getById", "customerId", customerId);
        return toResponse(customer, null);
    }

    @Override
    public List<CustomerListItemResponse> list(String search, String status, MembershipType membershipType) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "list", "search", search, "status", status);
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : null;
        List<Customer> list = customerRepository.search(searchParam, statusParam, membershipType);
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "list", "count", list.size());
        return list.stream().map(this::toListItem).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerResponse update(Long customerId, CustomerRequest request) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "update", "customerId", customerId);
        ServiceLoggingHelper.logGettingData(log, "Customer by id", "customerId", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    ServiceLoggingHelper.logValidationError(log, ResponseCodes.CUSTOMER_NOT_FOUND_CODE, "Customer not found");
                    return new InvalidRequestException(ResponseCodes.CUSTOMER_NOT_FOUND_CODE, "Customer not found");
                });

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            customerRepository.findByEmail(request.getEmail().trim())
                    .filter(c -> !c.getCustomerId().equals(customerId))
                    .ifPresent(c -> {
                        ServiceLoggingHelper.logValidationError(log, ResponseCodes.CUSTOMER_EMAIL_ALREADY_EXISTS_CODE, "Customer with this email already exists");
                        throw new InvalidRequestException(ResponseCodes.CUSTOMER_EMAIL_ALREADY_EXISTS_CODE, "Customer with this email already exists");
                    });
        }

        mapRequestToEntity(request, customer);
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            customer.setStatus(request.getStatus().trim());
        }
        customer.setModifiedDatetime(new Date());

        Customer saved = customerRepository.save(customer);
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "update", "customerId", saved.getCustomerId());
        return toResponse(saved, "Customer updated successfully.");
    }

    @Override
    @Transactional
    public void delete(Long customerId) {
        ServiceLoggingHelper.logStart(log, SERVICE_NAME, "delete", "customerId", customerId);
        ServiceLoggingHelper.logGettingData(log, "Customer by id", "customerId", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    ServiceLoggingHelper.logValidationError(log, ResponseCodes.CUSTOMER_NOT_FOUND_CODE, "Customer not found");
                    return new InvalidRequestException(ResponseCodes.CUSTOMER_NOT_FOUND_CODE, "Customer not found");
                });
        customerRepository.delete(customer);
        ServiceLoggingHelper.logEnd(log, SERVICE_NAME, "delete", "customerId", customerId);
    }

    private static String trim(String s) {
        return s != null ? s.trim() : null;
    }

    private void mapRequestToEntity(CustomerRequest request, Customer customer) {
        if (request.getFirstName() != null) customer.setFirstName(request.getFirstName().trim());
        if (request.getLastName() != null) customer.setLastName(trim(request.getLastName()));
        if (request.getNic() != null) customer.setNic(trim(request.getNic()));
        if (request.getDob() != null) customer.setDob(trim(request.getDob()));
        if (request.getGender() != null) customer.setGender(trim(request.getGender()));
        if (request.getCountryName() != null) customer.setCountryName(trim(request.getCountryName()));
        if (request.getProfileImage() != null) customer.setProfileImage(trim(request.getProfileImage()));
        if (request.getEmail() != null) customer.setEmail(trim(request.getEmail()));
        if (request.getPhoneNumber() != null) customer.setPhoneNumber(trim(request.getPhoneNumber()));
        if (request.getMembershipType() != null) customer.setMembershipType(request.getMembershipType());
    }

    private CustomerResponse toResponse(Customer c, String message) {
        CustomerResponse r = new CustomerResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setResponseMessage(message);
        r.setCustomerId(c.getCustomerId());
        r.setFirstName(c.getFirstName());
        r.setLastName(c.getLastName());
        r.setNic(c.getNic());
        r.setDob(c.getDob());
        r.setGender(c.getGender());
        r.setCountryName(c.getCountryName());
        r.setProfileImage(c.getProfileImage());
        r.setEmail(c.getEmail());
        r.setPhoneNumber(c.getPhoneNumber());
        r.setMembershipType(c.getMembershipType());
        r.setStatus(c.getStatus());
        return r;
    }

    private CustomerListItemResponse toListItem(Customer c) {
        CustomerListItemResponse r = new CustomerListItemResponse();
        r.setCustomerId(c.getCustomerId());
        r.setFirstName(c.getFirstName());
        r.setLastName(c.getLastName());
        r.setCountryName(c.getCountryName());
        r.setEmail(c.getEmail());
        r.setPhoneNumber(c.getPhoneNumber());
        r.setMembershipType(c.getMembershipType());
        r.setStatus(c.getStatus());
        return r;
    }
}
