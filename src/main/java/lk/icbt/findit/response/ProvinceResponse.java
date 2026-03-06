package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProvinceResponse extends Response {
    private Long provinceId;
    private String name;
    private String description;
    private Long code;
}
