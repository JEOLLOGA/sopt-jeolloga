package sopt.jeolloga.domain.member.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sopt.jeolloga.domain.templestay.api.dto.TemplestayRes;

import java.util.List;

public interface RecentViewRepository extends JpaRepository<RecentView, Long> {
    List<RecentView> findByMemberOrderByIdAsc(Member member);

    @Query(value = "SELECT t.id AS templestayId, t.temple_name, t.templestay_name, t.tag, " +
            "c.region, c.type, img.img_url, " +
            "EXISTS ( " +
            "    SELECT 1 FROM wishlist w WHERE w.templestay_id = t.id AND w.member_id = :userId " +
            ") AS liked " +
            "FROM recent_view rv " +
            "JOIN templestay t ON rv.templestay_id = t.id " +
            "JOIN category c ON t.id = c.templestay_id " +
            "LEFT JOIN ( " +
            "    SELECT templestay_id, MIN(img_url) AS img_url " +
            "    FROM templestay_image " +
            "    GROUP BY templestay_id " +
            ") img ON t.id = img.templestay_id " +
            "WHERE rv.member_id = :userId " +
            "ORDER BY rv.id DESC",
            nativeQuery = true)
    List<Object[]> findRecentViewedTemplestays(@Param("userId") Long userId);

}
