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
package fu.hbs.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fu.hbs.entities.RoomService;

public interface ServiceService {
//    List<com.example.entities.Service> getAllServices();

    /**
     * Search for room services by name and return a paginated list of matching
     * services.
     *
     * @param serviceName the name of the service to search for
     * @param pageable    object specifying the page and size of results
     * @return a paginated list of room services matching the search criteria
     */
    Page<RoomService> searchByName(String serviceName, Pageable pageable);

    /**
     * Get a paginated list of all available room services.
     *
     * @param pageable object specifying the page and size of results
     * @return a paginated list of all room services
     */
    Page<RoomService> getAllServices(Pageable pageable);

    RoomService findById(Long serviceId);

    List<RoomService> getAllServices();

//	List<com.example.entities.Service> searchByName(String serviceName);

    List<RoomService> getAllServicesByStatus(Boolean status);

}
