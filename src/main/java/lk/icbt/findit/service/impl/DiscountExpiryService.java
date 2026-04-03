package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.entity.Discount;
import lk.icbt.findit.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountExpiryService {

    private final DiscountRepository discountRepository;

    @Scheduled(cron = "0 0 1 * * ?", zone = Constants.TIME_ZONE)
    @Transactional
    public void deactivateDiscountsPastValidity() {
        ZoneId zone = ZoneId.of(Constants.TIME_ZONE);
        Date startOfToday = Date.from(LocalDate.now(zone).atStartOfDay(zone).toInstant());
        List<Discount> expired = discountRepository.findActiveWithEndDateBefore(
                Constants.DISCOUNT_ACTIVE_STATUS, startOfToday);
        if (expired.isEmpty()) {
            return;
        }
        Date now = new Date();
        for (Discount d : expired) {
            d.setStatus(Constants.DISCOUNT_INACTIVE_STATUS);
            d.setModifiedDatetime(now);
            discountRepository.save(d);
        }
        log.info("Discount expiry: {} discount(s) set to INACTIVE (endDate before {})", expired.size(), startOfToday);
    }
}
