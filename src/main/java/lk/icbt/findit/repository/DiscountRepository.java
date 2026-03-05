package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<Discount> findByStatus(String status);

    long countByStatus(String status);

    @Query("SELECT d FROM Discount d WHERE (:status IS NULL OR :status = '' OR d.status = :status) " +
            "AND (:itemId IS NULL OR d.discountId IN (SELECT di.discount.discountId FROM DiscountItem di WHERE di.item.itemId = :itemId)) " +
            "AND (:outletId IS NULL OR d.discountId IN (SELECT di2.discount.discountId FROM DiscountItem di2 WHERE di2.item.outlet.outletId = :outletId)) " +
            "ORDER BY d.discountId")
    List<Discount> findAllWithFilters(@Param("status") String status, @Param("itemId") Long itemId, @Param("outletId") Long outletId);
}
