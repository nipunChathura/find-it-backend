package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.outlet o WHERE " +
            "(:outletId IS NULL OR p.outlet.outletId = :outletId) AND " +
            "(:status IS NULL OR :status = '' OR p.status = :status) ORDER BY p.paymentId DESC")
    List<Payment> findAllWithFilters(@Param("outletId") Long outletId, @Param("status") String status);
}
