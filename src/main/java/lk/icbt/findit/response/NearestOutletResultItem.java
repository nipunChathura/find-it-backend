package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lk.icbt.findit.entity.BusinessCategory;
import lk.icbt.findit.entity.OutletType;
import lombok.Data;

import java.util.List;

/** One outlet in nearest-outlet search: outlet details, distance, current open status, and matching items. */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NearestOutletResultItem {

    private Long outletId;
    private String outletName;
    private String contactNumber;
    private String emailAddress;
    private String addressLine1;
    private String addressLine2;
    private OutletType outletType;
    private BusinessCategory businessCategory;
    private Double latitude;
    private Double longitude;
    private String status;
    private Double rating;

    /** Distance from customer location in kilometres. */
    private Double distanceKm;

    /** OPEN or CLOSED at current datetime (from outlet_schedule by type: NORMAL, EMERGENCY, TEMPORARY, DAILY). */
    private String currentStatus;

    /** Schedule type used for open/closed: NORMAL, EMERGENCY, TEMPORARY, or DAILY. Null if closed with no schedule. */
    private String scheduleType;

    /** True if this outlet is in the customer's favorites (customer_favorite). */
    @JsonProperty("is_favorite")
    private Boolean isFavorite;

    /** Favorite record id; present when is_favorite is true. */
    @JsonProperty("customer_favorite_id")
    private Long customerFavoriteId;

    /** Nickname for this outlet in favorites; present when is_favorite is true. */
    private String nickname;

    /** Matching items at this outlet (items that match the search item name, available and active). */
    private List<NearestOutletItemDetailResponse> items;
}
