package lk.icbt.findit.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.icbt.findit.entity.Role;
import lombok.Data;

@Data
public class UserAddRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 100)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255)
    private String password;

    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;

    @NotNull(message = "Role is required")
    private Role role;

    private Long merchantId;
    private Long subMerchantId;

    @Pattern(regexp = "^(ACTIVE|INACTIVE|PENDING|APPROVED)$", message = "Status must be ACTIVE, INACTIVE, PENDING, or APPROVED")
    private String status;
}
