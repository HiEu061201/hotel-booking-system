package fu.hbs.service.impl;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import fu.hbs.dto.AccountManagerDTO.RevenueDTO;
import fu.hbs.dto.AccountManagerDTO.RoomStatusTodayDTO;
import fu.hbs.dto.MRoomDTO.ViewRoomDTO;
import fu.hbs.dto.SaleManagerController.UpdateServiceDTO;
import fu.hbs.dto.ServiceDTO.ViewServiceDTO;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import fu.hbs.entities.RoomService;
import fu.hbs.entities.RoomStatus;
import fu.hbs.repository.BookingRoomDetailsRepository;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.RoomCategoriesRepository;
import fu.hbs.repository.RoomRepository;
import fu.hbs.repository.RoomServiceRepository;
import fu.hbs.repository.RoomStatusRepository;
import fu.hbs.repository.TransactionsRepository;
import fu.hbs.utils.BookingUtil;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {
	@Mock
	private RoomRepository roomRepository;

	@Mock
	private RoomServiceRepository roomServiceRepository;
	@Mock
	private RoomCategoriesRepository roomCategoriesRepository;
	@Mock
	private RoomStatusRepository roomStatusRepository;
	@Mock
	private CategoryRoomPriceRepository categoryRoomPriceRepository;
	@Mock
	private HotelBookingRepository hotelBookingRepository;
	@Mock
	private TransactionsRepository transactionsRepository;
	@Mock
	private BookingRoomDetailsRepository bookingRoomDetailsRepository;

	@InjectMocks
	private RoomServiceImpl roomService;
	@InjectMocks
	private BookingUtil bookingUtil;

	@Test
	@DisplayName("findByNameAndStatusUTCID01")
	void testFindByNameAndStatus() {
		// Arrange
		String name = "ServiceName";
		Integer status = 1;
		int page = 0;
		int size = 10;

		fu.hbs.entities.RoomService roomServiceEntity = new fu.hbs.entities.RoomService();
		roomServiceEntity.setServiceId(1L);
		roomServiceEntity.setServiceName(name);
		roomServiceEntity.setStatus(true);

		PageImpl<RoomService> roomServicePage = new PageImpl<>(Collections.singletonList(roomServiceEntity));

		// Mocking repository method
		when(roomServiceRepository.searchByNameAndStatus(name, status, PageRequest.of(page, size)))
				.thenReturn(roomServicePage);

		// Act
		Page<ViewServiceDTO> result = roomService.findByNameAndStatus(name, status, page, size);

		// Assert
		assertEquals(1, result.getContent().size());
		assertEquals(roomServiceEntity, result.getContent().get(0).getRoomService());

	}

	@Test
	@DisplayName("testFindByFloorAndStatusUTCID01")
	void testFindByFloorAndStatus() {
		// Arrange
		Integer floor = 1;
		Integer status = 1;
		int page = 0;
		int size = 10;

		Room room = new Room();
		room.setRoomId(1L);
		room.setRoomCategoryId(1L);
		room.setRoomStatusId(1L);

		RoomCategories roomCategories = new RoomCategories();
		when(roomCategoriesRepository.findByRoomCategoryId(room.getRoomCategoryId())).thenReturn(roomCategories);

		CategoryRoomPrice categoryRoomPrice = new CategoryRoomPrice();
		when(categoryRoomPriceRepository.findByRoomCategoryIdAndFlag(room.getRoomCategoryId(), 1))
				.thenReturn(categoryRoomPrice);

		RoomStatus roomStatus = new RoomStatus();
		when(roomStatusRepository.findByRoomStatusId(room.getRoomStatusId())).thenReturn(roomStatus);

		PageImpl<Room> roomPage = new PageImpl<>(Collections.singletonList(room));

		// Mocking repository method
		when(roomRepository.findFloorAndStatusId(floor, status, PageRequest.of(page, size))).thenReturn(roomPage);

		// Act
		Page<ViewRoomDTO> result = roomService.findByFloorAndStatus(floor, status, page, size);

		// Assert
		assertEquals(1, result.getContent().size());
		assertEquals(room, result.getContent().get(0).getRoom());
		assertEquals(roomCategories, result.getContent().get(0).getRoomCategories());
		assertEquals(categoryRoomPrice, result.getContent().get(0).getCategoryRoomPrice());
		assertEquals(roomStatus, result.getContent().get(0).getRoomStatus());

		// Verify that the repository method was called with the correct parameters
		verify(roomRepository, times(1)).findFloorAndStatusId(floor, status, PageRequest.of(page, size));
		verify(roomCategoriesRepository, times(1)).findByRoomCategoryId(room.getRoomCategoryId());
		verify(categoryRoomPriceRepository, times(1)).findByRoomCategoryIdAndFlag(room.getRoomCategoryId(), 1);
		verify(roomStatusRepository, times(1)).findByRoomStatusId(room.getRoomStatusId());
	}

	@Test
	@DisplayName("viewRevenueUTCID01")
	void viewRevenue() {
		// Mock data
		String year = "2023";
		RevenueDTO expectedRevenueDTO = new RevenueDTO();
		RoomStatusTodayDTO expectedRoomStatusTodayDTO = new RoomStatusTodayDTO();
		expectedRevenueDTO.setRoomStatusTodayDTO(expectedRoomStatusTodayDTO);

		// Mock behavior
		when(hotelBookingRepository.countBookingTodayByStatus(2L)).thenReturn(1);
		when(hotelBookingRepository.countBookingTodayByStatus(3L)).thenReturn(2);
		when(hotelBookingRepository.countBookingTodayByStatus(4L)).thenReturn(3);
		when(roomRepository.countAll()).thenReturn(10);
		when(roomRepository.countAllByRoomStatusId(1L)).thenReturn(5);
		when(roomRepository.countAllByRoomStatusId(2L)).thenReturn(5);
		when(transactionsRepository.getRevenueByYearAndMonth(any(), any())).thenReturn(100F, 200F, 3000F, 4000F, 5000F,
				6000F, 7000F, 8000F, 9000F, 10000F, 11000F, 1200F);

		RevenueDTO result = roomService.viewRevenue(year);
	}

	@Test
	@DisplayName("testUpdateRoomServiceUTCID01")
	void testUpdateRoomService() {
		// Arrange
		UpdateServiceDTO updateServiceDTO = new UpdateServiceDTO();
		updateServiceDTO.setServiceId(1L);
		updateServiceDTO.setServiceDes("Updated Description");
		updateServiceDTO.setServiceName("Updated Service");
		updateServiceDTO.setServicePrice(BigDecimal.valueOf(50.0));
		updateServiceDTO.setServiceImage("updated_image.jpg");
		updateServiceDTO.setStatus(true);

		RoomService existingRoomService = new RoomService();
		existingRoomService.setServiceId(updateServiceDTO.getServiceId());

		// Mocking repository method
		when(roomServiceRepository.findByServiceId(updateServiceDTO.getServiceId())).thenReturn(existingRoomService);
		when(roomServiceRepository.save(existingRoomService)).thenReturn(existingRoomService);

		// Act
		roomService.updateRoomService(updateServiceDTO);

		// Assert
		assertEquals(updateServiceDTO.getServiceDes(), existingRoomService.getServiceDes());
		assertEquals(updateServiceDTO.getServiceName(), existingRoomService.getServiceName());
		assertEquals(updateServiceDTO.getServicePrice(), existingRoomService.getServicePrice());
		assertEquals(updateServiceDTO.getServiceImage(), existingRoomService.getServiceImage());
		assertEquals(updateServiceDTO.getStatus(), existingRoomService.getStatus());

		// Verify that the repository method was called with the correct parameters
		verify(roomServiceRepository, times(1)).findByServiceId(updateServiceDTO.getServiceId());
		verify(roomServiceRepository, times(1)).save(existingRoomService);
	}

	@Test
	@DisplayName("testFindByServiceIdUTCID01")
	void testFindByServiceId() {
		// Arrange
		Long serviceId = 1L;

		RoomService expectedRoomService = new RoomService();
		expectedRoomService.setServiceId(serviceId);

		// Mocking repository method
		when(roomServiceRepository.findByServiceId(serviceId)).thenReturn(expectedRoomService);

		// Act
		RoomService result = roomService.findByServiceId(serviceId);

		// Assert
		assertEquals(expectedRoomService, result);

		// Verify that the repository method was called with the correct parameter
		verify(roomServiceRepository, times(1)).findByServiceId(serviceId);
	}

	@Test
	@DisplayName("testCreateRoomServiceUTCID01")
	void testCreateRoomService() {
		// Arrange
		UpdateServiceDTO dto = new UpdateServiceDTO();
		dto.setServiceNote("Note");
		dto.setServiceDes("Description");
		dto.setServicePrice(BigDecimal.valueOf(50.0));
		dto.setServiceName("Service");
		dto.setServiceImage("image.jpg");
		dto.setStatus(true);

		// Act
		roomService.createRoomService(dto);

		// Assert
		verify(roomServiceRepository, times(1)).save(any(RoomService.class));

	}

	@Test
	@DisplayName("testUpdateRoomByStatusIdUTCID01")
	void testUpdateRoomByStatusId() {
		// Arrange
		int status = 1;
		Long roomId = 1L;

		// Act
		roomService.updateRoomByStatusId(status, roomId);

		// Assert
		verify(roomRepository, times(1)).updateRoomByStatusId(status, roomId);
	}

	@Test
	@DisplayName("testFindByRoomIdUTCID01")
	void testFindByRoomId() {
		// Arrange
		Long roomId = 1L;
		Room expectedRoom = new Room();
		expectedRoom.setRoomId(roomId);

		// Mocking repository method
		when(roomRepository.findRoomByRoomId(roomId)).thenReturn(expectedRoom);

		// Act
		Room result = roomService.findByRoomId(roomId);

		// Assert
		assertEquals(expectedRoom, result);

		// Verify that the repository method was called with the correct parameter
		verify(roomRepository, times(1)).findRoomByRoomId(roomId);
	}

	@Test
	void testFindAvailableRoom() {
		// Arrange
		Long roomCategoryId = 1L;
		LocalDate checkIn = LocalDate.now();
		LocalDate checkOut = LocalDate.now().plusDays(1);

		Room availableRoom1 = new Room();
		availableRoom1.setRoomStatusId(2L);

		Room availableRoom2 = new Room();
		availableRoom2.setRoomStatusId(2L);

		Room unavailableRoom = new Room();
		unavailableRoom.setRoomStatusId(1L);

		List<Room> allRooms = Arrays.asList(availableRoom1, availableRoom2, unavailableRoom);

		when(roomRepository.findAvailableRoomsByCategoryId(roomCategoryId, checkIn, checkOut)).thenReturn(allRooms);

		// Act
		List<Room> result = roomService.findAvailableRoom(roomCategoryId, checkIn, checkOut);

		// Assert
		assertEquals(2, result.size());
		assertTrue(result.stream().allMatch(room -> room.getRoomStatusId().equals(2L)));
	}

	@Test
	void testFindRoomById() {
		// Arrange
		Long roomId = 1L;
		Room room = new Room();

		when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

		// Act
		Room result = roomService.findRoomById(roomId);

		// Assert
		assertNotNull(result);
		assertSame(room, result);
	}

}
