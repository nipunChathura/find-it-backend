package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Data;
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
    private Long userId;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String status;
    private Date lastLogin;
    private String isSystemUser;
}
