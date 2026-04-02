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
    private String discountType;   
    private Double discountValue;
    private String startDate;      
    private String endDate;        
    private String discountStatus; 
    private String discountImage;
    private List<Long> itemIds;
}
