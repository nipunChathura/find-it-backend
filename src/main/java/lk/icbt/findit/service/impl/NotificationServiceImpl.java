package lk.icbt.findit.service.impl;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Notification;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.NotificationRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.request.SendNotificationRequest;
import lk.icbt.findit.response.NotificationResponse;
import lk.icbt.findit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final String PENDING_ADMIN_TYPE = "PENDING_APPROVAL";

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationResponse sendAndSave(SendNotificationRequest request) {
        NotificationResponse saved = saveNotification(
                request.getUserId(),
                request.getType(),
                request.getTitle(),
                request.getBody() != null ? request.getBody() : ""
        );
        if (request.getToken() != null && !request.getToken().isBlank()) {
            try {
                sendPushNotification(request.getToken(), request.getTitle(), request.getBody() != null ? request.getBody() : "");
            } catch (InvalidRequestException e) {
                log.warn("Push notification failed for user {}: {}", request.getUserId(), e.getMessage());
                // Notification is already saved; do not fail the request
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public NotificationResponse saveNotification(Long userId, String type, String title, String body) {
        Notification entity = new Notification();
        entity.setUserId(userId);
        entity.setType(type != null ? type.trim() : null);
        entity.setTitle(title != null ? title.trim() : null);
        entity.setBody(body != null ? body.trim() : null);
        entity.setIsRead(false);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        Notification saved = notificationRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.NOTIFICATION_NOT_FOUND_CODE, "Notification not found"));
        notification.setIsRead(true);
        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = toResponse(saved);
        response.setResponseMessage("Notification marked as read.");
        return response;
    }

    @Override
    public List<NotificationResponse> getNotificationsByType(Long userId, String type) {
        if (type == null || type.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "Type parameter is required");
        }
        List<Notification> list = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type.trim());
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void notifyUserIds(List<Long> userIds, String type, String title, String body) {
        if (userIds == null || userIds.isEmpty()) return;
        for (Long userId : userIds) {
            try {
                saveNotification(userId, type != null ? type : "NOTIFICATION", title, body);
                log.debug("Notified userId={} type={}", userId, type);
            } catch (Exception e) {
                log.warn("Failed to notify userId={} type={}: {}", userId, type, e.getMessage());
            }
        }
    }

    @Override
    public void notifyAdminsOfPendingItem(String itemType, String itemName, String detail) {
        List<User> admins = userRepository.findByRoleIn(List.of(Role.SYSADMIN, Role.ADMIN));
        if (admins.isEmpty()) {
            log.debug("No admin users found to notify of PENDING item: {} - {}", itemType, itemName);
            return;
        }
        String title = "Pending: " + itemType + " - " + (itemName != null ? itemName : "");
        String body = detail != null && !detail.isBlank() ? detail : (itemType + " is pending approval.");
        for (User admin : admins) {
            try {
                saveNotification(admin.getUserId(), PENDING_ADMIN_TYPE, title, body);
                log.debug("Notified admin userId={} of PENDING item: {} - {}", admin.getUserId(), itemType, itemName);
            } catch (Exception e) {
                log.warn("Failed to notify admin userId={} of PENDING item {}: {}", admin.getUserId(), itemType, e.getMessage());
            }
        }
    }

    @Override
    public void sendPushNotification(String token, String title, String body) {
        if (token == null || token.isBlank()) {
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "FCM token is required");
        }
        if (FirebaseApp.getApps().isEmpty()) {
            throw new InvalidRequestException(ResponseCodes.FIREBASE_MESSAGING_ERROR_CODE, "Firebase is not initialized. Check serviceAccount.json.");
        }
        try {
            Message message = Message.builder()
                    .setToken(token.trim())
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title != null ? title : "")
                            .setBody(body != null ? body : "")
                            .build())
                    .build();
            FirebaseMessaging.getInstance().send(message);
            log.debug("Push notification sent successfully for token (truncated).");
        } catch (FirebaseMessagingException e) {
            log.error("Firebase FCM error: {}", e.getMessage());
            throw new InvalidRequestException(ResponseCodes.FIREBASE_MESSAGING_ERROR_CODE, "Failed to send push notification: " + e.getMessage());
        }
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setId(n.getId());
        r.setUserId(n.getUserId());
        r.setType(n.getType());
        r.setTitle(n.getTitle());
        r.setBody(n.getBody());
        r.setIsRead(n.getIsRead());
        r.setCreatedAt(n.getCreatedAt());
        return r;
    }
}
