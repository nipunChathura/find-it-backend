package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistrictResponse extends Response {
    private Long districtId;
    private String name;
    private String description;
    private Long code;
    private Long provinceId;
}
