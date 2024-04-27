package fu.hbs.repository;

import fu.hbs.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query(value = "SELECT * FROM hotelsystem.feedback s WHERE \n" +
            "    (?1 IS NULL OR LOWER(s.title) LIKE CONCAT('%', LOWER(?1), '%')) \n" +
            "    AND (?2 IS NULL OR s.status = ?2) \n" +
            "    OR (?1 IS NULL AND ?2 IS NULL) ORDER BY feedback_id DESC", nativeQuery = true)
    Page<Feedback> findAllByTitleAndStatus(String name, Integer status, PageRequest pageRequest);

    Feedback findByHotelBookingId(Long hotelBookingId);
}
