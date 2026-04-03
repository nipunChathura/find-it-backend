package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;

import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.SubscriptionStatus;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutletSubscriptionExpiryService {

    private static final String NOTIFICATION_TYPE = "OUTLET_SUBSCRIPTION_EXPIRED";

    private final OutletRepository outletRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 12 * * ?", zone = Constants.TIME_ZONE)
    @Transactional
    public void expireOutletsWithSubscriptionEnded() {
        Date now = new Date();
        List<String> statuses = List.of(
                Constants.OUTLET_ACTIVE_STATUS,
                Constants.OUTLET_PENDING_STATUS,
                Constants.OUTLET_PENDING_SUBSCRIPTION_STATUS
        );
        List<Outlet> candidates = outletRepository.findByStatusInAndSubscriptionValidUntilBefore(statuses, now);
        int expired = 0;
        for (Outlet outlet : candidates) {
            if (outlet.getSubscriptionValidUntil() == null) {
                continue;
            }
            if (!outlet.getSubscriptionValidUntil().before(now)) {
                continue;
            }
            outlet.setStatus(Constants.OUTLET_EXPIRED_SUBSCRIPTION_STATUS);
            outlet.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
            outlet.setModifiedDatetime(now);
            outletRepository.save(outlet);
            notifyMerchantAndSubMerchantUsersOfExpiry(outlet);
            expired++;
        }
        if (expired > 0) {
            log.info("Outlet subscription expiry: {} outlet(s) set to EXPIRED_SUBSCRIPTION (subscriptionValidUntil < {})", expired, now);
        }
    }

    private void notifyMerchantAndSubMerchantUsersOfExpiry(Outlet outlet) {
        Set<Long> userIds = new LinkedHashSet<>();
        if (outlet.getMerchant() != null) {
            Long merchantId = outlet.getMerchant().getMerchantId();
            userRepository.findByMerchantIdAndRoleAndStatusNot(merchantId, Role.MERCHANT, Constants.USER_DELETED_STATUS)
                    .stream()
                    .map(User::getUserId)
                    .forEach(userIds::add);
        }
        if (outlet.getSubMerchant() != null) {
            Long subMerchantId = outlet.getSubMerchant().getSubMerchantId();
            userRepository.findBySubMerchantIdAndRoleAndStatusNot(subMerchantId, Role.SUBMERCHANT, Constants.USER_DELETED_STATUS)
                    .stream()
                    .map(User::getUserId)
                    .forEach(userIds::add);
        }
        if (userIds.isEmpty()) {
            return;
        }
        String outletLabel = outlet.getOutletName() != null ? outlet.getOutletName() : "Outlet";
        notificationService.notifyUserIds(
                new ArrayList<>(userIds),
                NOTIFICATION_TYPE,
                "Outlet subscription expired",
                "Your outlet \"" + outletLabel + "\" subscription has expired. Please renew to continue.");
    }
}
