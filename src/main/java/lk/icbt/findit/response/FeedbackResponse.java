package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackResponse extends Response {

    private Long feedbackId;
    private String feedbackText;
    private Double rating;
    private Long customerId;
    private Long outletId;
    private String outletName;
    private Date createdDatetime;
}
