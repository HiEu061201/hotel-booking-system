package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import fu.hbs.service.impl.CategoryRoomPriceServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fu.hbs.dto.MRoomDTO.CreateCategoryRoomPriceDTO;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.repository.CategoryRoomPriceRepository;

@ExtendWith(MockitoExtension.class)
class CategoryRoomPriceServiceImplTest {

    @Mock
    private CategoryRoomPriceRepository categoryRoomPriceRepository;

    @InjectMocks
    private CategoryRoomPriceServiceImpl categoryRoomPriceService;

    @Test
    @DisplayName("testFindByCateRoomPriceIdUTCID01")
    void testFindByCateRoomPriceId() {
        // Given
        Long categoryId = 1L;
        CategoryRoomPrice expectedCategoryRoomPrice = new CategoryRoomPrice();
        when(categoryRoomPriceRepository.getCategoryId(categoryId)).thenReturn(expectedCategoryRoomPrice);

        // When
        CategoryRoomPrice result = categoryRoomPriceService.findByCateRoomPriceId(categoryId);

        // Then

        verify(categoryRoomPriceRepository, times(1)).getCategoryId(categoryId);

        assertEquals(expectedCategoryRoomPrice, result);
    }

    @Test
    @DisplayName("CreateCategoryRoomPriceUTCID01")
    void testCreateCategoryRoomPrice() {
        // Given
        CreateCategoryRoomPriceDTO dto = new CreateCategoryRoomPriceDTO();
        dto.setRoomCategoryId(1L);
        dto.setStartDate(Date.valueOf(LocalDate.now()));
        dto.setEndDate(Date.valueOf(LocalDate.now().plusDays(7)));
        dto.setPrice(BigDecimal.valueOf(100.0));

        // When
        categoryRoomPriceService.createCategoryRoomPrice(dto);

        // Then
        // Verify that updateFlag was called with the correct argument
        verify(categoryRoomPriceRepository, times(1)).updateFlag(dto.getRoomCategoryId());

        // Verify that save was called with the expected CategoryRoomPrice object
        verify(categoryRoomPriceRepository, times(1)).save(any(CategoryRoomPrice.class));
    }
}
