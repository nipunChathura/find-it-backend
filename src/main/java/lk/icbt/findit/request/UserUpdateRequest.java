package lk.icbt.findit.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.icbt.findit.entity.Role;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Size(min = 4, max = 100)
    private String username;

    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;

    private Role role;

    private Long merchantId;
    private Long subMerchantId;

    @Pattern(regexp = "^(ACTIVE|INACTIVE|PENDING|APPROVED|DELETED)$", message = "Status must be ACTIVE, INACTIVE, PENDING, APPROVED, or DELETED")
    private String status;
}
