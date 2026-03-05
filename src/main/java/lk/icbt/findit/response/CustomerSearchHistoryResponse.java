package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSearchHistoryResponse extends Response {

    private Long id;
    private Long customerId;
    private String searchText;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;
    private Long categoryId;
    private String outletType;
    private LocalDateTime createdAt;
}
