package lk.icbt.findit.repository;

import lk.icbt.findit.entity.MerchantType;
import lk.icbt.findit.entity.SubMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubMerchantRepository extends JpaRepository<SubMerchant, Long> {

    boolean existsByMerchantEmail(String merchantEmail);

    Optional<SubMerchant> findBySubMerchantIdAndMerchant_MerchantId(Long subMerchantId, Long merchantId);

    @Query("SELECT s FROM SubMerchant s JOIN FETCH s.merchant WHERE s.status != :excludedStatus")
    List<SubMerchant> findAllByStatusNot(@Param("excludedStatus") String excludedStatus);

    @Query("SELECT s FROM SubMerchant s JOIN FETCH s.merchant WHERE s.status != :excludedStatus "
            + "AND (:name = '' OR LOWER(s.merchantName) LIKE LOWER(CONCAT('%', :name, '%'))) "
            + "AND (:email = '' OR LOWER(s.merchantEmail) LIKE LOWER(CONCAT('%', :email, '%'))) "
            + "AND (:status = '' OR s.status = :status) "
            + "AND (:merchantType IS NULL OR s.merchantType = :merchantType)")
    List<SubMerchant> findAllWithFilters(
            @Param("excludedStatus") String excludedStatus,
            @Param("name") String name,
            @Param("email") String email,
            @Param("status") String status,
            @Param("merchantType") MerchantType merchantType);
}
