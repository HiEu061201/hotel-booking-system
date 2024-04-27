/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 27/10/2023    1.0        HieuLBM          First Deploy
 */
package fu.hbs.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fu.hbs.entities.Room;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByRoomCategoryId(Long roomCategoryId);

    Room findRoomByRoomId(Long roomId);

    @Query(value = "SELECT r.*\n" +
            "FROM room r\n" +
            "LEFT JOIN room_categories rc ON r.room_category_id = rc.room_category_id\n" +
            "WHERE rc.number_person >= ?3 \n" +
            "  AND r.room_id NOT IN (\n" +
            "    SELECT brd.room_id\n" +
            "    FROM booking_room_details brd\n" +
            "    INNER JOIN hotel_booking hb ON brd.hotel_booking_id = hb.hotel_booking_id\n" +
            "    WHERE (\n" +
            "      (hb.check_in BETWEEN ?1 AND ?2 )\n" +
            "      OR (hb.check_out BETWEEN ?1 AND ?2 )\n" +
            "    )\n" +
            "    AND hb.status_id IN (1, 2)\n" +
            "  );\n", nativeQuery = true)
    List<Room> getAllRoom(LocalDate checkIn, LocalDate checkOut, int numberPerson);

    @Query(value = "SELECT r.*\n" +
            "FROM room r\n" +
            "LEFT JOIN room_categories rc ON r.room_category_id = rc.room_category_id\n" +
            "WHERE r.room_category_id = ?1 " +
            "AND r.room_id NOT IN (\n" +
            "SELECT brd.room_id\n" +
            "FROM booking_room_details brd\n" +
            "INNER JOIN hotel_booking hb ON brd.hotel_booking_id = hb.hotel_booking_id\n" +
            "WHERE ((hb.check_in BETWEEN ?2 AND ?3 )\n" +
            "OR (hb.check_out BETWEEN ?2 AND ?3 ))" +
            " AND hb.status_id IN (1, 2));", nativeQuery = true)
    List<Room> findAvailableRoomsByCategoryId(Long id, LocalDate checkIn, LocalDate checkOut);

    @Query(value = "SELECT *\n" +
            "FROM room\n" +
            "WHERE \n" +
            "  (?1 IS NULL OR floor = COALESCE(?1, floor))\n" +
            "  AND \n" +
            "  (?2 IS NULL OR room_status_id = COALESCE(?2, room_status_id));\n", nativeQuery = true)
    Page<Room> findFloorAndStatusId(Integer floor, Integer status, Pageable pageable);


    @Query(value = "SELECT * FROM room WHERE room_id in (?1)", nativeQuery = true)
    List<Room> getAllWhereIdIn(List<Long> roomIds);

    int countAllByRoomStatusId(Long id);

    @Query(value = "SELECT COUNT(*) FROM hotelsystem.room;", nativeQuery = true)
    int countAll();


    int countRoomByRoomCategoryId(Long categoryId);

    @Query(value = "SELECT r.*\n" +
            "FROM room r\n" +
            "LEFT JOIN room_categories rc ON r.room_category_id = rc.room_category_id\n" +
            "WHERE r.room_category_id = ?1 " +
            "AND r.room_id NOT IN (\n" +
            "SELECT brd.room_id\n" +
            "FROM booking_room_details brd\n" +
            "INNER JOIN hotel_booking hb ON brd.hotel_booking_id = hb.hotel_booking_id\n" +
            "WHERE ((hb.check_in BETWEEN CURDATE() AND CURDATE() + INTERVAL 1 DAY )\n" +
            "OR (hb.check_out BETWEEN CURDATE() AND CURDATE() + INTERVAL 1 DAY ))" +
            " AND hb.status_id = 1 OR hb.status_id = 2  );", nativeQuery = true)
    List<Room> findAvailableRoomsByCategoryIdToday(Long categoryId);

    @Query(value = "SELECT r.* FROM room r\n" +
            "LEFT JOIN room_categories rc ON r.room_category_id = rc.room_category_id\n" +
            "WHERE r.room_category_id = ?1 \n" +
            "AND r.room_id  IN ( \n" +
            "SELECT brd.room_id\n" +
            "FROM booking_room_details brd\n" +
            "INNER JOIN hotel_booking hb ON brd.hotel_booking_id = hb.hotel_booking_id\n" +
            "WHERE ((hb.check_in BETWEEN CURDATE() AND CURDATE() + INTERVAL 1 DAY )\n" +
            "OR (hb.check_out BETWEEN CURDATE() AND CURDATE() + INTERVAL 1 DAY ))\n" +
            "AND hb.status_id = ?2 );", nativeQuery = true)
    List<Room> findOccupiedByCategoryIdToday(Long categoryId, int status);

    @Query(value = "SELECT r.*\n" +
            "FROM room r\n" +
            "LEFT JOIN room_categories rc ON r.room_category_id = rc.room_category_id\n" +
            "WHERE  " +
            "r.room_id NOT IN (\n" +
            "SELECT brd.room_id\n" +
            "FROM booking_room_details brd\n" +
            "INNER JOIN hotel_booking hb ON brd.hotel_booking_id = hb.hotel_booking_id\n" +
            "WHERE ((hb.check_in BETWEEN ?1 AND ?2 )\n" +
            "OR (hb.check_out BETWEEN ?1 AND ?2 ))" +
            " AND hb.status_id = 1 );", nativeQuery = true)
    List<Room> findAvailableRoomsByDate(LocalDate checkIn, LocalDate checkOut);


    @Modifying
    @Transactional
    @Query(value = "UPDATE room\n" +
            "SET room_status_id = ?1 \n" +
            "WHERE room_id = ?2 ", nativeQuery = true)
    void updateRoomByStatusId(int status, Long roomId);

    Room findByRoomImageId(Long imageId);
}
