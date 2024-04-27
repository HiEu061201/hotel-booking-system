package fu.hbs.dto.VnpayDTO;

import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HotelBookingInPaymentDTO {
    private HotelBooking hotelBooking;
    private User user;
}
