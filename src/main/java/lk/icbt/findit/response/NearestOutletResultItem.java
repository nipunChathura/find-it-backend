package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lk.icbt.findit.entity.BusinessCategory;
import lk.icbt.findit.entity.OutletType;
import lk.icbt.findit.entity.SubscriptionStatus;
import lombok.Data;

import java.util.List;


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
    private SubscriptionStatus subscriptionStatus;
    private Double rating;

    
    private Double distanceKm;

    
    private String currentStatus;

    
    private String scheduleType;

    
    @JsonProperty("is_favorite")
    private Boolean isFavorite;

    
    @JsonProperty("customer_favorite_id")
    private Long customerFavoriteId;

    
    private String nickname;

    
    private List<NearestOutletItemDetailResponse> items;
}
