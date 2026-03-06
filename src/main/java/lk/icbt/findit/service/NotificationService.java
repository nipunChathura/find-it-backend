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

    /**
     * Get all notifications for a user (read and unread), ordered by created date descending.
     */
    List<NotificationResponse> getNotificationsByUserId(Long userId);

    NotificationResponse markAsRead(Long notificationId);

    List<NotificationResponse> getNotificationsByType(Long userId, String type);

    /**
     * Sends a push notification via FCM. Does not persist to database.
     */
    void sendPushNotification(String token, String title, String body);

    /**
     * Notifies all admin-type users (SYSADMIN, ADMIN) when a PENDING item is saved.
     * Saves an in-app notification for each admin. Does not throw; logs and ignores errors.
     *
     * @param itemType  e.g. "User", "Merchant", "Sub-merchant", "Outlet"
     * @param itemName  display name or identifier of the item
     * @param detail   optional extra detail (e.g. "Pending approval")
     */
    void notifyAdminsOfPendingItem(String itemType, String itemName, String detail);

    /**
     * Sends an in-app notification to each of the given user IDs. Does not throw; logs and ignores per-user errors.
     *
     * @param userIds  user IDs to notify
     * @param type     notification type (e.g. "MERCHANT_APPROVAL", "MERCHANT_REJECTED", "SUB_MERCHANT_APPROVAL", "SUB_MERCHANT_REJECTED")
     * @param title    notification title
     * @param body     notification body
     */
    void notifyUserIds(List<Long> userIds, String type, String title, String body);
}
