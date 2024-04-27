/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 20/10/2023    1.0        HieuDT          First Deploy
 * 23/10/2023	 2.0		HieuDT			Update
 */
package fu.hbs.repository;

import java.time.LocalDate;
import java.util.List;

import fu.hbs.entities.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fu.hbs.entities.RoomService;

@Repository
public interface RoomServiceRepository extends JpaRepository<RoomService, Long> {
    List<RoomService> findByServiceNameContaining(String serviceName);

    RoomService findByServiceId(Long serviceId);

    @Query(value = "SELECT * FROM service s WHERE \n" +
            "    (?1 IS NULL OR LOWER(s.service_name) LIKE CONCAT('%', LOWER(?1), '%')) \n" +
            "    AND (?2 IS NULL OR s.status = ?2) \n" +
            "    OR (?1 IS NULL AND ?2 IS NULL)\n", nativeQuery = true)
    Page<RoomService> searchByNameAndStatus(String name, Integer status, Pageable pageable);

    List<RoomService> getAllByServiceIdIn(List<Long> serviceIds);
    
    List<RoomService> findAllByStatusIs(Boolean status);
    
}
