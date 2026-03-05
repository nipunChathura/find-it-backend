package lk.icbt.findit.repository;

import lk.icbt.findit.entity.CustomerFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerFavoriteRepository extends JpaRepository<CustomerFavorite, Long> {

    List<CustomerFavorite> findByCustomer_CustomerIdOrderByIdAsc(Long customerId);

    /** Favorites for a customer and any of the given outlet IDs (for nearest-outlet response). */
    List<CustomerFavorite> findByCustomer_CustomerIdAndOutlet_OutletIdIn(Long customerId, List<Long> outletIds);

    Optional<CustomerFavorite> findByIdAndCustomer_CustomerId(Long id, Long customerId);

    Optional<CustomerFavorite> findByCustomer_CustomerIdAndOutlet_OutletId(Long customerId, Long outletId);

    void deleteByIdAndCustomer_CustomerId(Long id, Long customerId);

    boolean existsByCustomer_CustomerIdAndOutlet_OutletId(Long customerId, Long outletId);
}
