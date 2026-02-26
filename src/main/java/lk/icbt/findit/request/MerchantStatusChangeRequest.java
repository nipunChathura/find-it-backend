package lk.icbt.findit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MerchantStatusChangeRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|PENDING)$", message = "Status must be ACTIVE, INACTIVE, or PENDING")
    private String status;

    private String inactiveReason;
}
