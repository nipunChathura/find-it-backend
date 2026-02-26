package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response for merchant / sub-merchant login. Includes JWT token and user/merchant context.
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
}
