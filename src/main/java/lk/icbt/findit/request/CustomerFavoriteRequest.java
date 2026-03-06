package lk.icbt.findit.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerFavoriteRequest {

    @NotNull(message = "Outlet ID is required")
    private Long outletId;

    /** Optional nickname for this favorite outlet */
    private String nickname;
}
