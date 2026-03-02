package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.request.SendNotificationRequest;
import lk.icbt.findit.response.NotificationResponse;
import lk.icbt.findit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Notification API: send (and optionally push via FCM), list unread, mark as read, list by type.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'USER', 'CUSTOMER')")
    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<NotificationResponse> send(@Valid @RequestBody SendNotificationRequest request) {
        NotificationResponse result = notificationService.sendAndSave(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'USER', 'CUSTOMER')")
    @GetMapping(value = "/unread/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<NotificationResponse>> getUnread(@PathVariable Long userId) {
        List<NotificationResponse> list = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'USER', 'CUSTOMER')")
    @PostMapping(value = "/read/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        NotificationResponse result = notificationService.markAsRead(id);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT', 'USER', 'CUSTOMER')")
    @GetMapping(value = "/type/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<NotificationResponse>> getByType(
            @PathVariable Long userId,
            @RequestParam String type) {
        List<NotificationResponse> list = notificationService.getNotificationsByType(userId, type);
        return ResponseEntity.ok(list);
    }
}
