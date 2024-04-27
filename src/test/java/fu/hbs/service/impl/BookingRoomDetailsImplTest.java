package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fu.hbs.dto.HotelBookingDTO.BookingDetailsDTO;
import fu.hbs.dto.User.CustomUserDetails;
import fu.hbs.entities.BankList;
import fu.hbs.entities.BookingRoomDetails;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.CustomerCancellation;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.PaymentType;
import fu.hbs.entities.RefundAccount;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import fu.hbs.entities.Transactions;
import fu.hbs.entities.TransactionsType;
import fu.hbs.entities.User;
import fu.hbs.repository.BankListRepository;
import fu.hbs.repository.BookingRoomDetailsRepository;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.CustomerCancellationRepository;
import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.HotelBookingServiceRepository;
import fu.hbs.repository.PaymentTypeRepository;
import fu.hbs.repository.RefundAccountRepository;
import fu.hbs.repository.RoomCategoriesRepository;
import fu.hbs.repository.RoomRepository;
import fu.hbs.repository.TransactionsRepository;
import fu.hbs.repository.TransactionsTyPeRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.utils.BookingDayUtils;

@ExtendWith(MockitoExtension.class)
class BookingRoomDetailsImplTest {
	@Mock
	private BookingRoomDetailsRepository bookingRoomDetailsRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private HotelBookingRepository hotelBookingRepository;
	@Mock
	private RoomRepository roomRepository;
	@Mock
	private RoomCategoriesRepository roomCategoriesRepository;
	@Mock
	private CategoryRoomPriceRepository categoryRoomPriceRepository;
	@Mock
	private RefundAccountRepository refundAccountRepository;
	@Mock
	private BankListRepository bankListRepository;
	@Mock
	private CustomerCancellationRepository customerCancellationRepository;
	@Mock
	private TransactionsRepository transactionsRepository;
	@Mock
	private PaymentTypeRepository paymentTypeRepository;
	@Mock
	private BookingDayUtils bookingDayUtils;
	@Mock
	private HotelBookingServiceRepository hotelBookingServiceRepository;
	@Mock
	private TransactionsTyPeRepository transactionsTyPeRepository;
	@InjectMocks
	private BookingRoomDetailsImpl bookingRoomDetailsImpl;

	@Test
	void saveBookingRoomDetails() {
		// Arrange
		BookingRoomDetails bookingRoomDetails = new BookingRoomDetails();
		bookingRoomDetails.setBookingRoomId(1L);
		bookingRoomDetails.setHotelBookingId(12L);
		bookingRoomDetails.setRoomCategoryId(2L);

		// Mock the behavior of the repositories
		when(bookingRoomDetailsRepository.save(bookingRoomDetails)).thenReturn(bookingRoomDetails);

		// Act
		BookingRoomDetails result = bookingRoomDetailsImpl.save(bookingRoomDetails);

		verify(bookingRoomDetailsRepository, times(1)).save(bookingRoomDetails);

		assertEquals(bookingRoomDetails, result);

		verify(bookingRoomDetailsRepository, times(1)).save(any(BookingRoomDetails.class));

	}

