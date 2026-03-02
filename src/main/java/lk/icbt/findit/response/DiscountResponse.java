package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountResponse extends Response {

    private Long discountId;
    private String discountName;
    private String discountType;   // PERCENTAGE, FIXED_AMOUNT
    private Double discountValue;
    private String startDate;      // ISO date
    private String endDate;        // ISO date
    private String discountStatus; // ACTIVE, INACTIVE, etc.
    private List<Long> itemIds;
}
