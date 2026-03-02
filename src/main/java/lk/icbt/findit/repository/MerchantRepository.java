package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Merchant;
import lk.icbt.findit.entity.MerchantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    boolean existsByMerchantEmail(String merchantEmail);

    boolean existsByMerchantEmailAndMerchantIdNot(String merchantEmail, Long merchantId);

    List<Merchant> findByStatusNot(String status);

    @Query("SELECT m FROM Merchant m WHERE m.status != :excludedStatus "
            + "AND (:name = '' OR LOWER(m.merchantName) LIKE LOWER(CONCAT('%', :name, '%'))) "
            + "AND (:email = '' OR LOWER(m.merchantEmail) LIKE LOWER(CONCAT('%', :email, '%'))) "
            + "AND (:username = '' OR LOWER(COALESCE(m.username, '')) LIKE LOWER(CONCAT('%', :username, '%'))) "
            + "AND (:status = '' OR m.status = :status) "
            + "AND (:merchantType IS NULL OR m.merchantType = :merchantType)")
    List<Merchant> findAllWithFilters(
            @Param("excludedStatus") String excludedStatus,
            @Param("name") String name,
            @Param("email") String email,
            @Param("username") String username,
            @Param("status") String status,
            @Param("merchantType") MerchantType merchantType);
}
