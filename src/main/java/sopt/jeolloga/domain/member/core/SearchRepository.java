package sopt.jeolloga.domain.member.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sopt.jeolloga.domain.member.core.Search;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {
    @Query("SELECT s.id, s.content FROM Search s WHERE s.member.id = :userId ORDER BY s.id DESC")
    List<Object[]> findTop10ByMemberIdOrderByIdDesc(Long userId);

    void deleteAllByMemberId(Long memberId);
}
