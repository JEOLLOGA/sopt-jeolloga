package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplestayImageRepository extends JpaRepository<TemplestayImage, Long> {
    List<TemplestayImage> findAllByTemplestayId(Long templestayId);
}