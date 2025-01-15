package sopt.jeolloga.domain.wishlist.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sopt.jeolloga.domain.member.Member;
import sopt.jeolloga.domain.templestay.core.Templestay;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByMemberAndTemplestay(Member member, Templestay templestay);
}
