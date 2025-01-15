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

    @Query(value = "SELECT t.id AS templestay_id, t.temple_name, t.templestay_name, t.tag, CAST(c.region AS SIGNED) AS region, CAST(c.type AS SIGNED) AS type, i.img_url " +
            "FROM templestay t " +
            "LEFT JOIN category c ON t.id = c.templestay_id " +
            "LEFT JOIN ( " +
            "    SELECT templestay_id, MIN(id) AS min_id " +
            "    FROM templestay_image " +
            "    GROUP BY templestay_id " +
            ") min_img ON t.id = min_img.templestay_id " +
            "LEFT JOIN templestay_image i ON min_img.min_id = i.id " +
            "WHERE t.id IN :ids", nativeQuery = true)
    List<Object[]> findTemplestayWithDetails(@Param("ids") List<Long> ids);


}



