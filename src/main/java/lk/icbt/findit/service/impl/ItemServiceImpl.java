package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Category;
import lk.icbt.findit.entity.Item;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.CategoryRepository;
import lk.icbt.findit.repository.DiscountItemRepository;
import lk.icbt.findit.repository.ItemRepository;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.repository.UserRepository;
import lk.icbt.findit.request.ItemRequest;
import lk.icbt.findit.response.ItemListItemResponse;
import lk.icbt.findit.response.ItemResponse;
import lk.icbt.findit.service.ItemService;
import lk.icbt.findit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final OutletRepository outletRepository;
    private final DiscountItemRepository discountItemRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ItemResponse create(ItemRequest request) {
        return create(request, null);
    }

    @Override
    @Transactional
    public ItemResponse create(ItemRequest request, String authenticatedUsername) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.CATEGORY_NOT_FOUND_CODE, "Category not found"));
        Outlet outlet = outletRepository.findById(request.getOutletId())
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));

        Item item = new Item();
        item.setItemName(request.getItemName() != null ? request.getItemName().trim() : null);
        item.setItemDescription(trim(request.getItemDescription()));
        item.setCategory(category);
        item.setOutlet(outlet);
        item.setPrice(request.getPrice());
        item.setAvailability(request.getAvailability() != null ? request.getAvailability() : true);
        item.setItemImage(request.getItemImage());
        item.setStatus(request.getStatus() != null && !request.getStatus().isBlank()
                ? request.getStatus().trim()
                : Constants.ITEM_ACTIVE_STATUS);

        Date now = new Date();
        item.setCreatedDatetime(now);
        item.setModifiedDatetime(now);
        item.setVersion(1);

        Item saved = itemRepository.save(item);
        if (authenticatedUsername != null && !authenticatedUsername.isBlank()) {
            userRepository.findByUsername(authenticatedUsername).ifPresent(actor -> {
                if (actor.getRole() == Role.SUBMERCHANT && saved.getOutlet() != null
                        && saved.getOutlet().getSubMerchant() != null && saved.getOutlet().getMerchant() != null) {
                    notificationService.notifySubMerchantActionToMerchantAndSubMerchant(
                            saved.getOutlet().getMerchant().getMerchantId(),
                            saved.getOutlet().getSubMerchant().getSubMerchantId(),
                            saved.getOutlet().getSubMerchant().getMerchantName(),
                            "Item added",
                            "Outlet: " + saved.getOutlet().getOutletName() + ". Item: " + saved.getItemName());
                }
            });
        }
        return toResponse(saved, itemIdsWithActiveDiscount(Collections.singletonList(saved.getItemId())), "Item created successfully.");
    }

    @Override
    public ItemResponse getById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.ITEM_NOT_FOUND_CODE, "Item not found"));
        Set<Long> withDiscount = itemIdsWithActiveDiscount(Collections.singletonList(item.getItemId()));
        return toResponse(item, withDiscount, null);
    }

    @Override
    public List<ItemListItemResponse> getByOutletId(Long outletId) {
        if (outletId == null) {
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "Outlet ID is required");
        }
        outletRepository.findById(outletId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));
        List<Item> list = itemRepository.findByOutletId(outletId);
        Set<Long> withDiscount = list.isEmpty() ? Collections.emptySet() : itemIdsWithActiveDiscount(list.stream().map(Item::getItemId).toList());
        return list.stream().map(i -> toListItem(i, withDiscount)).collect(Collectors.toList());
    }

    @Override
    public List<ItemListItemResponse> search(String search, Long categoryId, Long outletId, String status, Boolean availability) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : null;
        List<Item> list = itemRepository.search(searchParam, categoryId, outletId, statusParam, availability);
        Set<Long> withDiscount = list.isEmpty() ? Collections.emptySet() : itemIdsWithActiveDiscount(list.stream().map(Item::getItemId).toList());
        return list.stream().map(i -> toListItem(i, withDiscount)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemResponse update(Long itemId, ItemRequest request) {
        return update(itemId, request, null);
    }

    @Override
    @Transactional
    public ItemResponse update(Long itemId, ItemRequest request, String authenticatedUsername) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.ITEM_NOT_FOUND_CODE, "Item not found"));

        if (request.getItemName() != null) item.setItemName(request.getItemName().trim());
        if (request.getItemDescription() != null) item.setItemDescription(request.getItemDescription().trim());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new InvalidRequestException(ResponseCodes.CATEGORY_NOT_FOUND_CODE, "Category not found"));
            item.setCategory(category);
        }
        if (request.getOutletId() != null) {
            Outlet outlet = outletRepository.findById(request.getOutletId())
                    .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));
            item.setOutlet(outlet);
        }
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getAvailability() != null) item.setAvailability(request.getAvailability());
        if (request.getItemImage() != null) item.setItemImage(request.getItemImage());
        if (request.getStatus() != null && !request.getStatus().isBlank()) item.setStatus(request.getStatus().trim());

        item.setModifiedDatetime(new Date());

        Item saved = itemRepository.save(item);
        if (authenticatedUsername != null && !authenticatedUsername.isBlank()) {
            userRepository.findByUsername(authenticatedUsername).ifPresent(actor -> {
                if (actor.getRole() == Role.SUBMERCHANT && saved.getOutlet() != null
                        && saved.getOutlet().getSubMerchant() != null && saved.getOutlet().getMerchant() != null) {
                    notificationService.notifySubMerchantActionToMerchantAndSubMerchant(
                            saved.getOutlet().getMerchant().getMerchantId(),
                            saved.getOutlet().getSubMerchant().getSubMerchantId(),
                            saved.getOutlet().getSubMerchant().getMerchantName(),
                            "Item updated",
                            "Outlet: " + saved.getOutlet().getOutletName() + ". Item: " + saved.getItemName());
                }
            });
        }
        return toResponse(saved, itemIdsWithActiveDiscount(Collections.singletonList(saved.getItemId())), "Item updated successfully.");
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.ITEM_NOT_FOUND_CODE, "Item not found"));
        itemRepository.delete(item);
    }

    private static String trim(String s) {
        return s != null ? s.trim() : null;
    }

    private Set<Long> itemIdsWithActiveDiscount(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) return Collections.emptySet();
        return new HashSet<>(discountItemRepository.findItemIdsWithActiveDiscount(itemIds, new Date()));
    }

    private ItemResponse toResponse(Item i, Set<Long> itemIdsWithActiveDiscount, String message) {
        ItemResponse r = new ItemResponse();
        r.setStatus(ResponseStatus.SUCCESS.getStatus());
        r.setResponseCode(ResponseCodes.SUCCESS_CODE);
        r.setResponseMessage(message);
        r.setItemId(i.getItemId());
        r.setItemName(i.getItemName());
        r.setItemDescription(i.getItemDescription());
        r.setCategoryId(i.getCategory() != null ? i.getCategory().getCategoryId() : null);
        r.setCategoryName(i.getCategory() != null ? i.getCategory().getCategoryName() : null);
        r.setCategoryTypeName(i.getCategory() != null && i.getCategory().getCategoryType() != null ? i.getCategory().getCategoryType().name() : null);
        r.setOutletId(i.getOutlet() != null ? i.getOutlet().getOutletId() : null);
        r.setOutletName(i.getOutlet() != null ? i.getOutlet().getOutletName() : null);
        r.setPrice(i.getPrice());
        r.setAvailability(i.getAvailability());
        r.setDiscountAvailability(itemIdsWithActiveDiscount != null && itemIdsWithActiveDiscount.contains(i.getItemId()));
        r.setItemImage(i.getItemImage());
        r.setStatus(i.getStatus());
        return r;
    }

    private ItemListItemResponse toListItem(Item i, Set<Long> itemIdsWithActiveDiscount) {
        ItemListItemResponse r = new ItemListItemResponse();
        r.setItemId(i.getItemId());
        r.setItemName(i.getItemName());
        r.setItemDescription(i.getItemDescription());
        r.setCategoryId(i.getCategory() != null ? i.getCategory().getCategoryId() : null);
        r.setCategoryName(i.getCategory() != null ? i.getCategory().getCategoryName() : null);
        r.setCategoryTypeName(i.getCategory() != null && i.getCategory().getCategoryType() != null ? i.getCategory().getCategoryType().name() : null);
        r.setOutletId(i.getOutlet() != null ? i.getOutlet().getOutletId() : null);
        r.setOutletName(i.getOutlet() != null ? i.getOutlet().getOutletName() : null);
        r.setPrice(i.getPrice());
        r.setAvailability(i.getAvailability());
        r.setDiscountAvailability(itemIdsWithActiveDiscount != null && itemIdsWithActiveDiscount.contains(i.getItemId()));
        r.setItemImage(i.getItemImage());
        r.setStatus(i.getStatus());
        return r;
    }
}
