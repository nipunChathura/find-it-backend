package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "discount_items")
@Getter
@Setter
public class DiscountItem extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountItemId;
    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
