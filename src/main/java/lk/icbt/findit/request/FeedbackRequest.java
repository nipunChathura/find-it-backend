package lk.icbt.findit.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackRequest {

    @NotNull(message = "Outlet ID is required")
    private Long outletId;

    private String feedbackText;

    /** Rating e.g. 1.0 to 5.0 */
    private Double rating;
}
