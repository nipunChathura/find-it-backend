package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Role;
import lk.icbt.findit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    long countByCreatedDatetimeBetween(Date start, Date end);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailIgnoreCaseAndRole(String email, Role role);

    List<User> findByMerchantIdAndRole(Long merchantId, Role role);

    /** Same as findByMerchantIdAndRole but excludes users with the given status (e.g. DELETED). */
    List<User> findByMerchantIdAndRoleAndStatusNot(Long merchantId, Role role, String status);

    List<User> findByMerchantIdInAndRole(List<Long> merchantIds, Role role);

    /** For filtering merchants by login username (username stored in users table). */
    List<User> findByRoleAndMerchantIdNotNullAndUsernameContainingIgnoreCase(Role role, String username);

    /** For filtering sub-merchants by login username. */
    List<User> findByRoleAndSubMerchantIdNotNullAndUsernameContainingIgnoreCase(Role role, String username);

    List<User> findBySubMerchantIdAndRole(Long subMerchantId, Role role);

    /** Same as findBySubMerchantIdAndRole but excludes users with the given status (e.g. DELETED). */
    List<User> findBySubMerchantIdAndRoleAndStatusNot(Long subMerchantId, Role role, String status);

    /** Users for the given sub-merchant IDs with role SUBMERCHANT, excluding given status (for profile image lookup). */
    List<User> findBySubMerchantIdInAndRoleAndStatusNot(List<Long> subMerchantIds, Role role, String status);

    /** Find all users with any of the given roles (e.g. SYSADMIN, ADMIN for admin-type users). */
    List<User> findByRoleIn(List<Role> roles);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long count();

    long countByStatus(String status);

    long countByStatusNot(String status);

    long countByStatusNotInAndRoleIn(Collection<String> status, Collection<Role> role);

    /** Count users by role, excluding given status (e.g. DELETED). */
    long countByRoleAndStatusNot(Role role, String status);
}
