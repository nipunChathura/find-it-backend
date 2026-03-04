package lk.icbt.findit.service.impl;

import lk.icbt.findit.common.Constants;
import lk.icbt.findit.common.ResponseCodes;
import lk.icbt.findit.common.ResponseStatus;
import lk.icbt.findit.entity.Category;
import lk.icbt.findit.entity.Item;
import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.exception.InvalidRequestException;
import lk.icbt.findit.repository.CategoryRepository;
import lk.icbt.findit.repository.ItemRepository;
import lk.icbt.findit.repository.OutletRepository;
import lk.icbt.findit.request.ItemRequest;
import lk.icbt.findit.response.ItemListItemResponse;
import lk.icbt.findit.response.ItemResponse;
import lk.icbt.findit.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final OutletRepository outletRepository;

    @Override
    @Transactional
    public ItemResponse create(ItemRequest request) {
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
        return toResponse(saved, "Item created successfully.");
    }

    @Override
    public ItemResponse getById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.ITEM_NOT_FOUND_CODE, "Item not found"));
        return toResponse(item, null);
    }

    @Override
    public List<ItemListItemResponse> getByOutletId(Long outletId) {
        if (outletId == null) {
            throw new InvalidRequestException(ResponseCodes.MISSING_PARAMETER_CODE, "Outlet ID is required");
        }
        outletRepository.findById(outletId)
                .orElseThrow(() -> new InvalidRequestException(ResponseCodes.OUTLET_NOT_FOUND_CODE, "Outlet not found"));
        List<Item> list = itemRepository.findByOutletId(outletId);
        return list.stream().map(this::toListItem).collect(Collectors.toList());
    }

    @Override
    public List<ItemListItemResponse> search(String search, Long categoryId, Long outletId, String status, Boolean availability) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : null;
        List<Item> list = itemRepository.search(searchParam, categoryId, outletId, statusParam, availability);
        return list.stream().map(this::toListItem).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemResponse update(Long itemId, ItemRequest request) {
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
        return toResponse(saved, "Item updated successfully.");
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

    private ItemResponse toResponse(Item i, String message) {
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
        r.setItemImage(i.getItemImage());
        r.setStatus(i.getStatus());
        return r;
    }

    private ItemListItemResponse toListItem(Item i) {
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
        r.setItemImage(i.getItemImage());
        r.setStatus(i.getStatus());
        return r;
    }
}
