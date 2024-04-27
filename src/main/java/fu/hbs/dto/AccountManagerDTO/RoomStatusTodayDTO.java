package fu.hbs.dto.AccountManagerDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomStatusTodayDTO {
    private int totalRoom;
    private int occupiedRooms;
    private int vacantRooms;
    private double occupancyPercentage;
    private double vacancyPercentage;
}
