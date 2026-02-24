package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "merchants")
@Data
public class Merchant extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long merchantId;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "merchant_name", nullable = false)
    private String merchantName;
    @Column(name = "merchant_email", nullable = false, unique = true)
    private String merchantEmail;
    @Column(name = "merchant_address", nullable = false)
    private String merchantAddress;
    @Column(name = "merchant_phone_number", nullable = false)
    private String merchantPhoneNumber;
    @Column(name = "merchant_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantType merchantType;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "picture")
    private String picture;
    @Column(name = "inactive_reason")
    private String inactiveReason;
}
