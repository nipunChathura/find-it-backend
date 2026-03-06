package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lk.icbt.findit.entity.MembershipType;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerListItemResponse {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String countryName;
    private String email;
    private String phoneNumber;
    private MembershipType membershipType;
    private String status;
}
