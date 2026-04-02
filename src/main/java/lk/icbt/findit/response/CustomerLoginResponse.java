package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.MembershipType;
import lk.icbt.findit.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerLoginResponse extends Response {
    private String token;
    private Long userId;
    private String email;
    private String username;
    private String userStatus;
    private Role role;
    private Long customerId;
    private String profileImageUrl;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String nic;
    private String dob;
    private String gender;
    private String countryName;
    private MembershipType membershipType;
    private String customerStatus;
}
