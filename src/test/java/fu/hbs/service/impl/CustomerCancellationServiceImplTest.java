package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Collections;

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

import fu.hbs.dto.CancellationDTO.ViewCancellationDTO;
import fu.hbs.entities.CustomerCancellation;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.User;
import fu.hbs.repository.CustomerCancellationRepository;
import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerCancellationServiceImplTest {

	@Mock
	private CustomerCancellationRepository customerCancellationRepository;

	@Mock
	private HotelBookingRepository hotelBookingRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomerCancellationServiceImpl customerCancellationService;

	@Test
	public void testSave() {
		// Mock data
		CustomerCancellation customerCancellation = new CustomerCancellation();
		customerCancellation.setCancellationId(1L);

		// Mock repository response
		Mockito.when(customerCancellationRepository.save(customerCancellation)).thenReturn(customerCancellation);

		// Call the method
		CustomerCancellation result = customerCancellationService.saveCustomerCancellation(customerCancellation);

		// Verify the result
		assertEquals(1L, result.getCancellationId());

		// Ensure that customerCancellationRepository.save is called with the correct
		// parameter
		Mockito.verify(customerCancellationRepository, Mockito.times(1)).save(customerCancellation);
	}

	@Test
	@DisplayName("testFindCustomerCancellationNewUTCID01")
	public void testFindCustomerCancellationNew() {
		// Mock data
		CustomerCancellation customerCancellation = new CustomerCancellation();
		customerCancellation.setCancellationId(1L);

		// Mock repository response
		Mockito.when(customerCancellationRepository.findCustomerCancellationNewHotelBookingId(1L))
				.thenReturn(customerCancellation);

		// Call the method
		CustomerCancellation result = customerCancellationService.findCustomerCancellationNew(1L);

		// Verify the result
		assertEquals(1L, result.getCancellationId());

		// Ensure that
		// customerCancellationRepository.findCustomerCancellationNewHotelBookingId is
		// called with the correct parameter
		Mockito.verify(customerCancellationRepository, Mockito.times(1)).findCustomerCancellationNewHotelBookingId(1L);
	}

	@Test
	@DisplayName("testGetAllByStatusConfirmUTCID05")
	public void testGetAllByStatusConfirmWithAllNull() {
		// Mock data
		int page = 0;
		int size = 10;

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CustomerCancellation> customerCancellationsPage = new PageImpl<>(Collections.emptyList());

		// Mock repository responses
		Mockito.when(customerCancellationRepository.findAllStatusConfirm(pageRequest))
				.thenReturn(customerCancellationsPage);

		// Call the method
		Page<ViewCancellationDTO> result = customerCancellationService.getAllByStatusConfirm(null, null, page, size);

		// Verify the result
		assertEquals(0, result.getContent().size());

		// Ensure that the repository is called with the correct parameters
		Mockito.verify(customerCancellationRepository, Mockito.times(1)).findAllStatusConfirm(pageRequest);
	}

	@Test
	@DisplayName("testGetAllByStatusConfirmUTCID04")
	public void testGetAllByStatusConfirmWithStatusNotNull() {
		// Mock data
		int page = 0;
		int size = 10;
		int status = 1;

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CustomerCancellation> customerCancellationsPage = new PageImpl<>(Collections.emptyList());

		// Mock repository responses
		Mockito.when(customerCancellationRepository.findByStatusConfirm(status, pageRequest))
				.thenReturn(customerCancellationsPage);

		// Call the method
		Page<ViewCancellationDTO> result = customerCancellationService.getAllByStatusConfirm(null, status, page, size);

		// Verify the result
		assertEquals(0, result.getContent().size());

		// Ensure that the repository is called with the correct parameters
		Mockito.verify(customerCancellationRepository, Mockito.times(1)).findByStatusConfirm(status, pageRequest);
	}

	@Test
	@DisplayName("testGetAllByStatusConfirmUTCID03")
	public void testGetAllByStatusConfirmWithCheckInNotNull() {
		// Mock data
		int page = 0;
		int size = 10;
		LocalDate checkInDate = LocalDate.now();

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CustomerCancellation> customerCancellationsPage = new PageImpl<>(Collections.emptyList());

		// Mock repository responses
		Mockito.when(customerCancellationRepository.findByCheckInAndStatusConfirm(checkInDate, null, pageRequest))
				.thenReturn(customerCancellationsPage);

		// Call the method
		Page<ViewCancellationDTO> result = customerCancellationService.getAllByStatusConfirm(checkInDate, null, page,
				size);

		// Verify the result
		assertEquals(0, result.getContent().size());

		// Ensure that the repository is called with the correct parameters
		Mockito.verify(customerCancellationRepository, Mockito.times(1)).findByCheckInAndStatusConfirm(checkInDate,
				null, pageRequest);
	}

	@Test
	@DisplayName("testGetAllByStatusConfirmUTCID02")
	public void testGetAllByStatusConfirmWithAllNotNull() {
		// Mock data
		int page = 0;
		int size = 10;
		int status = 1;
		LocalDate checkInDate = LocalDate.now();

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CustomerCancellation> customerCancellationsPage = new PageImpl<>(Collections.emptyList());

		// Mock repository responses
		Mockito.when(customerCancellationRepository.findByCheckInAndStatusConfirm(checkInDate, status, pageRequest))
				.thenReturn(customerCancellationsPage);

		// Call the method
		Page<ViewCancellationDTO> result = customerCancellationService.getAllByStatusConfirm(checkInDate, status, page,
				size);

		// Verify the result
		assertEquals(0, result.getContent().size());

		// Ensure that the repository is called with the correct parameters
		Mockito.verify(customerCancellationRepository, Mockito.times(1)).findByCheckInAndStatusConfirm(checkInDate,
				status, pageRequest);
	}

	@Test
	@DisplayName("testGetAllByStatusConfirmUTCID06")

	public void testMappingLogicInGetAllByStatusConfirm() {
		// Mock data
		CustomerCancellation customerCancellation = new CustomerCancellation();
		customerCancellation.setCancellationId(1L);
		customerCancellation.setHotelBookingId(101L);

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses
		Mockito.when(hotelBookingRepository.findByHotelBookingId(101L)).thenReturn(hotelBooking);
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Mock Page object
		int page = 0;
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CustomerCancellation> customerCancellationsPage = new PageImpl<>(
				Collections.singletonList(customerCancellation));
		Mockito.when(customerCancellationRepository.findAllStatusConfirm(pageRequest))
				.thenReturn(customerCancellationsPage);

		// Call the method
		Page<ViewCancellationDTO> result = customerCancellationService.getAllByStatusConfirm(null, null, page, size);

		// Verify the result
		assertEquals(1, result.getContent().size());
		ViewCancellationDTO viewCancellationDTO = result.getContent().get(0);
		assertEquals(1L, viewCancellationDTO.getCustomerCancellation().getCancellationId());
		assertEquals(101L, viewCancellationDTO.getHotelBooking().getHotelBookingId());
		assertEquals(201L, viewCancellationDTO.getUser().getUserId());

	}

	@Test
	@DisplayName("testGetAllByStatusProcesssUTCID01")
	public void testGetAllByStatusProcessWithStatusNotNull() {
		// Mock data
		int page = 0;
		int size = 10;
		int status = 1;

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CustomerCancellation> customerCancellationsPage = new PageImpl<>(Collections.emptyList());

		// Mock repository responses
		Mockito.when(customerCancellationRepository.findByStatusConfirm(status, pageRequest))
				.thenReturn(customerCancellationsPage);

		// Call the method
		Page<ViewCancellationDTO> result = customerCancellationService.getAllByStatusProcess(null, status, page, size);

		// Verify the result
		assertEquals(0, result.getContent().size());

		// Ensure that the repository is called with the correct parameters
		Mockito.verify(customerCancellationRepository, Mockito.times(1)).findByStatusConfirm(status, pageRequest);
	}

	@Test
	@DisplayName("testGetAllByStatusProcesssUTCID03")
	public void testGetAllByStatusProcessWithCheckInNotNull() {
		// Mock data
		int page = 0;
		int size = 10;
		LocalDate checkInDate = LocalDate.now();

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CustomerCancellation> customerCancellationsPage = new PageImpl<>(Collections.emptyList());

		// Mock repository responses
		Mockito.when(customerCancellationRepository.findByCheckInAndStatusProcess(checkInDate, null, pageRequest))
				.thenReturn(customerCancellationsPage);

		// Call the method
		Page<ViewCancellationDTO> result = customerCancellationService.getAllByStatusProcess(checkInDate, null, page,
				size);

		// Verify the result
		assertEquals(0, result.getContent().size());

		// Ensure that the repository is called with the correct parameters
		Mockito.verify(customerCancellationRepository, Mockito.times(1)).findByCheckInAndStatusProcess(checkInDate,
				null, pageRequest);
	}

	@Test
	@DisplayName("testGetAllByStatusProcesssUTCID02")
	public void testGetAllByStatusProcessWithAllNotNull() {
		// Mock data
		int page = 0;
		int size = 10;
		int status = 1;
		LocalDate checkInDate = LocalDate.now();

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CustomerCancellation> customerCancellationsPage = new PageImpl<>(Collections.emptyList());

		// Mock repository responses
		Mockito.when(customerCancellationRepository.findByCheckInAndStatusProcess(checkInDate, status, pageRequest))
				.thenReturn(customerCancellationsPage);

		// Call the method
		Page<ViewCancellationDTO> result = customerCancellationService.getAllByStatusProcess(checkInDate, status, page,
				size);

		// Verify the result
		assertEquals(0, result.getContent().size());

		// Ensure that the repository is called with the correct parameters
		Mockito.verify(customerCancellationRepository, Mockito.times(1)).findByCheckInAndStatusProcess(checkInDate,
				status, pageRequest);
	}

	@Test
	@DisplayName("testGetAllByStatusProcesssUTCID05")

	public void testMappingLogicInGetAllByStatusProcesss() {
		// Mock data
		CustomerCancellation customerCancellation = new CustomerCancellation();
		customerCancellation.setCancellationId(1L);
		customerCancellation.setHotelBookingId(101L);

		HotelBooking hotelBooking = new HotelBooking();
		hotelBooking.setHotelBookingId(101L);
		hotelBooking.setUserId(201L);

		User user = new User();
		user.setUserId(201L);

		// Mock repository responses
		Mockito.when(hotelBookingRepository.findByHotelBookingId(101L)).thenReturn(hotelBooking);
		Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

		// Mock Page object
		int page = 0;
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<CustomerCancellation> customerCancellationsPage = new PageImpl<>(
				Collections.singletonList(customerCancellation));
		Mockito.when(customerCancellationRepository.findAllStatusProcess(pageRequest))
				.thenReturn(customerCancellationsPage);

		// Call the method
		Page<ViewCancellationDTO> result = customerCancellationService.getAllByStatusProcess(null, null, page, size);

		// Verify the result
		assertEquals(1, result.getContent().size());
		ViewCancellationDTO viewCancellationDTO = result.getContent().get(0);
		assertEquals(1L, viewCancellationDTO.getCustomerCancellation().getCancellationId());
		assertEquals(101L, viewCancellationDTO.getHotelBooking().getHotelBookingId());
		assertEquals(201L, viewCancellationDTO.getUser().getUserId());

	}
}
