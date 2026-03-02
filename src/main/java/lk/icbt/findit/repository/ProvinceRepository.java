package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {

    @Query("SELECT p FROM Province p WHERE " +
            "(:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR :description = '' OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :description, '%')))")
    List<Province> findAllWithSearch(@Param("name") String name, @Param("description") String description);
}
