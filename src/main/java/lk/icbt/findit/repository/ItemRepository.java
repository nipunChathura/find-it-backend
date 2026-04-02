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

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.category LEFT JOIN FETCH i.outlet WHERE i.outlet.outletId = :outletId AND (i.status IS NULL OR i.status <> 'DELETED') ORDER BY i.itemName")
    List<Item> findByOutletId(@Param("outletId") Long outletId);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.category c LEFT JOIN FETCH i.outlet o WHERE " +
            "(:search IS NULL OR :search = '' OR LOWER(i.itemName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(i.itemDescription) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:categoryId IS NULL OR i.category.categoryId = :categoryId) AND " +
            "(:outletId IS NULL OR i.outlet.outletId = :outletId) AND " +
            "(:status IS NULL OR :status = '' OR i.status = :status) AND " +
            "(:availability IS NULL OR i.availability = :availability) AND " +
            "(i.status <> 'DELETED' OR :status = 'DELETED')")
    List<Item> search(
            @Param("search") String search,
            @Param("categoryId") Long categoryId,
            @Param("outletId") Long outletId,
            @Param("status") String status,
            @Param("availability") Boolean availability);

    
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

    
    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.category c LEFT JOIN FETCH i.outlet o " +
            "WHERE i.availability = true AND i.status = 'ACTIVE' " +
            "AND o.status = 'ACTIVE' AND o.latitude IS NOT NULL AND o.longitude IS NOT NULL " +
            "AND (:categoryId IS NULL OR c.categoryId = :categoryId) " +
            "AND (:outletType IS NULL OR o.outletType = :outletType)")
    List<Item> findForNearestOutletSearchAllItems(
            @Param("categoryId") Long categoryId,
            @Param("outletType") OutletType outletType);

    long countByStatusNot(String itemDeletedStatus);

    
    long countByOutlet_OutletIdIn(List<Long> outletIds);

    
    long countByOutlet_OutletIdInAndStatusNot(List<Long> outletIds, String status);

    
    long countByOutlet_OutletIdAndStatusNot(Long outletId, String status);

    
    @Query("SELECT i.outlet.outletId, COUNT(i) FROM Item i WHERE i.outlet.outletId IN :outletIds AND (i.status IS NULL OR i.status <> :excludeStatus) GROUP BY i.outlet.outletId")
    List<Object[]> countByOutletIdInGroupByOutletId(@Param("outletIds") List<Long> outletIds, @Param("excludeStatus") String excludeStatus);
}
