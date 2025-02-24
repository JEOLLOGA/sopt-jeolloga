package sopt.jeolloga.domain.templestay.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = """
    SELECT t.id, t.temple_name, t.organized_name, t.tag,
           c.region, c.type, c.purpose, c.activity, c.etc,
           (SELECT ti.img_url
            FROM templestay_image ti
            WHERE ti.templestay_id = t.id
            ORDER BY ti.id ASC LIMIT 1) AS img_url
    FROM templestay t
    LEFT JOIN category c ON t.id = c.templestay_id
    WHERE t.temple_name LIKE %:query%
      AND (:region IS NULL OR c.region & :region != 0)
      AND (:type IS NULL OR c.type & :type != 0)
      AND (:purpose IS NULL OR c.purpose & :purpose != 0)
      AND (:activity IS NULL OR c.activity & :activity != 0)
      AND (:minPrice IS NULL OR c.price >= :minPrice)
      AND (:maxPrice IS NULL OR c.price <= :maxPrice)
      AND (:etc IS NULL OR c.etc & :etc != 0)
""", nativeQuery = true)
    List<Object[]> searchWithFiltersAndData(
            @Param("query") String query,
            @Param("region") Integer region,
            @Param("type") Integer type,
            @Param("purpose") Integer purpose,
            @Param("activity") Integer activity,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("etc") Integer etc
    );

    @Query("SELECT t.id FROM Templestay t WHERE t.templeName LIKE %:content%")
    List<Long> findIdsByTempleNameContaining(@Param("content") String content);






    @Query(value = "WITH FilteredCategories AS (" +
            "SELECT templestay_id FROM category " +
            "WHERE (:region = 0 OR (region & :region) > 0) " +
            "AND (:type = 0 OR (type & :type) > 0) " +
            "AND (:purpose = 0 OR (purpose & :purpose) > 0) " +
            "AND (:activity = 0 OR (activity & :activity) > 0) " +
            "AND (:etc = 0 OR (etc & :etc) > 0) " +
            "AND (price BETWEEN :minPrice AND :maxPrice) " +
            ") " +
            "SELECT t.id AS templestayId, t.temple_name, t.templestay_name, t.tag, " +
            "c.region, c.type, img.img_url, " +
            "EXISTS ( " +
            "    SELECT 1 FROM wishlist w WHERE w.templestay_id = t.id AND w.member_id = :userId " +
            ") AS liked " +
            "FROM templestay t " +
            "JOIN FilteredCategories fc ON t.id = fc.templestay_id " +
            "JOIN category c ON t.id = c.templestay_id " +
            "LEFT JOIN (SELECT templestay_id, img_url FROM templestay_image GROUP BY templestay_id) img " +
            "ON t.id = img.templestay_id " +
            "ORDER BY t.id",

            countQuery = "WITH FilteredCategories AS (" +
                    "SELECT templestay_id FROM category " +
                    "WHERE (:region = 0 OR (region & :region) > 0) " +
                    "AND (:type = 0 OR (type & :type) > 0) " +
                    "AND (:purpose = 0 OR (purpose & :purpose) > 0) " +
                    "AND (:activity = 0 OR (activity & :activity) > 0) " +
                    "AND (:etc = 0 OR (etc & :etc) > 0) " +
                    "AND (price BETWEEN :minPrice AND :maxPrice) " +
                    ") " +
                    "SELECT COUNT(*) FROM templestay t " +
                    "JOIN FilteredCategories fc ON t.id = fc.templestay_id " +
                    "JOIN category c ON t.id = c.templestay_id",
            nativeQuery = true)
    Page<Object[]> findFilteredTemplestay(
            @Param("region") Integer region,
            @Param("type") Integer type,
            @Param("purpose") Integer purpose,
            @Param("activity") Integer activity,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("etc") Integer etc,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query(value = "SELECT COUNT(*) FROM category c " +
                    "WHERE (:region = 0 OR (c.region & :region) > 0) " +
                    "AND (:type = 0 OR (c.type & :type) > 0) " +
                    "AND (:purpose = 0 OR (c.purpose & :purpose) > 0) " +
                    "AND (:activity = 0 OR (c.activity & :activity) > 0) " +
                    "AND (:etc = 0 OR (c.etc & :etc) > 0) " +
                    "AND (c.price BETWEEN :minPrice AND :maxPrice)",
            nativeQuery = true)
    long findFilteredTemplestayNum(
            @Param("region") Integer region,
            @Param("type") Integer type,
            @Param("purpose") Integer purpose,
            @Param("activity") Integer activity,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("etc") Integer etc
    );


    @Query(value = "WITH FilteredCategories AS (" +
            "SELECT templestay_id FROM category " +
            "WHERE (:region = 0 OR (region & :region) > 0) " +
            "AND (:type = 0 OR (type & :type) > 0) " +
            "AND (:purpose = 0 OR (purpose & :purpose) > 0) " +
            "AND (:activity = 0 OR (activity & :activity) > 0) " +
            "AND (:etc = 0 OR (etc & :etc) > 0) " +
            "AND (price BETWEEN :minPrice AND :maxPrice) " +
            ") " +
            "SELECT t.id AS templestayId, t.temple_name, t.templestay_name, t.tag, " +
            "c.region, c.type, img.img_url, " +
            "CASE WHEN w.templestay_id IS NOT NULL THEN TRUE ELSE FALSE END AS liked " +
            "FROM templestay t " +
            "JOIN FilteredCategories fc ON t.id = fc.templestay_id " +
            "JOIN category c ON t.id = c.templestay_id " +
            "LEFT JOIN (SELECT templestay_id, img_url FROM templestay_image GROUP BY templestay_id) img " +
            "ON t.id = img.templestay_id " +
            "LEFT JOIN wishlist w ON t.id = w.templestay_id AND (w.member_id = :userId OR :userId IS NULL) " +
            "WHERE (:content IS NULL OR t.temple_name LIKE CONCAT('%', :content, '%')) " + // ✅ 검색 키워드 반영
            "ORDER BY t.id",

            countQuery = "WITH FilteredCategories AS (" +
                    "SELECT templestay_id FROM category " +
                    "WHERE (:region = 0 OR (region & :region) > 0) " +
                    "AND (:type = 0 OR (type & :type) > 0) " +
                    "AND (:purpose = 0 OR (purpose & :purpose) > 0) " +
                    "AND (:activity = 0 OR (activity & :activity) > 0) " +
                    "AND (:etc = 0 OR (etc & :etc) > 0) " +
                    "AND (price BETWEEN :minPrice AND :maxPrice) " +
                    ") " +
                    "SELECT COUNT(*) FROM templestay t " +
                    "JOIN FilteredCategories fc ON t.id = fc.templestay_id " +
                    "JOIN category c ON t.id = c.templestay_id " +
                    "WHERE (:content IS NULL OR t.temple_name LIKE CONCAT('%', :content, '%'))",
            nativeQuery = true)
    Page<Object[]> searchFilteredTemplestay(
            @Param("content") String content,
            @Param("region") Integer region,
            @Param("type") Integer type,
            @Param("purpose") Integer purpose,
            @Param("activity") Integer activity,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("etc") Integer etc,
            @Param("userId") Long userId,
            Pageable pageable
    );
}