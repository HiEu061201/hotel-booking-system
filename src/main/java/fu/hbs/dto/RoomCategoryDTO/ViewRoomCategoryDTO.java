package fu.hbs.dto.RoomCategoryDTO;

import fu.hbs.entities.CategoryRoomPrice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewRoomCategoryDTO {
	private Long roomCategoryId;
	private String roomCategoryName;
	private CategoryRoomPrice categoryRoomPrice;
	private String description;
	private String image;
}
