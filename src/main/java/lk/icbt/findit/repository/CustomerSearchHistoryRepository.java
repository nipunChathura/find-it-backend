package lk.icbt.findit.repository;

import lk.icbt.findit.entity.CustomerSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerSearchHistoryRepository extends JpaRepository<CustomerSearchHistory, Long> {

    List<CustomerSearchHistory> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<CustomerSearchHistory> findByIdAndCustomerId(Long id, Long customerId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CustomerSearchHistory e WHERE e.id = :id AND e.customerId = :customerId")
    void deleteByIdAndCustomerId(@Param("id") Long id, @Param("customerId") Long customerId);
}
