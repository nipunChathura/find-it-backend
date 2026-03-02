package lk.icbt.findit.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DiscountRequest {

    @NotBlank(message = "Discount name is required")
    @Size(max = 255)
    private String discountName;

    @NotNull(message = "Discount type is required")
    private String discountType;   // PERCENTAGE or FIXED_AMOUNT

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0", inclusive = true, message = "Discount value must be >= 0")
    private Double discountValue;

    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 50)
    private String status;

    /** Item IDs to link to this discount. Optional; can be empty. */
    private List<Long> itemIds;
}
