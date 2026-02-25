package lk.icbt.findit.request;

import lombok.Data;

/**
 * Single request class for all user API endpoints.
 * Only the fields relevant to each endpoint are populated; others may be null.
 * Validation is performed in the service layer per operation.
 */
@Data
public class UserRequest {

    private Long userId;
    private String username;
    private String password;
    private String currentPassword;
    private String newPassword;
}
