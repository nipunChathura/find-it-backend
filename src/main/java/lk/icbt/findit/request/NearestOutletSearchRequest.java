package lk.icbt.findit.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request for customer app: search nearest outlets by item name, location, distance and filters.
 */
@Data
public class NearestOutletSearchRequest {

    /** Customer's latitude. */
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90", message = "Latitude must be between -90 and 90")
    private Double latitude;

    /** Customer's longitude. */
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180", message = "Longitude must be between -180 and 180")
    private Double longitude;

    /** Item name to search (partial match). */
    @NotNull(message = "Item name is required")
    private String itemName;

    /** Maximum distance from customer location in kilometres. Only outlets within this distance are returned. */
    @NotNull(message = "Distance (km) is required")
    @DecimalMin(value = "0.1", message = "Distance must be at least 0.1 km")
    @DecimalMax(value = "500", message = "Distance must be at most 500 km")
    private Double distanceKm;

    /** Optional: filter by item category ID. */
    private Long categoryId;

    /** Optional: filter by outlet type (e.g. PHYSICAL_STORE, ONLINE_STORE). */
    private String outletType;
}
