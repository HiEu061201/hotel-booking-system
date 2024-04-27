package fu.hbs.dto.AccountManagerDTO;

import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import lombok.Data;

import java.util.List;

@Data
public class RoomManagementItemDTO {
    private RoomCategories roomCategories;
    private int booked;
    private int empty;
    private int using;
}
