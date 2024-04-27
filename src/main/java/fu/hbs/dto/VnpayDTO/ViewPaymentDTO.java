package fu.hbs.dto.VnpayDTO;

import fu.hbs.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewPaymentDTO {
    private PaymentType paymentType;
    private Transactions Transactions;
    private HotelBooking hotelBooking;
    private TransactionsType transactionsType;
    private User user;
}
