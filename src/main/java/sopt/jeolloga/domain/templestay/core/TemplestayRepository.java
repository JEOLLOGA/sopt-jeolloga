package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplestayRepository extends JpaRepository<Templestay, Long> {
    Optional<Templestay> findById(Long id);
}
