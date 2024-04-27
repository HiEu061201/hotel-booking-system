package fu.hbs.dto.ReceptionistDTO;

import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.HotelBookingStatus;
import fu.hbs.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewBookingDTO {
    private User user;
    private HotelBooking hotelBooking;
    private HotelBookingStatus hotelBookingStatus;
}
