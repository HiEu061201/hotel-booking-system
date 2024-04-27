package fu.hbs.dto.MRoomDTO;

import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.RoomCategories;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewCategoryDTO {
    private RoomCategories roomCategories;
    private CategoryRoomPrice categoryRoomPrice;
}
