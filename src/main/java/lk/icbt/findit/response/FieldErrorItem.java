package lk.icbt.findit.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorItem {
    private String field;
    private String message;
    
    private String rejectedValue;
}
