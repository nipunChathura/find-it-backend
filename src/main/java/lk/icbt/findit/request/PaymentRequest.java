package lk.icbt.findit.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequest {

    @NotNull(message = "Outlet is required")
    private Long outletId;

    @NotNull(message = "Payment type is required")
    @Size(max = 50)
    private String paymentType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0", inclusive = false, message = "Amount must be > 0")
    private BigDecimal amount;

    private LocalDate paymentDate;

    @Size(max = 20)
    private String paidMonth;

    @Size(max = 500)
    private String receiptImage;

    @Size(max = 50)
    private String status;
}
