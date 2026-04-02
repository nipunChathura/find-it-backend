package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemListItemResponse {

    private Long itemId;
    private String itemName;
    private String itemDescription;
    private Long categoryId;
    private String categoryName;
    private String categoryTypeName;   
    private Long outletId;
    private String outletName;
    private BigDecimal price;
    private Boolean availability;
    
    private Boolean discountAvailability;
    private String itemImage;
    private String status;
}
