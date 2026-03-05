package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

/** Item detail in nearest-outlet search result (matching search item name). */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NearestOutletItemDetailResponse {

    private Long itemId;
    private String itemName;
    private String itemDescription;
    private String categoryName;
    private BigDecimal price;
    private Boolean availability;
    private String itemImage;
    private String status;
    /** True if the item has at least one active discount (current date within start/end). */
    private Boolean discountAvailable;
    /** Name of the applied discount when discountAvailable is true. */
    private String discountName;
    /** Price after discount when discountAvailable is true. */
    private BigDecimal offerPrice;
}
