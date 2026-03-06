package lk.icbt.findit.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequest {

    @NotBlank(message = "Item name is required")
    @Size(max = 255)
    private String itemName;

    @Size(max = 1000)
    private String itemDescription;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Outlet is required")
    private Long outletId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", inclusive = true, message = "Price must be >= 0")
    private BigDecimal price;

    private Boolean availability = true;

    @Size(max = 500)
    private String itemImage;

    @Size(max = 50)
    private String status;
}
