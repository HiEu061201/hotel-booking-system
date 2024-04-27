package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import fu.hbs.dto.MRoomDTO.ViewRoomCategoryPriceDTO;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.RoomCategories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.RoomCategoriesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RoomByCategoryServiceImplTest {

    @Mock
    RoomCategoriesRepository roomCategoriesRepository;
    @Mock
    CategoryRoomPriceRepository categoryRoomPriceRepository;

    @InjectMocks
    RoomByCategoryPriceServiceImpl roomByCategoryServiceImpl;


    @Test
    @DisplayName("searchByCategoryIdAndFlagUTCID01")
    void searchByCategoryIdAndFlag() {
        // Arrange
        int categoryId = 1;
        int flag = 1;
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);

        List<CategoryRoomPrice> mockCategoryRoomPrices = new ArrayList<>();
        CategoryRoomPrice categoryRoomPrice1 = new CategoryRoomPrice();
        categoryRoomPrice1.setRoomPriceId(1L);
        categoryRoomPrice1.setRoomCategoryId(1L);
        categoryRoomPrice1.setFlag(1);
        mockCategoryRoomPrices.add(categoryRoomPrice1);

        CategoryRoomPrice categoryRoomPrice2 = new CategoryRoomPrice();
        categoryRoomPrice2.setRoomPriceId(2L);
        categoryRoomPrice2.setRoomCategoryId(2L);
        categoryRoomPrice2.setFlag(1);
        mockCategoryRoomPrices.add(categoryRoomPrice2);

        when(categoryRoomPriceRepository.searchByCategoryIdAndFlag(categoryId, flag, pageRequest))
                .thenReturn(new PageImpl<>(mockCategoryRoomPrices));

        when(roomCategoriesRepository.findByRoomCategoryId(anyLong())).thenReturn(new RoomCategories());

        // Act
        Page<ViewRoomCategoryPriceDTO> result = roomByCategoryServiceImpl.searchByCategoryIdAndFlag(categoryId, flag, page, size);

        // Assert
        assertEquals(2, result.getTotalElements());

    }

}
