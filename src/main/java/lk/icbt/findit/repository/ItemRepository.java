package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Item;
import lk.icbt.findit.entity.OutletType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.category LEFT JOIN FETCH i.outlet WHERE i.outlet.outletId = :outletId ORDER BY i.itemName")
    List<Item> findByOutletId(@Param("outletId") Long outletId);

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

    /** For nearest-outlet search: items matching name, available, active; outlet active with lat/long; optional category and outlet type. */
    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.category c LEFT JOIN FETCH i.outlet o " +
            "WHERE LOWER(i.itemName) LIKE LOWER(CONCAT('%', :itemName, '%')) " +
            "AND i.availability = true AND i.status = 'ACTIVE' " +
            "AND o.status = 'ACTIVE' AND o.latitude IS NOT NULL AND o.longitude IS NOT NULL " +
            "AND (:categoryId IS NULL OR c.categoryId = :categoryId) " +
            "AND (:outletType IS NULL OR o.outletType = :outletType)")
    List<Item> findForNearestOutletSearch(
            @Param("itemName") String itemName,
            @Param("categoryId") Long categoryId,
            @Param("outletType") OutletType outletType);

    long countByStatusNot(String itemDeletedStatus);
}
