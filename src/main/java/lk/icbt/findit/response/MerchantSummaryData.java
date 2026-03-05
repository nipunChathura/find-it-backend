package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantSummaryData {
    private Long totalMerchants;
    private Long totalOutlets;
    private List<OutletDistributionItem> outletDistribution;
}
