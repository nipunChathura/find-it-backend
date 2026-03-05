package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Item;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.OutletType;
import lk.icbt.findit.exception.InvalidRequestException;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NearestOutletSearchServiceImpl implements NearestOutletSearchService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final ItemRepository itemRepository;
    private final OutletScheduleService outletScheduleService;

    @Override
    public NearestOutletSearchResponse searchNearestOutlets(NearestOutletSearchRequest request) {
        String itemName = request.getItemName() != null ? request.getItemName().trim() : "";
        if (itemName.isEmpty()) {
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "Item name is required");
        }

        OutletType outletType = null;
        if (request.getOutletType() != null && !request.getOutletType().isBlank()) {
            try {
                outletType = OutletType.valueOf(request.getOutletType().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException(ResponseCodes.VALIDATION_ERROR_CODE, "Invalid outlet type: " + request.getOutletType());
            }
        }

        List<Item> items = itemRepository.findForNearestOutletSearch(
                itemName,
                request.getCategoryId(),
                outletType
        );

        if (items.isEmpty()) {
            NearestOutletSearchResponse response = new NearestOutletSearchResponse();
            response.setStatus(ResponseStatus.SUCCESS.getStatus());
            response.setResponseCode(ResponseCodes.SUCCESS_CODE);
            response.setResponseMessage("No outlets found with matching items.");
            response.setOutlets(Collections.emptyList());
            return response;
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

            List<NearestOutletItemDetailResponse> itemDetails = outletItems.stream()
                    .map(this::toItemDetail)
                    .collect(Collectors.toList());
            resultItem.setItems(itemDetails);

            results.add(resultItem);
        }

        results.sort(Comparator.comparingDouble(NearestOutletResultItem::getDistanceKm));

        NearestOutletSearchResponse response = new NearestOutletSearchResponse();
        response.setStatus(ResponseStatus.SUCCESS.getStatus());
        response.setResponseCode(ResponseCodes.SUCCESS_CODE);
        response.setResponseMessage("Search completed.");
        response.setOutlets(results);
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

    private NearestOutletItemDetailResponse toItemDetail(Item item) {
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
        return d;
    }
}
