package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByCustomer_CustomerIdOrderByCreatedDatetimeDesc(Long customerId);

    List<Feedback> findByOutlet_OutletIdOrderByCreatedDatetimeDesc(Long outletId);

    long countByOutlet_OutletId(Long outletId);
}
