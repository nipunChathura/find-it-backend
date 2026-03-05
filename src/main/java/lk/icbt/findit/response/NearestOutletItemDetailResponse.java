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
}
