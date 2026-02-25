package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "cities")
@Getter
@Setter
public class City extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Long cityId;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;
}
