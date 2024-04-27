package fu.hbs.dto.HotelBookingDTO;

import fu.hbs.dto.CategoryRoomPriceDTO.DateInfoCategoryRoomPriceDTO;
import fu.hbs.entities.CategoryRoomPrice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TotalPriceDTO {
    private List<DateInfoCategoryRoomPriceDTO> dateInfoList;
    private List<CategoryRoomPrice> categoryRoomPrices;
    private Map<Long, Integer> roomCategoryMap;
    private BigDecimal totalPrice;
}
