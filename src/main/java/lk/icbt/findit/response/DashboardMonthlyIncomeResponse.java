package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Dashboard monthly income for chart: income per month for the last N months.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardMonthlyIncomeResponse extends Response {

    /** Number of months requested (e.g. 12). */
    private Integer months;
    /** Income per month, oldest first. */
    private List<MonthlyIncomeItem> incomeData;
}
