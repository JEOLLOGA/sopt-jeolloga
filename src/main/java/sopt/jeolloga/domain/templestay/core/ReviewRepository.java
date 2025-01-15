package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewLink(String link);
    List<Review> findByTempleName(String templeName);

}

