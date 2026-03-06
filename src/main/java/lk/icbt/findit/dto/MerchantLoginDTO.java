package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.Role;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantLoginDTO extends Response {
    private String token;
    private Long userId;
    private String username;
    private String userStatus;
    private Role role;
    private Long merchantId;
    private Long subMerchantId;
    private String profileImageUrl;
}
