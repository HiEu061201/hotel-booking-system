package fu.hbs.service.interfaces;

import fu.hbs.entities.HotelBookingService;

import java.util.List;

public interface HotelBookingServiceService {
    List<HotelBookingService> findAllByHotelBookingId(Long id);
}