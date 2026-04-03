package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.dto.CustomerLoginDTO;
import lk.icbt.findit.dto.PasswordChangeDTO;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.request.CustomerLoginRequest;
import lk.icbt.findit.request.CustomerFavoriteRequest;
import lk.icbt.findit.request.CustomerSearchHistoryRequest;
import lk.icbt.findit.request.FeedbackRequest;
import lk.icbt.findit.request.NearestOutletSearchRequest;
import lk.icbt.findit.request.ProfileImageChangeRequest;
import lk.icbt.findit.request.UserRequest;
import lk.icbt.findit.response.CustomerLoginResponse;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.response.CustomerFavoriteResponse;
import lk.icbt.findit.response.CustomerSearchHistoryResponse;
import lk.icbt.findit.response.FeedbackResponse;
import lk.icbt.findit.response.ItemListItemResponse;
import lk.icbt.findit.response.Response;
import lk.icbt.findit.response.NearestOutletSearchResponse;
import lk.icbt.findit.response.UserResponse;
import lk.icbt.findit.service.CustomerFavoriteService;
import lk.icbt.findit.service.CustomerSearchHistoryService;
import lk.icbt.findit.service.FeedbackService;
import lk.icbt.findit.service.ItemService;
import lk.icbt.findit.service.NearestOutletSearchService;
import lk.icbt.findit.service.NotificationService;
import lk.icbt.findit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping("/api/customer-app")
@RequiredArgsConstructor
public class CustomerAppController {

    private static final Logger log = LoggerFactory.getLogger(CustomerAppController.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final NearestOutletSearchService nearestOutletSearchService;
    private final CustomerSearchHistoryService customerSearchHistoryService;
    private final CustomerFavoriteService customerFavoriteService;
    private final FeedbackService feedbackService;
    private final ItemService itemService;
    private final NotificationService notificationService;

    
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

    
    @PutMapping(value = "/profile/image", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResponse> changeProfileImage(@Valid @RequestBody ProfileImageChangeRequest request) {
        User user = getAuthenticatedCustomer();
        UserResponse result = userService.changeProfileImage(user.getUserId(), request.getFileName());
        return ResponseEntity.ok(result);
    }

    
    @PostMapping(value = "/outlets/nearest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<NearestOutletSearchResponse> searchNearestOutlets(@Valid @RequestBody NearestOutletSearchRequest request) {
        User user = getAuthenticatedCustomer();
        Long customerId = user.getCustomerId(); 
        NearestOutletSearchResponse response = nearestOutletSearchService.searchNearestOutlets(request, customerId);
        return ResponseEntity.ok(response);
    }

    
    @GetMapping(value = "/items/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ItemListItemResponse>> searchItems(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, name = "outletId") Long outletId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean availability) {
        getAuthenticatedCustomer();
        List<ItemListItemResponse> list = itemService.search(search, categoryId, outletId, status, availability);
        return ResponseEntity.ok(list);
    }

    
    @PostMapping(value = "/search-history", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerSearchHistoryResponse> createSearchHistory(@Valid @RequestBody CustomerSearchHistoryRequest request) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        Long customerId = user.getCustomerId();
        if (request.getCustomerId() != null && !request.getCustomerId().equals(customerId)) {
            throw new InvalidRequestException(ResponseCodes.VALIDATION_ERROR_CODE, "customerId must match the authenticated customer");
        }
        if (request.getCustomerId() != null) {
            customerId = request.getCustomerId();
        }
        CustomerSearchHistoryResponse response = customerSearchHistoryService.create(customerId, request);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }

    
    @GetMapping(value = "/search-history", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CustomerSearchHistoryResponse>> listSearchHistory() {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        return ResponseEntity.ok(customerSearchHistoryService.listByCustomerId(user.getCustomerId()));
    }

    
    @GetMapping(value = "/search-history/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerSearchHistoryResponse> getSearchHistory(@PathVariable Long id) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        return ResponseEntity.ok(customerSearchHistoryService.getById(id, user.getCustomerId()));
    }

    
    @PutMapping(value = "/search-history/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerSearchHistoryResponse> updateSearchHistory(
            @PathVariable Long id,
            @Valid @RequestBody CustomerSearchHistoryRequest request) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        CustomerSearchHistoryResponse response = customerSearchHistoryService.update(id, user.getCustomerId(), request);
        notifyCustomerAction(user.getUserId(), "Search history updated", "Your search history was updated. Date & time: " + formatNotificationDateTime());
        return ResponseEntity.ok(response);
    }

    
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

    
    @PostMapping(value = "/feedback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<FeedbackResponse> saveFeedback(@Valid @RequestBody FeedbackRequest request) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        FeedbackResponse response = feedbackService.create(user.getCustomerId(), request);
        notifyCustomerAction(user.getUserId(), "Feedback submitted", "Your feedback was submitted successfully. Date & time: " + formatNotificationDateTime());
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }

    
    @GetMapping(value = "/feedback", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<FeedbackResponse>> listMyFeedback() {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        return ResponseEntity.ok(feedbackService.listByCustomerId(user.getCustomerId()));
    }

    
    @GetMapping(value = "/outlets/{outletId}/feedbacks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<FeedbackResponse>> listFeedbackByOutlet(@PathVariable Long outletId) {
        getAuthenticatedCustomer();
        return ResponseEntity.ok(feedbackService.listByOutletId(outletId));
    }

    
    @PostMapping(value = "/favorites", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerFavoriteResponse> createFavorite(@Valid @RequestBody CustomerFavoriteRequest request) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        CustomerFavoriteResponse response = customerFavoriteService.create(user.getCustomerId(), request);
        notifyCustomerAction(user.getUserId(), "Favorite added", "An outlet was added to your favorites. Date & time: " + formatNotificationDateTime());
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }

    
    @GetMapping(value = "/favorites", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CustomerFavoriteResponse>> listMyFavorites() {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        return ResponseEntity.ok(customerFavoriteService.listByCustomerId(user.getCustomerId()));
    }

    
    @GetMapping(value = "/favorites/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerFavoriteResponse> getFavorite(@PathVariable Long id) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        return ResponseEntity.ok(customerFavoriteService.getById(id, user.getCustomerId()));
    }

    
    @PutMapping(value = "/favorites/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CustomerFavoriteResponse> updateFavorite(
            @PathVariable Long id,
            @Valid @RequestBody CustomerFavoriteRequest request) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        CustomerFavoriteResponse response = customerFavoriteService.update(id, user.getCustomerId(), request);
        notifyCustomerAction(user.getUserId(), "Favorite updated", "Your favorite was updated. Date & time: " + formatNotificationDateTime());
        return ResponseEntity.ok(response);
    }

    
    @DeleteMapping(value = "/favorites/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Response> deleteFavorite(@PathVariable Long id) {
        User user = getAuthenticatedCustomer();
        ensureCustomerId(user);
        customerFavoriteService.delete(id, user.getCustomerId());
        notifyCustomerAction(user.getUserId(), "Favorite removed", "An outlet was removed from your favorites. Date & time: " + formatNotificationDateTime());
        Response response = new Response();
        response.setStatus("SUCCESS");
        response.setResponseCode("00");
        response.setResponseMessage("Favorite removed.");
        return ResponseEntity.ok(response);
    }

    
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

    
    private void notifyCustomerAction(Long userId, String title, String body) {
        try {
            notificationService.saveNotification(userId, "CUSTOMER", title, body);
        } catch (Exception e) {
            log.warn("Failed to send customer action notification for user {}: {}", userId, e.getMessage());
        }
    }

    private static String formatNotificationDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"));
    }
}
