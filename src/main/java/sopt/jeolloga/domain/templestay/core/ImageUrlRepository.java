package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageUrlRepository extends JpaRepository<ImageUrlEntity, Long> {

    @Query("SELECT c FROM ImageUrlEntity c WHERE c.id IN :ids")
    List<ImageUrlEntity> findByIdIn(@Param("ids") List<Long> ids);

}
