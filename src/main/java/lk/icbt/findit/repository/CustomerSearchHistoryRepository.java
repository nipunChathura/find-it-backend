package lk.icbt.findit.repository;

import lk.icbt.findit.entity.CustomerSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerSearchHistoryRepository extends JpaRepository<CustomerSearchHistory, Long> {

    List<CustomerSearchHistory> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<CustomerSearchHistory> findByIdAndCustomerId(Long id, Long customerId);

    void deleteByIdAndCustomerId(Long id, Long customerId);
}
