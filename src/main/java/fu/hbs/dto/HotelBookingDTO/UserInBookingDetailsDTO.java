package fu.hbs.dto.HotelBookingDTO;

import fu.hbs.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInBookingDetailsDTO {
    private User user;
    private RefundAccount refundAccount;
    private BankList bankList;
    private CustomerCancellation customerCancellation;
    private Transactions transactions;
    private PaymentType paymentType;
    private TransactionsType transactionsType;
}
