package fu.hbs.dto.MRoomDTO;


import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class CreateCategoryRoomPriceDTO {
    private Long roomCategoryId;
    private Date startDate;
    private Date endDate;
    private BigDecimal price;
    private int flag;
}

