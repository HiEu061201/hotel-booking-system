package fu.hbs.service.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import fu.hbs.dto.HotelBookingDTO.SaveCheckoutDTO;
import fu.hbs.dto.HotelBookingDTO.CreateHotelBookingDTO;
import fu.hbs.dto.HotelBookingDTO.SaveCheckinDTO;

import fu.hbs.dto.ReceptionistDTO.ViewBookingDTO;
import fu.hbs.entities.HotelBooking;
import org.springframework.data.domain.Page;

public interface ReceptionistBookingService {
    public List<HotelBooking> findAll();

    public HotelBooking findById(Long id);

    public void save(HotelBooking booking);

    Long createHotelBookingByReceptionist(CreateHotelBookingDTO bookingRequest);

    boolean checkout(SaveCheckoutDTO saveCheckoutDTO);

    BigDecimal getTotalPriceOfHotelBooking(Long hotelBooking);

    Boolean checkIn(Long hotelBookingId);

    Page<ViewBookingDTO> searchCheckInAndCheckOutAndStatus(LocalDate checkIn, LocalDate checkOut, Integer status, int page, int size);


    Page<ViewBookingDTO> searchCheckInAndCheckOut(LocalDate checkIn, LocalDate checkOut, Integer status, int defaultPage, int defaultSize);


    Boolean checkIn(SaveCheckinDTO checkinDTO);
}