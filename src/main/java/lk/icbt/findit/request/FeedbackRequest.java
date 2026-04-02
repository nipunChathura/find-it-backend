package lk.icbt.findit.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackRequest {

    @NotNull(message = "Outlet ID is required")
    private Long outletId;

    private String feedbackText;

    
    private Double rating;
}
