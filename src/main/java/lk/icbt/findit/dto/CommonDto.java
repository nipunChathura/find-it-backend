package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonDto {
    private String status;
    private String responseCode;
    private String responseMessage;

    private Long userId;
    private Long customerId;
    private Long merchantId;
}
