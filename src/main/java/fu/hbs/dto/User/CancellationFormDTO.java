package fu.hbs.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CancellationFormDTO {
    private Long hotelBookingId;
    private Long reasonId;
    private Long bankId;
    private String otherReason;
    private String accountNumber;
    private String accountName;
    private String refundAmount;
}
