package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Discount;
import lk.icbt.findit.entity.DiscountItem;
import lk.icbt.findit.entity.DiscountType;
import lk.icbt.findit.entity.Item;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.DiscountItemRepository;
import lk.icbt.findit.repository.DiscountRepository;
import lk.icbt.findit.repository.ItemRepository;
import lk.icbt.findit.request.DiscountRequest;
import lk.icbt.findit.response.DiscountListItemResponse;
import lk.icbt.findit.response.DiscountResponse;
import lk.icbt.findit.response.ItemIdNameResponse;
import lk.icbt.findit.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountItemRepository discountItemRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public DiscountResponse create(DiscountRequest request) {
        Discount discount = new Discount();
        discount.setDiscountName(trim(request.getDiscountName()));
        discount.setDiscountType(parseDiscountType(request.getDiscountType()));
        discount.setDiscountValue(request.getDiscountValue());
        discount.setStartDate(toDate(request.getStartDate()));
        discount.setEndDate(toDate(request.getEndDate()));
        discount.setStatus(request.getStatus() != null && !request.getStatus().isBlank()
                ? request.getStatus().trim()
                : Constants.DISCOUNT_ACTIVE_STATUS);

        Date now = new Date();
        discount.setCreatedDatetime(now);
        discount.setModifiedDatetime(now);
        discount.setVersion(1);

        Discount saved = discountRepository.save(discount);
        linkItems(saved.getDiscountId(), request.getItemIds());
        List<Long> itemIds = request.getItemIds() != null ? request.getItemIds() : Collections.emptyList();
        return toResponse(saved, itemIds, "Discount created successfully.");
    }

    @Override
    public DiscountResponse getById(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.DISCOUNT_NOT_FOUND_CODE, "Discount not found"));
        List<Long> itemIds = discountItemRepository.findByDiscount_DiscountId(discountId).stream()
                .map(di -> di.getItem().getItemId())
                .collect(Collectors.toList());
        return toResponse(discount, itemIds, null);
    }

    @Override
    public List<DiscountListItemResponse> list(String status, Long itemId, Long outletId) {
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : null;
        List<Discount> list = discountRepository.findAllWithFilters(statusParam, itemId, outletId);
        if (list.isEmpty()) return Collections.emptyList();
        List<Long> discountIds = list.stream().map(Discount::getDiscountId).collect(Collectors.toList());
        List<DiscountItem> allItems = discountItemRepository.findByDiscount_DiscountIdInWithItem(discountIds);
        Map<Long, List<Long>> itemIdsByDiscount = new HashMap<>();
        Map<Long, List<ItemIdNameResponse>> itemsByDiscount = new HashMap<>();
        Map<Long, Long> outletIdByDiscount = new HashMap<>();
        Map<Long, String> outletNameByDiscount = new HashMap<>();
        for (DiscountItem di : allItems) {
            Long did = di.getDiscount().getDiscountId();
            itemIdsByDiscount.computeIfAbsent(did, k -> new ArrayList<>()).add(di.getItem().getItemId());
            itemsByDiscount
                    .computeIfAbsent(did, k -> new ArrayList<>())
                    .add(ItemIdNameResponse.builder()
                            .itemId(di.getItem().getItemId())
                            .itemName(di.getItem().getItemName())
                            .build());
            if (di.getItem().getOutlet() != null) {
                outletIdByDiscount.putIfAbsent(did, di.getItem().getOutlet().getOutletId());
                outletNameByDiscount.putIfAbsent(did, di.getItem().getOutlet().getOutletName());
            }
        }
        return list.stream()
                .map(d -> toListItem(d,
                        itemIdsByDiscount.getOrDefault(d.getDiscountId(), Collections.emptyList()),
                        itemsByDiscount.getOrDefault(d.getDiscountId(), Collections.emptyList()),
                        outletIdByDiscount.get(d.getDiscountId()),
                        outletNameByDiscount.get(d.getDiscountId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DiscountResponse update(Long discountId, DiscountRequest request) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.DISCOUNT_NOT_FOUND_CODE, "Discount not found"));

        if (request.getDiscountName() != null) discount.setDiscountName(request.getDiscountName().trim());
        if (request.getDiscountType() != null) discount.setDiscountType(parseDiscountType(request.getDiscountType()));
        if (request.getDiscountValue() != null) discount.setDiscountValue(request.getDiscountValue());
        if (request.getStartDate() != null) discount.setStartDate(toDate(request.getStartDate()));
        if (request.getEndDate() != null) discount.setEndDate(toDate(request.getEndDate()));
        if (request.getStatus() != null && !request.getStatus().isBlank()) discount.setStatus(request.getStatus().trim());

        discount.setModifiedDatetime(new Date());

        if (request.getItemIds() != null) {
            discountItemRepository.deleteByDiscount_DiscountId(discountId);
            linkItems(discountId, request.getItemIds());
        }

        Discount saved = discountRepository.save(discount);
        List<Long> itemIds = discountItemRepository.findByDiscount_DiscountId(discountId).stream()
                .map(di -> di.getItem().getItemId())
                .collect(Collectors.toList());
        return toResponse(saved, itemIds, "Discount updated successfully.");
    }

    @Override
    @Transactional
    public void delete(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.DISCOUNT_NOT_FOUND_CODE, "Discount not found"));
        discountItemRepository.deleteByDiscount_DiscountId(discountId);
        discountRepository.delete(discount);
    }

    private void linkItems(Long discountId, List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) return;
        Discount discount = discountRepository.getReferenceById(discountId);
        Date now = new Date();
        for (Long itemId : itemIds) {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new InvalidRequestException(ResponseCodes.ITEM_NOT_FOUND_CODE, "Item not found: " + itemId));
            DiscountItem di = new DiscountItem();
            di.setDiscount(discount);
            di.setItem(item);
            di.setCreatedDatetime(now);
            di.setModifiedDatetime(now);
            di.setVersion(1);
            discountItemRepository.save(di);
        }
    }

    private static String trim(String s) {
        return s != null ? s.trim() : null;
    }

    private static DiscountType parseDiscountType(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return DiscountType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(ResponseCodes.FAILED_CODE, "Invalid discount type. Use PERCENTAGE or FIXED_AMOUNT.");
        }
    }

    private static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static String formatDate(Date date) {
        if (date == null) return null;
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private DiscountResponse toResponse(Discount d, List<Long> itemIds, String message) {
        DiscountResponse r = new DiscountResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());  // API result
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setResponseMessage(message);
        r.setDiscountId(d.getDiscountId());
        r.setDiscountName(d.getDiscountName());
        r.setDiscountType(d.getDiscountType() != null ? d.getDiscountType().name() : null);
        r.setDiscountValue(d.getDiscountValue());
        r.setStartDate(formatDate(d.getStartDate()));
        r.setEndDate(formatDate(d.getEndDate()));
        r.setStatus(d.getStatus());
        r.setItemIds(itemIds);
        return r;
    }

    private DiscountListItemResponse toListItem(Discount d, List<Long> itemIds, List<ItemIdNameResponse> items, Long outletId, String outletName) {
        DiscountListItemResponse r = new DiscountListItemResponse();
        r.setDiscountId(d.getDiscountId());
        r.setDiscountName(d.getDiscountName());
        r.setDiscountType(d.getDiscountType() != null ? d.getDiscountType().name() : null);
        r.setDiscountValue(d.getDiscountValue());
        r.setStartDate(formatDate(d.getStartDate()));
        r.setEndDate(formatDate(d.getEndDate()));
        r.setDiscountStatus(d.getStatus());
        r.setOutletId(outletId);
        r.setOutletName(outletName);
        r.setItemIds(itemIds);
        r.setItems(items);
        return r;
    }
}
