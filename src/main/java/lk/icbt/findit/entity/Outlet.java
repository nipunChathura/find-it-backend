package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "outlets")
@Data
public class Outlet extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outletId;

    // Outlet basic details
    private String outletName;
    @Enumerated(EnumType.STRING)
    private OutletType outletType;
    @Enumerated(EnumType.STRING)
    private BusinessCategory businessCategory;

    // Owner
    @ManyToOne(fetch = FetchType.LAZY)
    private Merchant merchant;

    // contact details
    private String contactNumber;
    private String emailAddress;

    // Address details
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String province;
    private String postalCode;

    // Location (optional – useful for maps / GIS)
    private Double latitude;
    private Double longitude;

    // Business registration details
    private String businessRegistrationNumber;
    private String taxIdentificationNumber;

    // Bank details
    private String bankName;
    private String bankBranch;
    private String accountNumber;
    private String accountHolderName;

    // Onboarding status
    private String outletStatus;    // PENDING, APPROVED, REJECTED
    private String Remarks;

    // Onboarding status
    private String onboardingStatus;    // PENDING, APPROVED, REJECTED
    private String onboardingRemarks;

    // Onboarding status
    private String paymentStatus;    // PENDING, APPROVED, REJECTED
    private String paymentRemarks;
}
