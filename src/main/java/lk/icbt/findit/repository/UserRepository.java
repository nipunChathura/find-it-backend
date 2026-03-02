package lk.icbt.findit.repository;

import lk.icbt.findit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailIgnoreCaseAndRole(String email, lk.icbt.findit.entity.Role role);

    List<User> findByMerchantIdAndRole(Long merchantId, lk.icbt.findit.entity.Role role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long count();
}
