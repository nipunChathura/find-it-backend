package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "outlets")
@Getter
@Setter
public class Outlet extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outletId;
    @Column(name = "outlet_name")
    private String outletName;
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
    @ManyToOne
    @JoinColumn(name = "sub_merchant_id")
    private SubMerchant subMerchant;
    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;
    @Column(name = "tax_identification_number")
    private String taxIdentificationNumber;
    @Column(name = "postal_code")
    private String postalCode;
    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;
    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    @Column(name = "contact_number")
    private String contactNumber;
    @Column(name = "email_address")
    private String emailAddress;
    @Column(name = "address_line_1")
    private String addressLine1;
    @Column(name = "address_line_2")
    private String addressLine2;
    @Enumerated(EnumType.STRING)
    @Column(name = "outlet_type")
    private OutletType outletType;
    @Enumerated(EnumType.STRING)
    @Column(name = "business_category")
    private BusinessCategory businessCategory;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_branch")
    private String bankBranch;
    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "account_holder_name")
    private String accountHolderName;
    @Column(name = "status")
    private String status;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "onboarding_status")
    private String onboardingStatus;
    @Column(name = "onboarding_remarks")
    private String onboardingRemarks;

}
