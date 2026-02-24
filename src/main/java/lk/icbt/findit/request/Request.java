package lk.icbt.findit.request;

import lombok.Data;

@Data
public class Request {
    private Long userId;
    private Long customerId;
    private Long merchantId;
}
