package fu.hbs.service.interfaces;

import fu.hbs.dto.VnpayDTO.ViewPaymentDTO;
import fu.hbs.entities.Transactions;
import org.springframework.data.domain.Page;


import java.time.LocalDate;

public interface TransactionsService {
    Transactions saveTransactions(Transactions vnpayTransactions);

    Page<ViewPaymentDTO> findByCreateDateAndPaymentId(LocalDate createDate, Long paymentId, int page, int size);


    Transactions findFirstTransactionOfHotelBooking(Long hotelBookingId);

    Page<ViewPaymentDTO> findByCreateDateAndPaymentIdAndTypeId(LocalDate checkIn, Long paymentId, Long TypeId, int defaultPage, int defaultSize);
}
