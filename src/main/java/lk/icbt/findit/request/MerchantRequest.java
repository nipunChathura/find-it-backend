package lk.icbt.findit.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;

/**
 * Request for merchant API endpoints (e.g. onboarding).
 */
@Data
public class MerchantRequest {

    @NotBlank(message = "Merchant name is required")
    @Size(max = 255)
    private String merchantName;

    @NotBlank(message = "Merchant email is required")
    @Email(message = "Invalid email format")
    private String merchantEmail;

    @Size(max = 20)
    private String merchantNic;

    private String merchantProfileImage;

    @NotBlank(message = "Merchant address is required")
    @Size(max = 500)
    private String merchantAddress;

    @NotBlank(message = "Merchant phone number is required")
    @Pattern(regexp = "^(?:\\+94|0)?7\\d{8}$", message = "Invalid phone number")
    private String merchantPhoneNumber;

    @NotNull(message = "Merchant type is required")
    private MerchantType merchantType;
}