	@Test
	@DisplayName("getBookingDetailsUTCID01")
	void getBookingDetails() {
		// Arrange
		Authentication authentication = mock(Authentication.class);
		UserDetails userDetails = new CustomUserDetails("HieuLBM@gmail.com", "12345",
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
		when(authentication.getPrincipal()).thenReturn(userDetails);

		Long hotelBookingId = 1L;

		// Mocking userRepository
		User mockUser = new User();
		mockUser.setUserId(1L);
		mockUser.setEmail("HieuLBM@gmail.com");
		mockUser.setPassword("12345");
		mockUser.setStatus(true);
		when(userRepository.getUserByEmail(userDetails.getUsername())).thenReturn(mockUser);

		// Mocking hotelBookingRepository
		HotelBooking mockHotelBooking = new HotelBooking();
		mockHotelBooking.setHotelBookingId(hotelBookingId);
		mockHotelBooking.setCheckIn(Instant.now()); // Set a non-null check-in date
		mockHotelBooking.setCheckOut(Instant.now().plus(1, ChronoUnit.DAYS)); // Set a non-null check-out date
		mockHotelBooking.setCheckInActual(Instant.now()); // Set a non-null check-in date
		mockHotelBooking.setCheckOutActual(Instant.now().plus(1, ChronoUnit.DAYS)); // Set a non-null check-out date

		// Set up other properties for mockHotelBooking as needed
		when(hotelBookingRepository.findByHotelBookingId(hotelBookingId)).thenReturn(mockHotelBooking);

		// Mocking customerCancellationRepository
		CustomerCancellation mockCancellation = new CustomerCancellation();
		// Set up other properties for mockCancellation as needed
		when(customerCancellationRepository.findCustomerCancellationNewHotelBookingId(hotelBookingId))
				.thenReturn(mockCancellation);

		// Mocking transactionsRepository
		Transactions mockTransactions = new Transactions();
		mockTransactions.setTransactionId(1L);

		Transactions mockTransactions1 = new Transactions();
		mockTransactions1.setTransactionId(2L);

		List<Transactions> mockTransactionsList = new ArrayList<>();
		mockTransactionsList.add(mockTransactions);
		mockTransactionsList.add(mockTransactions1);
		// Set up other properties for mockTransactions as needed
		when(transactionsRepository.findByHotelBookingId(hotelBookingId)).thenReturn(mockTransactionsList);

		TransactionsType transactionsType = new TransactionsType();
		when(transactionsTyPeRepository.findByTransactionsTypeId(mockTransactions.getTransactionsTypeId()))
				.thenReturn(transactionsType);
		// Mocking paymentTypeRepository
		PaymentType mockPaymentType = new PaymentType();
		// Set up other properties for mockPaymentType as needed
		when(paymentTypeRepository.findByPaymentId(mockTransactions.getPaymentId())).thenReturn(mockPaymentType);

		// Mocking roomRepository
		Room mockRoom = new Room();
		mockRoom.setRoomId(1L);
		mockRoom.setRoomCategoryId(1L); // Set a non-null category ID

		when(roomRepository.findById(anyLong())).thenReturn(Optional.of(mockRoom));

		// Mocking roomCategoriesRepository
		RoomCategories mockRoomCategories = new RoomCategories();
		mockRoomCategories.setRoomCategoryId(1L);
		when(roomCategoriesRepository.findByRoomCategoryId(anyLong())).thenReturn(mockRoomCategories);

		// Mocking categoryRoomPriceRepository
		CategoryRoomPrice mockCategoryRoomPrice = new CategoryRoomPrice();
		mockCategoryRoomPrice.setRoomCategoryId(1L);
		when(categoryRoomPriceRepository.getCategoryId(anyLong())).thenReturn(mockCategoryRoomPrice);

		// Mocking bookingDayUtils and its dependencies as needed
		when(bookingDayUtils.processDateInfo(any(), any())).thenReturn(Collections.emptyList());

		// Mocking bookingRoomDetailsRepository
		List<BookingRoomDetails> bookingRoomDetails = new ArrayList<>();
		BookingRoomDetails mockBookingRoomDetails = new BookingRoomDetails();
		mockBookingRoomDetails.setRoomId(1L);
		mockBookingRoomDetails.setRoomCategoryId(1L);
		bookingRoomDetails.add(mockBookingRoomDetails);
		when(bookingRoomDetailsRepository.getAllByHotelBookingId(hotelBookingId)).thenReturn(bookingRoomDetails);

		// Act
		BookingDetailsDTO resultDTO = bookingRoomDetailsImpl.getBookingDetails(authentication, hotelBookingId);

		// Assert
		assertNotNull(resultDTO);
		assertEquals(mockUser, resultDTO.getUser());
		assertEquals(mockHotelBooking, resultDTO.getHotelBooking());

	}

	@Test
	@DisplayName("getBookingDetailsByHotelBookingUTCID01")
	void getBookingDetailsByHotelBooking() {
		// Arrange
		Long hotelBookingId = 1L;

		// Mocking userRepository
		User mockUser = new User();
		mockUser.setUserId(1L);
		mockUser.setEmail("HieuLBM@gmail.com");
		mockUser.setPassword("12345");
		mockUser.setStatus(true);
		when(userRepository.findByUserId(ArgumentMatchers.any())).thenReturn(mockUser);

		// Mocking hotelBookingRepository
		HotelBooking mockHotelBooking = new HotelBooking();
		mockHotelBooking.setHotelBookingId(hotelBookingId);
		mockHotelBooking.setCheckIn(Instant.now()); // Set a non-null check-in date
		mockHotelBooking.setCheckOut(Instant.now().plus(1, ChronoUnit.DAYS)); // Set a non-null check-out date
		mockHotelBooking.setCheckInActual(Instant.now()); // Set a non-null check-in date
		mockHotelBooking.setCheckOutActual(Instant.now().plus(1, ChronoUnit.DAYS)); // Set a non-null check-out date
		// Set up other properties for mockHotelBooking as needed
		when(hotelBookingRepository.findByHotelBookingId(hotelBookingId)).thenReturn(mockHotelBooking);

		// Mocking customerCancellationRepository
		CustomerCancellation mockCancellation = new CustomerCancellation();
		// Set up other properties for mockCancellation as needed
		when(customerCancellationRepository.findCustomerCancellationNewHotelBookingId(hotelBookingId))
				.thenReturn(mockCancellation);

		// Mocking transactionsRepository
		Transactions mockTransactions = new Transactions();
		// Set up other properties for mockTransactions as needed
		when(transactionsRepository.findByHotelBookingIdNew(hotelBookingId)).thenReturn(mockTransactions);

		// Mocking refundAccountRepository
		RefundAccount mockRefundAccount = new RefundAccount();
		// Set up other properties for mockRefundAccount as needed
		when(refundAccountRepository.findByAccountId(mockCancellation.getAccountId())).thenReturn(mockRefundAccount);

		// Mocking bankListRepository
		BankList mockBankList = new BankList();
		// Set up other properties for mockBankList as needed
		when(bankListRepository.findByBankId(mockRefundAccount.getBankId())).thenReturn(mockBankList);

		// Mocking paymentTypeRepository
		PaymentType mockPaymentType = new PaymentType();
		// Set up other properties for mockPaymentType as needed
		when(paymentTypeRepository.findByPaymentId(mockTransactions.getPaymentId())).thenReturn(mockPaymentType);

		// Mocking roomRepository
		Room mockRoom = new Room();
		mockRoom.setRoomId(1L);
		mockRoom.setRoomCategoryId(1L); // Set a non-null category ID
		when(roomRepository.findById(anyLong())).thenReturn(Optional.of(mockRoom));

		// Mocking roomCategoriesRepository
		RoomCategories mockRoomCategories = new RoomCategories();
		mockRoomCategories.setRoomCategoryId(1L);
		when(roomCategoriesRepository.findByRoomCategoryId(anyLong())).thenReturn(mockRoomCategories);

		// Mocking categoryRoomPriceRepository
		CategoryRoomPrice mockCategoryRoomPrice = new CategoryRoomPrice();
		mockCategoryRoomPrice.setRoomCategoryId(1L);
		when(categoryRoomPriceRepository.getCategoryId(anyLong())).thenReturn(mockCategoryRoomPrice);

		// Mocking bookingDayUtils and its dependencies as needed
		when(bookingDayUtils.processDateInfo(any(), any())).thenReturn(Collections.emptyList());

		// Mocking bookingRoomDetailsRepository
		List<BookingRoomDetails> bookingRoomDetailsList = new ArrayList<>();
		BookingRoomDetails mockBookingRoomDetails = new BookingRoomDetails();
		mockBookingRoomDetails.setRoomId(1L);
		mockBookingRoomDetails.setRoomCategoryId(1L);
		bookingRoomDetailsList.add(mockBookingRoomDetails);
		when(bookingRoomDetailsRepository.getAllByHotelBookingId(hotelBookingId)).thenReturn(bookingRoomDetailsList);

		// Act
		BookingDetailsDTO resultDTO = bookingRoomDetailsImpl.getBookingDetailsByHotelBooking(hotelBookingId);

		// Assert
		assertNotNull(resultDTO);

	}

	@Test
	void testGetBookingDetailsByHotelBookingId() {
		// Arrange
		Long hotelBookingId = 1L;

		// Tạo danh sách mock BookingRoomDetails
		List<BookingRoomDetails> mockBookingRoomDetailsList = new ArrayList<>();
		BookingRoomDetails mockBookingRoomDetails1 = new BookingRoomDetails();
		mockBookingRoomDetails1.setRoomId(1L);
		mockBookingRoomDetails1.setRoomCategoryId(1L);
		mockBookingRoomDetailsList.add(mockBookingRoomDetails1);

		// Stub phương thức trong bookingRoomDetailsRepository để trả về danh sách mock
		// khi gọi với hotelBookingId
		when(bookingRoomDetailsRepository.getAllByHotelBookingId(hotelBookingId))
				.thenReturn(mockBookingRoomDetailsList);

		// Act
		List<BookingRoomDetails> result = bookingRoomDetailsImpl.getBookingDetailsByHotelBookingId(hotelBookingId);

		// Assert
		assertEquals(mockBookingRoomDetailsList, result);

		// Verify rằng phương thức đã được gọi đúng lúc với đúng tham số
		verify(bookingRoomDetailsRepository, times(1)).getAllByHotelBookingId(hotelBookingId);
	}

}
