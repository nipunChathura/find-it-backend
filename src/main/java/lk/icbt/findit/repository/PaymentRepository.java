package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    long countByCreatedDatetimeBetween(Date start, Date end);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.createdDatetime BETWEEN :start AND :end")
    BigDecimal sumAmountByCreatedDatetimeBetween(@Param("start") Date start, @Param("end") Date end);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.outlet o WHERE " +
            "(:outletId IS NULL OR p.outlet.outletId = :outletId) AND " +
            "(:status IS NULL OR :status = '' OR p.status = :status) ORDER BY p.paymentId DESC")
    List<Payment> findAllWithFilters(@Param("outletId") Long outletId, @Param("status") String status);

    /** Pending payments for the given outlet IDs (for merchant app dashboard). */
    List<Payment> findByOutlet_OutletIdInAndStatus(List<Long> outletIds, String status);
}
