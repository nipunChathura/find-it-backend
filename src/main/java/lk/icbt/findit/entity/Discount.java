package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "discounts")
@Getter
@Setter
public class Discount extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;
    @Column(name = "discount_name")
    private String discountName;
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType;
    @Column(name = "discount_value")
    private Double discountValue;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "status")
    private String status;
}
