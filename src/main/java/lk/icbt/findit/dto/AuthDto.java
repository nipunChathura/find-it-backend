package lk.icbt.findit.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthDto extends CommonDto{
    private String registrationType;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String picture;
    private String address;

    // response
    private String token;
    private String role;
}
