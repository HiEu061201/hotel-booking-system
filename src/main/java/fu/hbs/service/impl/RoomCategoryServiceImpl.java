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
 *
 */
package fu.hbs.service.impl;

import java.util.ArrayList;
import java.util.List;

import fu.hbs.dto.User.BookRoomByCategory;
import fu.hbs.dto.MRoomDTO.ViewCategoryDTO;
import fu.hbs.dto.RoomCategoryDTO.ViewRoomCategoryDTO;
import fu.hbs.entities.*;
import fu.hbs.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fu.hbs.service.interfaces.RoomCategoryService;

@Service
public class RoomCategoryServiceImpl implements RoomCategoryService {
    @Autowired
    private RoomCategoriesRepository roomCategoriesRepository;
    @Autowired
    private CategoryRoomPriceRepository categoryRoomPriceRepository;
    @Autowired
    RoomServiceRepository serviceRepository;
    @Autowired
    RoomImageRepository roomImageRepository;

    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomFurnitureRepository roomFurnitureRepository;
    @Autowired
    CategoryRoomFurnitureRepository categoryRoomFurnitureRepository;

    /**
     * Get a list of all room categories along with their details.
     *
     * @return List of RoomCategoryDTO containing room category information
     */
    /**
     * Get information about rooms within a specific category.
     *
     * @param categoryId the category ID for which room information is requested
     * @return BookRoomByCategory object containing room details
     */
    @Override
    public BookRoomByCategory getRoomCategoryDetails(Long categoryId) {
        RoomCategories roomCategories = new RoomCategories();
        List<Room> room = roomRepository.findByRoomCategoryId(categoryId);

        List<CategoryRoomPrice> categoryRoomPrices = new ArrayList<>();
        List<RoomImage> images = new ArrayList<>();
        List<RoomFurniture> roomFurnitures = new ArrayList<>();
        RoomImage roomImage = new RoomImage();
        RoomFurniture roomFurniture = new RoomFurniture();

        List<CategoryRoomFurniture> categoryRoomFurnitures = categoryRoomFurnitureRepository
                .findByRoomCategoryId(categoryId);
        List<RoomService> allService = serviceRepository.findAll();

        for (int i = 0; i < room.size(); i++) {
            roomImage = roomImageRepository.findByRoomImageId(room.get(i).getRoomImageId());
            images.add(roomImage);
        }

        for (int i = 0; i < categoryRoomFurnitures.size(); i++) {
            roomFurniture = roomFurnitureRepository.findByFurnitureId(categoryRoomFurnitures.get(i).getFurnitureId());
            roomFurnitures.add((roomFurniture));
        }

        BookRoomByCategory bookRoomByCategory = new BookRoomByCategory();
        bookRoomByCategory.setRoomCategories(roomCategoriesRepository.findByRoomCategoryId(categoryId));
        bookRoomByCategory.setCategoryRoomPrice(categoryRoomPriceRepository.findByRoomCategoryIdAndFlag(categoryId, 1));
        bookRoomByCategory.setCategoryRoomFurnitures(categoryRoomFurnitures);
        bookRoomByCategory.setImages(images);
        bookRoomByCategory.setServices(allService);
        bookRoomByCategory.setRooms(room);
        bookRoomByCategory.setRoomFurnitures(roomFurnitures);

        return bookRoomByCategory;
    }

    @Override
    public List<ViewRoomCategoryDTO> getAllRoom() {
        List<RoomCategories> roomCategories = roomCategoriesRepository.findAll();
        List<ViewRoomCategoryDTO> roomCategoryDTOS = new ArrayList<>();

        for (RoomCategories roomCategory : roomCategories) {
            ViewRoomCategoryDTO roomCategoryDTO = new ViewRoomCategoryDTO();
            roomCategoryDTO.setRoomCategoryId(roomCategory.getRoomCategoryId());
            roomCategoryDTO.setRoomCategoryName(roomCategory.getRoomCategoryName());
            roomCategoryDTO.setDescription(roomCategory.getDescription());
            roomCategoryDTO.setCategoryRoomPrice(
                    categoryRoomPriceRepository.findByRoomCategoryIdAndFlag(roomCategory.getRoomCategoryId(), 1));
            roomCategoryDTO.setImage(roomCategory.getImage());
            roomCategoryDTOS.add(roomCategoryDTO);
        }

        return roomCategoryDTOS;
    }

    @Override
    public List<RoomCategories> findAvailableRoomCategories(int numberOfPeople) {
        return roomCategoriesRepository.findByNumberPersonGreaterThanEqual(numberOfPeople);
    }

    @Override
    public RoomCategories getRoomCategoryId(Long id) {
        return roomCategoriesRepository.findByRoomCategoryId(id);
    }

    @Override
    public RoomCategories deleteByRoomCategoryId(Long id) {
        return roomCategoriesRepository.deleteByRoomCategoryId(id);
    }

    @Override
    public Page<ViewCategoryDTO> searchByNameAndStatus(Integer categoryId, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<RoomCategories> categoriesPage = roomCategoriesRepository.searchByNameAndStatus(categoryId, status, pageRequest);
        return categoriesPage.map(roomCategories -> {
            CategoryRoomPrice categoryRoomPrice = categoryRoomPriceRepository.findByRoomCategoryIdAndFlag(roomCategories.getRoomCategoryId(), 1);
            ViewCategoryDTO viewCategoryDTO = new ViewCategoryDTO();
            viewCategoryDTO.setRoomCategories(roomCategories);
            viewCategoryDTO.setCategoryRoomPrice(categoryRoomPrice);
            return viewCategoryDTO;
        });

    }

    @Override
    public List<RoomCategories> getAllRoomCategories() {
        return roomCategoriesRepository.findAll();
    }
}
