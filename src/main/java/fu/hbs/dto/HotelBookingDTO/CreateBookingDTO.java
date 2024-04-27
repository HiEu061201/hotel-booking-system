package fu.hbs.dto.HotelBookingDTO;

import fu.hbs.dto.CategoryRoomPriceDTO.DateInfoCategoryRoomPriceDTO;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateBookingDTO {
    private LocalDate checkIn = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toLocalDate();
    private LocalDate checkOut = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toLocalDate().plusDays(1);
    private List<RoomCategories> roomCategoriesList;
    private List<Room> rooms;
    private BigDecimal totalPrice;
    private BigDecimal depositPrice;
    private BigDecimal allPrice;
    private int totalDay;
    private Map<Long, Integer> roomCategoryMap;
    private Map<Long, BigDecimal> totalPriceByCategoryId;
    private List<DateInfoCategoryRoomPriceDTO> dateInfoList;
    private List<CategoryRoomPrice> categoryRoomPrice;
}
