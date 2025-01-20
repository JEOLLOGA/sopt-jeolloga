package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplestayRepository extends JpaRepository<Templestay, Long> {

    @Query(value = "SELECT t.id AS templestay_id, t.temple_name, t.organized_name, t.tag, CAST(c.region AS SIGNED) AS region, CAST(c.type AS SIGNED) AS type, i.img_url " +
            "FROM templestay t " +
            "LEFT JOIN category c ON t.id = c.templestay_id " +
            "LEFT JOIN ( " +
            "    SELECT templestay_id, MIN(id) AS min_id " +
            "    FROM templestay_image " +
            "    GROUP BY templestay_id " +
            ") min_img ON t.id = min_img.templestay_id " +
            "LEFT JOIN templestay_image i ON min_img.min_id = i.id " +
            "WHERE t.id IN :ids AND t.tag IS NOT NULL", nativeQuery = true)
    Page<Object[]> findTemplestayWithDetails(@Param("ids") List<Long> ids, Pageable pageable);

    @Query("SELECT DISTINCT t.templeName FROM Templestay t")
    List<String> findDistinctTempleNames();

    @Query(value = "SELECT t.id, t.temple_name, t.organized_name, t.tag " +
            "FROM templestay t " +
            "WHERE t.temple_name LIKE %:query%",
            countQuery = "SELECT COUNT(*) FROM templestay t WHERE t.temple_name LIKE %:query%",
            nativeQuery = true)
    Page<Object[]> searchByTempleNameWithPagination(String query, Pageable pageable);

    @Query("""
        SELECT t.id, t.templeName, t.organizedName, t.tag
        FROM Templestay t
        JOIN Category c ON t.id = c.templestayId
        WHERE (:sanitizedQuery IS NULL OR t.templeName LIKE %:sanitizedQuery%)
          AND (:regionFilter IS NULL OR (c.region & :regionFilter) > 0)
          AND (:typeFilter IS NULL OR (c.type & :typeFilter) > 0)
          AND (:purposeFilter IS NULL OR (c.purpose & :purposeFilter) > 0)
          AND (:experienceFilter IS NULL OR (c.experience & :experienceFilter) > 0)
          AND (:etcFilter IS NULL OR (c.etc & :etcFilter) > 0)
          AND (:priceMin IS NULL OR t.price >= :priceMin)
          AND (:priceMax IS NULL OR t.price <= :priceMax)
        """)
    Page<Object[]> searchWithFilters(
            @Param("sanitizedQuery") String sanitizedQuery,
            @Param("regionFilter") Integer regionFilter,
            @Param("typeFilter") Integer typeFilter,
            @Param("purposeFilter") Integer purposeFilter,
            @Param("experienceFilter") Integer experienceFilter,
            @Param("etcFilter") Integer etcFilter,
            @Param("priceMin") Integer priceMin,
            @Param("priceMax") Integer priceMax,
            Pageable pageable);

}