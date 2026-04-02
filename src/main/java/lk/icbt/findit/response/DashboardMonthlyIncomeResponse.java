package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardMonthlyIncomeResponse extends Response {

    
    private Integer months;
    
    private List<MonthlyIncomeItem> incomeData;
}
