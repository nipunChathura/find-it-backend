package lk.icbt.findit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OutletStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;
}
