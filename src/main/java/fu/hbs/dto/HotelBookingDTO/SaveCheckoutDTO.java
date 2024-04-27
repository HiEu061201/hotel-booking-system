package fu.hbs.dto.HotelBookingDTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SaveCheckoutDTO {
    private Long hotelBookingId;
    private BigDecimal servicePrice = BigDecimal.ZERO;
    private BigDecimal surcharge = BigDecimal.ZERO;
    private Long paymentTypeId = 2L;
    private List<SaveCheckoutHotelServiceDTO> hotelServices = new ArrayList<>();
}