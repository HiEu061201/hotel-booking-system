package fu.hbs.dto.HotelBookingDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GuestBookingDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private BigDecimal paymentAmount;
    private String orderInfo;
    private String note;

}
