package lk.icbt.findit.repository;

import lk.icbt.findit.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

    @Query("SELECT d FROM District d WHERE d.province.provinceId = :provinceId AND " +
            "(:name IS NULL OR :name = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<District> findByProvinceIdWithNameSearch(@Param("provinceId") Long provinceId, @Param("name") String name);
}
