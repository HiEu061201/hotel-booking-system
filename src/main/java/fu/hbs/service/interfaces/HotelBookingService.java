package fu.hbs.service.interfaces;

import fu.hbs.dto.User.CancellationFormDTO;
import fu.hbs.dto.User.HotelBookingAvailable;
import fu.hbs.dto.HotelBookingDTO.CreateBookingDTO;
import fu.hbs.dto.HotelBookingDTO.ViewHotelBookingDTO;
import fu.hbs.dto.ReceptionistDTO.ViewBookingDTO;
import fu.hbs.entities.HotelBooking;
import fu.hbs.exceptionHandler.CancellationExistException;
import fu.hbs.exceptionHandler.ResetExceptionHandler;
import fu.hbs.exceptionHandler.RoomCategoryNamesNullException;
import fu.hbs.exceptionHandler.SearchRoomCustomerExceptionHandler;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface HotelBookingService {


    HotelBookingAvailable findBookingsByDates(LocalDate checkIn, LocalDate checkOut, int numberPerson) throws SearchRoomCustomerExceptionHandler, RoomCategoryNamesNullException;

    Page<ViewHotelBookingDTO> findAllBookingByUserId(Long id, LocalDate checkIn, Integer status, int page, int size);

    HotelBooking saveHotelBooking(HotelBooking hotelBooking);

    CreateBookingDTO createBooking
            (List<Long> roomCategoryNames, List<Integer> selectedRoomCategories, LocalDate checkIn, LocalDate
                    checkOut);

    void cancelBooking(CancellationFormDTO cancellationFormDTO, Authentication authentication) throws CancellationExistException;

    Long saveRoomAfterBooking(Authentication authentication, HttpSession session, BigDecimal totalPrice) throws ResetExceptionHandler;

    void sendBookingRequest(Long hotelBookingId) throws ResetExceptionHandler;

    void changeStatusId(Long hotelBookingId);

    HotelBooking findById(Long id);

    Page<ViewBookingDTO> searchCheckInAndCheckOutAndStatus(LocalDate checkIn, LocalDate checkOut, Integer status, int page, int size);

    int getDayType(LocalDate startDate);
}
