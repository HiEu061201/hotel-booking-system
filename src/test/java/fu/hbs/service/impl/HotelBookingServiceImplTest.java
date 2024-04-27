package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.thymeleaf.TemplateEngine;

import fu.hbs.dto.HotelBookingDTO.BookingDetailsDTO;
import fu.hbs.dto.HotelBookingDTO.CreateBookingDTO;
import fu.hbs.dto.HotelBookingDTO.GuestBookingDTO;
import fu.hbs.dto.HotelBookingDTO.ViewHotelBookingDTO;
import fu.hbs.dto.ReceptionistDTO.ViewBookingDTO;
import fu.hbs.dto.User.CancellationFormDTO;
import fu.hbs.dto.User.CustomUserDetails;
import fu.hbs.dto.User.HotelBookingAvailable;
import fu.hbs.entities.BookingRoomDetails;
import fu.hbs.entities.CategoryRoomFurniture;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.CustomerCancellation;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.HotelBookingStatus;
import fu.hbs.entities.RefundAccount;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import fu.hbs.entities.RoomFurniture;
import fu.hbs.entities.RoomImage;
import fu.hbs.entities.Transactions;
import fu.hbs.entities.User;
import fu.hbs.entities.UserRole;
import fu.hbs.exceptionHandler.CancellationExistException;
import fu.hbs.exceptionHandler.MailExceptionHandler;
import fu.hbs.exceptionHandler.ResetExceptionHandler;
import fu.hbs.exceptionHandler.RoomCategoryNamesNullException;
import fu.hbs.exceptionHandler.SearchRoomCustomerExceptionHandler;
import fu.hbs.repository.BookingRoomDetailsRepository;
import fu.hbs.repository.CategoryRoomFurnitureRepository;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.CustomerCancellationReasonRepository;
import fu.hbs.repository.CustomerCancellationRepository;
import fu.hbs.repository.FeedbackRepository;
import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.HotelBookingStatusRepository;
import fu.hbs.repository.RefundAccountRepository;
import fu.hbs.repository.RoomCategoriesRepository;
import fu.hbs.repository.RoomFurnitureRepository;
import fu.hbs.repository.RoomImageRepository;
import fu.hbs.repository.RoomRepository;
import fu.hbs.repository.RoomStatusRepository;
import fu.hbs.repository.TransactionsRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.repository.UserRoleRepository;
import fu.hbs.service.interfaces.BookingRoomDetailsService;
import fu.hbs.utils.BookingDayUtils;
import fu.hbs.utils.EmailUtil;
import fu.hbs.utils.StringDealer;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HotelBookingServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomCategoriesRepository roomCategoriesRepository;

    @Mock
    private CategoryRoomFurnitureRepository categoryRoomFurnitureRepository;

    @Mock
    private RoomFurnitureRepository roomFurnitureRepository;
    @Mock
    private RoomImageRepository imageRepository;

    @Mock
    private CategoryRoomPriceRepository categoryRoomPriceRepository;
    @Mock
    private HotelBookingRepository hotelBookingRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoomStatusRepository roomStatusRepository;
    @Mock
    private CustomerCancellationReasonRepository customerCancellationReasonRepository;
    @Mock
    private CustomerCancellationRepository customerCancellationRepository;
    @Mock
    private RefundAccountRepository refundAccountRepository;
    @Mock
    private BookingRoomDetailsRepository bookingRoomDetailsRepository;
    @Mock
    private BookingRoomDetailsService bookingRoomDetailsService;
    @Mock
    private HotelBookingStatusRepository hotelBookingStatusRepository;
    @Mock
    private TransactionsRepository transactionsRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private StringDealer stringDealer;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private EmailUtil emailUtil;
    @Mock
    private BookingDayUtils bookingDayUtils;

    @InjectMocks
    private HotelBookingServiceImpl hotelBookingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveHotelBooking() {
        // Arrange
        HotelBooking hotelBooking = new HotelBooking(); // You need to create a HotelBooking instance

        // Set up mock behavior
        when(hotelBookingRepository.save(any(HotelBooking.class))).thenReturn(hotelBooking);

        // Act
        HotelBooking savedBooking = hotelBookingService.saveHotelBooking(hotelBooking);

        // Assert
        assertNotNull(savedBooking);
    }

    @Test
    @DisplayName("FindBookingsByDatesUTCID01")
    void testFindBookingsByDates() throws RoomCategoryNamesNullException, IllegalArgumentException {
        LocalDate checkIn = LocalDate.now().minusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5);
        int numberPerson = 3;

        List<Room> mockRooms = new ArrayList<>();
        Room room1 = new Room();
        room1.setRoomId(1L);
        room1.setRoomCategoryId(1L);
        room1.setRoomStatusId(2L);
        mockRooms.add(room1);

        when(roomRepository.getAllRoom(any(), any(), eq(numberPerson))).thenReturn(mockRooms);

        RoomCategories roomCategory = new RoomCategories();
        roomCategory.setRoomCategoryId(1L);
        when(roomCategoriesRepository.findDistinctByRoomCategoryId(1L)).thenReturn(roomCategory);

        List<CategoryRoomFurniture> mockCategoryRoomFurnitures = new ArrayList<>();

        CategoryRoomFurniture categoryRoomFurniture = new CategoryRoomFurniture();
        categoryRoomFurniture.setFurnitureId(1L);
        mockCategoryRoomFurnitures.add(categoryRoomFurniture);
        when(categoryRoomFurnitureRepository.findByRoomCategoryId(1L)).thenReturn(mockCategoryRoomFurnitures);

        RoomFurniture roomFurniture = new RoomFurniture();
        roomFurniture.setFurnitureId(1L);
        when(roomFurnitureRepository.findByFurnitureId(1L)).thenReturn(roomFurniture);

        RoomImage roomImage = new RoomImage();
        roomImage.setRoomImageId(1L);
        when(imageRepository.findByRoomImageId(1L)).thenReturn(roomImage);

        CategoryRoomPrice categoryRoomPrice = new CategoryRoomPrice();
        categoryRoomPrice.setRoomCategoryId(1L);
        when(categoryRoomPriceRepository.getCategoryId(1L)).thenReturn(categoryRoomPrice);

        when(bookingDayUtils.getDayType(any())).thenReturn(1);

        HotelBookingAvailable result = hotelBookingService.findBookingsByDates(checkIn, checkOut, numberPerson);

        assertEquals(1, result.getRoomCategories().size());
        assertEquals(1, result.getCategoryRoomFurnitures().size());
        assertEquals(1, result.getRoomFurnitures().size());
        assertEquals(1, result.getTotalRoom().size());
        assertEquals(1, result.getCategoryRoomPrices().size());
    }

    @Test
    @DisplayName("FindBookingsByDatesUTCID02")
    void FindBookingsByDatesUTCID02() {
        LocalDate checkIn = null;
        LocalDate checkOut = LocalDate.now();
        int numberPerson = 3;
        SearchRoomCustomerExceptionHandler exception = assertThrows(SearchRoomCustomerExceptionHandler.class, () -> {
            hotelBookingService.findBookingsByDates(checkIn, checkOut, numberPerson);
        });
        assertEquals("CheckIn và CheckOut không thể null", exception.getMessage());

    }

    @Test
    @DisplayName("FindBookingsByDatesUTCID03")
    void FindBookingsByDatesUTCID03() {
        LocalDate checkIn = LocalDate.now().minusDays(2);
        LocalDate checkOut = null;
        int numberPerson = 3;

        SearchRoomCustomerExceptionHandler exception = assertThrows(SearchRoomCustomerExceptionHandler.class, () -> {
            hotelBookingService.findBookingsByDates(checkIn, checkOut, numberPerson);
        });
        assertEquals("CheckIn và CheckOut không thể null", exception.getMessage());

    }

    @Test
    @DisplayName("FindBookingsByDatesUTCID04")
    void FindBookingsByDatesUTCID04() {
        LocalDate checkIn = LocalDate.now().minusDays(2);
        LocalDate checkOut = null;
        int numberPerson = 3;
        SearchRoomCustomerExceptionHandler exception = assertThrows(SearchRoomCustomerExceptionHandler.class, () -> {
            hotelBookingService.findBookingsByDates(checkIn, checkOut, numberPerson);
        });
        assertEquals("CheckIn và CheckOut không thể null", exception.getMessage());

    }

    @Test
    @DisplayName("FindBookingsByDatesUTCID05")
    void FindBookingsByDatesUTCID05() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now();
        int numberPerson = 3;

        // Act and Assert
        SearchRoomCustomerExceptionHandler exception = assertThrows(SearchRoomCustomerExceptionHandler.class, () -> {
            hotelBookingService.findBookingsByDates(checkIn, checkOut, numberPerson);
        });

        assertEquals("CheckIn không trùng CheckOut", exception.getMessage());

    }

    @Test
    @DisplayName("FindBookingsByDatesUTCID06")
    void FindBookingsByDatesUTCID06() {
        // Arrange

        LocalDate checkOut = LocalDate.now().minusDays(2);
        LocalDate checkIn = LocalDate.now().plusDays(5);

        int numberPerson = 3;

        // Act and Assert
        SearchRoomCustomerExceptionHandler exception = assertThrows(SearchRoomCustomerExceptionHandler.class, () -> {
            // Call the method under test
            hotelBookingService.findBookingsByDates(checkIn, checkOut, numberPerson);
        });

        // Check that the exception has the correct message
        assertEquals("CheckIn phải trước CheckOut", exception.getMessage());
    }

    @Test
    void testFindBookingsByDatesNoAvailableRooms() throws RoomCategoryNamesNullException {
        // Arrange
        LocalDate checkOut = LocalDate.now().minusDays(2);
        LocalDate checkIn = LocalDate.now().plusDays(5);
        int numberPerson = 3;

        // Mock behavior for roomRepository
        when(roomRepository.getAllRoom(checkIn, checkOut, numberPerson)).thenReturn(Collections.emptyList());

        // Act and Assert
        assertThrows(RoomCategoryNamesNullException.class,
                () -> hotelBookingService.findBookingsByDates(checkIn, checkOut, numberPerson),
                "Không có phòng nào trong khoảng thời gian này");
    }

    @Test
    @DisplayName("saveRoomAfterBookingUTCD01")
    void saveRoomAfterBooking() throws ResetExceptionHandler {
        // Arrange
        HttpSession session = new MockHttpSession();

        // Tạo đối tượng Authentication với thông tin giả định
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        UserDetails userDetails = new CustomUserDetails("HieuLBM@gmail.com", "12345",
                Collections.singletonList(authority));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "12345",
                Collections.singletonList(authority));

        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now();

        CreateBookingDTO createBookingDTO = new CreateBookingDTO();
        Map<Long, Integer> roomCategoryMap = new HashMap<>();
        roomCategoryMap.put(1L, 789);
        roomCategoryMap.put(2L, 101);
        createBookingDTO.setRoomCategoryMap(roomCategoryMap);
        BigDecimal result = BigDecimal.valueOf(20000);
        createBookingDTO.setDepositPrice(result);
        createBookingDTO.setCheckIn(checkInDate);
        createBookingDTO.setCheckOut(checkOutDate);
        BigDecimal totalPrice = BigDecimal.valueOf(20000);
        createBookingDTO.setTotalPrice(totalPrice);
        RoomCategories mockRoomCategory1 = new RoomCategories();
        mockRoomCategory1.setRoomCategoryId(1L);

        RoomCategories mockRoomCategory2 = new RoomCategories();
        mockRoomCategory2.setRoomCategoryId(2L);

        List<RoomCategories> roomCategoriesList = new ArrayList<>();
        roomCategoriesList.add(mockRoomCategory1);
        roomCategoriesList.add(mockRoomCategory2);
        createBookingDTO.setRoomCategoriesList(roomCategoriesList);

        session.setAttribute("createBookingDTO", createBookingDTO);

        // Mocking userRepository
        User user = new User();
        user.setUserId(1L);
        user.setEmail("HieuLBM@gmail.com");
        user.setPassword("12345");
        user.setStatus(true);
        when(userRepository.getUserByEmail(userDetails.getUsername())).thenReturn(user);

        // Mocking hotelBookingRepository
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotelBookingId(1L);
        hotelBooking.setDepositPrice(BigDecimal.valueOf(0));
        when(hotelBookingRepository.save(any(HotelBooking.class))).thenReturn(hotelBooking);

        // Mocking roomRepository
        List<Room> rooms = new ArrayList<>();
        Room room = new Room();
        room.setRoomId(1L);
        Room room1 = new Room();
        room1.setRoomId(2L);
        Room room2 = new Room();
        room2.setRoomId(3L);
        rooms.add(room);
        rooms.add(room1);
        rooms.add(room2);
        when(roomRepository.findAvailableRoomsByCategoryId(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(rooms);
        Instant checkInInstant = Instant.now();
        Instant checkOutInstant = Instant.now().plus(1, ChronoUnit.DAYS);
        hotelBooking.setCheckIn(checkInInstant);
        hotelBooking.setCheckOut(checkOutInstant);
        // Act
        Long bookingId = hotelBookingService.saveRoomAfterBooking(authentication, session, totalPrice);

        // Assert
        assertNotNull(bookingId);
        assertTrue(bookingId > 0);

        // Verify that the mocked methods were called with the expected arguments
        verify(hotelBookingRepository, times(1)).save(any(HotelBooking.class));
        verify(roomRepository, atLeastOnce()).findAvailableRoomsByCategoryId(anyLong(), any(LocalDate.class),
                any(LocalDate.class));
        verify(bookingRoomDetailsRepository, atLeastOnce()).save(any(BookingRoomDetails.class));
        verify(userRepository, times(1)).getUserByEmail("HieuLBM@gmail.com");

    }

    @Test
    @DisplayName("saveRoomAfterBookingUTCD02")
    void saveRoomAfterBookingForGuest() throws ResetExceptionHandler {
        HttpSession session = new MockHttpSession();
        // Ensure the session contains guestBookingDTO
        GuestBookingDTO guestBookingDTO = new GuestBookingDTO();
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now();

        CreateBookingDTO createBookingDTO = new CreateBookingDTO();
        Map<Long, Integer> roomCategoryMap = new HashMap<>();
        roomCategoryMap.put(1L, 789);
        roomCategoryMap.put(2L, 101);
        createBookingDTO.setRoomCategoryMap(roomCategoryMap);
        BigDecimal result = BigDecimal.valueOf(20000);
        createBookingDTO.setDepositPrice(result);
        createBookingDTO.setCheckIn(checkInDate);
        createBookingDTO.setCheckOut(checkOutDate);
        BigDecimal totalPrice = BigDecimal.valueOf(20000);
        createBookingDTO.setTotalPrice(totalPrice);
        RoomCategories mockRoomCategory1 = new RoomCategories();
        mockRoomCategory1.setRoomCategoryId(1L);

        RoomCategories mockRoomCategory2 = new RoomCategories();
        mockRoomCategory2.setRoomCategoryId(2L);

        List<RoomCategories> roomCategoriesList = new ArrayList<>();
        roomCategoriesList.add(mockRoomCategory1);
        roomCategoriesList.add(mockRoomCategory2);
        createBookingDTO.setRoomCategoriesList(roomCategoriesList);

        session.setAttribute("createBookingDTO", createBookingDTO);

        guestBookingDTO.setName("GuestName");
        guestBookingDTO.setAddress("GuestAddress");
        guestBookingDTO.setPhone("GuestPhone");
        guestBookingDTO.setEmail("guest@example.com");
        guestBookingDTO.setPaymentAmount(totalPrice);
        session.setAttribute("guestBookingDTO", guestBookingDTO);

        // Mocking hotelBookingRepository
        HotelBooking hotelBooking1 = new HotelBooking();
        hotelBooking1.setHotelBookingId(1L);
        when(hotelBookingRepository.save(any(HotelBooking.class))).thenReturn(hotelBooking1);

        // Mocking roomRepository
        List<Room> rooms = new ArrayList<>();
        Room room = new Room();
        room.setRoomId(1L);
        Room room1 = new Room();
        room1.setRoomId(2L);
        Room room2 = new Room();
        room2.setRoomId(3L);
        rooms.add(room);
        rooms.add(room1);
        rooms.add(room2);
        when(roomRepository.findAvailableRoomsByCategoryId(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(rooms);
        Instant checkInInstant1 = Instant.now();
        Instant checkOutInstant1 = Instant.now().plus(1, ChronoUnit.DAYS);
        hotelBooking1.setCheckIn(checkInInstant1);
        hotelBooking1.setCheckOut(checkOutInstant1);
        hotelBooking1.setDepositPrice(BigDecimal.valueOf(0));

        // Mocking bookingRoomDetailsService
        BookingDetailsDTO bookingDetailsDTO = new BookingDetailsDTO();
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotelBookingId(1L);
        bookingDetailsDTO.setHotelBooking(hotelBooking);
        when(bookingRoomDetailsService.getBookingDetailsByHotelBooking(anyLong())).thenReturn(bookingDetailsDTO);

        // Act
        Long bookingId = hotelBookingService.saveRoomAfterBooking(null, session, totalPrice);

        // Assert
        assertNotNull(bookingId);
        assertTrue(bookingId > 0);

    }

    @Test
    @DisplayName("createBookingUTCID01")
    void testCreateBooking() {
        // Arrange
        List<Long> roomCategoryNames = Arrays.asList(1L, 2L);
        List<Integer> selectedRoomCategories = Arrays.asList(2, 1);
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2);

        // Mock the behavior of repositories
        RoomCategories mockRoomCategory1 = new RoomCategories();
        mockRoomCategory1.setRoomCategoryId(1L);

        RoomCategories mockRoomCategory2 = new RoomCategories();
        mockRoomCategory2.setRoomCategoryId(2L);

        CategoryRoomPrice categoryRoomPrice = new CategoryRoomPrice();
        categoryRoomPrice.setRoomCategoryId(mockRoomCategory1.getRoomCategoryId());
        categoryRoomPrice.setRoomPriceId(1L);

        CategoryRoomPrice categoryRoomPrice1 = new CategoryRoomPrice();
        categoryRoomPrice1.setRoomCategoryId(mockRoomCategory2.getRoomCategoryId());
        categoryRoomPrice1.setRoomPriceId(2L);

        when(roomCategoriesRepository.findByRoomCategoryId(1L)).thenReturn(mockRoomCategory1);
        when(roomCategoriesRepository.findByRoomCategoryId(2L)).thenReturn(mockRoomCategory2);

        when(categoryRoomPriceRepository.getCategoryId(1L)).thenReturn(categoryRoomPrice);
        when(categoryRoomPriceRepository.getCategoryId(2L)).thenReturn(categoryRoomPrice1);

        // Act
        CreateBookingDTO createBookingDTO = hotelBookingService.createBooking(roomCategoryNames, selectedRoomCategories,
                checkIn, checkOut);

        // Assert
        assertNotNull(createBookingDTO, "tạo booking thành công");

    }

    @Test
    @DisplayName("createBookingUTCID02")
    void testCreateBookingWithNullRoomCategoryNames() {
        // Arrange
        List<Long> roomCategoryNames = null;
        List<Integer> selectedRoomCategories = Arrays.asList(2, 1);
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> hotelBookingService.createBooking(roomCategoryNames, selectedRoomCategories, checkIn, checkOut),
                "Bạn chưa đặt phòng nào");
    }

    @Test
    @DisplayName("createBookingUTCID03")
    void testCreateBookingWithEmptyRoomCategoryNames() {
        // Arrange
        List<Long> roomCategoryNames = Collections.emptyList();
        List<Integer> selectedRoomCategories = Arrays.asList(2, 1);
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> hotelBookingService.createBooking(roomCategoryNames, selectedRoomCategories, checkIn, checkOut),
                "Bạn chưa đặt phòng nào");
    }

    @Test
    @DisplayName("createBookingUTCID04")
    void testCreateBookingWithNullSelectedRoomCategories() {
        // Arrange
        List<Long> roomCategoryNames = Arrays.asList(1L, 2L);
        List<Integer> selectedRoomCategories = null;
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> hotelBookingService.createBooking(roomCategoryNames, selectedRoomCategories, checkIn, checkOut),
                "Số lượng phòng không hợp lệ");
    }

    @Test
    @DisplayName("createBookingUTCID05")
    void testCreateBookingWithEmptySelectedRoomCategories() {
        // Arrange
        List<Long> roomCategoryNames = Arrays.asList(1L, 2L);
        List<Integer> selectedRoomCategories = Collections.emptyList();
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> hotelBookingService.createBooking(roomCategoryNames, selectedRoomCategories, checkIn, checkOut),
                "Số lượng phòng không hợp lệ");
    }

    @Test
    @DisplayName("createBookingUTCID06")
    void testCreateBookingWithMismatchedSizes() {
        // Arrange
        List<Long> roomCategoryNames = Arrays.asList(1L, 2L, 3L);
        List<Integer> selectedRoomCategories = Arrays.asList(2, 1);
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> hotelBookingService.createBooking(roomCategoryNames, selectedRoomCategories, checkIn, checkOut),
                "Số lượng phòng không khớp với loại phòng");
    }

    @Test
    @DisplayName("createBookingUTCID07")
    void testCreateBookingWithEmptyRoomCategoryMap() {
        // Arrange
        List<Long> roomCategoryNames = Arrays.asList(1L, 2L);
        List<Integer> selectedRoomCategories = Arrays.asList(0, 0);
        // roomCategoryMap
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(2);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> hotelBookingService.createBooking(roomCategoryNames, selectedRoomCategories, checkIn, checkOut),
                "Bạn chưa đặt phòng nào");
    }

    @Test
    public void testChangeStatusId() {
        // Arrange
        Long hotelBookingId = 1L;
        HotelBooking hotelBooking = new HotelBooking();
        when(hotelBookingRepository.findByHotelBookingId(hotelBookingId)).thenReturn(hotelBooking);

        // Act
        hotelBookingService.changeStatusId(hotelBookingId);

        // Assert
        assertEquals(4L, hotelBooking.getStatusId());

        // Verify that the save method was called with the correct argument
        verify(hotelBookingRepository, times(1)).save(eq(hotelBooking));
    }

    @Test
    @DisplayName("findByIdUTCID01")
    public void testFindById() {
        // Arrange
        Long id = 1L;
        HotelBooking expectedHotelBooking = new HotelBooking();
        when(hotelBookingRepository.findById(id)).thenReturn(Optional.of(expectedHotelBooking));

        // Act
        HotelBooking result = hotelBookingService.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(expectedHotelBooking, result);
    }

    @Test
    @DisplayName("findByIdUTCID02")
    public void testFindByIdWhenNotFound() {
        // Arrange
        Long id = 1L;
        when(hotelBookingRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        HotelBooking result = hotelBookingService.findById(id);

        // Assert
        assertNull(result, "Không tìm thấy id");
    }

    @Test
    public void testMappingLogicInSearchCheckInCheckOutAndStatus() {
        // Mock data
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotelBookingId(101L);
        hotelBooking.setUserId(201L);
        hotelBooking.setStatusId(1L);

        User user = new User();
        user.setUserId(201L);

        HotelBookingStatus hotelBookingStatus = new HotelBookingStatus();
        hotelBookingStatus.setStatusId(1L);

        // Mock repository responses
        Mockito.lenient().when(hotelBookingRepository.findById(101L)).thenReturn(Optional.of(hotelBooking));
        Mockito.lenient().when(userRepository.findByUserId(201L)).thenReturn(user);
        Mockito.lenient().when(hotelBookingStatusRepository.findByStatusId(1L)).thenReturn(hotelBookingStatus);

        // Mock Page object
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);

        LocalDateTime checkInTime = LocalDateTime.of(checkIn, LocalTime.MIN);
        LocalDateTime checkOutTime = LocalDateTime.of(checkOut, LocalTime.MAX);
        Integer status = 1;
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<HotelBooking> hotelBookingPage = new PageImpl<>(Collections.singletonList(hotelBooking));
        Mockito.when(hotelBookingRepository.findAllCheckInAndCheckOutAndStatus(eq(checkInTime), eq(checkOutTime),
                eq(status), eq(pageRequest))).thenReturn(hotelBookingPage);

        // Call the method
        Page<ViewBookingDTO> result = hotelBookingService.searchCheckInAndCheckOutAndStatus(checkIn, checkOut, status,
                page, size);

        // Verify the result
        assertEquals(1, result.getContent().size());
        ViewBookingDTO viewBookingDTO = result.getContent().get(0);
        assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
        assertEquals(201L, viewBookingDTO.getUser().getUserId());
        assertEquals(1L, viewBookingDTO.getHotelBookingStatus().getStatusId());

        // Ensure that the repository methods are called with the correct parameters
        Mockito.verify(hotelBookingRepository, Mockito.times(1)).findAllCheckInAndCheckOutAndStatus(eq(checkInTime),
                eq(checkOutTime), eq(status), eq(pageRequest));
        Mockito.verify(userRepository, Mockito.times(1)).findByUserId(201L);
        Mockito.verify(hotelBookingStatusRepository, Mockito.times(1)).findByStatusId(1L);
    }

    @Test
    public void testMappingLogicInSearchCheckInAndStatus() {
        // Mock data
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotelBookingId(101L);
        hotelBooking.setUserId(201L);
        hotelBooking.setStatusId(1L);

        User user = new User();
        user.setUserId(201L);

        HotelBookingStatus hotelBookingStatus = new HotelBookingStatus();
        hotelBookingStatus.setStatusId(1L);

        // Mock Page object
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);
        Integer status = 1;
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        // Mock repository responses
        Mockito.when(hotelBookingRepository.findByCheckIn(eq(checkIn), eq(status), eq(pageRequest)))
                .thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
        Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);
        Mockito.when(hotelBookingStatusRepository.findByStatusId(1L)).thenReturn(hotelBookingStatus);

        // Call the method
        Page<ViewBookingDTO> result = hotelBookingService.searchCheckInAndCheckOutAndStatus(checkIn, null, status, page,
                size);

        // Verify the result
        assertEquals(1, result.getContent().size());
        ViewBookingDTO viewBookingDTO = result.getContent().get(0);
        assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
        assertEquals(201L, viewBookingDTO.getUser().getUserId());
        assertEquals(1L, viewBookingDTO.getHotelBookingStatus().getStatusId());

        // Ensure that the repository methods are called with the correct parameters
        Mockito.verify(hotelBookingRepository, Mockito.times(1)).findByCheckIn(eq(checkIn), eq(status),
                eq(pageRequest));
        Mockito.verify(userRepository, Mockito.times(1)).findByUserId(201L);
        Mockito.verify(hotelBookingStatusRepository, Mockito.times(1)).findByStatusId(1L);
    }

    @Test
    public void testMappingLogicInSearchCheckOutAndStatus() {
        // Mock data
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotelBookingId(101L);
        hotelBooking.setUserId(201L);
        hotelBooking.setStatusId(1L);

        User user = new User();
        user.setUserId(201L);

        HotelBookingStatus hotelBookingStatus = new HotelBookingStatus();
        hotelBookingStatus.setStatusId(1L);

        // Mock Page object
        LocalDate checkOut = LocalDate.now();

        Integer status = 1;
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        // Mock repository responses
        Mockito.when(hotelBookingRepository.findByCheckOut(eq(checkOut), eq(status), eq(pageRequest)))
                .thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
        Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);
        Mockito.when(hotelBookingStatusRepository.findByStatusId(1L)).thenReturn(hotelBookingStatus);

        // Call the method
        Page<ViewBookingDTO> result = hotelBookingService.searchCheckInAndCheckOutAndStatus(null, checkOut, status,
                page, size);

        // Verify the result
        assertEquals(1, result.getContent().size());
        ViewBookingDTO viewBookingDTO = result.getContent().get(0);
        assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
        assertEquals(201L, viewBookingDTO.getUser().getUserId());
        assertEquals(1L, viewBookingDTO.getHotelBookingStatus().getStatusId());

        // Ensure that the repository methods are called with the correct parameters
        Mockito.verify(hotelBookingRepository, Mockito.times(1)).findByCheckOut(eq(checkOut), eq(status),
                eq(pageRequest));
        Mockito.verify(userRepository, Mockito.times(1)).findByUserId(201L);
        Mockito.verify(hotelBookingStatusRepository, Mockito.times(1)).findByStatusId(1L);
    }

    @Test
    public void testMappingLogicInSearchByStatus() {
        // Mock data
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotelBookingId(101L);
        hotelBooking.setUserId(201L);
        hotelBooking.setStatusId(1L);

        User user = new User();
        user.setUserId(201L);

        HotelBookingStatus hotelBookingStatus = new HotelBookingStatus();
        hotelBookingStatus.setStatusId(1L);

        // Mock Page object

        Integer status = 1;
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        // Mock repository responses
        Mockito.when(hotelBookingRepository.findByStatusId(eq(status), eq(pageRequest)))
                .thenReturn(new PageImpl<>(Collections.singletonList(hotelBooking)));
        Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);
        Mockito.when(hotelBookingStatusRepository.findByStatusId(1L)).thenReturn(hotelBookingStatus);

        // Call the method
        Page<ViewBookingDTO> result = hotelBookingService.searchCheckInAndCheckOutAndStatus(null, null, status, page,
                size);

        // Verify the result
        assertEquals(1, result.getContent().size());
        ViewBookingDTO viewBookingDTO = result.getContent().get(0);
        assertEquals(101L, viewBookingDTO.getHotelBooking().getHotelBookingId());
        assertEquals(201L, viewBookingDTO.getUser().getUserId());
        assertEquals(1L, viewBookingDTO.getHotelBookingStatus().getStatusId());

        // Ensure that the repository methods are called with the correct parameters
        Mockito.verify(hotelBookingRepository, Mockito.times(1)).findByStatusId(eq(status), eq(pageRequest));
        Mockito.verify(userRepository, Mockito.times(1)).findByUserId(201L);
        Mockito.verify(hotelBookingStatusRepository, Mockito.times(1)).findByStatusId(1L);
    }

    @Test
    @DisplayName("cancelBookingUTCID01")
    public void testCancelBooking_SuccessfulCancellation() throws CancellationExistException {
        // Mock data
        CancellationFormDTO cancellationFormDTO = new CancellationFormDTO();
        cancellationFormDTO.setHotelBookingId(101L);
        cancellationFormDTO.setAccountNumber("123456789");
        cancellationFormDTO.setAccountName("John Doe");
        cancellationFormDTO.setBankId(1L);
        cancellationFormDTO.setReasonId(1L);
        cancellationFormDTO.setOtherReason("Not satisfied");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setUserId(201L);
        user.setEmail("john.doe@example.com");
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(6L);

        // Mock repository responses
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(userDetails.getUsername()).thenReturn(user.getEmail());
        Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(userRoleRepository.findByUserIdAndAndRoleId(user.getUserId(), 6L)).thenReturn(userRole);

        Transactions transactions = new Transactions();
        transactions.setTransactionId(301L);
        Mockito.when(transactionsRepository.findByHotelBookingIdNew(cancellationFormDTO.getHotelBookingId()))
                .thenReturn(transactions);

        RefundAccount refundAccount = new RefundAccount();
        refundAccount.setAccountId(401L);
        Mockito.when(refundAccountRepository.save(Mockito.any(RefundAccount.class))).thenReturn(refundAccount);

        List<CustomerCancellation> cancellationList = new ArrayList<>();

        // Mock repository responses for findAll
        Mockito.when(customerCancellationRepository.findAll()).thenReturn(cancellationList);

        // Mock repository responses for save
        Mockito.when(customerCancellationRepository.save(Mockito.any(CustomerCancellation.class)))
                .thenReturn(mock(CustomerCancellation.class));

        // Call the method
        assertDoesNotThrow(() -> hotelBookingService.cancelBooking(cancellationFormDTO, authentication));

    }

    @Test
    @DisplayName("cancelBookingUTCID02")
    public void testCancelBooking_SuccessfulCancellation_WithoutUserRole() throws CancellationExistException {
        // Mock data
        CancellationFormDTO cancellationFormDTO = new CancellationFormDTO();
        cancellationFormDTO.setHotelBookingId(101L);
        cancellationFormDTO.setAccountNumber("123456789");
        cancellationFormDTO.setAccountName("John Doe");
        cancellationFormDTO.setBankId(1L);
        cancellationFormDTO.setReasonId(1L);
        cancellationFormDTO.setOtherReason("Not satisfied");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setUserId(201L);
        user.setEmail("john.doe@example.com");

        // Mock repository responses
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(userDetails.getUsername()).thenReturn(user.getEmail());
        Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(userRoleRepository.findByUserIdAndAndRoleId(user.getUserId(), 6L)).thenReturn(null); // Simulate no
        // user role

        Transactions transactions = new Transactions();
        transactions.setTransactionId(301L);
        Mockito.when(transactionsRepository.findByHotelBookingIdNew(cancellationFormDTO.getHotelBookingId()))
                .thenReturn(transactions);

        RefundAccount refundAccount = new RefundAccount();
        refundAccount.setAccountId(401L);
        Mockito.when(refundAccountRepository.save(Mockito.any(RefundAccount.class))).thenReturn(refundAccount);

        List<CustomerCancellation> cancellationList = new ArrayList<>();

        // Mock repository responses for findAll
        Mockito.when(customerCancellationRepository.findAll()).thenReturn(cancellationList);

        // Mock repository responses for save
        Mockito.when(customerCancellationRepository.save(Mockito.any(CustomerCancellation.class)))
                .thenReturn(mock(CustomerCancellation.class));

        // Call the method
        assertDoesNotThrow(() -> hotelBookingService.cancelBooking(cancellationFormDTO, authentication));

    }

    private BookingDetailsDTO createBookingDetailsDTO() {
        BookingDetailsDTO bookingDetailsDTO = new BookingDetailsDTO();

        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setEmail("test@example.com");

        bookingDetailsDTO.setHotelBooking(hotelBooking);

        return bookingDetailsDTO;
    }

    @Test
    @DisplayName("sendBookingRequestUTCID01")
    public void testSendBookingRequest_NullEmail() throws ResetExceptionHandler {
        // Mock data
        Long hotelBookingId = 1L;
        BookingDetailsDTO bookingDetailsDTO = createBookingDetailsDTO();
        bookingDetailsDTO.getHotelBooking().setEmail(null);

        // Mock dependencies
        when(bookingRoomDetailsService.getBookingDetailsByHotelBooking(hotelBookingId)).thenReturn(bookingDetailsDTO);

        // Call the method and expect MailExceptionHandler
        MailExceptionHandler exception = assertThrows(MailExceptionHandler.class,
                () -> hotelBookingService.sendBookingRequest(hotelBookingId));

        // Verify exception message if needed
        assertEquals("Email không hợp lệ", exception.getMessage());

        // Verify interactions
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendBookingRequestUTCID02")
    public void testSendBookingRequest_TemplateException() throws ResetExceptionHandler {
        // Mock data
        Long hotelBookingId = 1L;
        BookingDetailsDTO bookingDetailsDTO = createBookingDetailsDTO();

        // Mock dependencies
        when(bookingRoomDetailsService.getBookingDetailsByHotelBooking(hotelBookingId)).thenReturn(bookingDetailsDTO);
        when(templateEngine.process((String) any(), any()))
                .thenThrow(new RuntimeException("Template Engine Exception"));

        // Call the method
        assertThrows(MailExceptionHandler.class, () -> hotelBookingService.sendBookingRequest(hotelBookingId));

        // Verify interactions
        verify(javaMailSender, never()).send((MimeMessage) any());
    }

    @Test
    @DisplayName("sendBookingRequestUTCID03")
    public void testSendBookingRequest_SuccessfulEmail() throws ResetExceptionHandler {
        // Mock data
        Long hotelBookingId = 1L;
        BookingDetailsDTO bookingDetailsDTO = createBookingDetailsDTO();

        // Mock dependencies
        when(bookingRoomDetailsService.getBookingDetailsByHotelBooking(hotelBookingId)).thenReturn(bookingDetailsDTO);
        when(templateEngine.process(anyString(), any())).thenReturn(null);

        // Call the method
        assertDoesNotThrow(() -> hotelBookingService.sendBookingRequest(hotelBookingId));

        // Verify interactions
        verify(javaMailSender, never()).send((MimeMessage) any());
    }

    @Test
    @DisplayName("sendBookingRequestUTCID04")
    public void testSendBookingRequest_InvalidBookingDetails() throws ResetExceptionHandler {
        // Mock data
        Long hotelBookingId = 1L;
        BookingDetailsDTO bookingDetailsDTO = null; // Set bookingDetailsDTO to null

        // Mock dependencies
        when(bookingRoomDetailsService.getBookingDetailsByHotelBooking(hotelBookingId)).thenReturn(bookingDetailsDTO);

        // Call the method and assert ResetExceptionHandler is thrown
        ResetExceptionHandler exception = assertThrows(ResetExceptionHandler.class,
                () -> hotelBookingService.sendBookingRequest(hotelBookingId));

        // Verify interactions
        verify(javaMailSender, never()).send((MimeMessage) any());

        // Verify exception message
        assertEquals("Chi tiết đặt phòng không hợp lệ", exception.getMessage());
    }

    @Test
    @DisplayName("findAllBookingByUserIdUTCID01")
    public void testFindAllByUserId() {
        // Mock data
        Long userId = 1L;
        LocalDate checkIn = null;
        Integer status = null;
        int page = 0;
        int size = 10;

        // Mock repository responses
        List<HotelBooking> hotelBookings = Arrays.asList(createMockHotelBooking(101L, userId, LocalDate.now(), 1),
                createMockHotelBooking(102L, userId, LocalDate.now(), 2));
        Page<HotelBooking> hotelBookingPage = new PageImpl<>(hotelBookings);

        when(hotelBookingRepository.findAllByUserId(userId, PageRequest.of(page, size))).thenReturn(hotelBookingPage);

        // Call the method
        Page<ViewHotelBookingDTO> result = hotelBookingService.findAllBookingByUserId(userId, checkIn, status, page,
                size);

        // Verify the result
        assertEquals(hotelBookings.size(), result.getContent().size());

    }

    @Test
    @DisplayName("findAllBookingByUserIdUTCID02")
    public void testFindAllByUserIdAndCheckIn() {
        // Mock data
        Long userId = 1L;
        LocalDate checkIn = LocalDate.now();
        Integer status = null;
        int page = 0;
        int size = 10;

        // Mock repository responses
        List<HotelBooking> hotelBookings = Arrays.asList(createMockHotelBooking(101L, userId, LocalDate.now(), 1),
                createMockHotelBooking(102L, userId, LocalDate.now(), 2));
        Page<HotelBooking> hotelBookingPage = new PageImpl<>(hotelBookings);

        when(hotelBookingRepository.findAllByUserIdAndCheckIn(userId, checkIn, PageRequest.of(page, size)))
                .thenReturn(hotelBookingPage);

        // Call the method
        Page<ViewHotelBookingDTO> result = hotelBookingService.findAllBookingByUserId(userId, checkIn, status, page,
                size);

        // Verify the result
        assertEquals(hotelBookings.size(), result.getContent().size());

        // Ensure that the repository methods are called with the correct parameters
        verify(hotelBookingRepository, times(1)).findAllByUserIdAndCheckIn(eq(userId), eq(checkIn),
                any(PageRequest.class));
        verify(customerCancellationRepository, times(2)).findCustomerCancellationNewHotelBookingId(any());
        verify(hotelBookingStatusRepository, times(2)).findByStatusId(any());
        verify(userRepository, times(2)).findByUserId(any());
        verify(feedbackRepository, times(2)).findByHotelBookingId(any());
    }

    @Test
    @DisplayName("findAllBookingByUserIdUTCID03")
    public void testFindAllByUserIdAndStatusId() {
        // Mock data
        Long userId = 1L;
        LocalDate checkIn = null;
        Integer status = 2; // Assuming a specific status value
        int page = 0;
        int size = 10;

        // Mock repository responses
        List<HotelBooking> hotelBookings = Arrays.asList(createMockHotelBooking(101L, userId, LocalDate.now(), status),
                createMockHotelBooking(102L, userId, LocalDate.now(), status));
        Page<HotelBooking> hotelBookingPage = new PageImpl<>(hotelBookings);

        when(hotelBookingRepository.findAllByUserIdAndStatusId(userId, status, PageRequest.of(page, size)))
                .thenReturn(hotelBookingPage);

        // Call the method
        Page<ViewHotelBookingDTO> result = hotelBookingService.findAllBookingByUserId(userId, checkIn, status, page,
                size);

        // Verify the result
        assertEquals(hotelBookings.size(), result.getContent().size());

        // Ensure that the repository methods are called with the correct parameters
        verify(hotelBookingRepository, times(1)).findAllByUserIdAndStatusId(eq(userId), eq(status),
                any(PageRequest.class));
        verify(customerCancellationRepository, times(2)).findCustomerCancellationNewHotelBookingId(any());
        verify(hotelBookingStatusRepository, times(2)).findByStatusId(any());
        verify(userRepository, times(2)).findByUserId(any());
        verify(feedbackRepository, times(2)).findByHotelBookingId(any());
    }

    @Test
    @DisplayName("findAllBookingByUserIdUTCID04")
    public void testFindAllByUserIdAndCheckInAndStatusId() {
        // Mock data
        Long userId = 1L;
        LocalDate checkIn = LocalDate.now();
        Integer status = 2; // Assuming a specific status value
        int page = 0;
        int size = 10;

        // Mock repository responses
        List<HotelBooking> hotelBookings = Arrays.asList(createMockHotelBooking(101L, userId, checkIn, status),
                createMockHotelBooking(102L, userId, checkIn, status));
        Page<HotelBooking> hotelBookingPage = new PageImpl<>(hotelBookings);

        when(hotelBookingRepository.findAllByUserIdAndCheckInAndStatusId(userId, checkIn, status,
                PageRequest.of(page, size))).thenReturn(hotelBookingPage);

        // Call the method
        Page<ViewHotelBookingDTO> result = hotelBookingService.findAllBookingByUserId(userId, checkIn, status, page,
                size);

        // Verify the result
        assertEquals(hotelBookings.size(), result.getContent().size());

        // Ensure that the repository methods are called with the correct parameters
        verify(hotelBookingRepository, times(1)).findAllByUserIdAndCheckInAndStatusId(eq(userId), eq(checkIn),
                eq(status), any(PageRequest.class));
        verify(customerCancellationRepository, times(2)).findCustomerCancellationNewHotelBookingId(any());
        verify(hotelBookingStatusRepository, times(2)).findByStatusId(any());
        verify(userRepository, times(2)).findByUserId(any());
        verify(feedbackRepository, times(2)).findByHotelBookingId(any());
    }

    private HotelBooking createMockHotelBooking(Long id, Long userId, LocalDate checkIn, Integer status) {
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotelBookingId(id);
        hotelBooking.setUserId(userId);
        hotelBooking.setCheckIn(checkIn.atStartOfDay(ZoneOffset.UTC).toInstant());
        hotelBooking.setStatusId(Long.valueOf(status));
        return hotelBooking;
    }

}