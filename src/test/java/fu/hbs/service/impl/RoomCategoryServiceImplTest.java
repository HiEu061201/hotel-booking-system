package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import fu.hbs.dto.User.BookRoomByCategory;
import fu.hbs.dto.MRoomDTO.ViewCategoryDTO;
import fu.hbs.dto.RoomCategoryDTO.ViewRoomCategoryDTO;
import fu.hbs.entities.CategoryRoomFurniture;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import fu.hbs.repository.CategoryRoomFurnitureRepository;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.RoomCategoriesRepository;
import fu.hbs.repository.RoomFurnitureRepository;
import fu.hbs.repository.RoomImageRepository;
import fu.hbs.repository.RoomRepository;
import fu.hbs.repository.RoomServiceRepository;

@ExtendWith(MockitoExtension.class)
class RoomCategoryServiceImplTest {
    @Mock
    RoomServiceRepository serviceRepository;
    @Mock
    RoomImageRepository roomImageRepository;
    @Mock
    RoomRepository roomRepository;
    @Mock
    RoomFurnitureRepository roomFurnitureRepository;
    @Mock
    CategoryRoomFurnitureRepository categoryRoomFurnitureRepository;
    @Mock
    private RoomCategoriesRepository roomCategoriesRepository;

    @Mock
    private CategoryRoomPriceRepository categoryRoomPriceRepository;

    @InjectMocks
    private RoomCategoryServiceImpl roomCategoryService;

    @Test
    @DisplayName("getAllRoomUTCID01")
    void getAllRoom() {

        List<RoomCategories> mockRoomCategories = new ArrayList<>();
        RoomCategories mockRoomCategory = new RoomCategories();
        mockRoomCategory.setRoomCategoryId(1L);
        mockRoomCategory.setRoomCategoryName("Single");
        mockRoomCategory.setDescription("Description");
        mockRoomCategory.setImage("image");
        mockRoomCategories.add(mockRoomCategory);

        when(roomCategoriesRepository.findAll()).thenReturn(mockRoomCategories);
        when(categoryRoomPriceRepository.findByRoomCategoryIdAndFlag(anyLong(), anyInt()))
                .thenReturn(new CategoryRoomPrice());

        // Act
        List<ViewRoomCategoryDTO> result = roomCategoryService.getAllRoom();

        // Assert
        assertEquals(1, result.size());

    }

    @Test
    @DisplayName("getRoomCategoryDetailsUTCID01")
    void getRoomCategoryDetailsUTCID01() {
        List<fu.hbs.entities.RoomService> list = new ArrayList<>();
        List<Room> rooms = new ArrayList<>();
        Room room = new Room();
        room.setRoomImageId(1L);
        room.setRoomCategoryId(1L);
        rooms.add(room);

        List<CategoryRoomFurniture> categoryRoomFurnitures = new ArrayList<CategoryRoomFurniture>();
        CategoryRoomFurniture categoryRoomFurniture = new CategoryRoomFurniture();
        categoryRoomFurniture.setNote("ook");
        categoryRoomFurniture.setFurnitureId(1L);
        categoryRoomFurnitures.add(categoryRoomFurniture);

        when(roomRepository.findByRoomCategoryId(Mockito.<Long>any())).thenReturn(rooms);
        when(categoryRoomFurnitureRepository.findByRoomCategoryId(Mockito.<Long>any())).thenReturn(new ArrayList<>());
        when(serviceRepository.findAll()).thenReturn(new ArrayList<>());
        when(categoryRoomFurnitureRepository.findByRoomCategoryId(Mockito.<Long>any()))
                .thenReturn(categoryRoomFurnitures);

        BookRoomByCategory bookRoomByCategory = roomCategoryService.getRoomCategoryDetails(1l);

        assertEquals(1, bookRoomByCategory.getRooms().size());
        assertEquals(1, bookRoomByCategory.getCategoryRoomFurnitures().size());

    }

    @Test
    @DisplayName("getRoomCategoryDetailsUTCID02")
    void getRoomCategoryDetailsWithEmptyLists() {
        when(roomRepository.findByRoomCategoryId(Mockito.anyLong())).thenReturn(Collections.emptyList());

        when(categoryRoomFurnitureRepository.findByRoomCategoryId(Mockito.anyLong())).thenReturn(Collections.emptyList());

        when(serviceRepository.findAll()).thenReturn(Collections.emptyList());

        BookRoomByCategory bookRoomByCategory = roomCategoryService.getRoomCategoryDetails(1L);

        assertTrue(bookRoomByCategory.getRooms().isEmpty(), "CategoryId không hợp lệ");
    }

