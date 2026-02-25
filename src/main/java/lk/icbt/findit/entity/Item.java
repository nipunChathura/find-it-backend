package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Getter
@Setter
public class Item extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;
    @Column(name = "item_name")
    private String itemName;
    @Column(name = "item_description")
    private String itemDescription;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "outlet_id")
    private Outlet outlet;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "availability")
    private Boolean availability;
    @Column(name = "item_image")
    private String itemImage;
    @Column(name = "status")
    private String status;

}
