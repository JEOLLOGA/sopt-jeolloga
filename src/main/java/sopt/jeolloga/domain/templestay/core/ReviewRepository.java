package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewLink(String link);
    @Query("SELECT r FROM Review r WHERE r.templeName = :templeName")
    Page<Review> findByTempleName(String templeName, Pageable pageable);
}