package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    boolean existsByMerchantEmail(String merchantEmail);

    boolean existsByMerchantEmailAndMerchantIdNot(String merchantEmail, Long merchantId);
}
