package fu.hbs.service.interfaces;

import fu.hbs.dto.MRoomDTO.CreateCategoryRoomPriceDTO;
import fu.hbs.entities.CategoryRoomPrice;

public interface CategoryRoomPriceService {
    CategoryRoomPrice findByCateRoomPriceId(Long id);

    void createCategoryRoomPrice(CreateCategoryRoomPriceDTO dto);
}
