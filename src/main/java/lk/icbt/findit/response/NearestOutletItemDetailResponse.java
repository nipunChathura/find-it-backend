package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;


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
    
    private Boolean discountAvailable;
    
    private String discountName;
    
    private BigDecimal offerPrice;
}
