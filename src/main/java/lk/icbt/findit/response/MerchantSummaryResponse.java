package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Merchant summary for dashboard: total merchants (excl. deleted), total outlets,
 * outlet distribution by business category for pie chart.
 * Supports both data.* and root-level fields for frontend compatibility.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantSummaryResponse extends Response {

    /** Total merchants (excludes DELETED). */
    private Long totalMerchants;
    /** Total outlets. */
    private Long totalOutlets;
    /** For pie chart: [{ label, value }, ...] by business category. */
    private List<OutletDistributionItem> outletDistribution;
    /** Same fields wrapped for data.totalMerchants / data.outletDistribution access. */
    private MerchantSummaryData data;
}
