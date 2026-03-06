package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "merchants")
@Getter
@Setter
public class Merchant extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long merchantId;
    @Column(name = "merchant_name", nullable = false)
    private String merchantName;
    @Column(name = "merchant_email", nullable = false, unique = true)
    private String merchantEmail;
    @Column(name = "merchant_nic")
    private String merchantNic;
    @Column(name = "merchant_profile_image")
    private String merchantProfileImage;
    @Column(name = "merchant_address", nullable = false)
    private String merchantAddress;
    @Column(name = "merchant_phone_number", nullable = false)
    private String merchantPhoneNumber;
    @Column(name = "merchant_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantType merchantType;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "inactive_reason")
    private String inactiveReason;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "merchant",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SubMerchant> subMerchants;
}