    @Test
    @DisplayName("searchByNameAndStatusUTCID01")
    void searchByNameAndStatus() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RoomCategories> mockRoomCategories = new ArrayList<>();
        RoomCategories mockRoomCategory = new RoomCategories();
        mockRoomCategory.setRoomCategoryId(1L);
        mockRoomCategory.setRoomCategoryName("Double");
        mockRoomCategory.setDescription("Description");
        mockRoomCategory.setImage("image");
        mockRoomCategories.add(mockRoomCategory);

        Page<RoomCategories> mockPage = new PageImpl<>(mockRoomCategories);

        when(roomCategoriesRepository.searchByNameAndStatus(anyInt(), anyInt(), eq(pageRequest))).thenReturn(mockPage);
        when(categoryRoomPriceRepository.findByRoomCategoryIdAndFlag(anyLong(), anyInt()))
                .thenReturn(new CategoryRoomPrice());

        // Act
        Page<ViewCategoryDTO> result = roomCategoryService.searchByNameAndStatus(1, 1, 0, 10);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Double", result.getContent().get(0).getRoomCategories().getRoomCategoryName());

    }

    @Test
    void findAvailableRoomCategories() {
        // Arrange
        int numberOfPeople = 2;

        List<RoomCategories> mockRoomCategories = new ArrayList<>();
        RoomCategories roomCategory1 = new RoomCategories();
        roomCategory1.setRoomCategoryId(1L);
        roomCategory1.setRoomCategoryName("Double");
        roomCategory1.setNumberPerson(2);
        mockRoomCategories.add(roomCategory1);

        RoomCategories roomCategory2 = new RoomCategories();
        roomCategory2.setRoomCategoryId(2L);
        roomCategory2.setRoomCategoryName("Suite");
        roomCategory2.setNumberPerson(3);
        mockRoomCategories.add(roomCategory2);

        when(roomCategoriesRepository.findByNumberPersonGreaterThanEqual(numberOfPeople))
                .thenReturn(mockRoomCategories);

        // Act
        List<RoomCategories> result = roomCategoryService.findAvailableRoomCategories(numberOfPeople);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Double", result.get(0).getRoomCategoryName());
        assertEquals("Suite", result.get(1).getRoomCategoryName());
        // Add more assertions based on your actual implementation
    }

    @Test
    void getRoomCategoryId() {
        // Arrange
        Long roomId = 1L;
        RoomCategories mockRoomCategory = new RoomCategories();
        mockRoomCategory.setRoomCategoryId(roomId);
        mockRoomCategory.setRoomCategoryName("Suite");
        mockRoomCategory.setDescription("Description");
        mockRoomCategory.setImage("image");

        when(roomCategoriesRepository.findByRoomCategoryId(roomId)).thenReturn(mockRoomCategory);

        // Act
        RoomCategories result = roomCategoryService.getRoomCategoryId(roomId);

        // Assert
        assertEquals("Suite", result.getRoomCategoryName());

    }

    @Test
    void deleteByRoomCategoryId() {
        // Arrange
        Long roomId = 1L;
        RoomCategories mockRoomCategory = new RoomCategories();
        mockRoomCategory.setRoomCategoryId(roomId);
        mockRoomCategory.setRoomCategoryName("Standard");
        mockRoomCategory.setDescription("Description");
        mockRoomCategory.setImage("image");

        when(roomCategoriesRepository.deleteByRoomCategoryId(roomId)).thenReturn(mockRoomCategory);

        // Act
        RoomCategories result = roomCategoryService.deleteByRoomCategoryId(roomId);

        // Assert
        assertEquals("Standard", result.getRoomCategoryName());

    }

    @Test
    void getAllRoomCategories() {
        // Arrange
        List<RoomCategories> mockRoomCategories = new ArrayList<>();
        RoomCategories roomCategory1 = new RoomCategories();
        roomCategory1.setRoomCategoryId(1L);
        roomCategory1.setRoomCategoryName("Standard");
        mockRoomCategories.add(roomCategory1);

        RoomCategories roomCategory2 = new RoomCategories();
        roomCategory2.setRoomCategoryId(2L);
        roomCategory2.setRoomCategoryName("Suite");
        mockRoomCategories.add(roomCategory2);

        when(roomCategoriesRepository.findAll()).thenReturn(mockRoomCategories);

        // Act
        List<RoomCategories> result = roomCategoryService.getAllRoomCategories();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Standard", result.get(0).getRoomCategoryName());
        assertEquals("Suite", result.get(1).getRoomCategoryName());

    }

}
