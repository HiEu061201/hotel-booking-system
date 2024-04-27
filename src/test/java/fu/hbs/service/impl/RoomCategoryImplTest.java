package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import fu.hbs.dto.RoomCategoryDTO.ViewRoomCategoryDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.RoomCategories;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.RoomCategoriesRepository;

@ExtendWith(MockitoExtension.class)
class RoomCategoryImplTest {
    @Mock
    private CategoryRoomPriceRepository categoryRoomPriceRepository;

    @Mock
    private RoomCategoriesRepository roomCategoriesRepository;

    @InjectMocks
    private RoomCategoryServiceImpl roomCategoryImpl;

    /**
     * Get a list of all room categories along with their details.
     *
     * @return List of RoomCategoryDTO containing room category information
     */

    /**
     * Method under test: {@link RoomCategoryImpl#getAllRoom()}
     */
    /**
     * Method under test: {@link RoomCategoryServiceImpl#getAllRoom()}
     */
    @Test
    void testGetAllRoom() {
        RoomCategories roomCategories = new RoomCategories();
        roomCategories.setDescription("The characteristics of someone or something");
        roomCategories.setImage("Image");

        ArrayList<RoomCategories> roomCategoriesList = new ArrayList<>();
        roomCategoriesList.add(roomCategories);
        when(roomCategoriesRepository.findAll()).thenReturn(roomCategoriesList);

        CategoryRoomPrice categoryRoomPrice = new CategoryRoomPrice();
        categoryRoomPrice.setPrice(new BigDecimal("2.3"));
        categoryRoomPrice.setRoomCategoryId(1L);
        categoryRoomPrice.setRoomPriceId(1L);
  

        List<ViewRoomCategoryDTO> categoryDTOs = roomCategoryImpl.getAllRoom();
        assertEquals(1, categoryDTOs.size());
    }
}
