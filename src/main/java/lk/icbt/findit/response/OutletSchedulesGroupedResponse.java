package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletSchedulesGroupedResponse {

    @JsonProperty("NORMAL")
    private List<OutletScheduleRowResponse> normal;
    @JsonProperty("EMERGENCY")
    private List<OutletScheduleRowResponse> emergency;
    @JsonProperty("TEMPORARY")
    private List<OutletScheduleRowResponse> temporary;
    @JsonProperty("DAILY")
    private List<OutletScheduleRowResponse> daily;
}
