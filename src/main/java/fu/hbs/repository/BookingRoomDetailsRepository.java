package fu.hbs.repository;

import fu.hbs.entities.BookingRoomDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRoomDetailsRepository extends JpaRepository<BookingRoomDetails, Long> {

    List<BookingRoomDetails> getAllByHotelBookingId(Long hotelBookingId);

    List<BookingRoomDetails> findByHotelBookingIdIn(List<Long> hotelBookingIds);
}
