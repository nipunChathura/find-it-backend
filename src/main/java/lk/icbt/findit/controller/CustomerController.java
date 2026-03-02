package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.CustomerLoginDTO;
import lk.icbt.findit.entity.MembershipType;
import lk.icbt.findit.request.CustomerLoginRequest;
import lk.icbt.findit.request.CustomerOnboardingRequest;
import lk.icbt.findit.request.CustomerRequest;
import lk.icbt.findit.response.CustomerListItemResponse;
import lk.icbt.findit.response.CustomerLoginResponse;
import lk.icbt.findit.response.CustomerOnboardingResponse;
import lk.icbt.findit.response.CustomerResponse;
import lk.icbt.findit.service.CustomerService;
import lk.icbt.findit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Customer APIs. Onboarding is public. CRUD: SYSADMIN, ADMIN only.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerLoginResponse> login(@Valid @RequestBody CustomerLoginRequest request) {
        CustomerLoginDTO dto = userService.loginCustomer(
                request.getEmail() != null ? request.getEmail().trim() : null,
                request.getPassword());
        CustomerLoginResponse response = new CustomerLoginResponse();
        BeanUtils.copyProperties(dto, response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/onboarding", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerOnboardingResponse> onboarding(@Valid @RequestBody CustomerOnboardingRequest request) {
        CustomerOnboardingResponse result = customerService.onboard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse result = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @GetMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long customerId) {
        CustomerResponse result = customerService.getById(customerId);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CustomerListItemResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) MembershipType membershipType) {
        List<CustomerListItemResponse> list = customerService.list(search, status, membershipType);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequest request) {
        CustomerResponse result = customerService.update(customerId, request);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN')")
    @DeleteMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long customerId) {
        customerService.delete(customerId);
        return ResponseEntity.noContent().build();
    }
}
