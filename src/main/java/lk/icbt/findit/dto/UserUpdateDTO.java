package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateDTO extends Response {
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private Long merchantId;
    private Long subMerchantId;
    private String status;

    private String userStatus;
    private String isSystemUser;
}
