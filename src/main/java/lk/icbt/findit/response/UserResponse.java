package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Single response class for all user API endpoints.
 * Only the fields relevant to each endpoint are populated; others may be null.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends Response {
    private String token;
    private Long userId;
    private String username;
    private String userStatus;
    private String isSystemUser;
    private Role role;
}
