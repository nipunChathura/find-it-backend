package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.CustomerLoginDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.request.CustomerLoginRequest;
import lk.icbt.findit.request.CustomerSearchHistoryRequest;
import lk.icbt.findit.request.NearestOutletSearchRequest;
import lk.icbt.findit.request.ProfileImageChangeRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.CustomerLoginResponse;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.response.CustomerSearchHistoryResponse;
import lk.icbt.findit.response.Response;
import lk.icbt.findit.response.NearestOutletSearchResponse;
import lk.icbt.findit.response.UserResponse;
import lk.icbt.findit.service.CustomerSearchHistoryService;
import lk.icbt.findit.service.NearestOutletSearchService;
import lk.icbt.findit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Customer mobile/app API. Endpoints for the customer-facing application (login, profile, etc.).
 */
@RestController
@RequestMapping("/api/customer-app")
@RequiredArgsConstructor
public class CustomerAppController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final NearestOutletSearchService nearestOutletSearchService;
    private final CustomerSearchHistoryService customerSearchHistoryService;

    /**
     * Customer login with email and password. Public. Returns JWT token and customer context.
     */
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

    /**
     * Change the authenticated customer's profile image. Upload image first via POST /api/images/upload?type=profile,
     * then send the returned fileName here. Sends an in-app notification on success.
     */
    @PutMapping(value = "/profile/image", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> changeProfileImage(@Valid @RequestBody ProfileImageChangeRequest request) {
        User user = getAuthenticatedCustomer();
        UserResponse result = userService.changeProfileImage(user.getUserId(), request.getFileName());
        return ResponseEntity.ok(result);
    }

    /**
     * Search nearest outlets by item name. Uses customer location (lat/long), max distance (km),
     * and optional category and outlet type. Returns only outlets that are currently OPEN and
     * have the matching item available; each outlet includes its matching items list.
     */
    @PostMapping(value = "/outlets/nearest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<NearestOutletSearchResponse> searchNearestOutlets(@Valid @RequestBody NearestOutletSearchRequest request) {
        NearestOutletSearchResponse response = nearestOutletSearchService.searchNearestOutlets(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a search history entry for the authenticated customer.
     */
    @PostMapping(value = "/search-history", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerSearchHistoryResponse> createSearchHistory(@Valid @RequestBody CustomerSearchHistoryRequest request) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        CustomerSearchHistoryResponse response = customerSearchHistoryService.create(user.getCustomerId(), request);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }

    /**
     * Get search history list for the authenticated customer (newest first).
     */
    @GetMapping(value = "/search-history", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CustomerSearchHistoryResponse>> listSearchHistory() {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        return ResponseEntity.ok(customerSearchHistoryService.listByCustomerId(user.getCustomerId()));
    }

    /**
     * Get a single search history entry by id (must belong to the authenticated customer).
     */
    @GetMapping(value = "/search-history/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerSearchHistoryResponse> getSearchHistory(@PathVariable Long id) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        return ResponseEntity.ok(customerSearchHistoryService.getById(id, user.getCustomerId()));
    }

    /**
     * Update a search history entry (must belong to the authenticated customer).
     */
    @PutMapping(value = "/search-history/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerSearchHistoryResponse> updateSearchHistory(
            @PathVariable Long id,
            @Valid @RequestBody CustomerSearchHistoryRequest request) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        return ResponseEntity.ok(customerSearchHistoryService.update(id, user.getCustomerId(), request));
    }

    /**
     * Delete a search history entry (must belong to the authenticated customer).
     */
    @DeleteMapping(value = "/search-history/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Response> deleteSearchHistory(@PathVariable Long id) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        customerSearchHistoryService.delete(id, user.getCustomerId());
        Response response = new Response();
        response.setStatus("SUCCESS");
        response.setResponseCode("00");
        response.setResponseMessage("Search history deleted.");
        return ResponseEntity.ok(response);
    }

    /**
     * Change the authenticated customer's password. Sends an in-app notification on success.
     */
    @PutMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Response> changePassword(@Valid @RequestBody UserRequest request) {
        User user = getAuthenticatedCustomer();
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setUsername(user.getUsername());
        dto.setCurrentPassword(request.getCurrentPassword());
        dto.setNewPassword(request.getNewPassword());
        PasswordChangeDTO result = userService.changePassword(dto);
        Response response = new Response();
        response.setStatus(result.getStatus());
        response.setResponseCode(result.getResponseCode());
        response.setResponseMessage(result.getResponseMessage());
        return ResponseEntity.ok(response);
    }

    private User getAuthenticatedCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException("401", "Not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseCodes.USER_NOT_FOUND_CODE, "User not found"));
        if (user.getRole() != Role.CUSTOMER) {
            throw new InvalidRequestException(
                    ResponseCodes.VALIDATION_ERROR_CODE, "Only customers can use this endpoint");
        }
        return user;
    }

    private void ensureCustomerId(User user) {
        if (user.getCustomerId() == null) {
            throw new InvalidRequestException(
                    ResponseCodes.CUSTOMER_NOT_FOUND_CODE, "Customer profile not linked. Search history is available only for customers.");
        }
    }
}
