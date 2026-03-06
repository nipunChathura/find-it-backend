package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegistrationDTO extends Response {

    /* Request fields (controller → service) */
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "Username must be 4-20 alphanumeric characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 12)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$",
            message = "Password must be 6-12 characters and contain at least one letter and one digit")
    private String password;

    /* Response fields (service → controller) */
    private Long userId;
    private String userStatus;
    private String isSystemUser;
    private Role role;
    private String profileImageUrl;
}
