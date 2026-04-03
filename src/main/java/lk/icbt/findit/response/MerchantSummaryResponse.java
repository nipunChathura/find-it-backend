package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantSummaryResponse extends Response {

    
    private Long totalMerchants;
    
    private Long totalOutlets;
    
    private List<OutletDistributionItem> outletDistribution;
    
    private MerchantSummaryData data;
}
