package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.SubscriptionStatus;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletDetailItem {

    private Long outletId;
    private String outletName;
    private String status;
    private SubscriptionStatus subscriptionStatus;
    private String addressLine1;
    private String addressLine2;
    private String contactNumber;
    private String emailAddress;
    private Double latitude;
    private Double longitude;
    private Double rating;
}
