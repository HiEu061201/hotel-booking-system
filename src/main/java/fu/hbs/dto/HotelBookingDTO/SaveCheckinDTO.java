package fu.hbs.dto.HotelBookingDTO;


import fu.hbs.entities.BookingRoomDetails;
import fu.hbs.entities.HotelBooking;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveCheckinDTO {
    private Long hotelBookingId;
    private List<SaveCheckinDetailDTO> saveCheckinDetailDTOS = new ArrayList<>();
    public static SaveCheckinDTO valueOf(HotelBooking hotelBooking, List<BookingRoomDetails> bookingRoomDetails){
        SaveCheckinDTO saveCheckinDTO = new SaveCheckinDTO();
        saveCheckinDTO.setHotelBookingId(hotelBooking.getHotelBookingId());
        for (BookingRoomDetails bookingRoomDetail : bookingRoomDetails) {
            SaveCheckinDetailDTO saveCheckinDetailDTO = new SaveCheckinDetailDTO();
            saveCheckinDetailDTO.setRoomID(bookingRoomDetail.getRoomId());
            saveCheckinDetailDTO.setBookingRoomId(bookingRoomDetail.getBookingRoomId());
            saveCheckinDTO.getSaveCheckinDetailDTOS().add(saveCheckinDetailDTO);
        }
        return saveCheckinDTO;
    }
}
