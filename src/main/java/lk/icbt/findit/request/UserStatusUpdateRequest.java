package lk.icbt.findit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|PENDING|APPROVED|DELETED)$", message = "Status must be ACTIVE, INACTIVE, PENDING, APPROVED, or DELETED")
    private String status;
}
