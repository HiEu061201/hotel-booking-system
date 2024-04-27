package fu.hbs.dto.HotelBookingDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class searchBookingDTO {
    private List<Long> roomCategoryNames;
    private List<Integer> selectedRoomCategories;
}
