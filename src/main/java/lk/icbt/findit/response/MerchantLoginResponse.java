package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response for merchant / sub-merchant login. Includes JWT token, user context,
 * and mainMerchantInfo / subMerchantInfo objects.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantLoginResponse extends Response {
    private String token;
    private Long userId;
    private String username;
    private String userStatus;
    private Role role;
    private Long merchantId;
    private Long subMerchantId;
    private String profileImageUrl;
    /** Full main merchant details (when role = MERCHANT, or parent when role = SUBMERCHANT). */
    private MainMerchantInfo mainMerchantInfo;
    /** Sub-merchant details (when role = SUBMERCHANT only). */
    private SubMerchantInfo subMerchantInfo;
}
