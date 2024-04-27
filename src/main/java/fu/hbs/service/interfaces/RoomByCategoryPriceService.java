/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 27/10/2023	 2.0	    HieuLBM			 First Deploy
 */
package fu.hbs.service.interfaces;

import fu.hbs.dto.MRoomDTO.ViewRoomCategoryPriceDTO;
import org.springframework.data.domain.Page;

public interface RoomByCategoryPriceService {

    Page<ViewRoomCategoryPriceDTO> searchByCategoryIdAndFlag(Integer categoryId, Integer flag, int page, int size);

}
