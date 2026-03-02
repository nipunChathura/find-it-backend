package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.request.DiscountRequest;
import lk.icbt.findit.response.DiscountListItemResponse;
import lk.icbt.findit.response.DiscountResponse;
import lk.icbt.findit.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Item Discount CRUD. Access: SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT.
 */
@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<DiscountResponse> create(@Valid @RequestBody DiscountRequest request) {
        DiscountResponse result = discountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "/{discountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<DiscountResponse> getById(@PathVariable Long discountId) {
        DiscountResponse result = discountService.getById(discountId);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<DiscountListItemResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long itemId) {
        List<DiscountListItemResponse> list = discountService.list(status, itemId);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PutMapping(value = "/{discountId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<DiscountResponse> update(
            @PathVariable Long discountId,
            @Valid @RequestBody DiscountRequest request) {
        DiscountResponse result = discountService.update(discountId, request);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @DeleteMapping(value = "/{discountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long discountId) {
        discountService.delete(discountId);
        return ResponseEntity.noContent().build();
    }
}
