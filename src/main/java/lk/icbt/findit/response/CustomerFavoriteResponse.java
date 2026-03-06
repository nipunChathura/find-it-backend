package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerFavoriteResponse extends Response {

    private Long id;
    private Long customerId;
    private Long outletId;
    private String nickname;

    /** Outlet details (name, status, address, contact, rating, etc.) */
    private OutletDetailItem outlet;
}
