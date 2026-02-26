package lk.icbt.findit.repository;

import lk.icbt.findit.entity.SubMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubMerchantRepository extends JpaRepository<SubMerchant, Long> {

    boolean existsByMerchantEmail(String merchantEmail);

    Optional<SubMerchant> findBySubMerchantIdAndMerchant_MerchantId(Long subMerchantId, Long merchantId);
}
