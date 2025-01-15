package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplestayRepository extends JpaRepository<Templestay, Long> {
    @Query("SELECT DISTINCT t.templeName FROM Templestay t")
    List<String> findDistinctTempleNames();
}
