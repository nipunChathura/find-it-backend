package lk.icbt.findit.repository;

import lk.icbt.findit.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query("SELECT c FROM City c WHERE c.district.districtId = :districtId AND " +
            "(:name IS NULL OR :name = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<City> findByDistrictIdWithNameSearch(@Param("districtId") Long districtId, @Param("name") String name);
}
