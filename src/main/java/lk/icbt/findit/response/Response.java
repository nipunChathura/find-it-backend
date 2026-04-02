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
    
    private List<FieldErrorItem> fieldErrors;
}
