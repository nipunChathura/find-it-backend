package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Customer;
import lk.icbt.findit.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant,Long> {
    Merchant findByMerchantEmail(String merchantEmail);
}
