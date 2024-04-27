package fu.hbs.dto.HotelBookingDTO;

import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomInDetailsDTO {
    private Room room;
    private RoomCategories roomCategories;
    private CategoryRoomPrice categoryRoomPrice;
 
}
