package fu.hbs.dto.User;

import java.util.List;

import fu.hbs.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookRoomByCategory {
    private List<Room> rooms;
    private List<CategoryRoomFurniture> categoryRoomFurnitures;
    private List<RoomService> services;
    private List<RoomImage> images;
    private List<RoomFurniture> roomFurnitures;
    private RoomCategories roomCategories;
    private CategoryRoomPrice categoryRoomPrice;
}
