package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import fu.hbs.constant.TransactionMessage;
import fu.hbs.dto.HotelBookingDTO.CreateHotelBookingDTO;
import fu.hbs.dto.HotelBookingDTO.CreateHotelBookingDetailDTO;
import fu.hbs.dto.HotelBookingDTO.SaveCheckinDTO;
import fu.hbs.dto.HotelBookingDTO.SaveCheckinDetailDTO;
import fu.hbs.dto.HotelBookingDTO.SaveCheckoutDTO;
import fu.hbs.dto.HotelBookingDTO.SaveCheckoutHotelServiceDTO;
import fu.hbs.dto.ReceptionistDTO.ViewBookingDTO;
import fu.hbs.entities.BookingRoomDetails;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.HotelBookingService;
import fu.hbs.entities.HotelBookingStatus;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomService;
import fu.hbs.entities.Transactions;
import fu.hbs.entities.User;
import fu.hbs.exceptionHandler.CheckoutException;
import fu.hbs.exceptionHandler.NotEnoughRoomAvalaibleException;
import fu.hbs.repository.BookingRoomDetailsRepository;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.HotelBookingServiceRepository;
import fu.hbs.repository.HotelBookingStatusRepository;
import fu.hbs.repository.RoomRepository;
import fu.hbs.repository.RoomServiceRepository;
import fu.hbs.repository.TransactionsRepository;
import fu.hbs.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ReceptionistBookingServiceImplTest {

	@Mock
	private HotelBookingRepository bookingRepository;

	@Mock
	private HotelBookingServiceRepository hotelBookingServiceRepository;

	@Mock
	private TransactionsRepository transactionsRepository;

	@Mock
	private RoomRepository roomRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CategoryRoomPriceRepository categoryRoomPriceRepository;

	@Mock
	private BookingRoomDetailsRepository bookingRoomDetailsRepository;

	@Mock
	private HotelBooking hotelBooking;

	@Mock
	private HotelBookingStatusRepository hotelBookingStatusRepository;

	@Mock
	private RoomServiceRepository roomServiceRepository;

	@InjectMocks
	private ReceptionistBookingServiceImpl bookingService;

	private SaveCheckoutDTO saveCheckoutDTO;

	private HotelBooking booking;
	private BookingRoomDetails bookingRoomDetails;
	private Room room;

	@BeforeEach
	public void setUp() {
		saveCheckoutDTO = new SaveCheckoutDTO();

		MockitoAnnotations.openMocks(this);
		booking = new HotelBooking();
		booking.setHotelBookingId(1L);
		booking.setCheckIn(Instant.now());
		booking.setStatusId(1L);

		bookingRoomDetails = new BookingRoomDetails();
		bookingRoomDetails.setHotelBookingId(1L);
		bookingRoomDetails.setRoomId(1L);

		room = new Room();
		room.setRoomId(1L);
		room.setRoomStatusId(2L);

		hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(1L);
		hotelBooking.setStatusId(1L);
		hotelBooking.setCheckIn(Instant.now());

		bookingRoomDetails = new BookingRoomDetails();
		bookingRoomDetails.setRoomId(1L);
		bookingRoomDetails.setHotelBookingId(hotelBooking.getHotelBookingId());

		room = new Room();
		room.setRoomId(1L);
		room.setRoomStatusId(2L);

		hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(1L);
		hotelBooking.setStatusId(2L);
		hotelBooking.setCheckIn(Instant.now());
		hotelBooking.setCheckOut(Instant.now());
		hotelBooking.setTotalPrice(BigDecimal.ZERO);
		hotelBooking.setDepositPrice(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("TestFindAllUTCD01")
	void testFindAll() {
		// Arrange
		HotelBooking booking1 = new HotelBooking();
		HotelBooking booking2 = new HotelBooking();
		List<HotelBooking> expectedBookings = Arrays.asList(booking1, booking2);

		// Mock the behavior of the bookingRepository.findAll() method
		when(bookingRepository.findAll()).thenReturn(expectedBookings);

		// Act
		List<HotelBooking> actualBookings = bookingService.findAll();

		// Assert
		assertEquals(expectedBookings, actualBookings);
		// You can add more assertions based on your specific requirements
	}

	@Test
	@DisplayName("testFindById_ExistingIdUTCD01")
	void testFindById_ExistingId() {
		// Arrange
		Long existingId = 1L;
		HotelBooking expectedBooking = new HotelBooking();

		// Mock the behavior of the bookingRepository.findById(existingId) method
		when(bookingRepository.findById(existingId)).thenReturn(Optional.of(expectedBooking));

		// Act
		HotelBooking actualBooking = bookingService.findById(existingId);

		// Assert
		assertEquals(expectedBooking, actualBooking);
	}

	@Test
	@DisplayName(" testFindById_NonExistingIdUTCD02")
	void testFindById_NonExistingId() {
		// Arrange
		Long nonExistingId = 2L;

		// Mock the behavior of the bookingRepository.findById(nonExistingId) method
		when(bookingRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		// Act and Assert
		assertThrows(RuntimeException.class, () -> {
			bookingService.findById(nonExistingId);
		});
	}

	@Test
	public void testCreateHotelBookingByReceptionist_ValidBooking_Success() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
		when(bookingRepository.save(any())).thenReturn(createSampleHotelBooking());
		when(bookingRoomDetailsRepository.saveAll(any())).thenReturn(createSampleBookingRoomDetailsList());
		when(categoryRoomPriceRepository.findAllById(any())).thenReturn(createSampleCategoryRoomPriceList());

		// Act
		Long bookingId = bookingService.createHotelBookingByReceptionist(bookingRequest);

		// Assert
		assertNotNull(bookingId);
		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_InvalidBookingTime_RuntimeExceptionThrown() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createInvalidBookingTimeRequest();

		// Act & Assert
		assertThrows(RuntimeException.class, () -> bookingService.createHotelBookingByReceptionist(bookingRequest));
		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_NotEnoughRoomAvailable_ExceptionThrown() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createNotEnoughRoomAvailableRequest();
		when(bookingRepository.save(any())).thenReturn(createSampleHotelBooking());
		when(categoryRoomPriceRepository.findAllById(any())).thenReturn(createSampleCategoryRoomPriceList());

		// Act & Assert
		assertThrows(NotEnoughRoomAvalaibleException.class,
				() -> bookingService.createHotelBookingByReceptionist(bookingRequest));
		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_ZeroRooms_ExceptionThrown() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
		bookingRequest.getBookingDetails().clear(); // Remove all rooms

		// Act & Assert
		assertThrows(RuntimeException.class, () -> bookingService.createHotelBookingByReceptionist(bookingRequest));
		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_RoomRepositoryInteraction() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
		when(bookingRepository.save(any())).thenReturn(createSampleHotelBooking());
		when(bookingRoomDetailsRepository.saveAll(any())).thenReturn(createSampleBookingRoomDetailsList());
		when(categoryRoomPriceRepository.findAllById(any())).thenReturn(createSampleCategoryRoomPriceList());

		// Act
		Long bookingId = bookingService.createHotelBookingByReceptionist(bookingRequest);

		// Assert
		verify(roomRepository, times(1)).findById(anyLong()); // Adjust this based on your actual interactions
		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_TransactionContent() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
		when(bookingRepository.save(any())).thenReturn(createSampleHotelBooking());
		when(bookingRoomDetailsRepository.saveAll(any())).thenReturn(createSampleBookingRoomDetailsList());
		when(categoryRoomPriceRepository.findAllById(any())).thenReturn(createSampleCategoryRoomPriceList());
		when(transactionsRepository.save(any())).thenReturn(createSampleTransaction());

		// Act
		Long bookingId = bookingService.createHotelBookingByReceptionist(bookingRequest);

		// Assert
		verify(transactionsRepository, times(1)).save(any());
		verify(transactionsRepository, times(1))
				.save(argThat(transaction -> transaction.getContent().equals(TransactionMessage.PAY.getMessage())));

		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_InvalidPaymentType_ExceptionThrown() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
		bookingRequest.setPaymentTypeId(999L); // Invalid payment type

		// Act & Assert
		assertThrows(RuntimeException.class, () -> bookingService.createHotelBookingByReceptionist(bookingRequest));
		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_WithPaymentType_Success() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
		when(bookingRepository.save(any())).thenReturn(createSampleHotelBooking());
		when(bookingRoomDetailsRepository.saveAll(any())).thenReturn(createSampleBookingRoomDetailsList());
		when(categoryRoomPriceRepository.findAllById(any())).thenReturn(createSampleCategoryRoomPriceList());

		// Mock transactionsRepository
		when(transactionsRepository.save(any())).thenReturn(createSampleTransaction());

		// Act
		Long bookingId = bookingService.createHotelBookingByReceptionist(bookingRequest);

		// Assert
		assertNotNull(bookingId);
		verify(transactionsRepository, times(1)).save(any());

		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_WithoutPaymentType_Success() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
		bookingRequest.setPaymentTypeId(1L); // Assuming payment type is 1 (No payment type)

		when(bookingRepository.save(any())).thenReturn(createSampleHotelBooking());
		when(bookingRoomDetailsRepository.saveAll(any())).thenReturn(createSampleBookingRoomDetailsList());
		when(categoryRoomPriceRepository.findAllById(any())).thenReturn(createSampleCategoryRoomPriceList());

		// Act
		Long bookingId = bookingService.createHotelBookingByReceptionist(bookingRequest);

		// Assert
		assertNotNull(bookingId);
		verify(transactionsRepository, never()).save(any());

		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_PayFull_TransactionCreated() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
		bookingRequest.setPayFull(true);
		when(bookingRepository.save(any())).thenReturn(createSampleHotelBooking());
		when(bookingRoomDetailsRepository.saveAll(any())).thenReturn(createSampleBookingRoomDetailsList());
		when(categoryRoomPriceRepository.findAllById(any())).thenReturn(createSampleCategoryRoomPriceList());

		// Act
		Long bookingId = bookingService.createHotelBookingByReceptionist(bookingRequest);

		// Assert
		verify(transactionsRepository, times(1)).save(any());
		// Add more assertions based on your specific requirements
	}

	@Test
	public void testCreateHotelBookingByReceptionist_PayHalf_TransactionCreated() {
		// Arrange
		CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
		bookingRequest.setPayFull(false);
		when(bookingRepository.save(any())).thenReturn(createSampleHotelBooking());
		when(bookingRoomDetailsRepository.saveAll(any())).thenReturn(createSampleBookingRoomDetailsList());
		when(categoryRoomPriceRepository.findAllById(any())).thenReturn(createSampleCategoryRoomPriceList());

		// Act
		Long bookingId = bookingService.createHotelBookingByReceptionist(bookingRequest);

		// Assert
		verify(transactionsRepository, times(1)).save(any());
		// Add more assertions based on your specific requirements
	}

//    @Test
//    public void testCreateHotelBookingByReceptionist_InvalidPaymentType_NoTransactionCreated() {
//        // Arrange
//        CreateHotelBookingDTO bookingRequest = createValidBookingRequest();
//        bookingRequest.setPaymentTypeId(999L);  // Invalid payment type
//        when(bookingRepository.save(any())).thenReturn(createSampleHotelBooking());
//        when(bookingRoomDetailsRepository.saveAll(any())).thenReturn(createSampleBookingRoomDetailsList());
//        when(categoryRoomPriceRepository.findAllById(any())).thenReturn(createSampleCategoryRoomPriceList());
//
//        // Act
//        Long bookingId = bookingService.createHotelBookingByReceptionist(bookingRequest);
//
//        // Assert
//        verify(transactionsRepository, never()).save(any());
//        // Add more assertions based on your specific requirements
//    }

	@Test
	public void testCheckoutWhenOperationIsSuccessfulThenReturnTrue() {
		SaveCheckoutDTO saveCheckoutDTO = new SaveCheckoutDTO();
		saveCheckoutDTO.setHotelBookingId(hotelBooking.getHotelBookingId());
		saveCheckoutDTO.setPaymentTypeId(2L);
		saveCheckoutDTO.setServicePrice(BigDecimal.ZERO);
		saveCheckoutDTO.setSurcharge(BigDecimal.ZERO);
		saveCheckoutDTO.setHotelServices(Arrays.asList(new SaveCheckoutHotelServiceDTO()));

		when(bookingRepository.findById(hotelBooking.getHotelBookingId())).thenReturn(Optional.of(hotelBooking));
		when(bookingRoomDetailsRepository.getAllByHotelBookingId(hotelBooking.getHotelBookingId()))
				.thenReturn(Arrays.asList());

		assertTrue(bookingService.checkout(saveCheckoutDTO));

		verify(bookingRepository, times(1)).findById(hotelBooking.getHotelBookingId());
		verify(bookingRoomDetailsRepository, times(1)).getAllByHotelBookingId(hotelBooking.getHotelBookingId());
	}

	@Test
	public void testCheckoutWhenBookingIsNotValidThenReturnFalse() {
		hotelBooking.setStatusId(1L);
		SaveCheckoutDTO saveCheckoutDTO = new SaveCheckoutDTO();
		saveCheckoutDTO.setHotelBookingId(hotelBooking.getHotelBookingId());

		when(bookingRepository.findById(hotelBooking.getHotelBookingId())).thenReturn(Optional.of(hotelBooking));

		assertThrows(CheckoutException.class, () -> bookingService.checkout(saveCheckoutDTO));

		verify(bookingRepository, times(1)).findById(hotelBooking.getHotelBookingId());
	}

	@Test
	public void testCheckoutWhenBookingDoesNotExistThenReturnFalse() {
		SaveCheckoutDTO saveCheckoutDTO = new SaveCheckoutDTO();
		saveCheckoutDTO.setHotelBookingId(hotelBooking.getHotelBookingId());

		when(bookingRepository.findById(hotelBooking.getHotelBookingId())).thenReturn(Optional.empty());

		assertFalse(bookingService.checkout(saveCheckoutDTO));

		verify(bookingRepository, times(1)).findById(hotelBooking.getHotelBookingId());
	}

	@Test
	public void testCheckoutWhenBookingIsAlreadyCheckedOutThenReturnFalse() {
		hotelBooking.setStatusId(3L); // Set status to checked out
		SaveCheckoutDTO saveCheckoutDTO = new SaveCheckoutDTO();
		saveCheckoutDTO.setHotelBookingId(hotelBooking.getHotelBookingId());

		when(bookingRepository.findById(hotelBooking.getHotelBookingId())).thenReturn(Optional.of(hotelBooking));

		assertFalse(bookingService.checkout(saveCheckoutDTO));

		verify(bookingRepository, times(1)).findById(hotelBooking.getHotelBookingId());
	}

	@Test
	void changeRoomStatus_SuccessfulChange() {
		BookingRoomDetails bookingRoomDetails1 = new BookingRoomDetails();
		bookingRoomDetails1.setRoomId(1L);

		BookingRoomDetails bookingRoomDetails2 = new BookingRoomDetails();
		bookingRoomDetails2.setRoomId(2L);

		List<BookingRoomDetails> hotelBookingDetails = Arrays.asList(bookingRoomDetails1, bookingRoomDetails2);

		Room bookedRoom1 = new Room();
		bookedRoom1.setRoomId(1L);

		Room bookedRoom2 = new Room();
		bookedRoom2.setRoomId(2L);

		when(roomRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(bookedRoom1, bookedRoom2));

		bookingService.changeRoomStatus(hotelBookingDetails);

		// Verify that setRoomStatusId(3L) was called on both booked rooms
		verify(bookedRoom1, times(1)).setRoomStatusId(3L);
		verify(bookedRoom2, times(1)).setRoomStatusId(3L);

		// Verify that saveAll was called on roomRepository with the modified rooms
		verify(roomRepository, times(1)).saveAll(Arrays.asList(bookedRoom1, bookedRoom2));
	}

	@Test
	void testCalculateRoomPrice() throws Exception {
		// Arrange
		List<BookingRoomDetails> bookingDetails = createTestBookingDetails();
		BigDecimal roomPrice = BigDecimal.ZERO;
		Instant checkIn = Instant.now();
		Instant checkOut = Instant.now().plusSeconds(3600); // Assuming 1 hour duration

		// Use reflection to access the private method
		Method method = ReceptionistBookingServiceImpl.class.getDeclaredMethod("calculateRoomPrice", List.class,
				BigDecimal.class, Instant.class, Instant.class);
		method.setAccessible(true);

		// Act
		BigDecimal result = (BigDecimal) method.invoke(null, bookingDetails, roomPrice, checkIn, checkOut);

	}

	private List<BookingRoomDetails> createTestBookingDetails() {
		List<BookingRoomDetails> bookingRoomDetailsList = new ArrayList<>();

		// Tạo một BookingRoomDetails giả định
		BookingRoomDetails bookingRoomDetails1 = new BookingRoomDetails();
		bookingRoomDetails1.setHotelBookingId(1L); // Điền vào ID của đặt phòng tương ứng
		bookingRoomDetails1.setRoomCategoryId(1L); // Điền vào ID của danh mục phòng tương ứng
		bookingRoomDetails1.setRoomId(101L); // Điền vào ID của phòng tương ứng
		// Các trường khác...

		// Tạo một BookingRoomDetails khác nếu cần thiết
		BookingRoomDetails bookingRoomDetails2 = new BookingRoomDetails();
		bookingRoomDetails2.setHotelBookingId(1L);
		bookingRoomDetails2.setRoomCategoryId(2L);
		bookingRoomDetails2.setRoomId(201L);
		// Các trường khác...

		// Thêm các đối tượng BookingRoomDetails vào danh sách
		bookingRoomDetailsList.add(bookingRoomDetails1);
		bookingRoomDetailsList.add(bookingRoomDetails2);

		return bookingRoomDetailsList;
	}

	private CreateHotelBookingDTO createValidBookingRequest() {
		CreateHotelBookingDTO bookingRequest = new CreateHotelBookingDTO();

		bookingRequest.setName("John Doe");
		bookingRequest.setEmail("john.doe@example.com");
		bookingRequest.setAddress("123 Main St");
		bookingRequest.setPhone("123456789");
		bookingRequest.setNotes("Special requests or notes");

		// Ngày check-in và check-out
		LocalDate checkInDate = LocalDate.now().plusDays(1);
		LocalDate checkOutDate = LocalDate.now().plusDays(3);
		bookingRequest.setCheckIn(checkInDate);
		bookingRequest.setCheckOut(checkOutDate);

		// Chi tiết đặt phòng
		List<CreateHotelBookingDetailDTO> bookingDetails = new ArrayList<>();
		CreateHotelBookingDetailDTO detail1 = new CreateHotelBookingDetailDTO();
		detail1.setRoomCategoryId(1L);
		detail1.setRoomNumber(2);
		bookingDetails.add(detail1);

		CreateHotelBookingDetailDTO detail2 = new CreateHotelBookingDetailDTO();
		detail2.setRoomCategoryId(2L);
		detail2.setRoomNumber(1);
		bookingDetails.add(detail2);

		bookingRequest.setBookingDetails(bookingDetails);

		// Thanh toán
		bookingRequest.setPayFull(true);
		bookingRequest.setPaymentTypeId(1L); // Assume payment type ID 1 is valid

		return bookingRequest;
	}

	private HotelBooking createSampleHotelBooking() {
		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setUserId(null);
		hotelBooking.setStatusId(1L);
		hotelBooking.setName("John Doe");
		hotelBooking.setEmail("john.doe@example.com");
		hotelBooking.setAddress("123 Main St");
		hotelBooking.setPhone("123456789");
		hotelBooking.setNote("Special requests or notes");

		// Ngày check-in và check-out
		LocalDate checkInLocalDate = LocalDate.now().plusDays(1);
		LocalDate checkOutLocalDate = LocalDate.now().plusDays(3);

		LocalDateTime checkInWithSpecificTime = LocalDateTime.of(checkInLocalDate.getYear(),
				checkInLocalDate.getMonth(), checkInLocalDate.getDayOfMonth(), 14, 0, 0);
		LocalDateTime checkoutWithSpecificTime = LocalDateTime.of(checkOutLocalDate.getYear(),
				checkOutLocalDate.getMonth(), checkOutLocalDate.getDayOfMonth(), 12, 30, 0);

		ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
		Instant checkOut = checkoutWithSpecificTime.atZone(zoneId).toInstant();
		Instant checkIn = checkInWithSpecificTime.atZone(zoneId).toInstant();

		hotelBooking.setCheckIn(checkIn);
		hotelBooking.setCheckOut(checkOut);
		hotelBooking.setCheckInActual(checkIn);
		hotelBooking.setCheckOutActual(checkOut);

		hotelBooking.setTotalRoom(3); // Assume there are 3 rooms in total for this booking

		// Giả sử bạn đã thực hiện các bước khác để thiết lập các thuộc tính khác của
		// đối tượng HotelBooking.

		return hotelBooking;
	}

	private List<BookingRoomDetails> createSampleBookingRoomDetailsList() {
		List<BookingRoomDetails> bookingRoomDetailsList = new ArrayList<>();

		// Tạo danh sách các phòng đặt
		Room room1 = new Room();
		room1.setRoomId(1L);
		room1.setRoomCategoryId(1L);

		Room room2 = new Room();
		room2.setRoomId(2L);
		room2.setRoomCategoryId(2L);

		Room room3 = new Room();
		room3.setRoomId(3L);
		room3.setRoomCategoryId(1L);

		// Giả sử bạn đã thực hiện các bước khác để thiết lập các thuộc tính khác của
		// đối tượng Room.

		// Tạo đối tượng BookingRoomDetails và thêm vào danh sách
		BookingRoomDetails bookingRoomDetail1 = new BookingRoomDetails();
		bookingRoomDetail1.setHotelBookingId(1L); // Assume booking ID is 1
		bookingRoomDetail1.setRoomCategoryId(1L);
		bookingRoomDetail1.setRoomId(room1.getRoomId());

		BookingRoomDetails bookingRoomDetail2 = new BookingRoomDetails();
		bookingRoomDetail2.setHotelBookingId(1L); // Assume booking ID is 1
		bookingRoomDetail2.setRoomCategoryId(2L);
		bookingRoomDetail2.setRoomId(room2.getRoomId());

		BookingRoomDetails bookingRoomDetail3 = new BookingRoomDetails();
		bookingRoomDetail3.setHotelBookingId(1L); // Assume booking ID is 1
		bookingRoomDetail3.setRoomCategoryId(1L);
		bookingRoomDetail3.setRoomId(room3.getRoomId());

		// Thêm vào danh sách
		bookingRoomDetailsList.add(bookingRoomDetail1);
		bookingRoomDetailsList.add(bookingRoomDetail2);
		bookingRoomDetailsList.add(bookingRoomDetail3);

		return bookingRoomDetailsList;
	}

	private List<CategoryRoomPrice> createSampleCategoryRoomPriceList() {
		List<CategoryRoomPrice> categoryRoomPriceList = new ArrayList<>();

		// Tạo danh sách giá theo loại phòng
		CategoryRoomPrice categoryRoomPrice1 = new CategoryRoomPrice();
		categoryRoomPrice1.setRoomCategoryId(1L);
		categoryRoomPrice1.setPrice(BigDecimal.valueOf(100));

		CategoryRoomPrice categoryRoomPrice2 = new CategoryRoomPrice();
		categoryRoomPrice2.setRoomCategoryId(2L);
		categoryRoomPrice2.setPrice(BigDecimal.valueOf(150));

		// Giả sử bạn đã thực hiện các bước khác để thiết lập các thuộc tính khác của
		// đối tượng CategoryRoomPrice.

		// Thêm vào danh sách
		categoryRoomPriceList.add(categoryRoomPrice1);
		categoryRoomPriceList.add(categoryRoomPrice2);

		return categoryRoomPriceList;
	}

	private CreateHotelBookingDTO createInvalidBookingTimeRequest() {
		CreateHotelBookingDTO bookingRequest = new CreateHotelBookingDTO();

		bookingRequest.setName("Jane Doe");
		bookingRequest.setEmail("jane.doe@example.com");
		bookingRequest.setAddress("456 Oak St");
		bookingRequest.setPhone("987654321");
		bookingRequest.setNotes("Special requests or notes");

		// Ngày check-in và check-out không hợp lệ
		// Ngày check-in và check-out
		LocalDate checkInDate = LocalDate.now().plusDays(1);
		LocalDate checkOutDate = LocalDate.now().plusDays(3);
		bookingRequest.setCheckIn(checkInDate);
		bookingRequest.setCheckOut(checkOutDate);

		// Chi tiết đặt phòng
		List<CreateHotelBookingDetailDTO> bookingDetails = new ArrayList<>();
		CreateHotelBookingDetailDTO detail1 = new CreateHotelBookingDetailDTO();
		detail1.setRoomCategoryId(1L);
		detail1.setRoomNumber(2);
		bookingDetails.add(detail1);

		bookingRequest.setBookingDetails(bookingDetails);

		// Thanh toán
		bookingRequest.setPayFull(true);
		bookingRequest.setPaymentTypeId(1L); // Assume payment type ID 1 is valid

		return bookingRequest;
	}

	private CreateHotelBookingDTO createNotEnoughRoomAvailableRequest() {
		CreateHotelBookingDTO bookingRequest = new CreateHotelBookingDTO();

		bookingRequest.setName("Bob Smith");
		bookingRequest.setEmail("bob.smith@example.com");
		bookingRequest.setAddress("789 Pine St");
		bookingRequest.setPhone("555555555");
		bookingRequest.setNotes("Special requests or notes");

		// Ngày check-in và check-out
		// Ngày check-in và check-out
		LocalDate checkInDate = LocalDate.now().plusDays(1);
		LocalDate checkOutDate = LocalDate.now().plusDays(3);
		bookingRequest.setCheckIn(checkInDate);
		bookingRequest.setCheckOut(checkOutDate);

		// Chi tiết đặt phòng
		List<CreateHotelBookingDetailDTO> bookingDetails = new ArrayList<>();

		// Giả sử sự cố: yêu cầu đặt phòng nhiều hơn số phòng có sẵn
		CreateHotelBookingDetailDTO detail1 = new CreateHotelBookingDetailDTO();
		detail1.setRoomCategoryId(1L);
		detail1.setRoomNumber(10); // Giả sử chỉ có 5 phòng có sẵn
		bookingDetails.add(detail1);

		bookingRequest.setBookingDetails(bookingDetails);

		// Thanh toán
		bookingRequest.setPayFull(true);
		bookingRequest.setPaymentTypeId(1L); // Assume payment type ID 1 is valid

		return bookingRequest;
	}

	private Transactions createSampleTransaction() {
		Transactions transaction = new Transactions();

		// Assuming necessary properties for the transaction
		transaction.setVnpayTransactionId("sampleTransactionId");
		transaction.setStatus("SampleStatus");
		transaction.setAmount(BigDecimal.valueOf(100)); // Set an appropriate amount
		transaction.setCreatedDate(LocalDateTime.now());
		transaction.setHotelBookingId(1L); // Assuming booking ID is 1
		transaction.setPaymentId(2L);
		transaction.setContent("Sample Content");
		transaction.setTransactionsTypeId(1L);

		return transaction;
	}

	@Test
	@DisplayName("testgetTotalPriceOfHotelBooking_ExistingIdUTCD01")
	public void testGetTotalPriceOfHotelBooking_ExistingBooking_Success() {
		// Arrange
		Long hotelBookingId = 1L;
		BigDecimal expectedTotalPrice = BigDecimal.valueOf(200); // Giả sử giá của đặt phòng là 200

		HotelBooking sampleBooking = new HotelBooking();
		sampleBooking.setHotelBookingId(hotelBookingId);
		sampleBooking.setTotalPrice(expectedTotalPrice);

		when(bookingRepository.findByHotelBookingId(hotelBookingId)).thenReturn(sampleBooking);

		// Act
		BigDecimal totalPrice = bookingService.getTotalPriceOfHotelBooking(hotelBookingId);

		// Assert
		assertEquals(expectedTotalPrice, totalPrice);
	}

	@Test
	@DisplayName("testgetTotalPriceOfHotelBooking_ExistingIdUTCD02")
	public void testGetTotalPriceOfHotelBooking_NonExistingBooking_ReturnsNull() {
		// Arrange
		Long nonExistingHotelBookingId = 999L;

		when(bookingRepository.findByHotelBookingId(nonExistingHotelBookingId)).thenReturn(null);

		// Act
		BigDecimal totalPrice = bookingService.getTotalPriceOfHotelBooking(nonExistingHotelBookingId);

		// Assert
		assertNull(totalPrice);
	}

	@Test
	void testSearchCheckInAndCheckOutAndStatus_OnlyCheckInAndCheckOut_ReturnsMatchingBookings() {
		// Arrange
		LocalDate checkIn = LocalDate.now();
		LocalDate checkOut = LocalDate.now().plusDays(3);
		LocalDateTime checkInTime = LocalDateTime.of(checkIn, LocalTime.MIN);
		LocalDateTime checkOutTime = LocalDateTime.of(checkOut, LocalTime.MAX);
		int page = 0;
		int size = 10;

		// Mock the repository to return a list of bookings
		when(bookingRepository.findAllCheckInAndCheckOutAndStatus(eq(checkInTime), eq(checkOutTime), isNull(), any()))
				.thenReturn(createSampleBookingPage());

		// Act
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(checkIn, checkOut, null, page,
				size);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.getContent().size());
	}

	@Test
	void testSearchCheckInAndCheckOutAndStatus_OnlyCheckIn_ReturnsMatchingBookings() {
		// Arrange
		LocalDate checkIn = LocalDate.now();
		Integer status = 1;
		int page = 0;
		int size = 10;

		// Mock the repository to return a list of bookings
		when(bookingRepository.findByCheckIn(eq(checkIn), eq(status), any())).thenReturn(createSampleBookingPage());

		// Act
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(checkIn, null, status, page,
				size);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.getContent().size());

	}

	@Test
	void testSearchCheckInAndCheckOutAndStatus_OnlyCheckOut_ReturnsMatchingBookings() {
		// Arrange
		LocalDate checkOut = LocalDate.now().plusDays(3);
		Integer status = 1;
		int page = 0;
		int size = 10;

		// Mock the repository to return a list of bookings
		when(bookingRepository.findByCheckOut(eq(checkOut), eq(status), any())).thenReturn(createSampleBookingPage());

		// Act
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(null, checkOut, status, page,
				size);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.getContent().size()); // Assuming two bookings are returned
		// Add more assertions based on your specific requirements
	}

	@Test
	void testSearchCheckInAndCheckOutAndStatus_OnlyStatus_ReturnsMatchingBookings() {
		// Arrange
		LocalDate checkIn = LocalDate.now();
		LocalDate checkOut = LocalDate.now().plusDays(3);
		int status = 1;
		int page = 0;
		int size = 10;

		// Mock the repository to return a list of bookings
		when(bookingRepository.findByStatusId(eq(status), any())).thenReturn(createSampleBookingPage());

		// Act
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(checkIn, checkOut, status, page,
				size);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.getContent().size()); // Assuming two bookings are returned
		// Add more assertions based on your specific requirements
	}

	@Test
	void testSearchCheckInAndCheckOutAndStatus_NoSearchCriteria_ReturnsAllBookings() {
		// Arrange
		int page = 0;
		int size = 10;

		// Mock the repository to return a list of bookings
		when(bookingRepository.findAllByValidBooking(any())).thenReturn(createSampleBookingPage());

		// Act
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(null, null, null, page, size);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.getContent().size()); // Assuming two bookings are returned
		// Add more assertions based on your specific requirements
	}

	private Page<HotelBooking> createSampleBookingPage() {
		List<HotelBooking> bookings = createSampleBookingList();
		return new PageImpl<>(bookings, PageRequest.of(0, 10), bookings.size());
	}

	private List<HotelBooking> createSampleBookingList() {
		HotelBooking booking1 = new HotelBooking();
		// Set booking properties as needed for the test

		HotelBooking booking2 = new HotelBooking();
		// Set booking properties as needed for the test

		return Arrays.asList(booking1, booking2);
	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOutAndStatus")
	public void testSearchCheckInAndCheckOutAndStatus() {
		// Mock data
		LocalDate checkIn = LocalDate.now();
		LocalDate checkOut = checkIn.plusDays(3);
		Integer status = 1;
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);
		hotelBooking.setStatusId(1L);

		User user = new User();
		user.setUserId(201L);

		HotelBookingStatus hotelBookingStatus = new HotelBookingStatus();
		hotelBookingStatus.setStatusId(Long.valueOf(status));
		LocalDateTime checkInTime = LocalDateTime.of(checkIn, LocalTime.MIN);
		LocalDateTime checkOutTime = LocalDateTime.of(checkOut, LocalTime.MAX);
		// Mock repository responses
		Mockito.when(bookingRepository.findAllCheckInAndCheckOutAndStatus(checkInTime, checkOutTime, status,
				PageRequest.of(page, size))).thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);
		Mockito.when(hotelBookingStatusRepository.findByStatusId(Long.valueOf(status))).thenReturn(hotelBookingStatus);

		// Call the method
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(checkIn, checkOut, status, page,
				size);

		// Verify the result
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());

	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOutAndStatus - CheckIn, CheckOut, Status=null")
	public void testSearchCheckInAndCheckOutAndStatusWithNullStatus() {
		// Mock data
		LocalDate checkIn = LocalDate.now();
		LocalDate checkOut = checkIn.plusDays(3);

		LocalDateTime checkInTime = LocalDateTime.of(checkIn, LocalTime.MIN);
		LocalDateTime checkOutTime = LocalDateTime.of(checkOut, LocalTime.MAX);
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses for the condition
		Mockito.when(bookingRepository.findAllCheckInAndCheckOutAndStatus(checkInTime, checkOutTime, null,
				PageRequest.of(page, size))).thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Call the method for the condition
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(checkIn, checkOut, null, page,
				size);

		// Verify the result for the condition
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
		assertNull(viewBookingDTO.getHotelBookingStatus()); // Assuming the status is not set in this case
	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOutAndStatus - CheckIn, CheckOut=null")
	public void testSearchCheckInAndCheckOutAndStatusWithNullCheckOut() {
		// Mock data
		LocalDate checkIn = LocalDate.now();
		Integer status = 1;
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses for the condition
		Mockito.when(bookingRepository.findByCheckIn(checkIn, status, PageRequest.of(page, size)))
				.thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Call the method for the condition
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(checkIn, null, status, page,
				size);

		// Verify the result for the condition
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
		assertNull(viewBookingDTO.getHotelBookingStatus()); // Assuming the status is not set in this case
	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOutAndStatus - CheckIn=null, CheckOut")
	public void testSearchCheckInAndCheckOutAndStatusWithNullCheckIn() {
		// Mock data
		LocalDate checkOut = LocalDate.now();
		Integer status = 1;
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses for the condition
		Mockito.when(bookingRepository.findByCheckOut(checkOut, status, PageRequest.of(page, size)))
				.thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Call the method for the condition
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(null, checkOut, status, page,
				size);

		// Verify the result for the condition
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
		assertNull(viewBookingDTO.getHotelBookingStatus()); // Assuming the status is not set in this case
	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOutAndStatus - CheckIn=null, CheckOut=null, Status")
	public void testSearchCheckInAndCheckOutAndStatusWithNullCheckInAndCheckOut() {
		// Mock data
		Integer status = 1;
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses for the condition
		Mockito.when(bookingRepository.findByStatusId(status, PageRequest.of(page, size)))
				.thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Call the method for the condition
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(null, null, status, page, size);

		// Verify the result for the condition
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
		assertNull(viewBookingDTO.getHotelBookingStatus()); // Assuming the status is not set in this case
	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOutAndStatus - Default Case")
	public void testSearchCheckInAndCheckOutAndStatusDefault() {
		// Mock data
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses for the default case
		Mockito.when(bookingRepository.findAllByValidBooking(PageRequest.of(page, size)))
				.thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Call the method for the default case
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOutAndStatus(null, null, null, page, size);

		// Verify the result for the default case
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
		assertNull(viewBookingDTO.getHotelBookingStatus()); // Assuming the status is not set in this case
	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOut - CheckIn, CheckOut, Status")
	public void testSearchCheckInAndCheckOutWithStatus() {
		// Mock data
		LocalDate checkIn = LocalDate.now();
		LocalDate checkOut = checkIn.plusDays(3);

		LocalDateTime checkInTime = LocalDateTime.of(checkIn, LocalTime.MIN);
		LocalDateTime checkOutTime = LocalDateTime.of(checkOut, LocalTime.MAX);
		Integer status = 1;
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);
		hotelBooking.setStatusId(Long.valueOf(status));

		User user = new User();
		user.setUserId(201L);

		HotelBookingStatus hotelBookingStatus = new HotelBookingStatus();
		hotelBookingStatus.setStatusId(Long.valueOf(status));

		// Mock repository responses for the condition
		Mockito.when(bookingRepository.findAllCheckInAndCheckOutAndStatus(checkInTime, checkOutTime, status,
				PageRequest.of(page, size))).thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);
		Mockito.when(hotelBookingStatusRepository.findByStatusId(Long.valueOf(status))).thenReturn(hotelBookingStatus);

		// Call the method for the condition
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOut(checkIn, checkOut, status, page, size);

		// Verify the result for the condition
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOut - CheckIn, CheckOut=null")
	public void testSearchCheckInAndCheckOutWithNullCheckOut() {
		// Mock data
		LocalDate checkIn = LocalDate.now();
		Integer status = 1;
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);
		hotelBooking.setStatusId(Long.valueOf(status));

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses for the condition
		Mockito.when(bookingRepository.findByCheckIn(checkIn, status, PageRequest.of(page, size)))
				.thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Call the method for the condition
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOut(checkIn, null, status, page, size);

		// Verify the result for the condition
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOut - CheckIn=null, CheckOut")
	public void testSearchCheckInAndCheckOutWithNullCheckIn() {
		// Mock data
		LocalDate checkOut = LocalDate.now();
		Integer status = 1;
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);
		hotelBooking.setStatusId(Long.valueOf(status));

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses for the condition
		Mockito.when(bookingRepository.findByCheckOut(checkOut, status, PageRequest.of(page, size)))
				.thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Call the method for the condition
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOut(null, checkOut, status, page, size);

		// Verify the result for the condition
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
	}

	@Test
	@DisplayName("testSearchCheckInAndCheckOut - Default Case")
	public void testSearchCheckInAndCheckOutDefault() {
		// Mock data
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);
		hotelBooking.setStatusId(1L);

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses for the default case
		Mockito.when(bookingRepository.findByStatusId(1, PageRequest.of(page, size)))
				.thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Call the method for the default case
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOut(null, null, null, page, size);

		// Verify the result for the default case
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
		assertEquals(1, viewBookingDTO.getHotelBookingStatus().getStatusId());
	}

	@Test
	void testGetTotalPriceOfHotelBooking_NonExistingId() {
		// Arrange
		Long nonExistingId = 2L;

		// Mock the behavior of the
		// bookingRepository.findByHotelBookingId(nonExistingId) method
		when(bookingRepository.findByHotelBookingId(nonExistingId)).thenReturn(null);

		// Act
		BigDecimal actualTotalPrice = bookingService.getTotalPriceOfHotelBooking(nonExistingId);

		// Assert
		assertNull(actualTotalPrice);
	}
	////////////

	@Test
	@DisplayName("testSearchCheckInAndCheckOut - CheckIn=null, CheckOut=null, Status")
	public void testSearchCheckInAndCheckOutWithNullCheckInAndCheckOut() {
		// Mock data
		Integer status = 1;
		int page = 0;
		int size = 10;

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);
		hotelBooking.setStatusId(Long.valueOf(status));

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses for the condition
		Mockito.when(bookingRepository.findByStatusId(status, PageRequest.of(page, size)))
				.thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Call the method for the condition
		Page<ViewBookingDTO> result = bookingService.searchCheckInAndCheckOut(null, null, status, page, size);

		// Verify the result for the condition
		assertEquals(1, result.getContent().size());
		ViewBookingDTO viewBookingDTO = result.getContent().get(0);
		assertEquals(201L, viewBookingDTO.getUser().getUserId());
		assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
	}

	@Test
    public void testCheckInWhenBookingIsValidThenReturnTrue() {
        when(bookingRepository.findByHotelBookingId(1L)).thenReturn(booking);
        when(bookingRoomDetailsRepository.getAllByHotelBookingId(1L)).thenReturn(Arrays.asList(bookingRoomDetails));
        when(roomRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(room));

        boolean result = bookingService.checkIn(1L);

        assertTrue(result);
        assertEquals(2L, booking.getStatusId());
        assertEquals(1L, room.getRoomStatusId());
    }

	@Test
	public void testCheckInWhenBookingIsInvalidThenReturnFalse() {
		booking.setCheckIn(Instant.now().plusSeconds(3600));
		when(bookingRepository.findByHotelBookingId(1L)).thenReturn(booking);

		boolean result = bookingService.checkIn(1L);

		assertFalse(result);
		assertEquals(1L, booking.getStatusId());
	}

	@Test
	public void testCheckInWhenRoomsAreNotReadyThenReturnFalse() {
		room.setRoomStatusId(3L);
		when(bookingRepository.findByHotelBookingId(1L)).thenReturn(booking);
		when(bookingRoomDetailsRepository.getAllByHotelBookingId(1L)).thenReturn(Arrays.asList(bookingRoomDetails));
		when(roomRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(room));

		boolean result = bookingService.checkIn(1L);

		assertFalse(result);
		assertEquals(1L, booking.getStatusId());
		assertEquals(3L, room.getRoomStatusId());
	}

	@Test
	void calculateServicePrice_SuccessfulCalculation() {
		SaveCheckoutHotelServiceDTO mockServiceDTO = new SaveCheckoutHotelServiceDTO();
		mockServiceDTO.setServiceId(1L);
		mockServiceDTO.setQuantity(2);

		RoomService mockRoomService = new RoomService();
		mockRoomService.setServiceId(1L);
		mockRoomService.setServicePrice(BigDecimal.TEN);

		when(roomServiceRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(mockRoomService));
		when(hotelBooking.getHotelBookingId()).thenReturn(1L);

		List<HotelBookingService> hotelBookingServiceList = new ArrayList<>();

		BigDecimal calculatedPrice = bookingService.calculateServicePrice(Collections.singletonList(mockServiceDTO),
				Map.of(1L, mockRoomService), BigDecimal.ZERO, hotelBooking, hotelBookingServiceList);

		assertEquals(BigDecimal.TEN.multiply(BigDecimal.valueOf(2)), calculatedPrice);

		// Additional assertion for hotelBookingServiceList
		assertEquals(1, hotelBookingServiceList.size());
		assertEquals(1L, hotelBookingServiceList.get(0).getServiceId());
		assertEquals(1L, hotelBookingServiceList.get(0).getHotelBookingId());
		// You may need to adjust other assertions based on your actual implementation
	}

	@Test
	public void testUpdateTotalPriceOfBooking() {
		// Mock HotelBooking
		HotelBooking hotelBooking = mock(HotelBooking.class);
		when(hotelBooking.getDepositPrice()).thenReturn(new BigDecimal("100")); // Set a deposit price

		// Call the method to test
		ReceptionistBookingServiceImpl.updateTotalPriceOfBooking(new BigDecimal("50"), new BigDecimal("200"),
				hotelBooking, BigDecimal.ZERO);

		// Verify that setTotalPrice was called with the expected value
		verify(hotelBooking).setTotalPrice(new BigDecimal("250"));
		verify(hotelBooking, never()).setRefundPrice(any(BigDecimal.class));
	}

	@Test
	public void testUpdateTotalPriceOfBookingWithZeroDeposit() {
		// Mock HotelBooking
		HotelBooking hotelBooking = mock(HotelBooking.class);
		when(hotelBooking.getDepositPrice()).thenReturn(BigDecimal.ZERO); // No deposit

		// Call the method to test
		ReceptionistBookingServiceImpl.updateTotalPriceOfBooking(new BigDecimal("50"), new BigDecimal("200"),
				hotelBooking, BigDecimal.ZERO);

		// Verify that setTotalPrice was called with the expected value
		verify(hotelBooking).setTotalPrice(new BigDecimal("250"));
	}

	@Test
	public void testUpdateTotalPriceOfBookingWithNullDeposit() {
		// Mock HotelBooking
		HotelBooking hotelBooking = mock(HotelBooking.class);
		when(hotelBooking.getDepositPrice()).thenReturn(null); // No deposit

		// Call the method to test
		ReceptionistBookingServiceImpl.updateTotalPriceOfBooking(new BigDecimal("50"), new BigDecimal("200"),
				hotelBooking, BigDecimal.ZERO);

		// Verify that setTotalPrice was called with the expected value
		verify(hotelBooking).setTotalPrice(new BigDecimal("250"));
	}

	@Test
    public void testCheckInWhenHotelBookingIdIsValidThenReturnTrue() {
        when(bookingRepository.findByHotelBookingId(hotelBooking.getHotelBookingId())).thenReturn(hotelBooking);
        when(bookingRoomDetailsRepository.getAllByHotelBookingId(hotelBooking.getHotelBookingId())).thenReturn(Arrays.asList(bookingRoomDetails));
        when(roomRepository.findAllById(Arrays.asList(bookingRoomDetails.getRoomId()))).thenReturn(Arrays.asList(room));

        SaveCheckinDTO checkinDTO = new SaveCheckinDTO();
        checkinDTO.setHotelBookingId(hotelBooking.getHotelBookingId());
        SaveCheckinDetailDTO checkinDetailDTO = new SaveCheckinDetailDTO();
        checkinDetailDTO.setBookingRoomId(bookingRoomDetails.getBookingRoomId());
        checkinDetailDTO.setRoomID(room.getRoomId());
        checkinDTO.setSaveCheckinDetailDTOS(Arrays.asList(checkinDetailDTO));

        assertTrue(bookingService.checkIn(checkinDTO));

        verify(bookingRepository, times(1)).findByHotelBookingId(hotelBooking.getHotelBookingId());
        verify(bookingRoomDetailsRepository, times(1)).getAllByHotelBookingId(hotelBooking.getHotelBookingId());
        verify(roomRepository, times(1)).findAllById(Arrays.asList(bookingRoomDetails.getRoomId()));
    }

	@Test
    public void testCheckInWhenHotelBookingIdIsInvalidThenReturnFalse() {
        when(bookingRepository.findByHotelBookingId(hotelBooking.getHotelBookingId())).thenReturn(null);

        SaveCheckinDTO checkinDTO = new SaveCheckinDTO();
        checkinDTO.setHotelBookingId(hotelBooking.getHotelBookingId());

        assertFalse(bookingService.checkIn(checkinDTO));

        verify(bookingRepository, times(1)).findByHotelBookingId(hotelBooking.getHotelBookingId());
    }

	@Test
	public void testCheckInWhenStatusIdIsNotOneThenReturnFalse() {
		hotelBooking.setStatusId(2L);
		when(bookingRepository.findByHotelBookingId(hotelBooking.getHotelBookingId())).thenReturn(hotelBooking);

		SaveCheckinDTO checkinDTO = new SaveCheckinDTO();
		checkinDTO.setHotelBookingId(hotelBooking.getHotelBookingId());

		assertFalse(bookingService.checkIn(checkinDTO));

		verify(bookingRepository, times(1)).findByHotelBookingId(hotelBooking.getHotelBookingId());
	}

	@Test
	public void testCheckInWhenCheckInDateIsNotValidThenReturnFalse() {
		hotelBooking.setCheckIn(Instant.now().plusSeconds(1));
		when(bookingRepository.findByHotelBookingId(hotelBooking.getHotelBookingId())).thenReturn(hotelBooking);

		SaveCheckinDTO checkinDTO = new SaveCheckinDTO();
		checkinDTO.setHotelBookingId(hotelBooking.getHotelBookingId());

		assertFalse(bookingService.checkIn(checkinDTO));

		verify(bookingRepository, times(1)).findByHotelBookingId(hotelBooking.getHotelBookingId());
	}

	@Test
	public void testCheckInWhenThereIsARoomNotReadyThenReturnFalse() {
		room.setRoomStatusId(1L);
		when(bookingRepository.findByHotelBookingId(hotelBooking.getHotelBookingId())).thenReturn(hotelBooking);
		when(bookingRoomDetailsRepository.getAllByHotelBookingId(hotelBooking.getHotelBookingId()))
				.thenReturn(Arrays.asList(bookingRoomDetails));
		when(roomRepository.findAllById(Arrays.asList(bookingRoomDetails.getRoomId()))).thenReturn(Arrays.asList(room));

		SaveCheckinDTO checkinDTO = new SaveCheckinDTO();
		checkinDTO.setHotelBookingId(hotelBooking.getHotelBookingId());
		SaveCheckinDetailDTO checkinDetailDTO = new SaveCheckinDetailDTO();
		checkinDetailDTO.setBookingRoomId(bookingRoomDetails.getBookingRoomId());
		checkinDetailDTO.setRoomID(room.getRoomId());
		checkinDTO.setSaveCheckinDetailDTOS(Arrays.asList(checkinDetailDTO));

		assertFalse(bookingService.checkIn(checkinDTO));

		verify(bookingRepository, times(1)).findByHotelBookingId(hotelBooking.getHotelBookingId());
		verify(bookingRoomDetailsRepository, times(1)).getAllByHotelBookingId(hotelBooking.getHotelBookingId());
		verify(roomRepository, times(1)).findAllById(Arrays.asList(bookingRoomDetails.getRoomId()));
	}

	@Test
	void calculateTotalPrice_SuccessfulCalculation() {
		BookingRoomDetails bookingRoomDetails1 = new BookingRoomDetails();
		bookingRoomDetails1.setRoomCategoryId(1L);

		BookingRoomDetails bookingRoomDetails2 = new BookingRoomDetails();
		bookingRoomDetails2.setRoomCategoryId(2L);

		List<BookingRoomDetails> bookingRoomDetailsList = Arrays.asList(bookingRoomDetails1, bookingRoomDetails2);

		CategoryRoomPrice categoryRoomPrice1 = new CategoryRoomPrice();
		categoryRoomPrice1.setRoomCategoryId(1L);
		categoryRoomPrice1.setPrice(BigDecimal.valueOf(100));

		CategoryRoomPrice categoryRoomPrice2 = new CategoryRoomPrice();
		categoryRoomPrice2.setRoomCategoryId(2L);
		categoryRoomPrice2.setPrice(BigDecimal.valueOf(150));

		when(categoryRoomPriceRepository.findAllById(Arrays.asList(1L, 2L)))
				.thenReturn(Arrays.asList(categoryRoomPrice1, categoryRoomPrice2));

		Instant checkin = Instant.now();
		Instant checkout = Instant.now().plusSeconds(3600);

		BigDecimal totalPrice = bookingService.calculateTotalPrice(bookingRoomDetailsList, checkin, checkout);

		// Verify that findAllById was called on categoryRoomPriceRepository with the
		// correct room category IDs
		verify(categoryRoomPriceRepository, times(1)).findAllById(Arrays.asList(1L, 2L));

//      // Verify that calculatePriceBetweenDate was called for each BookingRoomDetails
//      verify(BookingUtil, times(1)).calculatePriceBetweenDate(checkin, checkout, 1L, false);
//      verify(BookingUtil, times(1)).calculatePriceBetweenDate(checkin, checkout, 2L, false);

		// Verify the total price calculation
		BigDecimal expectedTotalPrice = BigDecimal.valueOf(100).add(BigDecimal.valueOf(150));
		assertEquals(expectedTotalPrice, totalPrice);
	}

}