package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse extends Response {

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
