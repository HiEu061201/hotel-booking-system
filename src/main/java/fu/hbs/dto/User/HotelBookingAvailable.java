package fu.hbs.dto.User;

import fu.hbs.dto.CategoryRoomPriceDTO.DateInfoCategoryRoomPriceDTO;
import fu.hbs.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HotelBookingAvailable {
    private List<Room> rooms;
    private List<RoomCategories> roomCategories;
    private List<RoomImage> roomImages;
    private List<RoomFurniture> roomFurnitures;
    private List<RoomService> roomServices;
    private List<CategoryRoomFurniture> categoryRoomFurnitures;
    private Map<Long, List<Room>> totalRoom;
    private List<CategoryRoomPrice> categoryRoomPrices;
    private BigDecimal total;
    private List<DateInfoCategoryRoomPriceDTO> dateInfoCategoryRoomPriceDTOS;

}
