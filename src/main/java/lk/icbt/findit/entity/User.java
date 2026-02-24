package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String status;
    private String isSystemUser;
}
