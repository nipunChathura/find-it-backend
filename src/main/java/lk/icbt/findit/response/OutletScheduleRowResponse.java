package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Single schedule row for grouped response (matches spec: dayOfWeek, openTime, closeTime, isClosed, reason, etc.). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletScheduleRowResponse {

    private Long id;
    private String dayOfWeek;
    private String specialDate;
    private String startDate;
    private String endDate;
    private String openTime;
    private String closeTime;
    @JsonProperty("isClosed")
    private String isClosed;   // "Y" or "N"
    private String reason;
    private Integer priority;
}
