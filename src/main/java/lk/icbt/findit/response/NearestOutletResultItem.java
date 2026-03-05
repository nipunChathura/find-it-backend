package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    /** OPEN or CLOSED at current datetime. */
    private String currentStatus;

    /** Matching items at this outlet (items that match the search item name, available and active). */
    private List<NearestOutletItemDetailResponse> items;
}
