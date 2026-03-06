package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse extends Response {

    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String body;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
