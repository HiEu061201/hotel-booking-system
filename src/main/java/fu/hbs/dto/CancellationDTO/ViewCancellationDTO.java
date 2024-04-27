package fu.hbs.dto.CancellationDTO;


import fu.hbs.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewCancellationDTO {
    private HotelBooking hotelBooking;
    private User user;
    private BookingRoomDetails bookingRoomDetails;
    private RoomCategories roomCategories;
    private CustomerCancellation customerCancellation;
    private RefundAccount refundAccount;
}
