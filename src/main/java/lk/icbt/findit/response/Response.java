package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private String status;
    private String responseCode;
    private String responseMessage;
    /** Present for all failure responses; lists error details (field-level for validation, or a single "error" entry). */
    private List<FieldErrorItem> fieldErrors;
}
