package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserApprovalDTO extends Response {

    /* Request field (controller → service) */
    private Long userId;

    /* Response fields (service → controller) */
    private String username;
    private String userStatus;
}
