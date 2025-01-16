package sopt.jeolloga.domain.wishlist.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sopt.jeolloga.domain.member.Member;
import sopt.jeolloga.domain.templestay.core.Templestay;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByMemberAndTemplestay(Member member, Templestay templestay);
    Page<Wishlist> findAllByMember(Member member, Pageable pageable);
    @Query("SELECT COUNT(w) > 0 FROM Wishlist w WHERE w.member.id = :memberId AND w.templestay.id = :templestayId")
    boolean existsByMemberIdAndTemplestayId(@Param("memberId") Long memberId, @Param("templestayId") Long templestayId);
}
