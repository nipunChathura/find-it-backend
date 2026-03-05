package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Merchant;
import lk.icbt.findit.entity.MerchantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    long countByCreatedDatetimeBetween(Date start, Date end);

    boolean existsByMerchantEmail(String merchantEmail);

    boolean existsByMerchantEmailAndMerchantIdNot(String merchantEmail, Long merchantId);

    List<Merchant> findByStatusNot(String status);

    @Query("SELECT m FROM Merchant m WHERE m.status != :excludedStatus "
            + "AND (:search = '' OR LOWER(m.merchantName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.merchantEmail) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND (:status = '' OR m.status = :status) "
            + "AND (:merchantType IS NULL OR m.merchantType = :merchantType)")
    List<Merchant> findAllWithFilters(
            @Param("excludedStatus") String excludedStatus,
            @Param("search") String search,
            @Param("status") String status,
            @Param("merchantType") MerchantType merchantType);

    long countByStatus(String status);

    long countByStatusNot(String merchantDeletedStatus);
}
