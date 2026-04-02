package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletStatusResponse {

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";

    private Long outletId;
    private String status;           

    
    private String scheduleType;

    
    @JsonProperty("is_closed")
    private String isClosed;         

    private String openTime;         
    private String closeTime;        
    private String reason;           
    private List<OutletScheduleItemResponse> todaySchedule;
}
