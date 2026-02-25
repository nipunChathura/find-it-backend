package lk.icbt.findit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @Column(name = "category_name")
    private String categoryName;
    @Column(name = "category_description")
    private String categoryDescription;
    @Column(name = "category_image")
    private String categoryImage;
    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryType categoryType;
    @Column(name = "status")
    private String status;
}
