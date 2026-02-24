package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "customers")
@Data
public class Customer extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "membership_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "picture")
    private String picture;
}
