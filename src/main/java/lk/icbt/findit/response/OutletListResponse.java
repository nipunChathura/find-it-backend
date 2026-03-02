package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Item for GET /api/outlets list (id, name, status, current open/closed). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletListResponse {

    private Long id;
    private String name;
    private String status;         // ACTIVE, INACTIVE, PENDING, etc.
    private String currentStatus;  // OPEN or CLOSED from schedule
}
