package fu.hbs.dto.MRoomDTO;

import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import fu.hbs.entities.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViewRoomDTO {
    private Room room;
    private RoomCategories roomCategories;
    private RoomStatus roomStatus;
    private CategoryRoomPrice categoryRoomPrice;
}
