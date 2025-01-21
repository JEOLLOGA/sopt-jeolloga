package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplestayImageRepository extends JpaRepository<TemplestayImage, Long> {
    @Query(value = "SELECT img_url FROM templestay_image WHERE templestay_id = :templestayId ORDER BY id ASC LIMIT 1", nativeQuery = true)
    Optional<String> findImgUrlByTemplestayId(@Param("templestayId") Long templestayId);

    @Query("SELECT c FROM TemplestayImage c WHERE c.id IN :ids")
    List<TemplestayImage> findByIdIn(@Param("ids") List<Long> ids);

    List<TemplestayImage> findAllByTemplestayId(Long templestayId);

}
