package fu.hbs.dto.RoomCategoryDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchCategoryDTO {
    private Long roomCategoryId;
    private String roomCategoryName;
    private int totalRoom;
}
