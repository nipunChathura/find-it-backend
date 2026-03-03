package lk.icbt.findit.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single validation error on a request field, returned in API error responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorItem {
    private String field;
    private String message;
    /** Rejected value; omitted or redacted for sensitive fields (e.g. password). */
    private String rejectedValue;
}
