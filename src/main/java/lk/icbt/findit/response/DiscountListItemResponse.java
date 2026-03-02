package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountListItemResponse {

    private Long discountId;
    private String discountName;
    private String discountType;
    private Double discountValue;
    private String startDate;
    private String endDate;
    private String discountStatus;
    private List<Long> itemIds;
    /** Item id and name for each linked item. */
    private List<ItemIdNameResponse> items;
}
