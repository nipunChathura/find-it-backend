package lk.icbt.findit.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthResponse extends Response {
    private Long userId;
    private Long customerId;
    private Long merchantId;

    private String token;
    private String role;
}
