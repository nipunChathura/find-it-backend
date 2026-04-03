package lk.icbt.findit.controller;

import jakarta.validation.Valid;
import lk.icbt.findit.request.PaymentRequest;
import lk.icbt.findit.response.PaymentListItemResponse;
import lk.icbt.findit.response.PaymentResponse;
import lk.icbt.findit.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse result = paymentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long paymentId) {
        PaymentResponse result = paymentService.getById(paymentId);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<PaymentListItemResponse>> list(
            @RequestParam(required = false) Long outletId,
            @RequestParam(required = false) String status) {
        List<PaymentListItemResponse> list = paymentService.list(outletId, status);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @PutMapping(value = "/{paymentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PaymentResponse> update(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse result = paymentService.update(paymentId, request);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('SYSADMIN', 'ADMIN', 'MERCHANT', 'SUBMERCHANT')")
    @DeleteMapping(value = "/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long paymentId) {
        paymentService.delete(paymentId);
        return ResponseEntity.noContent().build();
    }
}
