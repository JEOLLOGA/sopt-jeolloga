package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplestayImageRepository extends JpaRepository<TemplestayImage, Long> {

    @Query("SELECT c FROM TemplestayImage c WHERE c.id IN :ids")
    List<TemplestayImage> findByIdIn(@Param("ids") List<Long> ids);

}
