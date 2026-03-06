package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Customer;
import lk.icbt.findit.entity.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    long countByCreatedDatetimeBetween(Date start, Date end);

    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE " +
            "(:search IS NULL OR :search = '' OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.nic) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:status IS NULL OR :status = '' OR c.status = :status) AND " +
            "(:membershipType IS NULL OR c.membershipType = :membershipType)")
    List<Customer> search(
            @Param("search") String search,
            @Param("status") String status,
            @Param("membershipType") MembershipType membershipType);
}
