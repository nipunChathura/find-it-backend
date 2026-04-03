package lk.icbt.findit.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.icbt.findit.entity.ScheduleType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OutletScheduleRequest {

    @NotNull(message = "Schedule type is required")
    private ScheduleType scheduleType;

    
    @Size(max = 15)
    private String dayOfWeek;

    
    private LocalDate specialDate;

    
    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 5)
    @Pattern(regexp = "^(|([01]?[0-9]|2[0-3]):[0-5][0-9])$", message = "openTime must be HH:mm (e.g. 08:00) or empty")
    private String openTime;

    @Size(max = 5)
    @Pattern(regexp = "^(|([01]?[0-9]|2[0-3]):[0-5][0-9])$", message = "closeTime must be HH:mm (e.g. 18:00) or empty")
    private String closeTime;

    @JsonProperty("isClosed")
    private Boolean closed;   
    @Size(max = 255)
    private String reason;
    private Integer priority;
    private Boolean active;

    @AssertTrue(message = "For TEMPORARY schedule, when startDate or endDate is set, both are required and endDate must be >= startDate")
    public boolean isTemporaryDateRangeValid() {
        if (scheduleType != ScheduleType.TEMPORARY) return true;
        if (startDate == null && endDate == null) return true;
        if (startDate == null || endDate == null) return false;
        return !endDate.isBefore(startDate);
    }
}
