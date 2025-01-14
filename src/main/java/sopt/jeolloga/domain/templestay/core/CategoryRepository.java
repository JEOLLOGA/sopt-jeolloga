package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT c FROM CategoryEntity c WHERE c.id = :id")
    CategoryEntity findCategoryById(@Param("id") Long id);

    @Query("SELECT c FROM CategoryEntity c WHERE c.id IN :ids")
    List<CategoryEntity> findByIdIn(@Param("ids") List<Long> ids);

}