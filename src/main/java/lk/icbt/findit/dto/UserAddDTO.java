package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAddDTO extends Response {
    private String username;
    private String password;
    private String email;
    private Role role;
    private Long merchantId;
    private Long subMerchantId;
    private String status;

    private Long userId;
    private String userStatus;
    private String isSystemUser;
}
