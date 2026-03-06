package lk.icbt.findit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lk.icbt.findit.entity.MembershipType;

@Data
public class CustomerRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

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

    @Size(max = 255)
    private String email;

    @Size(max = 30)
    private String phoneNumber;

    @NotNull(message = "Membership type is required")
    private MembershipType membershipType;

    @Size(max = 50)
    private String status;
}
