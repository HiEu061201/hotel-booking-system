/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 27/10/2023    1.0        HieuLBM          First Deploy
 *
 */
package fu.hbs.repository;

import fu.hbs.entities.HotelBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
    List<HotelBooking> findByUserId(Long id);

    @Query(value = "SELECT * FROM hotel_booking WHERE user_id = ?1 AND valid_booking = 1 ORDER BY hotel_booking_id DESC", nativeQuery = true)
    Page<HotelBooking> findAllByUserId(Long id, Pageable pageable);


    @Query(value = "SELECT * FROM hotel_booking\n" +
            "WHERE user_id = ?1 \n" +
            "AND check_in LIKE  CONCAT(?2, '%') AND valid_booking = 1 ORDER BY hotel_booking_id DESC ", nativeQuery = true)
    Page<HotelBooking> findAllByUserIdAndCheckIn(Long userId, LocalDate checkIn, Pageable pageable);

    @Query(value = "SELECT * FROM hotel_booking\n" +
            "WHERE user_id = ?1 \n" +
            "AND status_id = ?2 AND valid_booking = 1 ORDER BY hotel_booking_id DESC ", nativeQuery = true)
    Page<HotelBooking> findAllByUserIdAndStatusId(Long userId, Integer status, Pageable pageable);

    @Query(value = "SELECT * FROM hotel_booking\n" +
            "WHERE user_id = ?1 \n" +
            "  AND check_in LIKE  CONCAT(?2, '%') \n" +
            "  AND status_id = ?3 AND valid_booking = 1 ORDER BY hotel_booking_id DESC ", nativeQuery = true)
    Page<HotelBooking> findAllByUserIdAndCheckInAndStatusId(Long userId, LocalDate checkIn, Integer status, Pageable pageable);

    @Query(value = "SELECT * FROM hotel_booking WHERE hotel_booking_id = ?1  AND valid_booking = 1  ", nativeQuery = true)
    HotelBooking findByHotelBookingId(Long hotelBookingId);


    @Query(value = "SELECT * FROM hotel_booking WHERE hotel_booking_id = ?1", nativeQuery = true)
    HotelBooking findByHotelBookingIdIsValid(Long hotelBookingId);

    @Query(value = "SELECT * FROM hotel_booking\n" +
            "WHERE " +
            "check_in >= ?1 AND check_out <= ?2 \n" +
            "AND (?3 IS NULL OR status_id = COALESCE(?3, status_id)) AND valid_booking = 1 ORDER BY hotel_booking_id DESC ", nativeQuery = true)
    Page<HotelBooking> findAllCheckInAndCheckOutAndStatus(LocalDateTime checkIn, LocalDateTime checkOut, Integer status, Pageable pageable);

    @Query(value = "SELECT * FROM hotelsystem.hotel_booking " +
            "WHERE ?1 IS NULL OR status_id = COALESCE(?1, status_id) AND valid_booking = 1 ORDER BY hotel_booking_id DESC ",
            nativeQuery = true)
    Page<HotelBooking> findByStatusId(Integer status, Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "UPDATE HotelBooking hb SET hb.statusId = ?1 WHERE hb.hotelBookingId = ?2 AND valid_booking = 1 ", nativeQuery = true)
    void updateStatus(Long statusId, Long hotelBookingId);


    @Query(value = "SELECT count(*) FROM hotelsystem.hotel_booking " +
            "WHERE check_in >= CURDATE() AND check_in <= CURDATE()+ INTERVAL 1 DAY " +
            "AND status_id = ?1 AND valid_booking = 1  ",
            nativeQuery = true)
    int countBookingTodayByStatus(Long status);

    @Query(value = "SELECT * FROM hotelsystem.hotel_booking " +
            "WHERE check_in >= CURDATE() AND check_in <= CURDATE()+ INTERVAL 1 DAY  AND valid_booking = 1",
            nativeQuery = true)
    List<HotelBooking> getAllTodayBooking();

    @Query(value = "SELECT COUNT(brd.room_id) as 'total' " +
            "FROM hotel_booking hb " +
            "INNER JOIN booking_room_details brd ON hb.hotel_booking_id = brd.hotel_booking_id " +
            "WHERE hb.check_in >= CURDATE() AND hb.check_in <= CURDATE() + INTERVAL 1 DAY " +
            "AND status_id = ?1 AND room_category_id = ?2 AND valid_booking = 1 " +
            "GROUP BY hb.status_id, brd.room_category_id ", nativeQuery = true)
    int getStatisticTodayByStatusAndRoomCategory(Long statusId, Long roomCategoryId);

    @Query(value = "SELECT * FROM hotel_booking\n" +
            "WHERE " +
            "check_in LIKE  CONCAT(?1, '%') AND (?2 IS NULL OR status_id = COALESCE(?2, status_id)) AND valid_booking = 1 ORDER BY hotel_booking_id DESC ", nativeQuery = true)
    Page<HotelBooking> findByCheckIn(LocalDate checkIn, Integer status, PageRequest pageRequest);

    @Query(value = "SELECT * FROM hotel_booking\n" +
            "WHERE " +
            "check_out LIKE  CONCAT(?1, '%') AND (?2 IS NULL OR status_id = COALESCE(?2, status_id)) AND valid_booking = 1 ORDER BY hotel_booking_id DESC  ", nativeQuery = true)
    Page<HotelBooking> findByCheckOut(LocalDate checkOut, Integer status, PageRequest pageRequest);


    @Query(value = "SELECT * FROM hotel_booking \n" +
            " WHERE  valid_booking = 1 ORDER BY hotel_booking_id DESC ", nativeQuery = true)
    Page<HotelBooking> findAllByValidBooking(PageRequest pageRequest);
}
