package lk.icbt.findit.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerFavoriteRequest {

    @NotNull(message = "Outlet ID is required")
    private Long outletId;

    
    private String nickname;

    @DecimalMin(value = "1.0", message = "Rating must be at least 1")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5")
    private Double rating;
}
