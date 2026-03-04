package lk.icbt.findit.repository;

import lk.icbt.findit.entity.Category;
import lk.icbt.findit.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE " +
            "c.status <> 'DELETED' AND " +
            "(:name IS NULL OR :name = '' OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:categoryType IS NULL OR c.categoryType = :categoryType) AND " +
            "(:status IS NULL OR :status = '' OR c.status = :status)")
    List<Category> findAllWithFilters(@Param("name") String name,
                                      @Param("categoryType") CategoryType categoryType,
                                      @Param("status") String status);
}
