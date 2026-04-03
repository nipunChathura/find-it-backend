package lk.icbt.findit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendNotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Type is required")
    @Size(max = 20)
    private String type;

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String body;

    
    @Size(max = 500)
    private String token;
}
