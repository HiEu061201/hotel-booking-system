package fu.hbs.repository;

import fu.hbs.entities.HotelBookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelBookingServiceRepository extends JpaRepository<HotelBookingService, Long> {
    List<HotelBookingService> getAllByHotelBookingId(Long id);

    void deleteAllByHotelBookingId(Long hotelId);

}