package fu.hbs.dto.CategoryRoomPriceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DateInfoCategoryRoomPriceDTO {
    private LocalDate date;
    private int dayType;

}
