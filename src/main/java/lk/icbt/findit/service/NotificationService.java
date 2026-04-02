package lk.icbt.findit.service;

import lk.icbt.findit.request.SendNotificationRequest;
import lk.icbt.findit.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    
    NotificationResponse sendAndSave(SendNotificationRequest request);

    
    NotificationResponse saveNotification(Long userId, String type, String title, String body);

    List<NotificationResponse> getUnreadNotifications(Long userId);

    
    List<NotificationResponse> getNotificationsByUserId(Long userId);

    NotificationResponse markAsRead(Long notificationId);

    List<NotificationResponse> getNotificationsByType(Long userId, String type);

    
    void sendPushNotification(String token, String title, String body);

    
    void notifyAdminsOfPendingItem(String itemType, String itemName, String detail);

    
    void notifyUserIds(List<Long> userIds, String type, String title, String body);

    
    void notifyMerchantOfSubMerchantAction(Long merchantId, String subMerchantName, String actionTitle, String actionBody);

    
    void notifySubMerchantActionToMerchantAndSubMerchant(Long merchantId, Long subMerchantId, String subMerchantName, String actionTitle, String actionBody);

    
    void notifyMerchantUsersOfAction(Long merchantId, String type, String title, String body);
}
