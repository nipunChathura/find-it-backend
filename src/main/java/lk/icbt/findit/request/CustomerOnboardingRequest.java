package lk.icbt.findit.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.icbt.findit.entity.MembershipType;
import lombok.Data;

/**
 * Request for customer onboarding (self-registration). Includes customer details and login credentials.
 */
@Data
public class CustomerOnboardingRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;

    /** Phone number; any country format allowed (e.g. +94 77 123 4567, +1 555 123 4567). */
    @Size(max = 30)
    private String phoneNumber;

    @Size(max = 20)
    private String nic;

    @Size(max = 20)
    private String dob;

    @Size(max = 20)
    private String gender;

    @Size(max = 100)
    private String countryName;

    @Size(max = 500)
    private String profileImage;

    @NotNull(message = "Membership type is required")
    private MembershipType membershipType;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "Username must be 4-20 alphanumeric characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$", message = "Password must be 6-12 characters with at least one letter and one digit")
    private String password;
}
