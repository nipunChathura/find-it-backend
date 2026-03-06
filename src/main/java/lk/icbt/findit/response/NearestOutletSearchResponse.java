package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/** Response for nearest outlet search: list of outlets (with distance and open status) each containing matching items. */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NearestOutletSearchResponse extends Response {

    private List<NearestOutletResultItem> outlets;
}
