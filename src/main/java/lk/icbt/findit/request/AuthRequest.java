package lk.icbt.findit.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthRequest extends  Request {

    private String registrationType;

    // customer and common
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String picture;

    // merchant
    private String address;

}
