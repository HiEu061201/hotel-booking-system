package fu.hbs.service.impl;

import fu.hbs.dto.MRoomDTO.CreateCategoryRoomPriceDTO;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.service.interfaces.CategoryRoomPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class CategoryRoomPriceServiceImpl implements CategoryRoomPriceService {
    @Autowired
    private CategoryRoomPriceRepository categoryRoomPriceRepository;

    @Override
    public CategoryRoomPrice findByCateRoomPriceId(Long id) {
        return categoryRoomPriceRepository.getCategoryId(id);
    }

    @Override
    public void createCategoryRoomPrice(CreateCategoryRoomPriceDTO dto) {
        // set old price's flag to 0
        categoryRoomPriceRepository.updateFlag(dto.getRoomCategoryId());
        // save
        CategoryRoomPrice categoryRoomPrice = new CategoryRoomPrice();
        categoryRoomPrice.setRoomCategoryId(dto.getRoomCategoryId());
        categoryRoomPrice.setStartDate(dto.getStartDate());
        categoryRoomPrice.setEndDate(dto.getEndDate());
        categoryRoomPrice.setPrice(dto.getPrice());
        categoryRoomPrice.setFlag(1);
        categoryRoomPrice.setCreatedDate(Date.valueOf(LocalDate.now()));
        categoryRoomPriceRepository.save(categoryRoomPrice);
    }
}
