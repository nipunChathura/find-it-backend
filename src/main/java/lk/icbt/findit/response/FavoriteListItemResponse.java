package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.BusinessCategory;
import lk.icbt.findit.entity.OutletType;
import lombok.Data;

import java.math.BigDecimal;

/** One favorite with full item and outlet details. */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriteListItemResponse {

    private Long favoriteId;
    // Item detail
    private Long itemId;
    private String itemName;
    private String itemDescription;
    private Long categoryId;
    private String categoryName;
    private String categoryTypeName;
    private BigDecimal price;
    private Boolean availability;
    private String itemImage;
    private String itemStatus;
    // Outlet detail
    private Long outletId;
    private String outletName;
    private String addressLine1;
    private String addressLine2;
    private String contactNumber;
    private String emailAddress;
    private OutletType outletType;
    private BusinessCategory businessCategory;
    private Double latitude;
    private Double longitude;
}
