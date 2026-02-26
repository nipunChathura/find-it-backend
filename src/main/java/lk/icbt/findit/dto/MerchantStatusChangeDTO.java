package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantStatusChangeDTO extends Response {

    private Long merchantId;
    private String newStatus;
    private String inactiveReason;

    private String merchantName;
    private String merchantEmail;
    private String merchantStatus;
}
