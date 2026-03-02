package lk.icbt.findit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request for customer login. Customers log in with email and password.
 */
@Data
public class CustomerLoginRequest {

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
