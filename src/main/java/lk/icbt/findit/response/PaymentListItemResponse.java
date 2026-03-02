package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentListItemResponse {

    private Long paymentId;
    private Long outletId;
    private String outletName;
    private String paymentType;
    private BigDecimal amount;
    private String paymentDate;
    private String paidMonth;
    private String receiptImage;
    private String paymentStatus;
}
