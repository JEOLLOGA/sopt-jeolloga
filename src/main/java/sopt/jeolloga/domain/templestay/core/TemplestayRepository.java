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
    @Query("SELECT DISTINCT t.templeName FROM Templestay t")
    List<String> findDistinctTempleNames();

    @Query(value = "SELECT t.id, t.temple_name, t.organized_name, t.tag " +
            "FROM templestay t " +
            "WHERE t.temple_name LIKE %:query%",
            countQuery = "SELECT COUNT(*) FROM templestay t WHERE t.temple_name LIKE %:query%",
            nativeQuery = true)
    Page<Object[]> searchByTempleNameWithPagination(String query, Pageable pageable);
}
