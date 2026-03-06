package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardSummaryResponse extends Response {

    private Long users;
    /** User count by role (excluding DELETED). */
    private Long usersAdmin;
    private Long usersSysadmin;
    private Long usersUser;
    private Long merchants;
    private Long subMerchants;
    private Long items;
    private Long customers;
    private Long outlets;
    private Long categories;
    private Long pendingApprovals;
    private Long activeDiscounts;
}
