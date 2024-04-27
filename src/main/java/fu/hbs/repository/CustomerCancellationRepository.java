package fu.hbs.repository;

import fu.hbs.entities.CustomerCancellation;
import fu.hbs.entities.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CustomerCancellationRepository extends JpaRepository<CustomerCancellation, Long> {

    CustomerCancellation findByCancellationId(Long id);

    CustomerCancellation findCustomerCancellationByAccountIdAndHotelBookingId(Long accId, Long hotelBookingId);

    @Query(value = "SELECT ra.*\n" +
            "            FROM hotelsystem.customer_cancellation ra\n" +
            "            WHERE hotel_booking_id = ?1 \n" +
            "            ORDER BY account_id DESC\n" +
            "            LIMIT 1", nativeQuery = true)
    CustomerCancellation findCustomerCancellationNewHotelBookingId(Long hotelBookingId);

    @Query(value = " SELECT * FROM hotelsystem.customer_cancellation\n" +
            "            WHERE status = 0", nativeQuery = true)
    List<CustomerCancellation> findByStatus();


    @Query(value = " SELECT * FROM hotelsystem.customer_cancellation\n" +
            "            WHERE status = ?1 ORDER BY cancellation_id DESC", nativeQuery = true)
    Page<CustomerCancellation> findByStatusConfirm(Integer status, Pageable pageable);

    @Query(value = "SELECT * FROM hotelsystem.customer_cancellation WHERE status IN (1, 2) ORDER BY cancellation_id DESC", nativeQuery = true)
    Page<CustomerCancellation> findAllStatusConfirm(Pageable pageable);

    @Query(value = "SELECT * FROM hotelsystem.customer_cancellation WHERE status IN (0, 1) ORDER BY cancellation_id DESC", nativeQuery = true)
    Page<CustomerCancellation> findAllStatusProcess(Pageable pageable);

    @Query(value = "SELECT cc.*\n" +
            "FROM customer_cancellation cc\n" +
            "LEFT JOIN hotel_booking hb ON hb.hotel_booking_id = cc.hotel_booking_id\n" +
            "WHERE hb.check_in  LIKE  CONCAT(?1, '%') AND (cc.status = ?2 OR ( ?2 IS NULL AND cc.status IN (0, 1))) ORDER BY cancellation_id DESC ;", nativeQuery = true)
    Page<CustomerCancellation> findByCheckInAndStatusProcess(LocalDate checkin, Integer status, Pageable pageable);


    @Query(value = "SELECT cc.*\n" +
            "FROM customer_cancellation cc\n" +
            "LEFT JOIN hotel_booking hb ON hb.hotel_booking_id = cc.hotel_booking_id\n" +
            "WHERE hb.check_in  LIKE  CONCAT(?1, '%') AND (cc.status = ?2 OR ( ?2 IS NULL AND cc.status IN (1, 2))) ORDER BY cancellation_id DESC ;", nativeQuery = true)
    Page<CustomerCancellation> findByCheckInAndStatusConfirm(LocalDate checkin, Integer status, Pageable pageable);


}
