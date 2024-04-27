package fu.hbs.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fu.hbs.entities.New;
import fu.hbs.entities.RoomService;
import org.springframework.data.jpa.repository.Query;

public interface NewsRepository extends JpaRepository<New, Long> {
    List<New> findByTitleContaining(String newsTitle);

    List<New> findByUserId(Long userId);

    New findByNewId(Long newId);

    @Query(value = "SELECT * FROM hotelsystem.news s WHERE \n" +
            "    (?1 IS NULL OR LOWER(s.title) LIKE CONCAT('%', LOWER(?1), '%')) \n" +
            "    AND (?2 IS NULL OR s.status = ?2) \n" +
            "    OR (?1 IS NULL AND ?2 IS NULL) ORDER BY new_id DESC", nativeQuery = true)
    Page<New> searchByNameAndStatus(String name, Integer status, Pageable pageable);
}
