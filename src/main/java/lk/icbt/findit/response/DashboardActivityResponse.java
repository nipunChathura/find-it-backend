package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Dashboard activity for the last N months: counts of new users, merchants, outlets, customers, payments per month.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardActivityResponse extends Response {

    /** Number of months requested (e.g. 6). */
    private Integer months;
    /** Activity per month, oldest first. */
    private List<ActivityMonthItem> activity;
}
