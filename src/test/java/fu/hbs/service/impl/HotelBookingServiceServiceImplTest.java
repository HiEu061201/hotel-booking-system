package fu.hbs.service.impl;

import static org.mockito.Mockito.*;

import fu.hbs.entities.HotelBookingService;
import fu.hbs.repository.HotelBookingServiceRepository;
import fu.hbs.service.impl.HotelBookingServiceServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HotelBookingServiceServiceImplTest {

    @Mock
    private HotelBookingServiceRepository hotelBookingServiceRepository;

    @InjectMocks
    private HotelBookingServiceServiceImpl hotelBookingService;

    @Test
    @DisplayName("testFindAllByHotelBookingIdUTCID01")
    public void testFindAllByHotelBookingId() {
        // Arrange
        Long bookingId = 1L;
        List<HotelBookingService> expectedServices = new ArrayList<>();
        expectedServices.add(new HotelBookingService());

        // Mock repository behavior
        when(hotelBookingServiceRepository.getAllByHotelBookingId(bookingId)).thenReturn(expectedServices);

        // Act
        List<HotelBookingService> actualServices = hotelBookingService.findAllByHotelBookingId(bookingId);

        // Assert
        assertEquals(expectedServices, actualServices);

        // Verify that the repository method was called with the correct argument
        verify(hotelBookingServiceRepository).getAllByHotelBookingId(bookingId);
    }
}
