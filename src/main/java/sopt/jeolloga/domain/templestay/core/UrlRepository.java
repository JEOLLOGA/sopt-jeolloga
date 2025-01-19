package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    @Query("SELECT u.templestayUrl FROM Url u WHERE u.templestayId = :templestayId")
    Optional<String> findImgUrlByTemplestayId(@Param("templestayId") Long templestayId);
    Optional<Url> findByTemplestayId(Long templestayId);

}
