package sopt.jeolloga.domain.templestay.core;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplestayRepository extends JpaRepository<TemplestayEntity, Long> {
//
//    @Query("SELECT c FROM TemplestayEntity c WHERE c.id IN :ids")
//    Page<TemplestayEntity> findByIdIn(@Param("ids") List<Long> ids, Pageable pageable);

    @Query("SELECT c FROM TemplestayEntity c WHERE c.id IN :ids")
    List<TemplestayEntity> findByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT t, i FROM TemplestayEntity t LEFT JOIN ImageUrlEntity i ON t.id = i.id WHERE t.id IN :ids")
    List<Object[]> findTemplestayWithImageUrls(@Param("ids") List<Long> ids);
}
