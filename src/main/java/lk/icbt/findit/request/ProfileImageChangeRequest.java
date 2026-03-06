package lk.icbt.findit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileImageChangeRequest {

    @NotBlank(message = "Image file name is required")
    @Size(max = 255)
    private String fileName;
}
