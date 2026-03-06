package lk.icbt.findit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.response.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistrictDTO extends Response {
    private Long districtId;
    private String name;
    private String description;
    private Long code;
    private Long provinceId;
}
