package lk.icbt.findit.service;

import lk.icbt.findit.request.ItemRequest;
import lk.icbt.findit.response.ItemListItemResponse;
import lk.icbt.findit.response.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse create(ItemRequest request);

    
    ItemResponse create(ItemRequest request, String authenticatedUsername);

    ItemResponse getById(Long itemId);

    List<ItemListItemResponse> getByOutletId(Long outletId);

    List<ItemListItemResponse> search(String search, Long categoryId, Long outletId, String status, Boolean availability);

    ItemResponse update(Long itemId, ItemRequest request);

    
    ItemResponse update(Long itemId, ItemRequest request, String authenticatedUsername);

    void delete(Long itemId);
}
