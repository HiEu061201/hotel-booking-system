package fu.hbs.dto.CancellationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConfirmRefundDTO {
    private Long hotelBookingId;
    private Long userId;
    private Long cancellationId;
    private BigDecimal refundPrice;
}
