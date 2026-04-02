package lk.icbt.findit.request;

import lombok.Data;


@Data
public class UserRequest {

    private Long userId;
    private String username;
    private String password;
    private String currentPassword;
    private String newPassword;
}
