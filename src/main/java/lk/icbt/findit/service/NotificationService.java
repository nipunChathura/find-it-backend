package lk.icbt.findit.service;

import lk.icbt.findit.request.SendNotificationRequest;
import lk.icbt.findit.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    /**
     * Saves a notification to the database and optionally sends a push notification if token is provided.
     */
    NotificationResponse sendAndSave(SendNotificationRequest request);

    /**
     * Saves a notification to the database only (no push).
     */
    NotificationResponse saveNotification(Long userId, String type, String title, String body);

    List<NotificationResponse> getUnreadNotifications(Long userId);

    NotificationResponse markAsRead(Long notificationId);

    List<NotificationResponse> getNotificationsByType(Long userId, String type);

    /**
     * Sends a push notification via FCM. Does not persist to database.
     */
    void sendPushNotification(String token, String title, String body);
}
