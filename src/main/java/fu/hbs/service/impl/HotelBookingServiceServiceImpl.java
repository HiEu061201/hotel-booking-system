package fu.hbs.service.impl;

import fu.hbs.entities.HotelBookingService;
import fu.hbs.repository.HotelBookingServiceRepository;
import fu.hbs.service.interfaces.HotelBookingServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelBookingServiceServiceImpl implements HotelBookingServiceService {
    @Autowired
    private HotelBookingServiceRepository hotelBookingServiceRepository;

    @Override
    public List<HotelBookingService> findAllByHotelBookingId(Long id) {
        return hotelBookingServiceRepository.getAllByHotelBookingId(id);
    }
}