package fu.hbs.service.interfaces;

import fu.hbs.dto.HotelBookingDTO.BookingDetailsDTO;
import fu.hbs.entities.BookingRoomDetails;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BookingRoomDetailsService {
    BookingRoomDetails save(BookingRoomDetails bookingRoomDetails);

    BookingDetailsDTO getBookingDetails(Authentication authentication, Long hotelBookingId);

    BookingDetailsDTO getBookingDetailsByHotelBooking(Long hotelBookingId);

    BookingDetailsDTO getBookingDetailsByHotelBookingIsValid(Long hotelBookingId);

    List<BookingRoomDetails> getBookingDetailsByHotelBookingId(Long hotelBookingId);
}
