/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 27/10/2023    1.0        HieuLBM          First Deploy
 * 29/10/2023	 2.0		HieuLBM			 edit getRoom
 */
package fu.hbs.service.impl;

import fu.hbs.dto.MRoomDTO.ViewRoomCategoryPriceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.RoomCategories;
import fu.hbs.repository.CategoryRoomFurnitureRepository;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.RoomCategoriesRepository;
import fu.hbs.repository.RoomFurnitureRepository;
import fu.hbs.repository.RoomImageRepository;
import fu.hbs.repository.RoomRepository;
import fu.hbs.repository.RoomServiceRepository;
import fu.hbs.service.interfaces.RoomByCategoryPriceService;

@Service
public class RoomByCategoryPriceServiceImpl implements RoomByCategoryPriceService {

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
    @Autowired
    RoomCategoriesRepository roomCategoriesRepository;
    @Autowired
    CategoryRoomPriceRepository categoryRoomPriceRepository;


    @Override
    public Page<ViewRoomCategoryPriceDTO> searchByCategoryIdAndFlag(Integer categoryId, Integer flag, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<CategoryRoomPrice> categoryRoomPrices = categoryRoomPriceRepository.searchByCategoryIdAndFlag(categoryId, flag, pageRequest);
        return categoryRoomPrices.map(categoryRoomPrice -> {

            RoomCategories roomCategories = roomCategoriesRepository.findByRoomCategoryId(categoryRoomPrice.getRoomCategoryId());
            ViewRoomCategoryPriceDTO viewRoomCategoryPriceDTO = new ViewRoomCategoryPriceDTO();
            viewRoomCategoryPriceDTO.setRoomCategories(roomCategories);
            viewRoomCategoryPriceDTO.setCategoryRoomPrice(categoryRoomPrice);
            System.out.println(viewRoomCategoryPriceDTO);
            return viewRoomCategoryPriceDTO;
        });
    }

}
