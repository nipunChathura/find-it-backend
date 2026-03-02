package lk.icbt.findit.repository;

import lk.icbt.findit.entity.DiscountItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountItemRepository extends JpaRepository<DiscountItem, Long> {

    List<DiscountItem> findByDiscount_DiscountId(Long discountId);

    List<DiscountItem> findByDiscount_DiscountIdIn(List<Long> discountIds);

    @Query("SELECT di FROM DiscountItem di JOIN FETCH di.discount JOIN FETCH di.item WHERE di.discount.discountId IN :discountIds")
    List<DiscountItem> findByDiscount_DiscountIdInWithItem(@Param("discountIds") List<Long> discountIds);

    void deleteByDiscount_DiscountId(Long discountId);
}
