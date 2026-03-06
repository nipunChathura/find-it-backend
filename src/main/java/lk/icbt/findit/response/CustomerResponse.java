package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lk.icbt.findit.entity.MembershipType;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponse extends Response {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String nic;
    private String dob;
    private String gender;
    private String countryName;
    private String profileImage;
    private String email;
    private String phoneNumber;
    private MembershipType membershipType;
    private String status;
}
