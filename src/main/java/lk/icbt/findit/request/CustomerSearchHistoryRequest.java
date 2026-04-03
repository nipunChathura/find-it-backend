package lk.icbt.findit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerSearchHistoryRequest {
    
    private Long customerId;
    private String searchText;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;
    private Long categoryId;
    private String outletType;
}
