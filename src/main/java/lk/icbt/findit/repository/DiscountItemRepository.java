package lk.icbt.findit.repository;

import lk.icbt.findit.entity.DiscountItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DiscountItemRepository extends JpaRepository<DiscountItem, Long> {

    @Query("SELECT DISTINCT di.item.itemId FROM DiscountItem di WHERE di.item.itemId IN :itemIds " +
            "AND di.discount.status = 'ACTIVE' AND di.discount.startDate <= :now " +
            "AND (di.discount.endDate IS NULL OR di.discount.endDate >= :now)")
    List<Long> findItemIdsWithActiveDiscount(@Param("itemIds") List<Long> itemIds, @Param("now") Date now);

    /** Fetch DiscountItem with discount for items that have an active discount (status ACTIVE, current date within start/end). One row per item-discount; same item may appear if in multiple discounts - caller may take first per item. */
    @Query("SELECT di FROM DiscountItem di JOIN FETCH di.discount d JOIN FETCH di.item WHERE di.item.itemId IN :itemIds " +
            "AND d.status = 'ACTIVE' AND d.startDate <= :now AND (d.endDate IS NULL OR d.endDate >= :now)")
    List<DiscountItem> findByItemItemIdInAndDiscountActiveAndDateValid(@Param("itemIds") List<Long> itemIds, @Param("now") Date now);

    List<DiscountItem> findByDiscount_DiscountId(Long discountId);

    List<DiscountItem> findByDiscount_DiscountIdIn(List<Long> discountIds);

    @Query("SELECT di FROM DiscountItem di JOIN FETCH di.discount JOIN FETCH di.item WHERE di.discount.discountId IN :discountIds")
    List<DiscountItem> findByDiscount_DiscountIdInWithItem(@Param("discountIds") List<Long> discountIds);

    void deleteByDiscount_DiscountId(Long discountId);
}
