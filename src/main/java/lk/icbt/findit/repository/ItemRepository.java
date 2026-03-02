package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.category c LEFT JOIN FETCH i.outlet o WHERE " +
            "(:search IS NULL OR :search = '' OR LOWER(i.itemName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(i.itemDescription) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:categoryId IS NULL OR i.category.categoryId = :categoryId) AND " +
            "(:outletId IS NULL OR i.outlet.outletId = :outletId) AND " +
            "(:status IS NULL OR :status = '' OR i.status = :status) AND " +
            "(:availability IS NULL OR i.availability = :availability)")
    List<Item> search(
            @Param("search") String search,
            @Param("categoryId") Long categoryId,
            @Param("outletId") Long outletId,
            @Param("status") String status,
            @Param("availability") Boolean availability);
}
