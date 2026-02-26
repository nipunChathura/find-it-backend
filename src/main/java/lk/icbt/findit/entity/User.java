package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "is_system_user", nullable = false, length = 1)
    private String isSystemUser;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "sub_merchant_id")
    private Long subMerchantId;
}
