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
package fu.hbs.service.interfaces;

import java.util.List;

import fu.hbs.dto.User.BookRoomByCategory;
import fu.hbs.dto.MRoomDTO.ViewCategoryDTO;
import fu.hbs.dto.RoomCategoryDTO.ViewRoomCategoryDTO;
import fu.hbs.entities.RoomCategories;
import org.springframework.data.domain.Page;

public interface RoomCategoryService {
    /**
     * Get a list of room categories with associated information.
     *
     * @return a list of RoomCategoryDTO objects containing details about room categories
     */
    List<ViewRoomCategoryDTO> getAllRoom();


    /**
     * Get information about rooms within a specific category.
     *
     * @param categoryId the identifier of the room category
     * @return a BookRoomByCategory object containing details about rooms and
     * related information
     */
    BookRoomByCategory getRoomCategoryDetails(Long categoryId);

    List<RoomCategories> findAvailableRoomCategories(int numberOfPeople);

    RoomCategories getRoomCategoryId(Long id);


    RoomCategories deleteByRoomCategoryId(Long id);

    Page<ViewCategoryDTO> searchByNameAndStatus(Integer categoryId, Integer status, int page, int size);

    List<RoomCategories> getAllRoomCategories();

}
