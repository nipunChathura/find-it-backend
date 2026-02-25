package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sub_merchants")
@Data
public class SubMerchant extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_merchant_id")
    private Long subMerchantId;
    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
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
}
