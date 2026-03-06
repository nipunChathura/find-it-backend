package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Response for GET /outlets/{id}/status: current open/closed and today's schedule. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletStatusResponse {

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";

    private Long outletId;
    private String status;           // OPEN or CLOSED

    /** Which schedule type was used: NORMAL, EMERGENCY, TEMPORARY, DAILY. Null if no schedule (e.g. holiday). */
    private String scheduleType;

    /** Y = closed, N = open. When Y, openTime/closeTime give the opening time range. */
    @JsonProperty("is_closed")
    private String isClosed;         // "Y" or "N"

    private String openTime;         // HH:mm opening time (applicable schedule range)
    private String closeTime;        // HH:mm closing time (applicable schedule range)
    private String reason;           // e.g. closure reason
    private List<OutletScheduleItemResponse> todaySchedule;
}
