package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Outlet;
import lk.icbt.findit.entity.OutletType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OutletRepository extends JpaRepository<Outlet, Long> {

    /** Find outlets that should be expired (subscription valid until passed). */
    @Query("SELECT o FROM Outlet o WHERE o.status IN :statuses AND o.subscriptionValidUntil < :before")
    List<Outlet> findByStatusInAndSubscriptionValidUntilBefore(
            @Param("statuses") List<String> statuses,
            @Param("before") Date before);

    @Query("SELECT o FROM Outlet o " +
            "JOIN FETCH o.merchant m " +
            "LEFT JOIN FETCH o.subMerchant s " +
            "LEFT JOIN FETCH o.province p " +
            "LEFT JOIN FETCH o.district d " +
            "LEFT JOIN FETCH o.city c " +
            "WHERE (:search = '' OR LOWER(o.outletName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(m.merchantName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR (s IS NOT NULL AND LOWER(s.merchantName) LIKE LOWER(CONCAT('%', :search, '%')))) " +
            "AND (:status = '' OR o.status = :status) " +
            "AND (:outletType IS NULL OR o.outletType = :outletType)")
    List<Outlet> findAllWithFilters(
            @Param("search") String search,
            @Param("status") String status,
            @Param("outletType") OutletType outletType);

    /** Direct outlets of main merchant (subMerchant is null). */
    @Query("SELECT o FROM Outlet o LEFT JOIN FETCH o.merchant LEFT JOIN FETCH o.subMerchant LEFT JOIN FETCH o.province LEFT JOIN FETCH o.district LEFT JOIN FETCH o.city WHERE o.merchant.merchantId = :merchantId AND o.subMerchant IS NULL")
    List<Outlet> findByMerchant_MerchantIdAndSubMerchantIsNull(@Param("merchantId") Long merchantId);

    /** Outlets assigned to a sub-merchant. */
    @Query("SELECT o FROM Outlet o LEFT JOIN FETCH o.merchant LEFT JOIN FETCH o.subMerchant LEFT JOIN FETCH o.province LEFT JOIN FETCH o.district LEFT JOIN FETCH o.city WHERE o.subMerchant.subMerchantId = :subMerchantId")
    List<Outlet> findBySubMerchant_SubMerchantId(@Param("subMerchantId") Long subMerchantId);
}
