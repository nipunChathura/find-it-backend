package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.CustomerFavorite;
import lk.icbt.findit.entity.Discount;
import lk.icbt.findit.entity.DiscountItem;
import lk.icbt.findit.entity.DiscountType;
import lk.icbt.findit.entity.Item;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.OutletType;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.CustomerFavoriteRepository;
import lk.icbt.findit.repository.DiscountItemRepository;
import lk.icbt.findit.repository.ItemRepository;
import lk.icbt.findit.request.NearestOutletSearchRequest;
import lk.icbt.findit.response.NearestOutletItemDetailResponse;
import lk.icbt.findit.response.NearestOutletResultItem;
import lk.icbt.findit.response.NearestOutletSearchResponse;
import lk.icbt.findit.response.OutletStatusResponse;
import lk.icbt.findit.service.NearestOutletSearchService;
import lk.icbt.findit.service.OutletScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NearestOutletSearchServiceImpl implements NearestOutletSearchService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    /** Default maximum number of outlets returned (nearest first). */
    private static final int DEFAULT_MAX_RESULTS = 20;

    private final ItemRepository itemRepository;
    private final DiscountItemRepository discountItemRepository;
    private final OutletScheduleService outletScheduleService;
    private final CustomerFavoriteRepository customerFavoriteRepository;

    @Override
    public NearestOutletSearchResponse searchNearestOutlets(NearestOutletSearchRequest request, Long customerId) {
        String itemName = request.getItemName() != null ? request.getItemName().trim() : null;

        OutletType outletType = null;
        if (request.getOutletType() != null && !request.getOutletType().isBlank()) {
            try {
                outletType = OutletType.valueOf(request.getOutletType().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException(ResponseCodes.VALIDATION_ERROR_CODE, "Invalid outlet type: " + request.getOutletType());
            }
        }

        List<Item> items;
        if (itemName != null && !itemName.isEmpty()) {
            items = itemRepository.findForNearestOutletSearch(
                    itemName,
                    request.getCategoryId(),
                    outletType
            );
        } else {
            items = itemRepository.findForNearestOutletSearchAllItems(
                    request.getCategoryId(),
                    outletType
            );
        }

        if (items.isEmpty()) {
            NearestOutletSearchResponse response = new NearestOutletSearchResponse();
            response.setStatus(ResponseStatus.SUCCESS.getStatus());
            response.setResponseCode(ResponseCodes.SUCCESS_CODE);
            response.setResponseMessage(itemName != null ? "No outlets found with matching items." : "No outlets found within criteria.");
            response.setOutlets(Collections.emptyList());
            return response;
        }

        List<Long> itemIds = items.stream().map(Item::getItemId).distinct().collect(Collectors.toList());
        Date nowDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        List<DiscountItem> discountItems = discountItemRepository.findByItemItemIdInAndDiscountActiveAndDateValid(itemIds, nowDate);
        Map<Long, Discount> itemIdToDiscount = new HashMap<>();
        for (DiscountItem di : discountItems) {
            itemIdToDiscount.putIfAbsent(di.getItem().getItemId(), di.getDiscount());
        }

        double custLat = request.getLatitude();
        double custLon = request.getLongitude();
        double maxDistanceKm = request.getDistanceKm();

        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<Item>> outletToItems = items.stream()
                .collect(Collectors.groupingBy(i -> i.getOutlet().getOutletId()));

        List<NearestOutletResultItem> results = new ArrayList<>();

        for (Map.Entry<Long, List<Item>> entry : outletToItems.entrySet()) {
            Long outletId = entry.getKey();
            List<Item> outletItems = entry.getValue();
            Outlet outlet = outletItems.get(0).getOutlet();

            double distanceKm = haversineKm(custLat, custLon, outlet.getLatitude(), outlet.getLongitude());
            if (distanceKm > maxDistanceKm) {
                continue;
            }

            // Open/closed from outlet_schedule by schedule type: NORMAL, EMERGENCY, TEMPORARY, DAILY
            OutletStatusResponse statusResponse = outletScheduleService.getOutletStatus(outletId, now);
            if (!OutletStatusResponse.STATUS_OPEN.equals(statusResponse.getStatus())) {
                continue;
            }

            NearestOutletResultItem resultItem = new NearestOutletResultItem();
            resultItem.setOutletId(outlet.getOutletId());
            resultItem.setOutletName(outlet.getOutletName());
            resultItem.setContactNumber(outlet.getContactNumber());
            resultItem.setEmailAddress(outlet.getEmailAddress());
            resultItem.setAddressLine1(outlet.getAddressLine1());
            resultItem.setAddressLine2(outlet.getAddressLine2());
            resultItem.setOutletType(outlet.getOutletType());
            resultItem.setBusinessCategory(outlet.getBusinessCategory());
            resultItem.setLatitude(outlet.getLatitude());
            resultItem.setLongitude(outlet.getLongitude());
            resultItem.setStatus(outlet.getStatus());
            resultItem.setRating(outlet.getRating());
            resultItem.setDistanceKm(round(distanceKm, 2));
            resultItem.setCurrentStatus(statusResponse.getStatus());
            resultItem.setScheduleType(statusResponse.getScheduleType());

            List<NearestOutletItemDetailResponse> itemDetails = outletItems.stream()
                    .map(item -> toItemDetail(item, itemIdToDiscount))
                    .collect(Collectors.toList());
            resultItem.setItems(itemDetails);

            results.add(resultItem);
        }

        // Join with customer_favorite: set is_favorite, customer_favorite_id, nickname per outlet
        Map<Long, CustomerFavorite> outletIdToFavorite = new HashMap<>();
        if (customerId != null && !results.isEmpty()) {
            List<Long> outletIds = results.stream().map(NearestOutletResultItem::getOutletId).distinct().collect(Collectors.toList());
            List<CustomerFavorite> favorites = customerFavoriteRepository.findByCustomer_CustomerIdAndOutlet_OutletIdIn(customerId, outletIds);
            for (CustomerFavorite f : favorites) {
                if (f.getOutlet() != null) {
                    outletIdToFavorite.put(f.getOutlet().getOutletId(), f);
                }
            }
        }
        for (NearestOutletResultItem item : results) {
            CustomerFavorite fav = outletIdToFavorite.get(item.getOutletId());
            item.setIsFavorite(fav != null);
            if (fav != null) {
                item.setCustomerFavoriteId(fav.getId());
                item.setNickname(fav.getNickname());
            }
        }

        results.sort(Comparator.comparingDouble(NearestOutletResultItem::getDistanceKm));

        List<NearestOutletResultItem> limited = results.size() <= DEFAULT_MAX_RESULTS
                ? results
                : results.subList(0, DEFAULT_MAX_RESULTS);

        NearestOutletSearchResponse response = new NearestOutletSearchResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setResponseMessage("Search completed.");
        response.setOutlets(limited);
        return response;
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        return (double) Math.round(value * factor) / factor;
    }

    private NearestOutletItemDetailResponse toItemDetail(Item item, Map<Long, Discount> itemIdToDiscount) {
        NearestOutletItemDetailResponse d = new NearestOutletItemDetailResponse();
        d.setItemId(item.getItemId());
        d.setItemName(item.getItemName());
        d.setItemDescription(item.getItemDescription());
        d.setPrice(item.getPrice());
        d.setAvailability(item.getAvailability());
        d.setItemImage(item.getItemImage());
        d.setStatus(item.getStatus());
        if (item.getCategory() != null) {
            d.setCategoryName(item.getCategory().getCategoryName());
        }
        Discount discount = itemIdToDiscount.get(item.getItemId());
        if (discount != null && item.getPrice() != null) {
            d.setDiscountAvailable(true);
            d.setDiscountName(discount.getDiscountName());
            d.setOfferPrice(computeOfferPrice(item.getPrice(), discount));
        } else {
            d.setDiscountAvailable(Boolean.FALSE);
        }
        return d;
    }

    private BigDecimal computeOfferPrice(BigDecimal price, Discount discount) {
        if (price == null || discount == null || discount.getDiscountValue() == null) {
            return price;
        }
        if (discount.getDiscountType() == DiscountType.PERCENTAGE) {
            double pct = 1.0 - (discount.getDiscountValue() / 100.0);
            return price.multiply(BigDecimal.valueOf(pct)).setScale(2, RoundingMode.HALF_UP);
        }
        if (discount.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            BigDecimal reduced = price.subtract(BigDecimal.valueOf(discount.getDiscountValue()));
            return reduced.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : reduced.setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }
}
