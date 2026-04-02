package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletScheduleItemResponse {

    private Long id;
    private ScheduleType scheduleType;
    private String dayOfWeek;
    private String specialDate;   
    private String startDate;
    private String endDate;
    private String openTime;
    private String closeTime;
    private Boolean closed;
    private String reason;
    private Integer priority;
    private Boolean active;
    private String status;   
}
