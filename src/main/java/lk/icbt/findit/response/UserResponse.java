package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends Response {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String userStatus;
    private String isSystemUser;
    private Role role;
    private Long merchantId;
    private Long subMerchantId;
    private Date createdDatetime;
    private String profileImageUrl;
}
