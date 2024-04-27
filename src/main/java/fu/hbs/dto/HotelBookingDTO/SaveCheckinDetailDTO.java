package fu.hbs.dto.HotelBookingDTO;

import lombok.Data;

@Data
public class SaveCheckinDetailDTO {
    private Long bookingRoomId;
    private Long roomID;
}