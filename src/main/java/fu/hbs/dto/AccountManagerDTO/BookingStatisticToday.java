package fu.hbs.dto.AccountManagerDTO;

import lombok.Data;

@Data
public class BookingStatisticToday {
    private Long hotelBookingId;
    private Long statusId;
    private Long roomCategoryId;
    private int total;
}
