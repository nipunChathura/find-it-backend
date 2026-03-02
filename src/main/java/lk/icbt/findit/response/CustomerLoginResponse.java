package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response for customer login. Includes JWT token and customer/user context.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerLoginResponse extends Response {
    private String token;
    private Long userId;
    private String email;
    private String username;
    private String userStatus;
    private Role role;
    private Long customerId;
}
