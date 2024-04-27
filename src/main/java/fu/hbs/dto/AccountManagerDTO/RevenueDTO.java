package fu.hbs.dto.AccountManagerDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueDTO {
    private int roomCanceled;
    private int roomCheckIn;
    private int roomCheckOut;
    private RoomStatusTodayDTO roomStatusTodayDTO;
    private Float[] revenue;
    private List<RoomManagementItemDTO> categories;
}
