package lk.icbt.findit.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.icbt.findit.entity.MerchantType;
import lombok.Data;

/** Request for admin add sub-merchant. merchantId is provided in the path. */
@Data
public class AdminSubMerchantAddRequest {

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

    /**
     * Optional. Login username for the sub-merchant user. When not provided, email is used for login.
     * When password is provided, a User record is created so the sub-merchant can log in.
     */
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "Username must be 4-20 alphanumeric characters")
    private String username;

    /**
     * Optional. When provided, a User record is created with this password so the sub-merchant can log in.
     */
    @Size(min = 6, max = 255)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$", message = "Password must be 6-12 characters with at least one letter and one digit")
    private String password;
}
